package com.gostreo.orion.api;

public interface Orchestrator {

    @SuppressWarnings("unchecked")
    default Class<? extends Orchestrator> getParameterizedType() {
        return (Class<? extends Orchestrator>) getClass().getGenericSuperclass();
    }

}
