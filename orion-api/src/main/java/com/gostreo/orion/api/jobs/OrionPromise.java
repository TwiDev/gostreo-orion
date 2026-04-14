package com.gostreo.orion.api.jobs;

import java.util.function.Consumer;
import java.util.function.Function;

public interface OrionPromise<T> {

    <R> OrionPromise<R> map(Function<? super T, ? extends R> mapper);

    <R> OrionPromise<R> flatMap(Function<? super T, ? extends OrionPromise<? extends R>> transformer);

    OrionPromise<T> onErrorResume(Function<Throwable, ? extends OrionPromise<? extends T>> fallback);

    void subscribe(Consumer<? super T> onSuccess, Consumer<Throwable> onError);
    T await();

}
