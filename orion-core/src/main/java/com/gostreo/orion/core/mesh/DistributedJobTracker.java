package com.gostreo.orion.core.mesh;

import com.gostreo.orion.common.tracker.DistributedTracker;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DistributedJobTracker implements DistributedTracker {

    private final Map<UUID, Sinks.One<Object>> resultSinks = new ConcurrentHashMap<>();
    private final Map<UUID, Sinks.Many<Object>> updateSinks = new ConcurrentHashMap<>();

    public Sinks.One<Object> createResultSink(UUID jobId) {
        Sinks.One<Object> sink = Sinks.one();
        resultSinks.put(jobId, sink);
        return sink;
    }

    public Sinks.Many<Object> createUpdateSink(UUID jobId) {
        Sinks.Many<Object> sink = Sinks.many().multicast().onBackpressureBuffer();
        updateSinks.put(jobId, sink);
        return sink;
    }

    public void resolveResult(UUID jobId, Object result) {
        Sinks.One<Object> sink = resultSinks.remove(jobId);
        if (sink != null) sink.tryEmitValue(result);
    }

    public void emitUpdate(UUID jobId, Object delta) {
        Sinks.Many<Object> sink = updateSinks.get(jobId);
        if (sink != null) sink.tryEmitNext(delta);
    }

}
