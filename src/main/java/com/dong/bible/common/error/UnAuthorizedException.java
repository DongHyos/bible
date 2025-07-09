package com.dong.bible.common.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UnAuthorizedException extends ResponseStatusException {
    public UnAuthorizedException(String reason) {
        super(HttpStatus.UNAUTHORIZED, reason);
    }
}
