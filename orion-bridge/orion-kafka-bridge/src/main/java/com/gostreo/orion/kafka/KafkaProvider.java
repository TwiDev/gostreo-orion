package com.gostreo.orion.kafka;

import com.gostreo.orion.api.Orchestrator;
import com.gostreo.orion.common.AbstractBridgeImplementation;
import com.gostreo.orion.common.AbstractOrionProvider;
import com.gostreo.orion.core.CoreImplementation;

public abstract class KafkaProvider<Parent extends Orchestrator> extends AbstractOrionProvider<Parent> {

    public KafkaProvider() {
        super(new CoreImplementation<>());
    }

    public KafkaProvider(AbstractBridgeImplementation<Parent> bridgeImplementation) {
        super(bridgeImplementation);
    }

    @Override
    public Class<? extends KafkaProvider> getParameterizedType() {
        return this.getClass();
    }
}

