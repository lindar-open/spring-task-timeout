package com.lindar.tasktimeout.core;

import java.util.Optional;

public interface TimeoutConfigurationExtractor {
    Optional<TimeoutConfiguration> getTimeoutConfiguration(Runnable task);
}
