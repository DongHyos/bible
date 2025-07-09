package com.dong.bible.common.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Base Request DTO
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
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BaseRequestDTO {
    /** 거래고유번호 **/
    @NotEmpty
    @Size(max=22)
    private String tuid;

    /** 플랫폼요청번호: API G/W 추적번호 **/
    @Builder.Default
    private Long plfmDmndNo = 0L;

    /** header **/
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private RequestHeaderDTO header;
}
