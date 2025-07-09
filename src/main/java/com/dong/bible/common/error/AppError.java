package com.dong.bible.common.error;

import lombok.Builder;
import lombok.Getter;

/**
 * Application error fields
 */
@Getter
@Builder
public class AppError {
    private String domain;
    private String field;
    private Object value;
    private String message;
}
