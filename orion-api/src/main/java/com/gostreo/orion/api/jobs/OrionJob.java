package com.gostreo.orion.api.jobs;

import com.gostreo.orion.api.Orion;
import com.gostreo.orion.api.cluster.ExecutionGraph;
import com.gostreo.orion.api.cluster.nodes.Node;

import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Future;

public interface OrionJob extends AutoCloseable, Future<OrionJob>, CompletionStage<OrionJob> {

    Node getCurrentlyExecutingNode();

    Orion<?> getOrigin();

    ExecutionGraph getExecutionGraph();

    long getExecutionTime();

    UUID getJobId();

    JobState getState();
}
