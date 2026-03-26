package com.gostreo.orion.core;

import com.gostreo.orion.api.Orchestrator;
import com.gostreo.orion.api.repository.OrionRepository;
import com.gostreo.orion.api.repository.OrionRepositoryProvider;
import com.gostreo.orion.common.AbstractBroker;
import com.gostreo.orion.common.BrokerInstance;
import com.gostreo.orion.core.repository.GenericRepositoryProvider;

import java.io.IOException;

public class Broker<Parent extends Orchestrator> extends AbstractBroker<Parent> {

    private final BrokerInstance<Parent> brokerInstance;

    public Broker(BrokerInstance<Parent> brokerInstance) {
        this.brokerInstance = brokerInstance;
    }

    @Override
    public <Provider extends OrionRepository<Parent>> OrionRepositoryProvider<Parent, Provider> getRepositoryProvider(Class<Provider> repositoryClass) {
        return new GenericRepositoryProvider<>(repositoryClass);
    }

    @Override
    public synchronized void close() throws IOException {
        brokerInstance.close(this);

        super.close();
    }
}
