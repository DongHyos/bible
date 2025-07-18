package com.dong.bible.web.controller;

import com.dong.bible.application.dto.DailyVerseDetailDto;
import com.dong.bible.application.dto.DailyVerseSummaryDto;
import com.dong.bible.application.service.DailyVerseApplicationService;
import com.dong.bible.common.response.AppResponse;
import com.dong.bible.web.dto.response.DailyVerseDto;
import com.dong.bible.web.dto.response.DailyVerseSimpleDto;
import com.dong.bible.web.mapper.DailyVerseResponseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/daily-verse")
@Slf4j
@RequiredArgsConstructor
public class DailyVerseController {

    private final DailyVerseApplicationService dailyVerseApplicationService;
    private final DailyVerseResponseMapper dailyVerseResponseMapper;

    /**
     * 오늘의 말씀 조회
     * 프론트엔드 메인 기능
     */
    @GetMapping("/today")
    public ResponseEntity<AppResponse<DailyVerseDto>> getTodayVerse() {
        log.info("오늘의 말씀 조회 API 호출");
        
        DailyVerseDetailDto dailyVerseDetail = dailyVerseApplicationService.getTodayVerse();
        DailyVerseDto response = dailyVerseResponseMapper.fromDetailDto(dailyVerseDetail);
        
        return ResponseEntity.ok(AppResponse.of(response));
    }

    /**
     * 특정 날짜의 오늘의 말씀 조회
     */
    @GetMapping("/date/{date}")
    public ResponseEntity<AppResponse<DailyVerseDto>> getVerseByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        log.info("특정 날짜 오늘의 말씀 조회 API 호출: {}", date);
        
        DailyVerseDetailDto dailyVerseDetail = dailyVerseApplicationService.getVerseByDate(date);
        DailyVerseDto response = dailyVerseResponseMapper.fromDetailDto(dailyVerseDetail);
        
        return ResponseEntity.ok(AppResponse.of(response));
    }

    /**
     * 최근 N일간의 오늘의 말씀 조회
     */
    @GetMapping("/recent")
    public ResponseEntity<AppResponse<List<DailyVerseSimpleDto>>> getRecentVerses(
            @RequestParam(defaultValue = "7") int days) {
        
        log.info("최근 {}일간 오늘의 말씀 조회 API 호출", days);
        
        // 최대 30일로 제한
        days = Math.min(days, 30);
        
        List<DailyVerseSummaryDto> dailyVerseSummaries = dailyVerseApplicationService.getRecentVerses(days);
        List<DailyVerseSimpleDto> response = dailyVerseResponseMapper.fromSummaryDtoList(dailyVerseSummaries);
        
        return ResponseEntity.ok(AppResponse.of(response));
    }

    /**
     * 앞으로 N일간의 오늘의 말씀 조회
     */
    @GetMapping("/upcoming")
    public ResponseEntity<AppResponse<List<DailyVerseSimpleDto>>> getUpcomingVerses(
            @RequestParam(defaultValue = "7") int days) {
        
        log.info("앞으로 {}일간 오늘의 말씀 조회 API 호출", days);
        
        // 최대 30일로 제한
        days = Math.min(days, 30);
        
        List<DailyVerseSummaryDto> dailyVerseSummaries = dailyVerseApplicationService.getUpcomingVerses(days);
        List<DailyVerseSimpleDto> response = dailyVerseResponseMapper.fromSummaryDtoList(dailyVerseSummaries);
        
        return ResponseEntity.ok(AppResponse.of(response));
    }

    /**
     * 특정 월의 오늘의 말씀 조회
     */
    @GetMapping("/month/{year}/{month}")
    public ResponseEntity<AppResponse<List<DailyVerseSimpleDto>>> getVersesByMonth(
            @PathVariable int year,
            @PathVariable int month) {
        
        log.info("{}년 {}월 오늘의 말씀 조회 API 호출", year, month);
        
        List<DailyVerseSummaryDto> dailyVerseSummaries = dailyVerseApplicationService.getVersesByMonth(year, month);
        List<DailyVerseSimpleDto> response = dailyVerseResponseMapper.fromSummaryDtoList(dailyVerseSummaries);
        
        return ResponseEntity.ok(AppResponse.of(response));
    }

    /**
     * 현재 월의 오늘의 말씀 조회
     */
    @GetMapping("/current-month")
    public ResponseEntity<AppResponse<List<DailyVerseSimpleDto>>> getCurrentMonthVerses() {
        LocalDate now = LocalDate.now();
        
        log.info("현재 월 오늘의 말씀 조회 API 호출: {}년 {}월", now.getYear(), now.getMonthValue());
        
        List<DailyVerseSummaryDto> dailyVerseSummaries = dailyVerseApplicationService.getVersesByMonth(now.getYear(), now.getMonthValue());
        List<DailyVerseSimpleDto> response = dailyVerseResponseMapper.fromSummaryDtoList(dailyVerseSummaries);
        
        return ResponseEntity.ok(AppResponse.of(response));
    }

    /**
     * 모든 오늘의 말씀 조회 (관리용)
     */
    @GetMapping("/all")
    public ResponseEntity<AppResponse<List<DailyVerseSimpleDto>>> getAllVerses() {
        log.info("모든 오늘의 말씀 조회 API 호출");
        
        List<DailyVerseSummaryDto> dailyVerseSummaries = dailyVerseApplicationService.getAllVerses();
        List<DailyVerseSimpleDto> response = dailyVerseResponseMapper.fromSummaryDtoList(dailyVerseSummaries);
        
        return ResponseEntity.ok(AppResponse.of(response));
    }
}
