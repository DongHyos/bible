package com.dong.bible.web.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 구절 응답 DTO (Web Layer)
 * 
 * DDD 학습 원칙:
 * - Builder 패턴 사용 (불변 객체)
 * - setter 제거 (가변성 방지)
 */
@Getter
@Builder
public class VerseResponse {
    private final Integer id;
    private final Integer bookId;
    private final String bookName;
    private final String bookAbbr;
    private final Integer chapter;
    private final Integer verse;
    private final String text;
    private final LocalDateTime createdAt;
}
