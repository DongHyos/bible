package com.dong.bible.domain.verse;

import java.util.Objects;

/**
 * 검색 관련도 점수 Value Object
 * ElasticSearch의 _score 값을 도메인에서 안전하게 다루기 위한 객체
 */
public final class SearchScore {
    
    private final double value;
    
    private SearchScore(double value) {
        validateScore(value);
        this.value = value;
    }
    
    /**
     * 검색 점수 생성
     * 
     * @param score 검색 점수 (0.0 이상)
     * @return SearchScore 객체
     * @throws IllegalArgumentException 점수가 유효하지 않은 경우
     */
    public static SearchScore of(double score) {
        return new SearchScore(score);
    }
    
    /**
     * 기본 점수 (관련도 없음)
     */
    public static SearchScore zero() {
        return new SearchScore(0.0);
    }
    
    /**
     * 최고 점수 (완전 일치)
     */
    public static SearchScore perfect() {
        return new SearchScore(1.0);
    }
    
    /**
     * 점수 값 반환
     */
    public double getValue() {
        return value;
    }
    
    /**
     * 높은 관련도인지 확인 (0.5 이상)
     */
    public boolean isHighRelevance() {
        return value >= 0.5;
    }
    
    /**
     * 중간 관련도인지 확인 (0.2 이상 0.5 미만)
     */
    public boolean isMediumRelevance() {
        return value >= 0.2 && value < 0.5;
    }
    
    /**
     * 낮은 관련도인지 확인 (0.2 미만)
     */
    public boolean isLowRelevance() {
        return value < 0.2 && value > 0.0;
    }
    
    /**
     * 관련도 없음 (0.0)
     */
    public boolean isZero() {
        return value == 0.0;
    }
    
    /**
     * 다른 점수와 비교
     */
    public int compareTo(SearchScore other) {
        return Double.compare(this.value, other.value);
    }
    
    /**
     * 더 높은 점수인지 확인
     */
    public boolean isHigherThan(SearchScore other) {
        return this.value > other.value;
    }
    
    /**
     * 관련도 등급 반환
     */
    public RelevanceLevel getRelevanceLevel() {
        if (isZero()) {
            return RelevanceLevel.NONE;
        } else if (isLowRelevance()) {
            return RelevanceLevel.LOW;
        } else if (isMediumRelevance()) {
            return RelevanceLevel.MEDIUM;
        } else {
            return RelevanceLevel.HIGH;
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SearchScore that = (SearchScore) obj;
        return Double.compare(that.value, value) == 0;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return String.format("SearchScore(%.3f)", value);
    }
    
    /**
     * 점수 유효성 검증
     */
    private void validateScore(double score) {
        if (Double.isNaN(score) || Double.isInfinite(score)) {
            throw new IllegalArgumentException("검색 점수는 유한한 숫자여야 합니다");
        }
        if (score < 0.0) {
            throw new IllegalArgumentException("검색 점수는 0.0 이상이어야 합니다: " + score);
        }
    }
    
    /**
     * 관련도 등급 열거형
     */
    public enum RelevanceLevel {
        NONE("관련도 없음"),
        LOW("낮은 관련도"),
        MEDIUM("중간 관련도"),
        HIGH("높은 관련도");
        
        private final String description;
        
        RelevanceLevel(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}