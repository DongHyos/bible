package com.dong.bible.domain.dailyverse;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * DailyVerse Repository Interface
 * 순수 DDD - 비즈니스 중심의 오늘의 말씀 저장소
 * 기술적 세부사항 없이 도메인 언어로만 표현
 */
public interface DailyVerseRepository {
    
    // === 오늘의 말씀 조회 ===
    
    /**
     * 오늘의 말씀 가져오기
     */
    Optional<DailyVerse> getTodaysVerse();
    
    /**
     * 특정 날짜의 말씀 가져오기
     */
    Optional<DailyVerse> getVerseForDate(DailyDate date);
    
    // === 말씀 목록 조회 ===
    
    /**
     * 최근 며칠간의 말씀들 가져오기
     */
    List<DailyVerse> getRecentVerses(int days);
    
    /**
     * 앞으로 며칠간의 말씀들 가져오기
     */
    List<DailyVerse> getUpcomingVerses(int days);
    
    /**
     * 특정 월의 말씀들 가져오기
     */
    List<DailyVerse> getVersesInMonth(int year, int month);
    
    /**
     * 특정 기간의 말씀들 가져오기
     */
    List<DailyVerse> getVersesInPeriod(LocalDate startDate, LocalDate endDate);
    
    /**
     * 활성화된 모든 말씀들 가져오기
     */
    List<DailyVerse> getAllActiveVerses();
    
    // === 말씀 저장/관리 ===
    
    /**
     * 오늘의 말씀 저장
     */
    DailyVerse store(DailyVerse dailyVerse);
    
    /**
     * 오늘의 말씀 제거 (Entity로 식별)
     */
    void remove(DailyVerse dailyVerse);
    
    // === 말씀 존재 확인 ===
    
    /**
     * 해당 날짜에 말씀이 있는지 확인
     */
    boolean hasVerseForDate(DailyDate date);
    
    /**
     * 오늘 말씀이 준비되어 있는지 확인
     */
    boolean isTodaysVerseReady();
    
    // === 말씀 개수 ===
    
    /**
     * 등록된 말씀 총 개수
     */
    long countAllVerses();
    
    /**
     * 활성화된 말씀 개수
     */
    long countActiveVerses();
}