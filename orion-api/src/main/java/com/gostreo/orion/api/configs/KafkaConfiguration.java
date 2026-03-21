package com.gostreo.orion.api.configs;

import com.gostreo.orion.api.Orchestrator;

public class KafkaConfiguration<Kafka extends Orchestrator> extends Configuration<Kafka> {

    private final String bootstrapServers;

    public KafkaConfiguration(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }
}
