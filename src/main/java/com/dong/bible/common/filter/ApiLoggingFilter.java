package com.dong.bible.common.filter;

import com.dong.bible.common.AppConstants;
import com.dong.bible.common.AppProperties;
import com.dong.bible.common.filter.wrapper.RequestWrapper;
import com.dong.bible.common.filter.wrapper.ResponseWrapper;
import com.dong.bible.common.request.RequestHeaderDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 전문 거래로그 Filter
 *
 * @author Won Gilho
 * @since Version 1.0
 * <pre>
 * ===================== Change history ======================
 * DATE          AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2023-08-04    Won Gilho     최초 생성
 * </pre>
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ApiLoggingFilter extends OncePerRequestFilter {
    final AppProperties properties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getContentType() != null && request.getContentType().startsWith("multipart/form-data")) {
            // multipart는 절대 wrapper 씌우지 말고 그대로 넘김
            filterChain.doFilter(request, response);
        } else {
            doFilterWrapped(new RequestWrapper(request), new ResponseWrapper(response), filterChain);
        }
    }

    protected void doFilterWrapped(RequestWrapper request, ContentCachingResponseWrapper response, FilterChain filterChain) throws ServletException, IOException {
        String req, res;
        LocalDateTime requestDateTime = LocalDateTime.now();
        RequestHeaderDTO header;
        try {
            req = logRequest(request);

            // header에 tuid 설정(log 처리 용)
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode reqJsonNode = objectMapper.readTree(req);
            header = objectMapper.treeToValue(reqJsonNode.get(AppConstants.TR_COMMON_HEADER), RequestHeaderDTO.class);

            JsonNode tuid = reqJsonNode.get(AppConstants.TR_COMMON_TUID);
            filterChain.doFilter(request, response);
        } finally {
            res = logResponse(response);
            response.copyBodyToResponse();
        }
    }



    private String logRequest(RequestWrapper request) throws IOException {
        String queryString = request.getQueryString();
        log.info("Request : {} uri=[{}] content-type=[{}]",
                request.getMethod(),
                queryString == null ? request.getRequestURI() : request.getRequestURI() + "?" + queryString,
                request.getContentType()
        );

        return logPayload("▶▶▶▶▶▶▶▶▶▶ Request", request.getContentType(), request.getInputStream());
    }

    private String logResponse(ContentCachingResponseWrapper response) throws IOException {
        return logPayload("◀◀◀◀◀◀◀◀◀◀ Response", response.getContentType(), response.getContentInputStream());
    }

    private String logPayload(String prefix, String contentType, InputStream inputStream) throws IOException {
        if (contentType != null && contentType.startsWith("multipart/form-data")) {
            log.info("{} Payload: Multipart Form Data", prefix);
            return ""; // 본문은 읽지 않음
        }

        ObjectMapper objectMapper = new ObjectMapper();
        boolean visible = isVisible(MediaType.valueOf(contentType == null ? "application/json" : contentType));
        if (visible) {
            byte[] content = StreamUtils.copyToByteArray(inputStream);
            if (content.length > 0) {
                String contentString = new String(content);
                try {
                    Object json = objectMapper.readValue(contentString, Object.class);
                    String logString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
                    log.info("{} Payload: \n{}", prefix, logString);
                } catch (JsonProcessingException e) {
                    log.info("{} Payload: Non-JSON Content", prefix);
                }
                return contentString;
            }
        } else {
            log.info("{} Payload: Binary Content", prefix);
        }
        return "";
    }

    private boolean isVisible(MediaType mediaType) {
        final List<MediaType> VISIBLE_TYPES = Arrays.asList(
                MediaType.valueOf("text/*"),
                MediaType.APPLICATION_FORM_URLENCODED,
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_XML,
                MediaType.valueOf("application/*+json"),
                MediaType.valueOf("application/*+xml"),
                MediaType.MULTIPART_FORM_DATA
        );

        return VISIBLE_TYPES.stream()
                .anyMatch(visibleType -> visibleType.includes(mediaType));
    }
}