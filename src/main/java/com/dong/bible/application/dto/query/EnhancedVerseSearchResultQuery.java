package com.dong.bible.application.dto.query;

import com.dong.bible.domain.verse.EnhancedSearchResult;
import com.dong.bible.domain.verse.SearchScore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 향상된 성경 구절 검색 결과 DTO
 * Application Layer - Enhanced 검색 결과 전달용
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnhancedVerseSearchResultQuery {
    
    // 기본 구절 정보
    private String id;
    private Integer bookId;
    private String bookName;
    private Integer chapter;
    private Integer verse;
    private String content;
    private String displayReference; // "창세기 1:1"
    
    // Enhanced 정보
    private String highlightedContent; // 하이라이팅된 텍스트
    private Double score; // 검색 관련도 점수
    private String relevanceLevel; // "HIGH", "MEDIUM", "LOW", "NONE"
    private Integer highlightCount; // 하이라이팅 개수
    private Boolean hasHighlight; // 하이라이팅 존재 여부
    private String searchKeyword; // 검색 키워드
    
    // 검색 품질 지표
    private Boolean isHighQuality; // 고품질 검색 결과 여부
    private Boolean isPerfectMatch; // 완벽한 매칭 여부
    private Boolean isPartialMatch; // 부분 매칭 여부
    private Double highlightQuality; // 하이라이팅 품질 점수
    
    /**
     * Enhanced 검색 결과 Domain 객체에서 DTO로 변환
     */
    public static EnhancedVerseSearchResultQuery from(EnhancedSearchResult enhancedResult) {
        var verse = enhancedResult.getVerse();
        var score = enhancedResult.getScore();
        var highlighted = enhancedResult.getHighlightedContent();
        
        return EnhancedVerseSearchResultQuery.builder()
                // 기본 구절 정보
                .id(createId(verse.getReference()))
                .bookId(extractBookId(verse)) // 이 부분은 별도 매핑 로직 필요
                .bookName(verse.getReference().getBookName())
                .chapter(verse.getReference().getChapter())
                .verse(verse.getReference().getVerse())
                .content(verse.getContent().getText())
                .displayReference(verse.getReference().toString())
                
                // Enhanced 정보
                .highlightedContent(highlighted.getHighlightedText())
                .score(score.getValue())
                .relevanceLevel(score.getRelevanceLevel().name())
                .highlightCount(highlighted.getHighlightCount())
                .hasHighlight(highlighted.hasHighlight())
                .searchKeyword(enhancedResult.getSearchKeyword())
                
                // 검색 품질 지표
                .isHighQuality(enhancedResult.isHighQualityResult())
                .isPerfectMatch(enhancedResult.isPerfectMatch())
                .isPartialMatch(enhancedResult.isPartialMatch())
                .highlightQuality(highlighted.getHighlightQuality())
                .build();
    }
    
    /**
     * 기본 검색 결과 DTO에서 Enhanced DTO로 변환 (하이라이팅 없음)
     */
    public static EnhancedVerseSearchResultQuery fromBasic(VerseSearchResultQuery basicResult) {
        return EnhancedVerseSearchResultQuery.builder()
                // 기본 정보 복사
                .id(basicResult.getId())
                .bookId(basicResult.getBookId())
                .bookName(basicResult.getBookName())
                .chapter(basicResult.getChapter())
                .verse(basicResult.getVerse())
                .content(basicResult.getContent())
                .displayReference(basicResult.getDisplayReference())
                
                // Enhanced 정보 기본값
                .highlightedContent(basicResult.getContent()) // 하이라이팅 없음
                .score(0.0)
                .relevanceLevel(SearchScore.RelevanceLevel.NONE.name())
                .highlightCount(0)
                .hasHighlight(false)
                .searchKeyword("")
                
                // 검색 품질 지표 기본값
                .isHighQuality(false)
                .isPerfectMatch(false)
                .isPartialMatch(false)
                .highlightQuality(0.0)
                .build();
    }
    
    /**
     * ElasticSearch 검색 결과로부터 직접 생성
     */
    public static EnhancedVerseSearchResultQuery fromElasticSearchResult(
            String id, Integer bookId, String bookName, Integer chapter, Integer verse,
            String originalContent, String displayReference,
            String[] highlightFragments, Double elasticScore, String searchKeyword) {
        
        // 하이라이팅된 텍스트 결합
        String highlightedText = (highlightFragments != null && highlightFragments.length > 0) 
            ? String.join(" ... ", highlightFragments)
            : originalContent;
        
        // 하이라이팅 개수 계산
        int highlightCount = countHighlights(highlightedText);
        boolean hasHighlight = highlightCount > 0;
        
        // 관련도 등급 계산
        SearchScore score = SearchScore.of(elasticScore != null ? elasticScore : 0.0);
        
        return EnhancedVerseSearchResultQuery.builder()
                // 기본 구절 정보
                .id(id)
                .bookId(bookId)
                .bookName(bookName)
                .chapter(chapter)
                .verse(verse)
                .content(originalContent)
                .displayReference(displayReference)
                
                // Enhanced 정보
                .highlightedContent(highlightedText)
                .score(score.getValue())
                .relevanceLevel(score.getRelevanceLevel().name())
                .highlightCount(highlightCount)
                .hasHighlight(hasHighlight)
                .searchKeyword(searchKeyword != null ? searchKeyword : "")
                
                // 검색 품질 지표
                .isHighQuality(score.isHighRelevance() && hasHighlight)
                .isPerfectMatch(score.getValue() >= 0.9 && highlightCount > 1)
                .isPartialMatch(score.isMediumRelevance() || score.isHighRelevance())
                .highlightQuality(calculateHighlightQuality(originalContent, highlightCount))
                .build();
    }
    
    /**
     * 기본 검색 결과 DTO로 변환 (역방향 호환성)
     */
    public VerseSearchResultQuery toBasicDto() {
        return VerseSearchResultQuery.builder()
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
     * 검색 결과 요약 정보
     */
    public SearchResultSummaryDto getSummary() {
        return SearchResultSummaryDto.builder()
                .reference(this.displayReference)
                .score(this.score)
                .relevanceLevel(this.relevanceLevel)
                .highlightCount(this.highlightCount)
                .hasHighlight(this.hasHighlight)
                .isHighQuality(this.isHighQuality)
                .searchKeyword(this.searchKeyword)
                .build();
    }
    
    // ========================================
    // Private 헬퍼 메서드들
    // ========================================
    
    /**
     * VerseReference로부터 ID 생성
     */
    private static String createId(com.dong.bible.domain.verse.VerseReference reference) {
        // bookId 정보가 domain에 없으므로 임시로 0 사용 (향후 개선 필요)
        return "0:" + reference.getChapter() + ":" + reference.getVerse();
    }
    
    /**
     * BibleVerse에서 bookId 추출 (향후 Domain 구조 개선 필요)
     */
    private static Integer extractBookId(com.dong.bible.domain.verse.BibleVerse verse) {
        // 현재 Domain에 bookId 정보가 없으므로 임시로 0 반환
        // TODO: Domain 구조 개선 시 실제 bookId 반환하도록 수정
        return 0;
    }
    
    /**
     * 하이라이팅 개수 계산
     */
    private static int countHighlights(String text) {
        if (text == null) return 0;
        return (int) text.split("<mark>").length - 1;
    }
    
    /**
     * 하이라이팅 품질 계산
     */
    private static double calculateHighlightQuality(String originalText, int highlightCount) {
        if (originalText == null || originalText.isEmpty() || highlightCount == 0) {
            return 0.0;
        }
        double density = (double) highlightCount / originalText.length() * 100;
        return Math.min(1.0, density);
    }
    
    /**
     * 검색 결과 요약 DTO
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchResultSummaryDto {
        private String reference;
        private Double score;
        private String relevanceLevel;
        private Integer highlightCount;
        private Boolean hasHighlight;
        private Boolean isHighQuality;
        private String searchKeyword;
    }
}