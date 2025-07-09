package com.dong.bible.service.impl;

import com.dong.bible.web.dto.response.DailyVerseDto;
import com.dong.bible.web.dto.response.DailyVerseSimpleDto;
import com.dong.bible.infrastructure.persistence.entity.DailyVerse;
import com.dong.bible.infrastructure.persistence.entity.KrvVerse;
import com.dong.bible.mapstruct.DailyVerseMapper;
import com.dong.bible.infrastructure.persistence.jpa.DailyVerseRepository;
import com.dong.bible.infrastructure.persistence.jpa.KrvVerseRepository;
import com.dong.bible.service.DailyVerseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DailyVerseServiceImpl implements DailyVerseService {

    private final DailyVerseRepository dailyVerseRepository;
    private final KrvVerseRepository krvVerseRepository;
    private final DailyVerseMapper dailyVerseMapper;

    @Override
    public DailyVerseDto getTodayVerse() {
        log.info("오늘의 말씀 조회");
        
        LocalDate today = LocalDate.now();
        DailyVerse dailyVerse = dailyVerseRepository.findByDateWithBook(today)
                .orElseThrow(() -> new RuntimeException("오늘의 말씀이 등록되지 않았습니다: " + today));
        
        DailyVerseDto result = dailyVerseMapper.toDto(dailyVerse);
        
        // 실제 성경 구절 내용 조회하여 설정
        String verseText = getVerseText(dailyVerse);
        result.setVerseText(verseText);
        
        return result;
    }

    @Override
    public DailyVerseDto getVerseByDate(LocalDate date) {
        log.info("특정 날짜 오늘의 말씀 조회: {}", date);
        
        DailyVerse dailyVerse = dailyVerseRepository.findByDateWithBook(date)
                .orElseThrow(() -> new RuntimeException("해당 날짜의 말씀이 등록되지 않았습니다: " + date));
        
        DailyVerseDto result = dailyVerseMapper.toDto(dailyVerse);
        
        // 실제 성경 구절 내용 조회하여 설정
        String verseText = getVerseText(dailyVerse);
        result.setVerseText(verseText);
        
        return result;
    }

    @Override
    public List<DailyVerseSimpleDto> getRecentVerses(int days) {
        log.info("최근 {}일간 오늘의 말씀 조회", days);
        
        LocalDate endDate = LocalDate.now().minusDays(1); // 어제까지
        LocalDate startDate = endDate.minusDays(days - 1);
        
        List<DailyVerse> dailyVerses = dailyVerseRepository.findRecentVerses(startDate, endDate);
        return dailyVerseMapper.toSimpleDtoList(dailyVerses);
    }

    @Override
    public List<DailyVerseSimpleDto> getUpcomingVerses(int days) {
        log.info("앞으로 {}일간 오늘의 말씀 조회", days);
        
        LocalDate startDate = LocalDate.now().plusDays(1); // 내일부터
        LocalDate endDate = startDate.plusDays(days - 1);
        
        List<DailyVerse> dailyVerses = dailyVerseRepository.findUpcomingVerses(startDate, endDate);
        return dailyVerseMapper.toSimpleDtoList(dailyVerses);
    }

    @Override
    public List<DailyVerseSimpleDto> getVersesByMonth(int year, int month) {
        log.info("{}년 {}월 오늘의 말씀 조회", year, month);
        
        List<DailyVerse> dailyVerses = dailyVerseRepository.findByYearAndMonth(year, month);
        return dailyVerseMapper.toSimpleDtoList(dailyVerses);
    }

    @Override
    public List<DailyVerseSimpleDto> getAllVerses() {
        log.info("모든 오늘의 말씀 조회");
        
        List<DailyVerse> dailyVerses = dailyVerseRepository.findByIsActiveTrueOrderByVerseDateDesc();
        return dailyVerseMapper.toSimpleDtoList(dailyVerses);
    }

    /**
     * DailyVerse에서 실제 성경 구절 내용 조회
     */
    private String getVerseText(DailyVerse dailyVerse) {
        try {
            Integer bookId = dailyVerse.getBook().getId();
            Short chapter = dailyVerse.getChapter();
            Short verseStart = dailyVerse.getVerseStart();
            Short verseEnd = dailyVerse.getVerseEnd();
            
            if (verseStart.equals(verseEnd)) {
                // 단일 절
                KrvVerse verse = krvVerseRepository.findByBookIdAndChapterAndVerse(bookId, chapter.intValue(), verseStart.intValue())
                        .orElse(null);
                return verse != null ? verse.getText() : "";
            } else {
                // 여러 절
                List<KrvVerse> verses = krvVerseRepository.findByBookIdAndChapterAndVerseRange(
                        bookId, chapter.intValue(), verseStart.intValue(), verseEnd.intValue());
                
                return verses.stream()
                        .map(v -> v.getVerse() + " " + v.getText())
                        .collect(Collectors.joining(" "));
            }
        } catch (Exception e) {
            log.error("성경 구절 조회 중 오류 발생: {}", e.getMessage());
            return "";
        }
    }
}