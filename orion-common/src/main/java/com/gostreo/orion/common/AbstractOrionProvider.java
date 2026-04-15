package com.gostreo.orion.common;

import com.gostreo.orion.api.Orchestrator;
import com.gostreo.orion.api.Orion;
import com.gostreo.orion.api.OrionProvider;
import com.gostreo.orion.api.configs.OrchestratorConfig;

import java.util.*;

public abstract class AbstractOrionProvider<Parent extends Orchestrator>
        implements OrionProvider<Parent>, Orchestrator {

    // Auto-closeable queue instance that automatically removes closed Orion instances
    private final Set<Orion<Parent>> activeOrionInstances = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<>()));

    private final AbstractBridgeImplementation<Parent> bridgeImplementation;

    public AbstractOrionProvider(AbstractBridgeImplementation<Parent> bridgeImplementation) {
        this.bridgeImplementation = bridgeImplementation;
    }

    /**
     * Get the freshest instance of the Orion.
     *
     * @return the Orion instance
     * @throws IllegalStateException if the Orion is not started
     */
    @Override
    public Orion<Parent> get() throws IllegalStateException {
        synchronized (activeOrionInstances) {
            return activeOrionInstances.stream()
                    .filter(orion -> !orion.isClosed())
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Orion is not started"));
        }
    }

    @Override
    public Orion<Parent> build(Orion.Builder<Parent> builder) {
        AbstractBroker<Parent> broker = bridgeImplementation.createBrokerInstance(activeOrionInstances::remove);
        activeOrionInstances.add(broker);
        return broker;
    }

    @Override
    public void closeAll() {
        final List<Orion<Parent>> snapshot;
        synchronized (activeOrionInstances) {
            snapshot = new ArrayList<>(activeOrionInstances);
        }

        RuntimeException shutdownFailure = null;

        for (Orion<Parent> instance : snapshot) {
            if (instance.isClosed()) {
                continue;
            }

            try {
                instance.close();
            } catch (Exception e) {
                if (shutdownFailure == null) {
                    shutdownFailure = new RuntimeException("Failed to cleanly close all Orion instances.");
                }
                shutdownFailure.addSuppressed(e);
            }
        }

        if (shutdownFailure != null) {
            throw shutdownFailure;
        }
    }

    public static <Parent extends Orchestrator> Builder<Parent> builder() {
        return new Builder<>();
    }

    public static final class Builder<Parent extends Orchestrator> {

        private OrchestratorConfig<Parent> config;

        public Builder() {}

        public Builder<Parent> withConfiguration(OrchestratorConfig<Parent> config) {
            this.config = config;

            return this;
        }

        public Parent build() {
            return null;
        }
    }
}
