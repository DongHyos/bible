package com.dong.bible.web.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * 구절 검색 결과 DTO (Web Layer)
 * 
 * DDD 학습 원칙:
 * - Builder 패턴 사용 (불변 객체)
 * - setter 제거 (가변성 방지)
 */
@Getter
@Builder
public class VerseSearchResultDto {
    private final Integer id;
    private final String bookName;
    private final Integer chapter;
    private final Integer verse;
    private final String text;
    private final String reference; // "창세기 1:1" 형태
}
