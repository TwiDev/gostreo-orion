package com.gostreo.orion.core.repository;

import com.gostreo.orion.api.Orchestrator;
import com.gostreo.orion.api.proto.OrionMessageProto;
import com.gostreo.orion.api.repository.OrionRepository;
import reactor.core.publisher.Mono;

public class CachedRepositoryProvider<Parent extends Orchestrator, Provider extends OrionRepository<Parent>> extends GenericRepositoryProvider<Parent, Provider> {

    private final GenericRepositoryProvider<Parent, Provider> provider;

    public CachedRepositoryProvider(GenericRepositoryProvider<Parent, Provider> provider) {
        super(provider.getBroker(), provider.getClassInterface());

        this.provider = provider;
    }

    @Override
    public Mono<Void> send(String topic, OrionMessageProto proto) {
        // Caching logic

        return super.send(topic, proto);
    }
}
