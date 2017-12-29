package com.lindar.tasktimeout.spring;

import com.lindar.tasktimeout.core.DefaultTimeoutManager;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ScheduledExecutorService;

public class SpringTimeoutTaskSchedulerFactory {

    public static TimeoutTaskScheduler newTimeoutTaskScheduler(TaskScheduler taskScheduler) {
        return new TimeoutTaskScheduler(taskScheduler, new DefaultTimeoutManager(new SpringTimeoutConfigurationExtractor()));
    }

    public static TimeoutTaskScheduler newTimeoutTaskScheduler(ScheduledExecutorService scheduledExecutorService) {
        return newTimeoutTaskScheduler(new ConcurrentTaskScheduler(scheduledExecutorService));
    }

    public static TimeoutTaskScheduler newTimeoutTaskScheduler(int poolSize) {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(poolSize);
        taskScheduler.initialize();
        return newTimeoutTaskScheduler(taskScheduler);
    }
}
