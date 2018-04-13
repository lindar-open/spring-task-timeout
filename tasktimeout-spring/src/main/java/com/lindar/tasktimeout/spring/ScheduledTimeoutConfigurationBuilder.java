package com.lindar.tasktimeout.spring;

import org.springframework.scheduling.TaskScheduler;

import java.util.concurrent.ScheduledExecutorService;

public interface ScheduledTimeoutConfigurationBuilder {

    static ScheduledTimeoutConfigurationBuilderWithoutTaskScheduler builder(){
        return new DefaultScheduledTimeoutConfigurationBuilder();
    }

    interface ScheduledTimeoutConfigurationBuilderWithoutTaskScheduler {
        ScheduledTimeoutConfigurationBuilderWithoutDefaultTimeout withPoolSize(int poolSize);
        ScheduledTimeoutConfigurationBuilderWithoutDefaultTimeout withExecutorService(ScheduledExecutorService scheduledExecutorService);
        ScheduledTimeoutConfigurationBuilderWithoutDefaultTimeout withTaskScheduler(TaskScheduler taskScheduler);
    }

    interface ScheduledTimeoutConfigurationBuilderWithoutDefaultTimeout {
        ConfiguredScheduledTimeoutConfigurationBuilder withDefaultTimeout(long defaultTimeout);
    }

    interface ConfiguredScheduledTimeoutConfigurationBuilder {
        SpringTimeoutTaskSchedulerFactoryBean build();
    }
}
