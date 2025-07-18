package com.dong.bible.application.service;

import com.dong.bible.application.dto.DailyVerseDetailDto;
import com.dong.bible.application.dto.DailyVerseSummaryDto;
import com.dong.bible.domain.dailyverse.DailyVerse;
import com.dong.bible.domain.dailyverse.DailyVerseRepository;
import com.dong.bible.domain.dailyverse.DailyDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DailyVerse Application Service
 * 비즈니스 로직을 조합하고 트랜잭션을 관리합니다.
 * Application DTO를 반환하여 계층별 책임을 분리합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DailyVerseApplicationService {

    private final DailyVerseRepository dailyVerseRepository;

    /**
     * 오늘의 말씀 상세 조회
     * Use Case: 오늘의 말씀 메인 페이지 표시
     */
    public DailyVerseDetailDto getTodayVerse() {
        log.info("오늘의 말씀 조회");

        DailyVerse dailyVerse = dailyVerseRepository.getTodaysVerse()
                .orElseThrow(() -> new IllegalArgumentException("오늘의 말씀이 등록되지 않았습니다"));

        return DailyVerseDetailDto.from(dailyVerse);
    }

    /**
     * 특정 날짜의 오늘의 말씀 상세 조회
     * Use Case: 특정 날짜 오늘의 말씀 상세 페이지 표시
     */
    public DailyVerseDetailDto getVerseByDate(LocalDate date) {
        log.info("특정 날짜 오늘의 말씀 조회: {}", date);

        DailyDate dailyDate = DailyDate.of(date); // Domain VO 사용

        DailyVerse dailyVerse = dailyVerseRepository.getVerseForDate(dailyDate)
                .orElseThrow(() -> new IllegalArgumentException("해당 날짜의 말씀이 등록되지 않았습니다: " + date));

        return DailyVerseDetailDto.from(dailyVerse);
    }

    /**
     * 최근 N일간의 오늘의 말씀 목록 조회
     * Use Case: 최근 오늘의 말씀 목록 페이지 표시
     */
    public List<DailyVerseSummaryDto> getRecentVerses(int days) {
        log.info("최근 {}일간 오늘의 말씀 조회", days);

        List<DailyVerse> dailyVerses = dailyVerseRepository.getRecentVerses(days);
        log.info("조회된 최근 오늘의 말씀 수: {}", dailyVerses.size());

        return dailyVerses.stream()
                .map(DailyVerseSummaryDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 앞으로 N일간의 오늘의 말씀 목록 조회
     * Use Case: 앞으로의 오늘의 말씀 목록 페이지 표시
     */
    public List<DailyVerseSummaryDto> getUpcomingVerses(int days) {
        log.info("앞으로 {}일간 오늘의 말씀 조회", days);

        List<DailyVerse> dailyVerses = dailyVerseRepository.getUpcomingVerses(days);
        log.info("조회된 앞으로의 오늘의 말씀 수: {}", dailyVerses.size());

        return dailyVerses.stream()
                .map(DailyVerseSummaryDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 월의 오늘의 말씀 목록 조회
     * Use Case: 월별 오늘의 말씀 목록 페이지 표시
     */
    public List<DailyVerseSummaryDto> getVersesByMonth(int year, int month) {
        log.info("{}년 {}월 오늘의 말씀 조회", year, month);

        List<DailyVerse> dailyVerses = dailyVerseRepository.getVersesInMonth(year, month);
        log.info("조회된 {}년 {}월 오늘의 말씀 수: {}", year, month, dailyVerses.size());

        return dailyVerses.stream()
                .map(DailyVerseSummaryDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 모든 오늘의 말씀 목록 조회 (관리용)
     * Use Case: 관리자 페이지에서 모든 오늘의 말씀 목록 표시
     */
    public List<DailyVerseSummaryDto> getAllVerses() {
        log.info("모든 오늘의 말씀 조회");

        List<DailyVerse> dailyVerses = dailyVerseRepository.getAllActiveVerses();
        log.info("조회된 모든 오늘의 말씀 수: {}", dailyVerses.size());

        return dailyVerses.stream()
                .map(DailyVerseSummaryDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 오늘의 말씀 존재 여부 확인
     * Use Case: 오늘의 말씀이 준비되었는지 확인
     */
    public boolean existsTodayVerse() {
        return dailyVerseRepository.isTodaysVerseReady();
    }

    /**
     * 특정 날짜의 말씀 존재 여부 확인
     * Use Case: 특정 날짜의 말씀이 준비되었는지 확인
     */
    public boolean existsVerseByDate(LocalDate date) {
        DailyDate dailyDate = DailyDate.of(date);
        return dailyVerseRepository.hasVerseForDate(dailyDate);
    }
}