package com.dong.bible.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Enhanced 성경 구절 검색 결과 Response DTO
 * Web Layer - 하이라이팅과 스코어링이 포함된 API 응답용
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class EnhancedVerseSearchResponse {
    
    // ========================================
    // 기본 구절 정보 (기존 Response와 동일)
    // ========================================
    
    private String id;
    private Integer bookId;
    private String bookName;
    private Integer chapter;
    private Integer verse;
    private String content; // 원본 텍스트
    private String displayReference; // "창세기 1:1"
    
    // ========================================
    // Enhanced 검색 정보
    // ========================================
    
    /**
     * 하이라이팅된 텍스트 (HTML 태그 포함)
     * 예: "태초에 <mark>하나님</mark>이 천지를 창조하시니라"
     */
    private String highlightedContent;
    
    /**
     * 검색 관련도 점수 (0.0 ~ 1.0+)
     */
    private Double score;
    
    /**
     * 관련도 등급 ("HIGH", "MEDIUM", "LOW", "NONE")
     */
    private String relevanceLevel;
    
    /**
     * 하이라이팅 개수
     */
    private Integer highlightCount;
    
    /**
     * 하이라이팅 존재 여부
     */
    private Boolean hasHighlight;
    
    /**
     * 검색 키워드
     */
    private String searchKeyword;
    
    // ========================================
    // 검색 품질 지표
    // ========================================
    
    /**
     * 고품질 검색 결과 여부
     * (높은 관련도 + 명확한 하이라이팅)
     */
    private Boolean isHighQuality;
    
    /**
     * 완벽한 매칭 여부
     * (최고 점수 + 여러 하이라이팅)
     */
    private Boolean isPerfectMatch;
    
    /**
     * 부분 매칭 여부
     * (중간 이상 관련도)
     */
    private Boolean isPartialMatch;
    
    /**
     * 하이라이팅 품질 점수 (0.0 ~ 1.0)
     */
    private Double highlightQuality;
    
    // ========================================
    // 응답 메타데이터
    // ========================================
    
    /**
     * 검색 처리 시간 (밀리초)
     */
    private Long processingTimeMs;
    
    /**
     * 검색 타입 ("content", "bookName", "advanced")
     */
    private String searchType;
    
    /**
     * 사용된 검색 옵션들
     */
    private SearchOptions searchOptions;
    
    /**
     * 기본 Response로 변환 (역호환성)
     */
    public VerseSearchResponse toBasicResponse() {
        return VerseSearchResponse.builder()
                .id(this.id)
                .bookId(this.bookId)
                .bookName(this.bookName)
                .chapter(this.chapter)
                .verse(this.verse)
                .content(this.content)
                .displayReference(this.displayReference)
                .build();
    }
    
    /**
     * 검색 요약 정보
     */
    public SearchSummary getSummary() {
        return SearchSummary.builder()
                .reference(this.displayReference)
                .score(this.score)
                .relevanceLevel(this.relevanceLevel)
                .highlightCount(this.highlightCount)
                .isHighQuality(this.isHighQuality)
                .build();
    }
    
    // ========================================
    // 내부 클래스들
    // ========================================
    
    /**
     * 검색 옵션 정보
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchOptions {
        private Boolean useSynonyms; // 동의어 사용 여부
        private String sortBy; // 정렬 기준
        private Boolean includeScore; // 점수 포함 여부
        private Integer fragmentSize; // 하이라이트 조각 크기
        private Integer maxFragments; // 최대 조각 수
    }
    
    /**
     * 검색 결과 요약
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchSummary {
        private String reference;
        private Double score;
        private String relevanceLevel;
        private Integer highlightCount;
        private Boolean isHighQuality;
    }
}