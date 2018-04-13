package com.lindar.tasktimeout.spring;

import com.lindar.tasktimeout.core.SchedulerTimeout;
import com.lindar.tasktimeout.core.TimeoutConfiguration;
import org.junit.Test;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.util.StringValueResolver;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SpringTimeoutConfigurationExtractorTest {

    private static final long DEFAULT_TIMEOUT = 9999;

    private final StringValueResolver embeddedValueResolver = mock(StringValueResolver.class);
    private final SpringTimeoutConfigurationExtractor extractor = new SpringTimeoutConfigurationExtractor(DEFAULT_TIMEOUT, embeddedValueResolver);


    @Test
    public void shouldNotTimeoutUnannotatedMethod() throws NoSuchMethodException {
        ScheduledMethodRunnable runnable = new ScheduledMethodRunnable(this, "methodWithoutAnnotation");
        Optional<TimeoutConfiguration> timeoutConfiguration = extractor.getTimeoutConfiguration(runnable);
        assertThat(timeoutConfiguration).isEmpty();
    }

    @Test
    public void shouldGetNameAndTimeoutTimeFromAnnotation() throws NoSuchMethodException {
        when(embeddedValueResolver.resolveStringValue("timeoutName")).thenReturn("timeoutName");
        ScheduledMethodRunnable runnable = new ScheduledMethodRunnable(this, "annotatedMethod");
        TimeoutConfiguration timeoutConfiguration = extractor.getTimeoutConfiguration(runnable).get();
        assertThat(timeoutConfiguration.getName()).isEqualTo("timeoutName");
        assertThat(timeoutConfiguration.getTimeout()).isEqualTo(100);
    }

    @Test
    public void shouldGetNameFromSpringVariable() throws NoSuchMethodException {
        when(embeddedValueResolver.resolveStringValue("${name}")).thenReturn("timeoutNameX");
        ScheduledMethodRunnable runnable = new ScheduledMethodRunnable(this, "annotatedMethodWithNameVariable");
        TimeoutConfiguration timeoutConfiguration = extractor.getTimeoutConfiguration(runnable).get();
        assertThat(timeoutConfiguration.getName()).isEqualTo("timeoutNameX");
    }

    @Test
    public void shouldTimeoutForDefaultTimeIfNoAnnotation() throws NoSuchMethodException {
        SchedulerTimeout annotation = getAnnotation("annotatedMethodWithoutTimeoutAtMostFor");
        long timeout = extractor.getTimeout(annotation);
        assertThat(timeout).isEqualTo(DEFAULT_TIMEOUT);
    }

    @Test
    public void shouldTimeoutTimeFromAnnotation() throws NoSuchMethodException {
        SchedulerTimeout annotation = getAnnotation("annotatedMethod");
        long timeoutAtMostFor = extractor.getTimeout(annotation);
        assertThat(timeoutAtMostFor).isEqualTo(100);
    }

    @Test
    public void shouldTimeoutTimeFromAnnotationWithString() throws NoSuchMethodException {
        when(embeddedValueResolver.resolveStringValue("${placeholder}")).thenReturn("5");
        SchedulerTimeout annotation = getAnnotation("annotatedMethodWithString");
        long timeoutAtMostFor = extractor.getTimeout(annotation);
        assertThat(timeoutAtMostFor).isEqualTo(5);
    }



    protected SchedulerTimeout getAnnotation(String method) throws NoSuchMethodException {
        return extractor.findAnnotation(new ScheduledMethodRunnable(this, method));
    }

    public void methodWithoutAnnotation() {

    }

    @SchedulerTimeout(name = "timeoutName", timeout = 100)
    public void annotatedMethod() {

    }

    @SchedulerTimeout(name = "timeoutName", timeoutString = "${placeholder}")
    public void annotatedMethodWithString() {

    }

    @SchedulerTimeout(name = "${name}")
    public void annotatedMethodWithNameVariable() {

    }

    @SchedulerTimeout(name = "timeoutName")
    public void annotatedMethodWithoutTimeoutAtMostFor() {

    }

    @SchedulerTimeout(name = "timeoutName", timeout = 10)
    public void annotatedMethodWithPositiveGracePeriod() {
    }

    @SchedulerTimeout(name = "timeoutName", timeoutString = "10")
    public void annotatedMethodWithPositiveGracePeriodWithString() {

    }
}
