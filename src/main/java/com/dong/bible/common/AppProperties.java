package com.dong.bible.common;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Properties for app
 */
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "app")
@Validated
@Getter
public class AppProperties {
    @NotNull
    private final Option option;

    @Getter
    @Accessors(fluent = true)
    @AllArgsConstructor
    public static final class Option{
        private boolean meta;
        private boolean trace;
        private boolean uuid;
        private boolean exception;
        private boolean errors;
        private boolean report;
    }

}
