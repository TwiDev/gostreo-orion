package com.gostreo.orion.templates;

import com.gostreo.orion.api.Orchestrator;
import com.gostreo.orion.api.Orion;
import com.gostreo.orion.api.OrionProvider;
import com.gostreo.orion.api.configs.KafkaConfiguration;
import com.gostreo.orion.api.messaging.OrionPriority;
import com.gostreo.orion.api.repository.OrionRepository;
import com.gostreo.orion.api.repository.OrionRepositoryProvider;
import com.gostreo.orion.api.repository.annotations.Channel;
import com.gostreo.orion.api.repository.annotations.Payload;
import com.gostreo.orion.api.repository.annotations.Post;
import com.gostreo.orion.api.repository.annotations.Priority;

import java.io.IOException;
import java.util.*;

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
        testRepository.sendMessage("hi");

        TestRepository repository = new OrionRepository.Builder<>(orion, TestRepository.class)
                .build();

        repository.sendMessage("hello");

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
        @Payload()
        void sendMessage(String message);

    }

    static abstract class OrionProviderImpl<Parent extends Orchestrator> implements OrionProvider<Parent>, Orchestrator {

        // Auto-closeable queue instance that automatically removes closed Orion instances
        private final Set<Orion<Parent>> activeOrionInstances = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<>()));

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
            Broker<Parent> broker = new Broker<>(){
                @Override
                public void close() throws IOException {
                    activeOrionInstances.remove(this);

                    super.close();
                }
            };
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
    }

    static final class LangchainWorkers extends Kafka<LangchainWorkers> implements OrionProvider<LangchainWorkers>{
        
        @Override
        public LangchainWorkers getParent() {
            return this;
        }
    }

    static abstract class Kafka<Parent extends Orchestrator> extends OrionProviderImpl<Parent>{
        @Override
        public Class<? extends Kafka> getParameterizedType() {
            return this.getClass();
        }
    }

    static class Broker<Parent extends Orchestrator> extends Orion<Parent> {

        @Override
        public void helloworld() {

        }

        @Override
        public <Provider extends OrionRepository<Parent>> OrionRepositoryProvider<Parent, Provider> getRepositoryProvider(Class<Provider> repositoryClass) {
            return null;
        }


    }

}
