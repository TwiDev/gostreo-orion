package com.gostreo.orion.api.repository.annotations;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface WorkerRequirements {
    WorkerRequirement[] value();
}
