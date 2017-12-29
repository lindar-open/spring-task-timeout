package com.lindar.tasktimeout.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SchedulerTimeout {
    String name() default "";
    long timeout() default -1;
    String timeoutString() default "";
}