package com.lindar.tasktimeout.core;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
@EqualsAndHashCode(of = "name")
public class TimeoutConfiguration {
    private final String  name;
    private final long timeout;
}
