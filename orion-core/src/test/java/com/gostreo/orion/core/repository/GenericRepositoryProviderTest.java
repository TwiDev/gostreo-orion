package com.gostreo.orion.core.repository;

import com.gostreo.orion.api.Orion;
import com.gostreo.orion.api.OrionProvider;
import com.gostreo.orion.api.configs.KafkaConfiguration;
import com.gostreo.orion.api.jobs.ExchangeOrionJob;
import com.gostreo.orion.api.jobs.OrionJob;
import com.gostreo.orion.api.messaging.OrionMessage;
import com.gostreo.orion.api.messaging.OrionPriority;
import com.gostreo.orion.api.repository.OrionRepository;
import com.gostreo.orion.api.repository.annotations.*;
import com.gostreo.orion.common.AbstractOrionProvider;
import com.gostreo.orion.core.CoreImplementation;
import com.gostreo.orion.core.jobs.Job;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class GenericRepositoryProviderTest {

    final ExecutorService executorService = Executors.newCachedThreadPool();

    @Test
    void checkJobForwardingThroughNodes() {
        OrionProvider<LangchainWorkers> provider = new LangchainWorkers();
        KafkaConfiguration<LangchainWorkers> config = new KafkaConfiguration<>("localhost:9092");

        Orion<LangchainWorkers> orion = new Orion.Builder<>(provider, config)
                .withRepository(TestRepository.class)
                .limit(10)
                .build();

        orion.helloworld();

        TestRepository testRepository = new TestRepository() {
            @Override
            public LangchainWorkers getOrchestrator() {
                return provider.getParent();
            }
        };

        try (OrionJob orionJob = testRepository.sendMessage("hi")) {
            executorService.submit(() -> {
                Job job = (Job) orionJob;
                job.process(OrionMessage.EMPTY);
                job.process(OrionMessage.EMPTY);
                job.process(OrionMessage.EMPTY);
                job.process(OrionMessage.EMPTY);

                System.out.println("Message processed");
            });

            System.out.println("hi");

            orionJob.thenAccept(job -> {
                System.out.println(job.getCurrentlyExecutingNode().getId());
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println("done");

//        try {
//            orion.close();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

    }

    static final class LangchainWorkers extends AbstractOrionProvider<LangchainWorkers> {

        public LangchainWorkers() {
            super(new CoreImplementation<>());
        }

        @Override
        public LangchainWorkers getParent() {
            return this;
        }
    }

    interface TestRepository extends OrionRepository<LangchainWorkers> {

        @Post
        @Channel(channelName = "test-channel")
        @Priority(priority = OrionPriority.HIGH)
        @Payload
        default OrionJob sendMessage(String message) {
            return new Job();
        }


    }
}