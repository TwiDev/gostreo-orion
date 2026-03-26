package com.gostreo.orion.core.repository;

import com.gostreo.orion.api.Orchestrator;
import com.gostreo.orion.api.repository.OrionRepository;
import com.gostreo.orion.api.repository.OrionRepositoryProvider;

import java.lang.reflect.Proxy;

public class GenericRepositoryProvider<Parent extends Orchestrator, Provider extends OrionRepository<Parent>>
        implements OrionRepositoryProvider<Parent, Provider> {

    private final Class<Provider> classInterface;

    public GenericRepositoryProvider(Class<Provider> classInterface) {
        this.classInterface = classInterface;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Provider build() {
        return (Provider) Proxy.newProxyInstance(
                this.getClass().getClassLoader(),
                new Class[]{classInterface},
                new RepositoryHandler<>(this));
    }

    @Override
    public String getId() {
        return "";
    }
}
