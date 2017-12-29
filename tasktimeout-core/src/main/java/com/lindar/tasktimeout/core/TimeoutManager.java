package com.lindar.tasktimeout.core;

public interface TimeoutManager {
    void executeWithTimeout(Runnable task);
}
