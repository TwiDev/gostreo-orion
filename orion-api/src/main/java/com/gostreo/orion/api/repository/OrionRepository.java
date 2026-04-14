package com.gostreo.orion.api.repository;

import com.gostreo.orion.api.Orchestrator;
import com.gostreo.orion.api.Orion;

/**
 * Repository function for orion instances
 */
public interface OrionRepository<Parent extends Orchestrator> {

    Parent getOrchestrator();

    static <Parent extends Orchestrator, Provider extends OrionRepository<Parent>>
                Builder<Parent, Provider> builder(Orion<Parent> orion, Class<Provider> repositoryClass) {

        return new Builder<>(orion, repositoryClass);
    }

    class Builder<Parent extends Orchestrator, Provider extends OrionRepository<Parent>> {

        private final Orion<Parent> orion;
        private final Class<Provider> repositoryClass;

        public Builder(Orion<Parent> orion, Class<Provider> repositoryClass) {
            this.orion = orion;
            this.repositoryClass = repositoryClass;
        }

        public Provider build() {
            return orion.getRepositoryProvider(repositoryClass)
                    .build();
        }

    }

}
