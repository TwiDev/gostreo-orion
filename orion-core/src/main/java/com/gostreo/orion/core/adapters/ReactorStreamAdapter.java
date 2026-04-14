package com.gostreo.orion.core.adapters;

import com.gostreo.orion.api.jobs.OrionStream;
import org.reactivestreams.Subscriber;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

public record ReactorStreamAdapter<T>(Flux<T> delegate) implements OrionStream<T> {

    @Override
    public void subscribe(Consumer<? super T> onNext, Consumer<Throwable> onError, Runnable onComplete) {
        this.delegate.subscribe(onNext, onError, onComplete);
    }

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        this.delegate.subscribe(subscriber);
    }
}
