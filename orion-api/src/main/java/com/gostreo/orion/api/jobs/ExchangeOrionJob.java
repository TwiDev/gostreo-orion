package com.gostreo.orion.api.jobs;

import com.gostreo.orion.api.messaging.OrionPayload;

public interface ExchangeOrionJob extends OrionJob {

    OrionPayload getPayload();

    int size();

}
