package com.lindar.tasktimeout.spring;

import com.lindar.tasktimeout.core.TimeoutManager;
import com.lindar.tasktimeout.core.TimeoutRunnable;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

import static java.util.Objects.requireNonNull;


public class TimeoutTaskScheduler implements TaskScheduler, DisposableBean {

    private final TaskScheduler taskScheduler;
    private final TimeoutManager timeoutManager;

    public TimeoutTaskScheduler(TaskScheduler taskScheduler, TimeoutManager timeoutManager) {
        this.taskScheduler = requireNonNull(taskScheduler);
        this.timeoutManager = timeoutManager;
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable task, Trigger trigger) {
        return taskScheduler.schedule(wrap(task), trigger);
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable task, Date startTime) {
        return taskScheduler.schedule(wrap(task), startTime);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Date startTime, long period) {
        return taskScheduler.scheduleAtFixedRate(wrap(task), startTime, period);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long period) {
        return taskScheduler.scheduleAtFixedRate(wrap(task), period);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Date startTime, long delay) {
        return taskScheduler.scheduleWithFixedDelay(wrap(task), startTime, delay);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long delay) {
        return taskScheduler.scheduleWithFixedDelay(wrap(task), delay);
    }

    private Runnable wrap(Runnable task) {
        return new TimeoutRunnable(task, timeoutManager);
    }

    @Override
    public void destroy() throws Exception {
        if (taskScheduler instanceof DisposableBean) {
            ((DisposableBean) taskScheduler).destroy();
        }
    }
}
