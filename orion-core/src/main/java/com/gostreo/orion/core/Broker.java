package com.gostreo.orion.core;

import com.gostreo.orion.api.Orchestrator;
import com.gostreo.orion.api.proto.OrionMessageProto;
import com.gostreo.orion.api.repository.OrionRepository;
import com.gostreo.orion.api.repository.OrionRepositoryProvider;
import com.gostreo.orion.common.AbstractBroker;
import com.gostreo.orion.common.BrokerInstance;
import com.gostreo.orion.core.repository.GenericRepositoryProvider;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Broker<Parent extends Orchestrator> extends AbstractBroker<Parent> {

    private final BrokerInstance<Parent> brokerInstance;

    private final Map<Class<?>, OrionRepositoryProvider<Parent, ?>> repositoryCache = new ConcurrentHashMap<>();

    public Broker(BrokerInstance<Parent> brokerInstance) {
        this.brokerInstance = brokerInstance;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Provider extends OrionRepository<Parent>> OrionRepositoryProvider<Parent, Provider> getRepositoryProvider(Class<Provider> repositoryClass) {

        //Todo: change that
        return (OrionRepositoryProvider<Parent, Provider>)
                repositoryCache.computeIfAbsent(repositoryClass, clazz -> {

            return new GenericRepositoryProvider<>(repositoryClass);
        });
    }

    @Override
    public synchronized void close() throws IOException {
        brokerInstance.close(this);

        super.close();
    }

    @Override
    public Mono<Void> send(String topic, OrionMessageProto proto) {
        int id = proto.getJobId();

        return null;
    }
}
