package com.lindar.tasktimeout.spring;


import com.lindar.tasktimeout.core.SchedulerTimeout;
import com.lindar.tasktimeout.core.TimeoutConfiguration;
import com.lindar.tasktimeout.core.TimeoutConfigurationExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Optional;

@Slf4j
public class SpringTimeoutConfigurationExtractor implements TimeoutConfigurationExtractor {

    static final long DEFAULT_TIMEOUT = Duration.ofHours(1).toMillis();

    private final StringValueResolver embeddedValueResolver;

    private final long defaultTimeout;

    public SpringTimeoutConfigurationExtractor(){
        this(DEFAULT_TIMEOUT, null);
    }

    public SpringTimeoutConfigurationExtractor(long defaultTimeout){
        this(defaultTimeout, null);
    }

    public SpringTimeoutConfigurationExtractor(long defaultTimeout, StringValueResolver embeddedValueResolver) {
        this.embeddedValueResolver = embeddedValueResolver;
        this.defaultTimeout = defaultTimeout;
    }

    @Override
    public Optional<TimeoutConfiguration> getTimeoutConfiguration(Runnable task) {
        if (task instanceof ScheduledMethodRunnable) {
            Method method = ((ScheduledMethodRunnable) task).getMethod();
            SchedulerTimeout annotation = AnnotationUtils.findAnnotation(method, SchedulerTimeout.class);
            if (shouldTimeout(annotation)) {
                return Optional.of(new TimeoutConfiguration(getName(annotation, method), getTimeout(annotation)));
            }
        }
        return Optional.empty();
    }

    private boolean shouldTimeout(SchedulerTimeout annotation) {
        return annotation != null;
    }

    SchedulerTimeout findAnnotation(Method method) {
        return AnnotatedElementUtils.getMergedAnnotation(method, SchedulerTimeout.class);
    }

    SchedulerTimeout findAnnotation(ScheduledMethodRunnable task) {
        Method method = task.getMethod();
        SchedulerTimeout annotation = findAnnotation(method);
        if (annotation != null) {
            return annotation;
        } else {
            // Try to find annotation on proxied class
            Class<?> targetClass = AopUtils.getTargetClass(task.getTarget());
            if (targetClass != null && !task.getTarget().getClass().equals(targetClass)) {
                try {
                    Method methodOnTarget = targetClass
                            .getMethod(method.getName(), method.getParameterTypes());
                    return findAnnotation(methodOnTarget);
                } catch (NoSuchMethodException e) {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    String getName(SchedulerTimeout annotation, Method method){

        if(annotation.name().length() > 0){

            if(embeddedValueResolver != null) {
                return embeddedValueResolver.resolveStringValue(annotation.name());
            }

            return annotation.name();
        }

        return method.getName();
    }

    long getTimeout(SchedulerTimeout annotation){

        long valueFromAnnotation = annotation.timeout();
        String stringValueFromAnnotation = annotation.timeoutString();

        if(valueFromAnnotation >= 0){
            return valueFromAnnotation;
        } else if (StringUtils.hasText(stringValueFromAnnotation)){
            if(embeddedValueResolver != null){
                stringValueFromAnnotation = embeddedValueResolver.resolveStringValue(stringValueFromAnnotation);
            }

            try {
                return Long.valueOf(stringValueFromAnnotation);
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("Invalid timeout value \"" + stringValueFromAnnotation + "\" - cannot parse into long");
            }
        }

        return defaultTimeout;
    }
}
