package com.gostreo.orion.core.repository;

import com.gostreo.orion.api.Orchestrator;
import com.gostreo.orion.api.proto.OrionMessageProto;
import com.gostreo.orion.api.repository.OrionRepository;
import com.gostreo.orion.api.repository.OrionRepositoryProvider;
import com.gostreo.orion.core.Broker;
import com.gostreo.orion.core.mesh.DistributedJobTracker;
import reactor.core.publisher.Mono;

import java.lang.reflect.Proxy;
import java.util.UUID;

public class GenericRepositoryProvider<Parent extends Orchestrator, Provider extends OrionRepository<Parent>>
        implements OrionRepositoryProvider<Parent, Provider> {

    private final DistributedJobTracker distributedJobTracker;
    private final Class<Provider> classInterface;
    private final Broker<Parent> broker;

    public GenericRepositoryProvider(Broker<Parent> broker, Class<Provider> classInterface) {
        this.classInterface = classInterface;
        this.broker = broker;

        this.distributedJobTracker = new DistributedJobTracker();
    }

    public Mono<Void> send(String topic, OrionMessageProto proto) {
        return broker.send(topic, proto);
    }

    protected Class<Provider> getClassInterface() {
        return classInterface;
    }

    protected Broker<Parent> getBroker() {
        return broker;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Provider build() {
        return (Provider) Proxy.newProxyInstance(
                this.getClass().getClassLoader(),
                new Class[]{classInterface},
                new RepositoryHandler<>(distributedJobTracker, this));
    }

    @Override
    public String getId() {
        return new UUID(
                UUID.randomUUID().getMostSignificantBits(),
                broker.getId().getLeastSignificantBits()).toString();
    }
}
