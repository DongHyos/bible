package com.dong.bible.domain.dailyverse;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Objects;

/**
 * 오늘의 말씀 날짜를 나타내는 Value Object
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DailyDate {
    
    private final LocalDate date;
    
    /**
     * 정적 팩토리 메서드 - LocalDate로 생성
     */
    public static DailyDate of(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("날짜는 null일 수 없습니다");
        }
        return new DailyDate(date);
    }
    
    /**
     * 정적 팩토리 메서드 - 오늘 날짜로 생성
     */
    public static DailyDate today() {
        return of(LocalDate.now());
    }
    
    /**
     * 오늘 날짜인지 확인
     */
    public boolean isToday() {
        return date.equals(LocalDate.now());
    }
    
    /**
     * 특정 월에 속하는지 확인
     */
    public boolean isInMonth(int year, int month) {
        return date.getYear() == year && date.getMonthValue() == month;
    }
    
    /**
     * 특정 날짜 범위에 속하는지 확인
     */
    public boolean isBetween(LocalDate startDate, LocalDate endDate) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DailyDate dailyDate = (DailyDate) o;
        return Objects.equals(date, dailyDate.date);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(date);
    }
    
    @Override
    public String toString() {
        return date.toString();
    }
}