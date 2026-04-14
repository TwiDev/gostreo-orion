package com.gostreo.orion.core.mesh;

import io.rsocket.RSocket;
import io.rsocket.core.RSocketServer;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.server.TcpServerTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.io.Closeable;
import java.io.IOException;

public class OrionMeshNode implements Closeable {

    private final Logger logger = LoggerFactory.getLogger(OrionMeshNode.class);
    private final DistributedJobTracker tracker;
    private final String nodeId;

    private Disposable disposable = null;

    public OrionMeshNode(String nodeId, DistributedJobTracker tracker) {
        this.nodeId = nodeId;
        this.tracker = tracker;
    }

    public void start(int port) {
        this.disposable = RSocketServer.create()
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

    @Override
    public void close() throws IOException {
        if(disposable != null) {
            disposable.dispose();
        }
    }
}
