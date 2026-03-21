package com.gostreo.orion.api;

public interface OrionProvider<Provider extends Orchestrator> {

    Provider getParent();

    Orion<Provider> get() throws IllegalStateException;

    Orion<Provider> build(Orion.Builder<Provider> builder);

    void closeAll();

}
