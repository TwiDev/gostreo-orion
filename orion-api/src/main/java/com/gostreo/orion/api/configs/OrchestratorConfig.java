package com.gostreo.orion.api.configs;

import com.gostreo.orion.api.Orchestrator;

public interface OrchestratorConfig<Parent extends Orchestrator> {

    OrchestratorConfig<?> EMPTY = new OrchestratorConfig<>() {};

}
