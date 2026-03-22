package com.gostreo.orion.core.repository;

import com.gostreo.orion.api.Orchestrator;
import com.gostreo.orion.api.repository.OrionRepository;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public record RepositoryHandler<Parent extends Orchestrator, Provider extends OrionRepository<Parent>>
        (AbstractRepositoryProvider<Parent, Provider> repositoryProvider) implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }
}
