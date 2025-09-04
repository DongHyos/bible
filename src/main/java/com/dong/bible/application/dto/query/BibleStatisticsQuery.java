package com.dong.bible.application.dto.query;

import lombok.Builder;
import lombok.Getter;

/**
 * 성경 통계 정보 Application DTO
 * Use Case: 성경 통계 대시보드 표시
 */
@Getter
@Builder
public class BibleStatisticsQuery {
    private final Integer totalBooks;
    private final Integer oldTestamentBooks;
    private final Integer newTestamentBooks;
    private final Integer totalChapters;
}
