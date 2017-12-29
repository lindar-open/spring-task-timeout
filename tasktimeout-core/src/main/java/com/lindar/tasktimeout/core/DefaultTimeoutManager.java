package com.lindar.tasktimeout.core;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.Objects.requireNonNull;

@Slf4j
public class DefaultTimeoutManager implements TimeoutManager {

    private final TimeoutConfigurationExtractor timeoutConfigurationExtractor;

    public DefaultTimeoutManager(TimeoutConfigurationExtractor timeoutConfigurationExtractor) {
        this.timeoutConfigurationExtractor = requireNonNull(timeoutConfigurationExtractor);
    }

    @Override
    public void executeWithTimeout(Runnable task) {
        Optional<TimeoutConfiguration> timeoutConfigurationOptional = timeoutConfigurationExtractor.getTimeoutConfiguration(task);
        if (!timeoutConfigurationOptional.isPresent()) {
            log.debug("No lock configuration for {}. Executing without lock.", task);
            task.run();
        } else {
            TimeoutConfiguration timeoutConfiguration = timeoutConfigurationOptional.get();
            try {
                log.debug("Schedule {} running with delay of {} mills.", timeoutConfiguration.getName(), timeoutConfiguration.getTimeout());
                Instant startTime = Instant.now();
                CompletableFuture.runAsync(task).get(timeoutConfiguration.getTimeout(), TimeUnit.MILLISECONDS);
                long durationMillis = Instant.now().toEpochMilli() - startTime.toEpochMilli();
                log.debug("Schedule {} finished in {} mills.", timeoutConfiguration.getName(), durationMillis);
            } catch (InterruptedException e) {
                log.debug("Interrupted schedule {}.", timeoutConfiguration.getName());
            } catch (ExecutionException e) {
                log.debug("Exception in schedule {}.", timeoutConfiguration.getName());
            } catch (TimeoutException e) {
                log.debug("Timeout exceeded {}.", timeoutConfiguration.getName());
            }

        }
    }
}
