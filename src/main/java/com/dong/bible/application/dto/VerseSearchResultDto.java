package com.dong.bible.application.dto;

import com.dong.bible.infrastructure.search.document.VerseSearchDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 성경 구절 검색 결과 DTO
 * Application Layer - 검색 결과 전달용
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerseSearchResultDto {
    
    private String id;
    private Integer bookId;
    private String bookName;
    private Integer chapter;
    private Integer verse;
    private String content;
    private String displayReference; // "창세기 1:1"
    
    /**
     * 검색 Document에서 DTO로 변환
     */
    public static VerseSearchResultDto from(VerseSearchDocument document) {
        return VerseSearchResultDto.builder()
                .id(document.getId())
                .bookId(document.getBookId())
                .bookName(document.getBookName())
                .chapter(document.getChapter())
                .verse(document.getVerse())
                .content(document.getContent())
                .displayReference(document.getDisplayReference())
                .build();
    }
}