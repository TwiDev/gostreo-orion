package com.gostreo.orion.api.jobs;

import java.util.function.Consumer;

public interface OrionStream<T> {

    void subscribe(Consumer<? super T> onNext, Consumer<Throwable> onError, Runnable onComplete);

}
