package com.lindar.tasktimeout.spring;


import com.lindar.tasktimeout.core.SchedulerTimeout;
import com.lindar.tasktimeout.core.TimeoutConfiguration;
import com.lindar.tasktimeout.core.TimeoutConfigurationExtractor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.support.ScheduledMethodRunnable;

import java.lang.reflect.Method;
import java.util.Optional;

public class SpringTimeoutConfigurationExtractor implements TimeoutConfigurationExtractor {
    @Override
    public Optional<TimeoutConfiguration> getTimeoutConfiguration(Runnable task) {
        if (task instanceof ScheduledMethodRunnable) {
            Method method = ((ScheduledMethodRunnable) task).getMethod();
            SchedulerTimeout annotation = AnnotationUtils.findAnnotation(method, SchedulerTimeout.class);
            if (shouldTimeout(annotation)) {
                return Optional.of(new TimeoutConfiguration(getName(annotation, method), annotation.timeout()));
            }
        }
        return Optional.empty();
    }

    private boolean shouldTimeout(SchedulerTimeout annotation) {
        return annotation != null;
    }

    private String getName(SchedulerTimeout annotation, Method method){
        if(annotation.name().length() > 0){
            return annotation.name();
        }

        return method.getName();
    }
}
