package com.gostreo.orion.core.jobs;

import com.gostreo.orion.api.Orion;
import com.gostreo.orion.api.cluster.ExecutionGraph;
import com.gostreo.orion.api.cluster.nodes.Node;
import com.gostreo.orion.api.jobs.JobState;
import com.gostreo.orion.api.jobs.OrionJob;
import com.gostreo.orion.api.messaging.OrionMessage;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Job extends CompletableFuture<OrionJob> implements OrionJob {

    public Job() {

    }

    public void process(OrionMessage message) {
        acceptEither(this, orionJob -> {

        });
    }

    @Override
    public Node getCurrentlyExecutingNode() {
        return null;
    }

    @Override
    public Orion<?> getOrigin() {
        return null;
    }

    @Override
    public ExecutionGraph getExecutionGraph() {
        return null;
    }

    @Override
    public long getExecutionTime() {
        return 0;
    }

    @Override
    public UUID getJobId() {
        return null;
    }

    @Override
    public JobState getState() {
        return null;
    }

    @Override
    public void close() throws Exception {

    }
}
