package com.gostreo.orion.api.configs;

import com.gostreo.orion.api.Orchestrator;

public abstract class Configuration<Parent extends Orchestrator> implements OrchestratorConfig<Parent>{

    public Configuration() {

    }
}
