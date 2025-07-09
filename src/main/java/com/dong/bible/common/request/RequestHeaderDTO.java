package com.dong.bible.common.request;

import lombok.*;

/**
 * Common request header dto
 *
 * @author Won Gilho
 * @since Version 1.0
 * <pre>
 * ===================== Change history ======================
 * DATE          AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2023-08-29    Won Gilho     최초 생성
 * </pre>
 */
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RequestHeaderDTO {
    /** 요청 IP */
    private String requestIp;

    /** url **/
    private String requestURI;

    /** Request timestamp **/
    private String timestamp;
}
