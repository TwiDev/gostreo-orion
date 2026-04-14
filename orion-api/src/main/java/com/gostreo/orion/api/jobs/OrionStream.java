package com.gostreo.orion.api.jobs;

import org.reactivestreams.Publisher;

import java.util.function.Consumer;

public interface OrionStream<T> extends Publisher<T> {

    void subscribe(Consumer<? super T> onNext, Consumer<Throwable> onError, Runnable onComplete);

}
