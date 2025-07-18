package com.dong.bible.domain.sermon;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

import java.time.LocalDate;
import java.util.Objects;

/**
 * 설교 기본 정보(제목, 날짜)를 나타내는 Value Object
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Value
public class SermonInfo {
    
    String title;
    LocalDate sermonDate;
    
    /**
     * 정적 팩토리 메서드 - 제목과 날짜로 생성
     */
    public static SermonInfo of(String title, LocalDate sermonDate) {
        validateTitle(title);
        validateDate(sermonDate);
        
        return new SermonInfo(title.trim(), sermonDate);
    }
    
    /**
     * 제목 유효성 검증
     */
    private static void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("설교 제목은 비어있을 수 없습니다");
        }
        
        String trimmed = title.trim();
        if (trimmed.length() > 200) {
            throw new IllegalArgumentException("설교 제목은 200자를 초과할 수 없습니다: " + trimmed.length() + "자");
        }
    }
    
    /**
     * 날짜 유효성 검증
     */
    private static void validateDate(LocalDate sermonDate) {
        if (sermonDate == null) {
            throw new IllegalArgumentException("설교 날짜는 null일 수 없습니다");
        }
        
        LocalDate today = LocalDate.now();
        LocalDate earliestDate = LocalDate.of(1900, 1, 1);
        
        if (sermonDate.isBefore(earliestDate)) {
            throw new IllegalArgumentException("설교 날짜는 1900년 이전일 수 없습니다: " + sermonDate);
        }
        
        if (sermonDate.isAfter(today.plusYears(1))) {
            throw new IllegalArgumentException("설교 날짜는 1년 후를 초과할 수 없습니다: " + sermonDate);
        }
    }
    
    /**
     * 오늘 설교인지 확인
     */
    public boolean isToday() {
        return sermonDate.equals(LocalDate.now());
    }
    
    /**
     * 특정 연도의 설교인지 확인
     */
    public boolean isInYear(int year) {
        return sermonDate.getYear() == year;
    }
    
    /**
     * 특정 월의 설교인지 확인
     */
    public boolean isInMonth(int year, int month) {
        return sermonDate.getYear() == year && sermonDate.getMonthValue() == month;
    }
    
    /**
     * 날짜 범위에 포함되는지 확인
     */
    public boolean isInDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return false;
        }
        
        return !sermonDate.isBefore(startDate) && !sermonDate.isAfter(endDate);
    }
    
    /**
     * 최근 설교인지 확인 (N일 이내)
     */
    public boolean isRecent(int days) {
        LocalDate cutoffDate = LocalDate.now().minusDays(days);
        return !sermonDate.isBefore(cutoffDate);
    }
    
    /**
     * 표시용 문자열 생성
     */
    public String toDisplayString() {
        return title + " (" + sermonDate + ")";
    }
    
    /**
     * 짧은 표시용 문자열 (제목만)
     */
    public String getShortDisplay() {
        if (title.length() <= 30) {
            return title;
        }
        return title.substring(0, 30) + "...";
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SermonInfo that = (SermonInfo) o;
        return Objects.equals(title, that.title) && 
               Objects.equals(sermonDate, that.sermonDate);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(title, sermonDate);
    }
    
    @Override
    public String toString() {
        return toDisplayString();
    }
}