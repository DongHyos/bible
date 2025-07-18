package com.dong.bible.application.dto;

import com.dong.bible.domain.dailyverse.DailyVerse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

/**
 * 오늘의 말씀 상세 조회용 Application DTO
 * Use Case: 오늘의 말씀 상세 API (/api/daily-verse/today, /api/daily-verse/date/{date})
 */
@Getter
@Builder
public class DailyVerseDetailDto {
    private final Long id;
    private final LocalDate verseDate;
    private final String bookName;
    private final Integer chapter;
    private final Integer verseStart;
    private final Integer verseEnd;
    private final String title;
    private final String description;
    private final boolean isActive;

    public static DailyVerseDetailDto from(DailyVerse domain) {
        return DailyVerseDetailDto.builder()
                .id(domain.getId())
                .verseDate(domain.getDate().getDate())
                .bookName(domain.getBookName())
                .chapter(domain.getChapter())
                .verseStart(domain.getVerseRange().getStartVerse())
                .verseEnd(domain.getVerseRange().getEndVerse())
                .title(domain.getMessage().getTitleOrEmpty())
                .description(domain.getMessage().getDescriptionOrEmpty())
                .isActive(domain.isActive())
                .build();
    }
}
