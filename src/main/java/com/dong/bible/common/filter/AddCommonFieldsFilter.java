package com.dong.bible.common.filter;

import com.dong.bible.common.AppConstants;
import com.dong.bible.common.AppProperties;
import com.dong.bible.common.request.RequestHeaderDTO;
import com.dong.bible.common.utils.AppUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * 전문 공통 필드 추가 filter
 *
 * @author Won Gilho
 * @since Version 1.0
 * <pre>
 * ===================== Change history ======================
 * DATE          AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2023-08-02    Won Gilho     최초 생성
 * </pre>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AddCommonFieldsFilter implements jakarta.servlet.Filter {

    private final AppProperties properties;

    private boolean isAllowed(String requestUri){
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        return false;
    }


    @Override
    public void doFilter(jakarta.servlet.ServletRequest request, jakarta.servlet.ServletResponse response, jakarta.servlet.FilterChain chain) throws IOException, jakarta.servlet.ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 이미 세팅된 경우 중복 방지
        if (httpRequest.getAttribute(AppConstants.TR_COMMON_HEADER) == null) {
            LocalDateTime requestDateTime = LocalDateTime.now();
            String timestamp = requestDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));

            RequestHeaderDTO header = RequestHeaderDTO.builder()
                    .requestIp(AppUtils.getClientIpAddr(httpRequest))
                    .timestamp(timestamp)
                    .requestURI(httpRequest.getRequestURI())
                    .build();

            // ✅ 어트리뷰트로 등록하여 어디서든 접근 가능하게
            httpRequest.setAttribute(AppConstants.TR_COMMON_HEADER, header);
        }

        chain.doFilter(request, response);
    }
}
