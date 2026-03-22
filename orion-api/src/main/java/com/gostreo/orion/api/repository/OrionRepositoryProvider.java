package com.gostreo.orion.api.repository;

import com.gostreo.orion.api.Orchestrator;

public interface OrionRepositoryProvider<Parent extends Orchestrator, Provider extends OrionRepository<Parent>> {

    Provider build();

    String getId();

}
