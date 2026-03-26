package com.gostreo.orion.core;

import com.gostreo.orion.api.Orchestrator;
import com.gostreo.orion.common.AbstractBridgeImplementation;
import com.gostreo.orion.common.AbstractBroker;
import com.gostreo.orion.common.BrokerInstance;

public class CoreImplementation<Parent extends Orchestrator> extends AbstractBridgeImplementation<Parent> {

    @Override
    public AbstractBroker<Parent> createBrokerInstance(BrokerInstance<Parent> instance) {
        return new Broker<>(instance);
    }
}
