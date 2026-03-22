package com.gostreo.orion.api.repository;

import com.gostreo.orion.api.Orchestrator;
import com.gostreo.orion.api.Orion;

/**
 * Repository function for orion instances
 */
public interface OrionRepository<Parent extends Orchestrator> {

    class Builder<Parent extends Orchestrator, Provider extends OrionRepository<Parent>> {

        private final Orion<Parent> orion;
        private final Class<Provider> repositoryClass;

        public Builder(Orion<Parent> orion, Class<Provider> repositoryClass) {
            this.orion = orion;
            this.repositoryClass = repositoryClass;
        }

        public Provider build() {
            return orion.getRepositoryProvider(repositoryClass).build();
        }

    }

}
