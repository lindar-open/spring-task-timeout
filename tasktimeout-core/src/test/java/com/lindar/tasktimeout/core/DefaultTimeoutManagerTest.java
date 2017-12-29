package com.lindar.tasktimeout.core;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static javax.management.timer.Timer.ONE_SECOND;
import static org.mockito.Mockito.*;

public class DefaultTimeoutManagerTest {

    public static final TimeoutConfiguration TIMEOUT_CONFIGURATION = new TimeoutConfiguration("name", ONE_SECOND);
    private final TimeoutConfigurationExtractor lockConfigurationExtractor = mock(TimeoutConfigurationExtractor.class);
    private final Runnable task = mock(Runnable.class);

    private final DelayedRunnable delayedTask = new DelayedRunnable();


    private final DefaultTimeoutManager defaultTimeoutManager = new DefaultTimeoutManager(lockConfigurationExtractor);


    @Test
    public void noConfigNoTimeout() {
        when(lockConfigurationExtractor.getTimeoutConfiguration(task)).thenReturn(Optional.empty());

        defaultTimeoutManager.executeWithTimeout(task);
        verify(task).run();
    }

    @Test
    public void executeWithTimeoutConfiguration() {
        when(lockConfigurationExtractor.getTimeoutConfiguration(task)).thenReturn(Optional.of(TIMEOUT_CONFIGURATION));

        defaultTimeoutManager.executeWithTimeout(task);
        verify(task).run();
    }

    @Test
    public void executeWithTimeout() {
        when(lockConfigurationExtractor.getTimeoutConfiguration(delayedTask)).thenReturn(Optional.of(TIMEOUT_CONFIGURATION));

        defaultTimeoutManager.executeWithTimeout(delayedTask);
    }

    @Test
    public void executeWithException() {
        when(lockConfigurationExtractor.getTimeoutConfiguration(task)).thenReturn(Optional.of(TIMEOUT_CONFIGURATION));
        Mockito.doThrow(new RuntimeException()).when(task).run();

        defaultTimeoutManager.executeWithTimeout(task);
    }


    public class DelayedRunnable implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}