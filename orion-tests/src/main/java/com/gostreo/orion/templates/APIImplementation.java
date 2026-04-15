package com.gostreo.orion.templates;

import com.gostreo.orion.api.Orion;
import com.gostreo.orion.api.OrionProvider;
import com.gostreo.orion.api.configs.KafkaConfiguration;
import com.gostreo.orion.api.jobs.ExchangeOrionJob;
import com.gostreo.orion.api.jobs.OrionJob;
import com.gostreo.orion.api.messaging.OrionPriority;
import com.gostreo.orion.api.repository.OrionRepository;
import com.gostreo.orion.api.repository.annotations.*;
import com.gostreo.orion.common.AbstractOrionProvider;
import com.gostreo.orion.kafka.KafkaProvider;
import reactor.core.publisher.Mono;

public class APIImplementation {

    static void main() {
        KafkaConfiguration<LangchainWorkers> config = new KafkaConfiguration<>("localhost:9092");

        OrionProvider<LangchainWorkers> provider = KafkaProvider.<LangchainWorkers>builder()
                .withConfiguration(config)
                .build();

        Orion<LangchainWorkers> orion = Orion.builder(provider)
                .withRepository(TestRepository.class)
                .limit(10)
                .build();


        // Todo: move config part to orion provider should be more logical for start and stop of original orion instances
        // -> Done !

        TestRepository testRepository = orion.getRepositoryProvider(TestRepository.class).build();

        TestRepository repository = OrionRepository.builder(orion, TestRepository.class)
                .build();

        repository.sendMessage("Hello").block();

        try {
            orion.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Channel(channelName = "test-channel")
    interface TestRepository extends OrionRepository<LangchainWorkers> {

        @Priority(priority = OrionPriority.HIGH)
        Mono<Void> sendMessage(
                @Payload(param = "message") String message
        );

    }


    static final class LangchainWorkers extends KafkaProvider<LangchainWorkers> {

        @Override
        public LangchainWorkers getParent() {
            return this;
        }
    }

}
