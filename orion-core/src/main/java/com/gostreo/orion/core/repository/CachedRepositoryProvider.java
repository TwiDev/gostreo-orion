package com.gostreo.orion.core.repository;

import com.gostreo.orion.api.Orchestrator;
import com.gostreo.orion.api.repository.OrionRepository;

public class CachedRepositoryProvider<Parent extends Orchestrator, Provider extends OrionRepository<Parent>> extends AbstractRepositoryProvider<Parent, Provider> {

    public CachedRepositoryProvider(Class<Provider> classInterface) {
        super(classInterface);
    }

    @Override
    public String getId() {
        return "";
    }
}
