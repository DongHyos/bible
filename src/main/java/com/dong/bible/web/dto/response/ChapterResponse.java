package com.dong.bible.web.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 장 응답 DTO (Web Layer)
 * 
 * DDD 학습 원칙:
 * - Builder 패턴 사용 (불변 객체)
 * - setter 제거 (가변성 방지)
 */
@Getter
@Builder
public class ChapterResponse {
    private final Integer bookId;
    private final String bookName;
    private final String bookAbbr;
    private final Integer chapter;
    private final Integer totalVerses;
    private final List<VerseSimpleResponse> verses;
}
