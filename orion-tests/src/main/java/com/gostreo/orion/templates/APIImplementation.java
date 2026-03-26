package com.gostreo.orion.templates;

import com.gostreo.orion.api.Orion;
import com.gostreo.orion.api.OrionProvider;
import com.gostreo.orion.api.configs.KafkaConfiguration;
import com.gostreo.orion.api.jobs.ExchangeOrionJob;
import com.gostreo.orion.api.jobs.OrionJob;
import com.gostreo.orion.api.messaging.OrionPriority;
import com.gostreo.orion.api.repository.OrionRepository;
import com.gostreo.orion.api.repository.annotations.*;
import com.gostreo.orion.kafka.KafkaProvider;

public class APIImplementation {

    static void main() {
        OrionProvider<LangchainWorkers> provider = new LangchainWorkers();
        KafkaConfiguration<LangchainWorkers> config = new KafkaConfiguration<>("localhost:9092");

        Orion<LangchainWorkers> orion = new Orion.Builder<>(provider, config)
                .withRepository(TestRepository.class)
                .limit(10)
                .build();

        orion.helloworld();

        TestRepository testRepository = orion.getRepositoryProvider(TestRepository.class).build();
        ExchangeOrionJob orionJob = testRepository.sendMessage("hi");

        orionJob.thenAccept(job -> {
            System.out.println(job.getCurrentlyExecutingNode().getId());
        });


        TestRepository repository = new OrionRepository.Builder<>(orion, TestRepository.class)
                .build();

        try(ExchangeOrionJob job = repository.sendMessage("hello")) {
            job.thenAccept(message -> {
                System.out.println(message.getCurrentlyExecutingNode().getId());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            orion.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    interface TestRepository extends OrionRepository<LangchainWorkers> {

        @Post
        @Channel(channelName = "test-channel")
        @Priority(priority = OrionPriority.HIGH)
        @Payload
        default OrionJob sendMessage() {
            return sendMessage(null);
        }

        @Post
        @Channel(channelName = "test-channel")
        @Priority(priority = OrionPriority.HIGH)
        @Payload
        ExchangeOrionJob sendMessage(String message);

    }


    static final class LangchainWorkers extends KafkaProvider<LangchainWorkers> {

        @Override
        public LangchainWorkers getParent() {
            return this;
        }
    }

}
