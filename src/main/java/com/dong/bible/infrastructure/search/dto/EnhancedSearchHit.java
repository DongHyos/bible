package com.dong.bible.infrastructure.search.dto;

import com.dong.bible.infrastructure.search.document.VerseSearchDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * ElasticSearch 검색 결과와 메타데이터를 포함하는 DTO
 * Infrastructure Layer - ElasticSearch 특화 정보 전달
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnhancedSearchHit {
    
    /**
     * 검색된 문서
     */
    private VerseSearchDocument document;
    
    /**
     * 검색 관련도 점수
     */
    private float score;
    
    /**
     * 하이라이팅된 필드들
     * key: 필드명 (ex: "content")
     * value: 하이라이팅된 텍스트 조각들
     */
    private Map<String, String[]> highlightFields;
    
    /**
     * 검색어와의 매칭 정보
     */
    private MatchInfo matchInfo;
    
    /**
     * ElasticSearch SearchHit으로부터 생성
     */
    public static EnhancedSearchHit from(VerseSearchDocument document, float score, 
                                       Map<String, String[]> highlightFields) {
        return EnhancedSearchHit.builder()
                .document(document)
                .score(score)
                .highlightFields(highlightFields)
                .matchInfo(MatchInfo.from(highlightFields))
                .build();
    }
    
    /**
     * 하이라이팅이 없는 기본 검색 결과로부터 생성
     */
    public static EnhancedSearchHit fromBasic(VerseSearchDocument document) {
        return EnhancedSearchHit.builder()
                .document(document)
                .score(1.0f)
                .highlightFields(Map.of())
                .matchInfo(MatchInfo.empty())
                .build();
    }
    
    /**
     * content 필드의 하이라이팅된 텍스트 반환
     */
    public String[] getContentHighlights() {
        return highlightFields != null ? 
               highlightFields.getOrDefault("content", new String[0]) : 
               new String[0];
    }
    
    /**
     * bookName 필드의 하이라이팅된 텍스트 반환
     */
    public String[] getBookNameHighlights() {
        return highlightFields != null ? 
               highlightFields.getOrDefault("bookName", new String[0]) : 
               new String[0];
    }
    
    /**
     * 하이라이팅 존재 여부
     */
    public boolean hasHighlight() {
        return highlightFields != null && !highlightFields.isEmpty();
    }
    
    /**
     * 매칭 정보 내부 클래스
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatchInfo {
        private int totalMatches;
        private int contentMatches;
        private int bookNameMatches;
        private boolean exactMatch;
        private boolean phraseMatch;
        
        /**
         * 하이라이트 정보로부터 매칭 정보 생성
         */
        public static MatchInfo from(Map<String, String[]> highlightFields) {
            if (highlightFields == null || highlightFields.isEmpty()) {
                return empty();
            }
            
            int contentMatches = countMatches(highlightFields.get("content"));
            int bookNameMatches = countMatches(highlightFields.get("bookName"));
            
            return MatchInfo.builder()
                    .totalMatches(contentMatches + bookNameMatches)
                    .contentMatches(contentMatches)
                    .bookNameMatches(bookNameMatches)
                    .exactMatch(false) // TODO: 실제 매칭 타입 분석 로직 추가
                    .phraseMatch(false) // TODO: 구문 매칭 여부 분석 로직 추가
                    .build();
        }
        
        /**
         * 빈 매칭 정보
         */
        public static MatchInfo empty() {
            return MatchInfo.builder()
                    .totalMatches(0)
                    .contentMatches(0)
                    .bookNameMatches(0)
                    .exactMatch(false)
                    .phraseMatch(false)
                    .build();
        }
        
        /**
         * 하이라이트 조각에서 매칭 개수 계산
         */
        private static int countMatches(String[] highlights) {
            if (highlights == null || highlights.length == 0) {
                return 0;
            }
            
            int count = 0;
            for (String highlight : highlights) {
                // <mark> 태그 개수로 매칭 횟수 추정
                count += (highlight.split("<mark>").length - 1);
            }
            return count;
        }
    }
}