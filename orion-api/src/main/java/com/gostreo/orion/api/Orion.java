package com.gostreo.orion.api;

import com.gostreo.orion.api.configs.OrchestratorConfig;
import com.gostreo.orion.api.repository.OrionRepository;
import com.gostreo.orion.api.repository.OrionRepositoryProvider;

import java.io.Closeable;
import java.io.IOException;
import java.util.UUID;

public abstract class Orion<Parent extends Orchestrator> implements Closeable {

    public static <Parent extends Orchestrator> Builder<Parent>
                builder(OrionProvider<Parent> provider, OrchestratorConfig<Parent> config) {
        return new Builder<>(provider, config);
    }

    private final UUID instanceId = UUID.randomUUID();
    private final Object closeLock = new Object();
    private boolean closed = false;

    public abstract <Provider extends OrionRepository<Parent>> OrionRepositoryProvider<Parent, Provider>
                        getRepositoryProvider(Class<Provider> repositoryClass);

    @Override
    public synchronized void close() throws IOException {
        synchronized (closeLock) {
            closed = true;
        }
    }

    public boolean isClosed() {
        synchronized (closeLock) {
            return closed;
        }
    }

    public UUID getId() {
        return instanceId;
    }

    public static final class Builder<Parent extends Orchestrator>  {

        private final Parent parent;
        private final OrchestratorConfig<Parent> config;
        private final OrionProvider<Parent> orion;

        public Builder(OrionProvider<Parent> provider, OrchestratorConfig<Parent> config) {
            this.parent = provider.getParent();
            this.config = config;
            this.orion = provider;
        }

        public Builder<Parent> withRepository(Class<? extends OrionRepository<Parent>> parent) {
            return this;
        }

        public Builder<Parent> limit(int limit) {
            return this;
        }

        public Orion<Parent> build() {
            return orion.build(this);
        }
    }
}
