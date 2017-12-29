package com.lindar.tasktimeout.core;

public class TimeoutRunnable implements Runnable {

    private final Runnable task;
    private final TimeoutManager timeoutManager;

    public TimeoutRunnable(Runnable task, TimeoutManager timeoutManager) {
        this.task = task;
        this.timeoutManager = timeoutManager;
    }

    @Override
    public void run() {
        timeoutManager.executeWithTimeout(task);
    }

}
