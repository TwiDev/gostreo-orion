package com.gostreo.orion.core.mesh;

import io.rsocket.RSocket;
import io.rsocket.core.RSocketServer;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.server.TcpServerTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public class OrionMeshNode {

    private final Logger logger = LoggerFactory.getLogger(OrionMeshNode.class);
    private final DistributedJobTracker tracker;
    private final String nodeId;

    public OrionMeshNode(String nodeId, DistributedJobTracker tracker) {
        this.nodeId = nodeId;
        this.tracker = tracker;
    }

    public void start(int port) {
        RSocketServer.create()
                .payloadDecoder(PayloadDecoder.ZERO_COPY)
                // Logique de réception de messages entre orchestrateurs
                .acceptor((setup, sendingRSocket) -> Mono.just(new RSocket() {
                    @Override
                    public Mono<Void> fireAndForget(io.rsocket.Payload payload) {
                        // Logique : "Hey, j'ai reçu une réponse Kafka pour ton Job"
                        // On décode et on appelle tracker.resolveResult(...)
                        return Mono.empty();
                    }
                }))
                .bind(TcpServerTransport.create(port))
                .subscribe();

        logger.debug("Node {} listening on port {}", nodeId, port);
    }

}
