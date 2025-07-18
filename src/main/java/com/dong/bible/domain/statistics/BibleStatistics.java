package com.dong.bible.domain.statistics;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 성경 통계 정보를 나타내는 도메인 객체
 * 
 * 이 객체는 여러 도메인 객체들의 협력으로 만들어지는 계산 결과를 담습니다.
 * Value Object의 특성을 가지며, 불변성을 보장합니다.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BibleStatistics {
    
    private final int totalBooks;
    private final int oldTestamentBooks;
    private final int newTestamentBooks;
    private final int totalChapters;
    private final double oldTestamentRatio;
    private final double newTestamentRatio;
    private final double averageChaptersPerBook;
    
    /**
     * 정적 팩토리 메서드
     */
    public static BibleStatistics of(int totalBooks, 
                                    int oldTestamentBooks, 
                                    int newTestamentBooks, 
                                    int totalChapters,
                                    double oldTestamentRatio,
                                    double newTestamentRatio,
                                    double averageChaptersPerBook) {
        validateInputs(totalBooks, oldTestamentBooks, newTestamentBooks, totalChapters);
        
        return new BibleStatistics(
            totalBooks,
            oldTestamentBooks,
            newTestamentBooks,
            totalChapters,
            oldTestamentRatio,
            newTestamentRatio,
            averageChaptersPerBook
        );
    }
    
    /**
     * 입력값 검증 (도메인 규칙)
     */
    private static void validateInputs(int totalBooks, int oldTestamentBooks, int newTestamentBooks, int totalChapters) {
        if (totalBooks < 0) {
            throw new IllegalArgumentException("총 성경책 수는 0 이상이어야 합니다: " + totalBooks);
        }
        if (oldTestamentBooks < 0) {
            throw new IllegalArgumentException("구약 성경책 수는 0 이상이어야 합니다: " + oldTestamentBooks);
        }
        if (newTestamentBooks < 0) {
            throw new IllegalArgumentException("신약 성경책 수는 0 이상이어야 합니다: " + newTestamentBooks);
        }
        if (totalChapters < 0) {
            throw new IllegalArgumentException("총 장수는 0 이상이어야 합니다: " + totalChapters);
        }
        if (totalBooks != oldTestamentBooks + newTestamentBooks) {
            throw new IllegalArgumentException("총 성경책 수가 구약 + 신약 수와 일치하지 않습니다");
        }
    }
    
    /**
     * 구약이 더 많은지 확인하는 비즈니스 로직
     */
    public boolean isOldTestamentDominant() {
        return oldTestamentBooks > newTestamentBooks;
    }
    
    /**
     * 평균 이상의 장수를 가진 성경책 비율이 높은지 확인
     */
    public boolean hasHighChapterDensity() {
        return averageChaptersPerBook > 20.0; // 도메인 전문가와 협의 필요
    }
    
    /**
     * 표시용 문자열
     */
    public String toDisplayString() {
        return String.format("총 %d권 (구약 %d권, 신약 %d권), 총 %d장, 평균 %.1f장/권",
            totalBooks, oldTestamentBooks, newTestamentBooks, totalChapters, averageChaptersPerBook);
    }
}