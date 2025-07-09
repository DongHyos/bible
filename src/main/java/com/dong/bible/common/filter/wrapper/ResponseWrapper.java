package com.dong.bible.common.filter.wrapper;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.util.ContentCachingResponseWrapper;



/**
 * 전문 거래로그용 Response Wrapper
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
public class ResponseWrapper extends ContentCachingResponseWrapper {
    public ResponseWrapper(HttpServletResponse response) {
        super(response);
    }
}