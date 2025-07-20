package com.dong.bible.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 성경 구절 검색 결과 Response DTO
 * Web Layer - API 응답용
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerseSearchResponse {
    
    private String id;
    private Integer bookId;
    private String bookName;
    private Integer chapter;
    private Integer verse;
    private String content;
    private String displayReference; // "창세기 1:1"
}