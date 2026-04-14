package com.gostreo.orion.core.engine;

import reactor.core.publisher.Mono;

import java.lang.management.MonitorInfo;

public class WorkflowEngine {

    public <T> Mono<T> executeActivity(String worflowId, String activityId, Mono<T> execution) {
        return execution.doOnNext(result -> {

        });
    }

}
