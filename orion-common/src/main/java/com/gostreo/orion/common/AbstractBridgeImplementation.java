package com.gostreo.orion.common;

import com.gostreo.orion.api.Orchestrator;

public abstract class AbstractBridgeImplementation<Parent extends Orchestrator> {

    public abstract AbstractBroker<Parent> createBrokerInstance(BrokerInstance<Parent> instance);

}
