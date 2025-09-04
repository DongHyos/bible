package com.dong.bible.domain.verse;

import java.util.Objects;

/**
 * 향상된 검색 결과 Value Object
 * 검색된 구절과 함께 점수, 하이라이팅 정보를 포함하는 종합적인 검색 결과
 */
public final class EnhancedSearchResult {
    
    private final BibleVerse verse;
    private final SearchScore score;
    private final HighlightedContent highlightedContent;
    private final String searchKeyword;
    
    private EnhancedSearchResult(BibleVerse verse, SearchScore score, 
                                HighlightedContent highlightedContent, String searchKeyword) {
        validateInputs(verse, score, highlightedContent);
        this.verse = verse;
        this.score = score;
        this.highlightedContent = highlightedContent;
        this.searchKeyword = searchKeyword != null ? searchKeyword.trim() : "";
    }
    
    /**
     * 향상된 검색 결과 생성
     * 
     * @param verse 검색된 구절
     * @param score 검색 점수
     * @param highlightedContent 하이라이팅된 내용
     * @param searchKeyword 검색 키워드
     * @return EnhancedSearchResult 객체
     */
    public static EnhancedSearchResult of(BibleVerse verse, SearchScore score, 
                                        HighlightedContent highlightedContent, String searchKeyword) {
        return new EnhancedSearchResult(verse, score, highlightedContent, searchKeyword);
    }
    
    /**
     * 기본 검색 결과를 향상된 결과로 변환 (점수와 하이라이팅 없음)
     * 
     * @param verse 검색된 구절
     * @return 기본적인 EnhancedSearchResult 객체
     */
    public static EnhancedSearchResult fromBasicResult(BibleVerse verse) {
        return new EnhancedSearchResult(
            verse, 
            SearchScore.zero(),
            HighlightedContent.withoutHighlight(verse.getContent().getText()),
            ""
        );
    }
    
    /**
     * ElasticSearch 결과로부터 생성
     * 
     * @param verse 검색된 구절
     * @param elasticScore ElasticSearch _score 값
     * @param originalText 원본 텍스트
     * @param highlightFragments ElasticSearch 하이라이팅 조각들
     * @param searchKeyword 검색 키워드
     * @return EnhancedSearchResult 객체
     */
    public static EnhancedSearchResult fromElasticSearchResult(BibleVerse verse, double elasticScore,
                                                             String originalText, String[] highlightFragments,
                                                             String searchKeyword) {
        SearchScore score = SearchScore.of(elasticScore);
        HighlightedContent content = HighlightedContent.fromElasticSearchHighlights(originalText, highlightFragments);
        return new EnhancedSearchResult(verse, score, content, searchKeyword);
    }
    
    /**
     * 검색된 구절 반환
     */
    public BibleVerse getVerse() {
        return verse;
    }
    
    /**
     * 검색 점수 반환
     */
    public SearchScore getScore() {
        return score;
    }
    
    /**
     * 하이라이팅된 내용 반환
     */
    public HighlightedContent getHighlightedContent() {
        return highlightedContent;
    }
    
    /**
     * 검색 키워드 반환
     */
    public String getSearchKeyword() {
        return searchKeyword;
    }
    
    /**
     * 높은 품질의 검색 결과인지 확인
     * 높은 관련도 점수와 명확한 하이라이팅을 가진 경우
     */
    public boolean isHighQualityResult() {
        return score.isHighRelevance() && highlightedContent.hasHighlight();
    }
    
    /**
     * 완벽한 매칭 결과인지 확인
     * 최고 점수와 여러 하이라이팅을 가진 경우
     */
    public boolean isPerfectMatch() {
        return score.getValue() >= 0.9 && highlightedContent.hasMultipleHighlights();
    }
    
    /**
     * 부분 매칭 결과인지 확인
     * 중간 이상 점수를 가진 경우
     */
    public boolean isPartialMatch() {
        return score.isMediumRelevance() || score.isHighRelevance();
    }
    
    /**
     * 검색 결과 요약 정보
     */
    public SearchResultSummary getSummary() {
        return SearchResultSummary.builder()
                .reference(verse.getReference().toString())
                .bookName(verse.getReference().getBookName())
                .chapter(verse.getReference().getChapter())
                .verse(verse.getReference().getVerse())
                .score(score.getValue())
                .relevanceLevel(score.getRelevanceLevel())
                .highlightCount(highlightedContent.getHighlightCount())
                .hasHighlight(highlightedContent.hasHighlight())
                .searchKeyword(searchKeyword)
                .build();
    }
    
    /**
     * 다른 검색 결과와 관련도 비교
     */
    public int compareByRelevance(EnhancedSearchResult other) {
        // 1차: 점수 비교
        int scoreComparison = score.compareTo(other.score);
        if (scoreComparison != 0) {
            return -scoreComparison; // 높은 점수가 우선
        }
        
        // 2차: 하이라이팅 개수 비교
        int highlightComparison = Integer.compare(
            other.highlightedContent.getHighlightCount(), 
            this.highlightedContent.getHighlightCount()
        );
        if (highlightComparison != 0) {
            return highlightComparison;
        }
        
        // 3차: 구절 번호 비교 (책, 장, 절 순)
        // VerseReference에 compareTo가 없으므로 직접 비교
        VerseReference thisRef = verse.getReference();
        VerseReference otherRef = other.verse.getReference();
        
        // 책 이름 비교
        int bookCompare = thisRef.getBookName().compareTo(otherRef.getBookName());
        if (bookCompare != 0) {
            return bookCompare;
        }
        
        // 장 비교
        int chapterCompare = Integer.compare(thisRef.getChapter(), otherRef.getChapter());
        if (chapterCompare != 0) {
            return chapterCompare;
        }
        
        // 절 비교
        return Integer.compare(thisRef.getVerse(), otherRef.getVerse());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EnhancedSearchResult that = (EnhancedSearchResult) obj;
        return Objects.equals(verse, that.verse) && 
               Objects.equals(score, that.score) && 
               Objects.equals(highlightedContent, that.highlightedContent) &&
               Objects.equals(searchKeyword, that.searchKeyword);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(verse, score, highlightedContent, searchKeyword);
    }
    
    @Override
    public String toString() {
        return String.format("EnhancedSearchResult(%s, score=%.3f, highlights=%d)", 
                           verse.getReference().toString(), 
                           score.getValue(), 
                           highlightedContent.getHighlightCount());
    }
    
    /**
     * 입력값 유효성 검증
     */
    private void validateInputs(BibleVerse verse, SearchScore score, HighlightedContent highlightedContent) {
        if (verse == null) {
            throw new IllegalArgumentException("구절은 null일 수 없습니다");
        }
        if (score == null) {
            throw new IllegalArgumentException("검색 점수는 null일 수 없습니다");
        }
        if (highlightedContent == null) {
            throw new IllegalArgumentException("하이라이팅된 내용은 null일 수 없습니다");
        }
    }
    
    /**
     * 검색 결과 요약 정보 내부 클래스
     */
    public static class SearchResultSummary {
        private final String reference;
        private final String bookName;
        private final Integer chapter;
        private final Integer verse;
        private final double score;
        private final SearchScore.RelevanceLevel relevanceLevel;
        private final int highlightCount;
        private final boolean hasHighlight;
        private final String searchKeyword;
        
        private SearchResultSummary(Builder builder) {
            this.reference = builder.reference;
            this.bookName = builder.bookName;
            this.chapter = builder.chapter;
            this.verse = builder.verse;
            this.score = builder.score;
            this.relevanceLevel = builder.relevanceLevel;
            this.highlightCount = builder.highlightCount;
            this.hasHighlight = builder.hasHighlight;
            this.searchKeyword = builder.searchKeyword;
        }
        
        public static Builder builder() {
            return new Builder();
        }
        
        // Getters
        public String getReference() { return reference; }
        public String getBookName() { return bookName; }
        public Integer getChapter() { return chapter; }
        public Integer getVerse() { return verse; }
        public double getScore() { return score; }
        public SearchScore.RelevanceLevel getRelevanceLevel() { return relevanceLevel; }
        public int getHighlightCount() { return highlightCount; }
        public boolean isHasHighlight() { return hasHighlight; }
        public String getSearchKeyword() { return searchKeyword; }
        
        public static class Builder {
            private String reference;
            private String bookName;
            private Integer chapter;
            private Integer verse;
            private double score;
            private SearchScore.RelevanceLevel relevanceLevel;
            private int highlightCount;
            private boolean hasHighlight;
            private String searchKeyword;
            
            public Builder reference(String reference) { this.reference = reference; return this; }
            public Builder bookName(String bookName) { this.bookName = bookName; return this; }
            public Builder chapter(Integer chapter) { this.chapter = chapter; return this; }
            public Builder verse(Integer verse) { this.verse = verse; return this; }
            public Builder score(double score) { this.score = score; return this; }
            public Builder relevanceLevel(SearchScore.RelevanceLevel relevanceLevel) { this.relevanceLevel = relevanceLevel; return this; }
            public Builder highlightCount(int highlightCount) { this.highlightCount = highlightCount; return this; }
            public Builder hasHighlight(boolean hasHighlight) { this.hasHighlight = hasHighlight; return this; }
            public Builder searchKeyword(String searchKeyword) { this.searchKeyword = searchKeyword; return this; }
            
            public SearchResultSummary build() {
                return new SearchResultSummary(this);
            }
        }
    }
}