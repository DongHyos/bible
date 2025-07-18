package com.dong.bible.infrastructure.persistence.repository;

import com.dong.bible.domain.dailyverse.DailyDate;
import com.dong.bible.domain.dailyverse.DailyVerse;
import com.dong.bible.domain.dailyverse.DailyVerseRepository;
import com.dong.bible.infrastructure.persistence.entity.DailyVerseEntity;
import com.dong.bible.infrastructure.persistence.jpa.DailyVerseJpaRepository;
import com.dong.bible.infrastructure.persistence.mapper.DailyVerseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * DailyVerse Repository의 JPA 구현체
 * 순수 Domain Repository를 Infrastructure에서 구현
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class DailyVerseRepositoryImpl implements DailyVerseRepository {
    
    private final DailyVerseJpaRepository jpaRepository;
    private final DailyVerseMapper mapper;
    
    @Override
    public Optional<DailyVerse> getTodaysVerse() {
        log.debug("Getting today's verse");
        
        LocalDate today = LocalDate.now();
        return jpaRepository.findByVerseDateAndIsActiveTrue(today)
                .map(mapper::toDomain);
    }
    
    @Override
    public Optional<DailyVerse> getVerseForDate(DailyDate date) {
        log.debug("Getting verse for date: {}", date);
        
        return jpaRepository.findByVerseDateAndIsActiveTrue(date.getDate())
                .map(mapper::toDomain);
    }
    
    @Override
    public List<DailyVerse> getRecentVerses(int days) {
        log.debug("Getting recent {} days verses", days);
        
        LocalDate endDate = LocalDate.now().minusDays(1); // 어제까지
        LocalDate startDate = endDate.minusDays(days - 1);
        
        return jpaRepository.findByVerseDateBetweenAndIsActiveTrueOrderByVerseDateDesc(
                startDate, endDate)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    @Override
    public List<DailyVerse> getUpcomingVerses(int days) {
        log.debug("Getting upcoming {} days verses", days);
        
        LocalDate startDate = LocalDate.now().plusDays(1); // 내일부터
        LocalDate endDate = startDate.plusDays(days - 1);
        
        return jpaRepository.findByVerseDateBetweenAndIsActiveTrueOrderByVerseDateAsc(
                startDate, endDate)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    @Override
    public List<DailyVerse> getVersesInMonth(int year, int month) {
        log.debug("Getting verses in {}/{}", year, month);
        
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        
        return jpaRepository.findByVerseDateBetweenAndIsActiveTrueOrderByVerseDateAsc(
                startDate, endDate)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    @Override
    public List<DailyVerse> getVersesInPeriod(LocalDate startDate, LocalDate endDate) {
        log.debug("Getting verses in period: {} ~ {}", startDate, endDate);
        
        return jpaRepository.findByVerseDateBetweenAndIsActiveTrueOrderByVerseDateAsc(
                startDate, endDate)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    @Override
    public List<DailyVerse> getAllActiveVerses() {
        log.debug("Getting all active verses");
        
        return jpaRepository.findByIsActiveTrueOrderByVerseDateDesc()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    @Override
    public DailyVerse store(DailyVerse dailyVerse) {
        log.debug("Storing daily verse: {}", dailyVerse.toDisplayString());
        
        DailyVerseEntity entity = mapper.toEntity(dailyVerse);
        DailyVerseEntity savedEntity = jpaRepository.save(entity);
        
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    public void remove(DailyVerse dailyVerse) {
        log.debug("Removing daily verse: {}", dailyVerse.toDisplayString());
        
        if (dailyVerse.getId() == null) {
            throw new IllegalArgumentException("Cannot remove DailyVerse without ID");
        }
        
        jpaRepository.deleteById(dailyVerse.getId());
    }
    
    @Override
    public boolean hasVerseForDate(DailyDate date) {
        log.debug("Checking if verse exists for date: {}", date);
        
        return jpaRepository.existsByVerseDateAndIsActiveTrue(date.getDate());
    }
    
    @Override
    public boolean isTodaysVerseReady() {
        log.debug("Checking if today's verse is ready");
        
        return hasVerseForDate(DailyDate.today());
    }
    
    @Override
    public long countAllVerses() {
        return jpaRepository.count();
    }
    
    @Override
    public long countActiveVerses() {
        return jpaRepository.countByIsActiveTrue();
    }
}