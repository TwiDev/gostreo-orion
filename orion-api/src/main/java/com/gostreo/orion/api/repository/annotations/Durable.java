package com.gostreo.orion.api.repository.annotations;

public @interface Durable {

    String compensateWith() default "";

    int retryAttempts() default 0;

}
