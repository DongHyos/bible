package com.dong.bible.service;

import com.dong.bible.web.dto.response.DailyVerseDto;
import com.dong.bible.web.dto.response.DailyVerseSimpleDto;

import java.time.LocalDate;
import java.util.List;

public interface DailyVerseService {
    
    // 오늘의 말씀 조회
    DailyVerseDto getTodayVerse();
    
    // 특정 날짜의 오늘의 말씀 조회
    DailyVerseDto getVerseByDate(LocalDate date);
    
    // 최근 N일간의 오늘의 말씀 조회
    List<DailyVerseSimpleDto> getRecentVerses(int days);
    
    // 앞으로 N일간의 오늘의 말씀 조회
    List<DailyVerseSimpleDto> getUpcomingVerses(int days);
    
    // 특정 월의 오늘의 말씀 조회
    List<DailyVerseSimpleDto> getVersesByMonth(int year, int month);
    
    // 모든 오늘의 말씀 조회 (관리용)
    List<DailyVerseSimpleDto> getAllVerses();
}