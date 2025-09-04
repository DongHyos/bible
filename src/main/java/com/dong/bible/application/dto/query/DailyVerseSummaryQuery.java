package com.dong.bible.application.dto.query;

import com.dong.bible.domain.dailyverse.DailyVerse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

/**
 * 오늘의 말씀 목록 조회용 Application DTO
 * Use Case: 오늘의 말씀 목록 API (/api/daily-verse/recent, /api/daily-verse/upcoming 등)
 * - description 제외하여 네트워크 비용 최적화
 */
@Getter
@Builder
public class DailyVerseSummaryQuery {
    private final Long id;
    private final LocalDate verseDate;
    private final String bookName;
    private final Integer chapter;
    private final Integer verseStart;
    private final Integer verseEnd;
    private final String title;
    private final boolean isActive;
    
    public static DailyVerseSummaryQuery from(DailyVerse domain) {
        return DailyVerseSummaryQuery.builder()
                .id(domain.getId())
                .verseDate(domain.getDate().getDate())
                .bookName(domain.getBookName())
                .chapter(domain.getChapter())
                .verseStart(domain.getVerseRange().getStartVerse())
                .verseEnd(domain.getVerseRange().getEndVerse())
                .title(domain.getMessage().getTitleOrEmpty())
                .isActive(domain.isActive())
                .build();
    }
}
