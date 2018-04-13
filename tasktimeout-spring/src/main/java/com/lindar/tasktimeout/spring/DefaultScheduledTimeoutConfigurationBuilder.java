package com.lindar.tasktimeout.spring;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ScheduledExecutorService;

import static com.lindar.tasktimeout.spring.SpringTimeoutConfigurationExtractor.DEFAULT_TIMEOUT;

public class DefaultScheduledTimeoutConfigurationBuilder implements ScheduledTimeoutConfigurationBuilder, ScheduledTimeoutConfigurationBuilder.ScheduledTimeoutConfigurationBuilderWithoutTaskScheduler, ScheduledTimeoutConfigurationBuilder.ConfiguredScheduledTimeoutConfigurationBuilder, ScheduledTimeoutConfigurationBuilder.ScheduledTimeoutConfigurationBuilderWithoutDefaultTimeout {

    private TaskScheduler taskScheduler;

    private long defaultTimeout = DEFAULT_TIMEOUT;

    @Override
    public ScheduledTimeoutConfigurationBuilderWithoutDefaultTimeout withPoolSize(int poolSize) {
        this.taskScheduler = createThreadPoolTaskScheduler(poolSize);
        return this;
    }

    @Override
    public ScheduledTimeoutConfigurationBuilderWithoutDefaultTimeout withExecutorService(ScheduledExecutorService scheduledExecutorService) {
        this.taskScheduler = new ConcurrentTaskScheduler(scheduledExecutorService);
        return this;
    }

    @Override
    public ScheduledTimeoutConfigurationBuilderWithoutDefaultTimeout withTaskScheduler(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
        return this;
    }


    private static ThreadPoolTaskScheduler createThreadPoolTaskScheduler(int poolSize) {
        ThreadPoolTaskScheduler newTaskScheduler = new ThreadPoolTaskScheduler();
        newTaskScheduler.setPoolSize(poolSize);
        newTaskScheduler.initialize();
        return newTaskScheduler;
    }

    @Override
    public ConfiguredScheduledTimeoutConfigurationBuilder withDefaultTimeout(long defaultTimeoutAtMostFor) {
        this.defaultTimeout = defaultTimeoutAtMostFor;
        return this;
    }


    @Override
    public SpringTimeoutTaskSchedulerFactoryBean build() {
        return new SpringTimeoutTaskSchedulerFactoryBean(taskScheduler, defaultTimeout);
    }
}