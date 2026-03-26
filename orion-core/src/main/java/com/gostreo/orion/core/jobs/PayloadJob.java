package com.gostreo.orion.core.jobs;

import com.gostreo.orion.api.jobs.ExchangeOrionJob;
import com.gostreo.orion.api.messaging.OrionPayload;

public class PayloadJob extends Job implements ExchangeOrionJob {

    public PayloadJob(OrionPayload payload) {

    }

    @Override
    public OrionPayload getPayload() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }
}
