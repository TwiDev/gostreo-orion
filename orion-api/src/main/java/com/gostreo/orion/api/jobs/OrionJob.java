package com.gostreo.orion.api.jobs;

import com.gostreo.orion.api.Orion;
import com.gostreo.orion.api.cluster.ExecutionGraph;
import com.gostreo.orion.api.cluster.nodes.Node;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Future;

public interface OrionJob<T> extends AutoCloseable {

    Node getCurrentlyExecutingNode();

    Orion<?> getOrigin();

    Mono<T> result();

    Flux<Object> updates();

    void cancel();

    ExecutionGraph getExecutionGraph();

    long getExecutionTime();

    UUID getJobId();

    JobState getState();
}
