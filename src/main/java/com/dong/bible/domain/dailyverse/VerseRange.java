package com.dong.bible.domain.dailyverse;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 구절 범위를 나타내는 Value Object (시작절~끝절)
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VerseRange {
    
    private final Integer startVerse;
    private final Integer endVerse;
    
    /**
     * 정적 팩토리 메서드 - 시작절과 끝절로 생성
     */
    public static VerseRange of(Integer startVerse, Integer endVerse) {
        validateVerses(startVerse, endVerse);
        return new VerseRange(startVerse, endVerse);
    }
    
    /**
     * 정적 팩토리 메서드 - 단일 절로 생성
     */
    public static VerseRange single(Integer verse) {
        if (verse == null || verse <= 0) {
            throw new IllegalArgumentException("구절 번호는 1 이상이어야 합니다: " + verse);
        }
        return new VerseRange(verse, verse);
    }
    
    /**
     * 구절 유효성 검증
     */
    private static void validateVerses(Integer startVerse, Integer endVerse) {
        if (startVerse == null || endVerse == null) {
            throw new IllegalArgumentException("구절 번호는 null일 수 없습니다");
        }
        
        if (startVerse <= 0 || endVerse <= 0) {
            throw new IllegalArgumentException("구절 번호는 1 이상이어야 합니다");
        }
        
        if (startVerse > endVerse) {
            throw new IllegalArgumentException("시작 절이 끝 절보다 클 수 없습니다: " + startVerse + " > " + endVerse);
        }
    }
    
    /**
     * 단일 절인지 확인
     */
    public boolean isSingleVerse() {
        return startVerse.equals(endVerse);
    }
    
    /**
     * 범위에 해당하는 절 개수
     */
    public int getVerseCount() {
        return endVerse - startVerse + 1;
    }
    
    /**
     * 특정 절이 이 범위에 포함되는지 확인
     */
    public boolean contains(Integer verse) {
        if (verse == null) {
            return false;
        }
        return verse >= startVerse && verse <= endVerse;
    }
    
    /**
     * 표시용 문자열 생성 ("16" 또는 "16-17")
     */
    public String toDisplayString() {
        if (isSingleVerse()) {
            return startVerse.toString();
        } else {
            return startVerse + "-" + endVerse;
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VerseRange that = (VerseRange) o;
        return Objects.equals(startVerse, that.startVerse) && 
               Objects.equals(endVerse, that.endVerse);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(startVerse, endVerse);
    }
    
    @Override
    public String toString() {
        return toDisplayString();
    }
}