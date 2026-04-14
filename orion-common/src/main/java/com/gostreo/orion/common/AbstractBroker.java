package com.gostreo.orion.common;

import com.gostreo.orion.api.Orchestrator;
import com.gostreo.orion.api.Orion;
import com.gostreo.orion.api.proto.OrionMessageProto;
import reactor.core.publisher.Mono;

public abstract class AbstractBroker<Parent extends Orchestrator> extends Orion<Parent> {

    @Override
    public void helloworld() {

    }

    public abstract Mono<Void> send(String topic, OrionMessageProto proto);

}