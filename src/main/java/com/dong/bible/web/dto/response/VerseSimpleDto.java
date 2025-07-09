package com.dong.bible.web.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * 간소화된 구절 응답 DTO (Web Layer)
 * Chapter 응답에서 사용
 * 
 * DDD 학습 원칙:
 * - Builder 패턴 사용 (불변 객체)
 * - setter 제거 (가변성 방지)
 */
@Getter
@Builder
public class VerseSimpleDto {
    private final Integer id;        // 구절의 고유 ID (PK)
    private final Integer verse;     // 절 번호 (1, 2, 3...)
    private final String text;       // 구절 내용
}