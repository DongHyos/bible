package com.dong.bible.infrastructure.persistence.jpa;

import com.dong.bible.infrastructure.persistence.entity.DailyVerse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyVerseRepository extends JpaRepository<DailyVerse, Long> {

    // 특정 날짜의 오늘의 말씀 조회 (활성화된 것만)
    Optional<DailyVerse> findByVerseDateAndIsActiveTrue(LocalDate verseDate);

    // 오늘의 말씀 조회 (활성화된 것만)
    @Query("SELECT dv FROM DailyVerse dv " +
           "JOIN FETCH dv.book " +
           "WHERE dv.verseDate = CURRENT_DATE " +
           "AND dv.isActive = true")
    Optional<DailyVerse> findTodayVerse();

    // 특정 날짜의 오늘의 말씀 조회 (책 정보 포함)
    @Query("SELECT dv FROM DailyVerse dv " +
           "JOIN FETCH dv.book " +
           "WHERE dv.verseDate = :date " +
           "AND dv.isActive = true")
    Optional<DailyVerse> findByDateWithBook(@Param("date") LocalDate date);

    // 최근 N일간의 오늘의 말씀 조회
    @Query("SELECT dv FROM DailyVerse dv " +
           "JOIN FETCH dv.book " +
           "WHERE dv.verseDate >= :startDate " +
           "AND dv.verseDate <= :endDate " +
           "AND dv.isActive = true " +
           "ORDER BY dv.verseDate DESC")
    List<DailyVerse> findRecentVerses(@Param("startDate") LocalDate startDate, 
                                     @Param("endDate") LocalDate endDate);

    // 앞으로 N일간의 오늘의 말씀 조회
    @Query("SELECT dv FROM DailyVerse dv " +
           "JOIN FETCH dv.book " +
           "WHERE dv.verseDate >= :startDate " +
           "AND dv.verseDate <= :endDate " +
           "AND dv.isActive = true " +
           "ORDER BY dv.verseDate ASC")
    List<DailyVerse> findUpcomingVerses(@Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);

    // 모든 활성화된 오늘의 말씀 조회 (최신순)
    List<DailyVerse> findByIsActiveTrueOrderByVerseDateDesc();

    // 특정 월의 오늘의 말씀 조회
    @Query("SELECT dv FROM DailyVerse dv " +
           "JOIN FETCH dv.book " +
           "WHERE YEAR(dv.verseDate) = :year " +
           "AND MONTH(dv.verseDate) = :month " +
           "AND dv.isActive = true " +
           "ORDER BY dv.verseDate ASC")
    List<DailyVerse> findByYearAndMonth(@Param("year") int year, @Param("month") int month);
}