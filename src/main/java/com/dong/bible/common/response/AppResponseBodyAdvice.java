package com.dong.bible.common.response;

import com.dong.bible.common.AppConstants;
import com.dong.bible.common.request.RequestHeaderDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * AppResponse advice
 *
 * @author Won Gilho
 * @since Version 1.0
 * <pre>
 * ===================== Change history ======================
 * DATE          AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2023-08-09    Won Gilho     최초 생성
 * </pre>
 */
@Slf4j
@RestControllerAdvice
public class AppResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> converter) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> converter, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        HttpServletRequest httpRequest = ((ServletServerHttpRequest) serverHttpRequest).getServletRequest();

        // ✅ null 안전 처리
        RequestHeaderDTO header = (RequestHeaderDTO) httpRequest.getAttribute(AppConstants.TR_COMMON_HEADER);
        String timestamp = (header != null) ? header.getTimestamp() : LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));

        if (body instanceof AppResponse appResponse) {
            appResponse.setTimestamp(timestamp);
        }

        return body;
    }
}
