package com.dong.bible.web.mapper;

import com.dong.bible.application.dto.DailyVerseDetailDto;
import com.dong.bible.application.dto.DailyVerseSummaryDto;
import com.dong.bible.web.dto.response.DailyVerseDto;
import com.dong.bible.web.dto.response.DailyVerseSimpleDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * DailyVerse Application DTO → Response DTO 매핑
 * Web Layer 전용 매핑 클래스
 */
@Component
@Slf4j
public class DailyVerseResponseMapper {
    
    // ========================================
    // Application DTO → Web DTO 변환
    // ========================================
    
    /**
     * DailyVerseDetailDto → DailyVerseDto 변환
     */
    public DailyVerseDto fromDetailDto(DailyVerseDetailDto detailDto) {
        if (detailDto == null) {
            return null;
        }
        
        return DailyVerseDto.builder()
                .id(detailDto.getId())
                .verseDate(detailDto.getVerseDate())
                .bookName(detailDto.getBookName())
                .chapter(detailDto.getChapter())
                .verseStart(detailDto.getVerseStart())    // shortValue() 제거
                .verseEnd(detailDto.getVerseEnd())        // shortValue() 제거
                .title(detailDto.getTitle())
                .description(detailDto.getDescription())
                .isActive(detailDto.isActive())
                // TODO: bookId, bookAbbr, verseText는 필요시 추가 구현
                .build();
    }
    
    /**
     * DailyVerseSummaryDto → DailyVerseSimpleDto 변환
     */
    public DailyVerseSimpleDto fromSummaryDto(DailyVerseSummaryDto summaryDto) {
        if (summaryDto == null) {
            return null;
        }
        
        return DailyVerseSimpleDto.builder()
                .id(summaryDto.getId())
                .verseDate(summaryDto.getVerseDate())
                .bookName(summaryDto.getBookName())
                .chapter(summaryDto.getChapter())
                .verseStart(summaryDto.getVerseStart())  // shortValue() 제거
                .verseEnd(summaryDto.getVerseEnd())      // shortValue() 제거
                .title(summaryDto.getTitle())
                .isActive(summaryDto.isActive())
                // TODO: bookId, bookAbbr는 필요시 추가 구현
                .build();
    }
    
    /**
     * List<DailyVerseSummaryDto> → List<DailyVerseSimpleDto> 변환
     */
    public List<DailyVerseSimpleDto> fromSummaryDtoList(List<DailyVerseSummaryDto> summaryDtos) {
        if (summaryDtos == null) {
            return Collections.emptyList();
        }
        
        return summaryDtos.stream()
                .map(this::fromSummaryDto)
                .filter(dto -> dto != null)
                .toList();
    }
    
    // === 헬퍼 메서드들 ===
    
    /**
     * 구절 참조 문자열 생성 (유틸리티 메서드)
     * "창세기 1:1-3" 또는 "창세기 1:1" 형태
     */
    public String buildVerseReference(DailyVerseSummaryDto summaryDto) {
        if (summaryDto == null || summaryDto.getBookName() == null) {
            return "";
        }
        
        String bookName = summaryDto.getBookName();
        Integer chapter = summaryDto.getChapter();
        Integer verseStart = summaryDto.getVerseStart();
        Integer verseEnd = summaryDto.getVerseEnd();
        
        if (verseStart.equals(verseEnd)) {
            // 단일 절: "창세기 1:1"
            return String.format("%s %d:%d", bookName, chapter, verseStart);
        } else {
            // 범위: "창세기 1:1-3"
            return String.format("%s %d:%d-%d", bookName, chapter, verseStart, verseEnd);
        }
    }
    
    /**
     * 구절 참조 문자열 생성 (상세용)
     */
    public String buildVerseReference(DailyVerseDetailDto detailDto) {
        if (detailDto == null || detailDto.getBookName() == null) {
            return "";
        }
        
        String bookName = detailDto.getBookName();
        Integer chapter = detailDto.getChapter();
        Integer verseStart = detailDto.getVerseStart();
        Integer verseEnd = detailDto.getVerseEnd();
        
        if (verseStart.equals(verseEnd)) {
            // 단일 절: "창세기 1:1"
            return String.format("%s %d:%d", bookName, chapter, verseStart);
        } else {
            // 범위: "창세기 1:1-3"
            return String.format("%s %d:%d-%d", bookName, chapter, verseStart, verseEnd);
        }
    }
}
