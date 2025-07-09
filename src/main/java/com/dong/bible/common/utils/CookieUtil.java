package com.dong.bible.common.utils;

import org.springframework.http.ResponseCookie;

import java.time.Duration;

public class CookieUtil {
    public static ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false) // 개발환경에서는 false
                .sameSite("Lax") // 개발환경에서는 Lax
                .path("/")
                .maxAge(Duration.ofDays(7))
                .build();
    }
}
