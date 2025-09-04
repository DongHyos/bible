package com.dong.bible.domain.verse;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 하이라이팅된 내용 Value Object
 * 검색어가 강조 표시된 텍스트를 도메인에서 안전하게 다루기 위한 객체
 */
public final class HighlightedContent {
    
    private final String originalText;
    private final String highlightedText;
    private final int highlightCount;
    
    private static final Pattern HIGHLIGHT_PATTERN = Pattern.compile("<mark>(.*?)</mark>");
    
    private HighlightedContent(String originalText, String highlightedText) {
        validateTexts(originalText, highlightedText);
        this.originalText = originalText;
        this.highlightedText = highlightedText;
        this.highlightCount = countHighlights(highlightedText);
    }
    
    /**
     * 하이라이팅된 내용 생성
     * 
     * @param originalText 원본 텍스트
     * @param highlightedText 하이라이팅된 텍스트 (HTML 태그 포함)
     * @return HighlightedContent 객체
     * @throws IllegalArgumentException 텍스트가 유효하지 않은 경우
     */
    public static HighlightedContent of(String originalText, String highlightedText) {
        return new HighlightedContent(originalText, highlightedText);
    }
    
    /**
     * 하이라이팅이 없는 내용 생성
     * 
     * @param text 원본 텍스트
     * @return 하이라이팅이 없는 HighlightedContent 객체
     */
    public static HighlightedContent withoutHighlight(String text) {
        if (text == null) {
            throw new IllegalArgumentException("텍스트는 null일 수 없습니다");
        }
        return new HighlightedContent(text, text);
    }
    
    /**
     * ElasticSearch 하이라이팅 결과로부터 생성
     * 
     * @param originalText 원본 텍스트
     * @param elasticsearchHighlights ElasticSearch 하이라이팅 배열 (여러 조각)
     * @return HighlightedContent 객체
     */
    public static HighlightedContent fromElasticSearchHighlights(String originalText, String[] elasticsearchHighlights) {
        if (originalText == null) {
            throw new IllegalArgumentException("원본 텍스트는 null일 수 없습니다");
        }
        
        if (elasticsearchHighlights == null || elasticsearchHighlights.length == 0) {
            return withoutHighlight(originalText);
        }
        
        // ElasticSearch는 하이라이팅된 조각들을 배열로 반환하므로 합침
        String combinedHighlighted = String.join(" ... ", elasticsearchHighlights);
        return new HighlightedContent(originalText, combinedHighlighted);
    }
    
    /**
     * 원본 텍스트 반환
     */
    public String getOriginalText() {
        return originalText;
    }
    
    /**
     * 하이라이팅된 텍스트 반환
     */
    public String getHighlightedText() {
        return highlightedText;
    }
    
    /**
     * 하이라이팅 개수 반환
     */
    public int getHighlightCount() {
        return highlightCount;
    }
    
    /**
     * 하이라이팅이 있는지 확인
     */
    public boolean hasHighlight() {
        return highlightCount > 0;
    }
    
    /**
     * 여러 개의 하이라이팅이 있는지 확인
     */
    public boolean hasMultipleHighlights() {
        return highlightCount > 1;
    }
    
    /**
     * 하이라이팅된 키워드들 추출
     * 
     * @return 하이라이팅된 키워드 배열
     */
    public String[] extractHighlightedKeywords() {
        if (!hasHighlight()) {
            return new String[0];
        }
        
        Matcher matcher = HIGHLIGHT_PATTERN.matcher(highlightedText);
        return matcher.results()
                .map(matchResult -> matchResult.group(1))
                .distinct()
                .toArray(String[]::new);
    }
    
    /**
     * HTML 태그 제거한 순수 텍스트 반환
     */
    public String getPlainText() {
        return highlightedText.replaceAll("</?mark>", "");
    }
    
    /**
     * 하이라이팅 품질 점수 계산 (0.0 ~ 1.0)
     * 하이라이팅 개수와 텍스트 길이를 고려한 품질 점수
     */
    public double getHighlightQuality() {
        if (!hasHighlight() || originalText.isEmpty()) {
            return 0.0;
        }
        
        // 하이라이팅 밀도를 기반으로 품질 계산
        double density = (double) highlightCount / originalText.length() * 100;
        return Math.min(1.0, density); // 최대 1.0으로 제한
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        HighlightedContent that = (HighlightedContent) obj;
        return Objects.equals(originalText, that.originalText) && 
               Objects.equals(highlightedText, that.highlightedText);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(originalText, highlightedText);
    }
    
    @Override
    public String toString() {
        return String.format("HighlightedContent(highlights=%d, length=%d)", 
                           highlightCount, originalText.length());
    }
    
    /**
     * 텍스트 유효성 검증
     */
    private void validateTexts(String originalText, String highlightedText) {
        if (originalText == null) {
            throw new IllegalArgumentException("원본 텍스트는 null일 수 없습니다");
        }
        if (highlightedText == null) {
            throw new IllegalArgumentException("하이라이팅된 텍스트는 null일 수 없습니다");
        }
        if (originalText.trim().isEmpty()) {
            throw new IllegalArgumentException("원본 텍스트는 비어있을 수 없습니다");
        }
    }
    
    /**
     * 하이라이팅 개수 계산
     */
    private int countHighlights(String text) {
        Matcher matcher = HIGHLIGHT_PATTERN.matcher(text);
        return (int) matcher.results().count();
    }
}