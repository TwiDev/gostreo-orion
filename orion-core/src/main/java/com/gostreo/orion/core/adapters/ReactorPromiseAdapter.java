package com.gostreo.orion.core.adapters;

import com.gostreo.orion.api.jobs.OrionPromise;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Function;

public record ReactorPromiseAdapter<T>(Mono<T> delegate) implements OrionPromise<T> {

    @Override
    public <R> OrionPromise<R> map(Function<? super T, ? extends R> mapper) {
        return new ReactorPromiseAdapter<>(this.delegate.map(mapper));
    }

    @Override
    public <R> OrionPromise<R> flatMap(Function<? super T, ? extends OrionPromise<? extends R>> transformer) {
        return new ReactorPromiseAdapter<>(
                this.delegate.flatMap(value -> {
                    OrionPromise<? extends R> resultPromise = transformer.apply(value);

                    return ((ReactorPromiseAdapter<? extends R>) resultPromise).delegate();
                })
        );
    }

    @Override
    public OrionPromise<T> onErrorResume(Function<Throwable, ? extends OrionPromise<? extends T>> fallback) {
        return new ReactorPromiseAdapter<>(
                this.delegate.onErrorResume(ex -> ((ReactorPromiseAdapter<? extends T>) fallback.apply(ex)).delegate())
        );
    }

    @Override
    public void subscribe(Consumer<? super T> onSuccess, Consumer<Throwable> onError) {
        this.delegate.subscribe(onSuccess, onError);
    }

    @Override
    public T await() {
        return this.delegate.block();
    }

}
