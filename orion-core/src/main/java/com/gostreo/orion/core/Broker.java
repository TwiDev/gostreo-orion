package com.gostreo.orion.core;

import com.gostreo.orion.api.Orchestrator;
import com.gostreo.orion.api.proto.OrionMessageProto;
import com.gostreo.orion.api.repository.OrionRepository;
import com.gostreo.orion.api.repository.OrionRepositoryProvider;
import com.gostreo.orion.common.AbstractBroker;
import com.gostreo.orion.common.BrokerInstance;
import com.gostreo.orion.core.mesh.OrionMeshNode;
import com.gostreo.orion.core.repository.GenericRepositoryProvider;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Broker<Parent extends Orchestrator> extends AbstractBroker<Parent> {

    private static final int DEFAULT_PORT = 9092;

    private final BrokerInstance<Parent> brokerInstance;

    private final Map<Class<?>, OrionRepositoryProvider<Parent, ?>> repositoryCache = new ConcurrentHashMap<>();

    private final OrionMeshNode orionMeshNode;

    public Broker(BrokerInstance<Parent> brokerInstance) {
        this.brokerInstance = brokerInstance;

        // Todo: handle tracker in a proper way
        this.orionMeshNode = new OrionMeshNode(UUID.randomUUID().toString(), null);
        this.orionMeshNode.start(DEFAULT_PORT);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Provider extends OrionRepository<Parent>> OrionRepositoryProvider<Parent, Provider> getRepositoryProvider(Class<Provider> repositoryClass) {

        //Todo: change that
        return (OrionRepositoryProvider<Parent, Provider>)
                repositoryCache.computeIfAbsent(repositoryClass, clazz -> {

            return new GenericRepositoryProvider<>(this, repositoryClass);
        });
    }

    @Override
    public synchronized void close() throws IOException {
        this.brokerInstance.close(this);

        this.orionMeshNode.close();

        super.close();
    }

    @Override
    public Mono<Void> send(String topic, OrionMessageProto proto) {
        int id = proto.getJobId();

        return null;
    }
}
