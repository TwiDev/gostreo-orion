package com.gostreo.orion.common;

import com.gostreo.orion.api.Orchestrator;

import java.io.IOException;

@FunctionalInterface
public interface BrokerInstance<Parent extends Orchestrator> {

    void close(AbstractBroker<Parent> abstractBroker) throws IOException;
}
