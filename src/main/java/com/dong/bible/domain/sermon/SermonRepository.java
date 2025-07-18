package com.dong.bible.domain.sermon;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Sermon Aggregate의 Repository 인터페이스 (Domain Layer)
 * 순수한 도메인 개념으로 설계되었으며, Infrastructure에서 구현합니다.
 */
public interface SermonRepository {
    
    // === 조회 메서드들 ===
    
    /**
     * ID로 설교 조회
     */
    Optional<Sermon> getById(Long id);
    
    /**
     * 특정 구절과 관련된 설교 조회
     */
    List<Sermon> findByVerse(Integer bookId, Short chapter, Short verse);
    
    /**
     * 설교자별 설교 조회 (이름 포함 검색)
     */
    List<Sermon> findByPastorNameContaining(String pastorName);
    
    /**
     * 교회별 설교 조회 (이름 포함 검색)
     */
    List<Sermon> findByChurchNameContaining(String churchName);
    
    /**
     * 제목으로 설교 검색 (제목 포함 검색)
     */
    List<Sermon> findByTitleContaining(String title);
    
    /**
     * 인기 설교 조회 (조회수 기준 상위 N개)
     */
    List<Sermon> findTopByViewCount(int limit);
    
    /**
     * 최신 설교 조회 (날짜 기준 최신 N개)
     */
    List<Sermon> findLatestSermons(int limit);
    
    /**
     * 특정 기간의 설교 조회
     */
    List<Sermon> findByDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * 특정 연도의 설교 조회
     */
    List<Sermon> findByYear(int year);
    
    /**
     * 특정 월의 설교 조회
     */
    List<Sermon> findByYearAndMonth(int year, int month);
    
    /**
     * 태그로 설교 검색
     */
    List<Sermon> findByTag(String tag);
    
    /**
     * 여러 태그 중 하나라도 포함하는 설교 검색
     */
    List<Sermon> findByTagsContaining(List<String> tags);
    
    /**
     * 조회수가 특정 수치 이상인 설교 조회
     */
    List<Sermon> findByViewCountGreaterThan(int viewCount);
    
    /**
     * 유튜브 영상이 있는 설교만 조회
     */
    List<Sermon> findWithYoutubeVideo();
    
    /**
     * 특정 설교자의 최신 설교 조회
     */
    List<Sermon> findLatestByPastor(String pastorName, int limit);
    
    /**
     * 특정 교회의 최신 설교 조회
     */
    List<Sermon> findLatestByChurch(String churchName, int limit);
    
    // === 저장 및 삭제 메서드들 ===
    
    /**
     * 설교 저장 (생성 또는 수정)
     */
    Sermon store(Sermon sermon);
    
    /**
     * 설교 삭제
     */
    void remove(Sermon sermon);
    
    /**
     * ID로 설교 삭제
     */
    void removeById(Long id);
    
    // === 존재 여부 확인 ===
    
    /**
     * ID로 존재 여부 확인
     */
    boolean exists(Long id);
    
    /**
     * 특정 제목과 날짜의 설교가 존재하는지 확인
     */
    boolean existsSermon(String title, LocalDate sermonDate);
    
    // === 통계 메서드들 ===
    
    /**
     * 전체 설교 개수
     */
    long getTotalCount();
    
    /**
     * 특정 설교자의 설교 개수
     */
    long countByPastor(String pastorName);
    
    /**
     * 특정 교회의 설교 개수
     */
    long countByChurch(String churchName);
    
    /**
     * 특정 연도의 설교 개수
     */
    long countByYear(int year);
    
    /**
     * 유튜브 영상이 있는 설교 개수
     */
    long countWithYoutubeVideo();
    
    // === 비즈니스 특화 메서드들 ===
    
    /**
     * 설교 조회수 증가 (원자적 연산)
     */
    void incrementViewCount(Long sermonId);
    
    /**
     * 설교 좋아요 증가 (원자적 연산)
     */
    void incrementLikeCount(Long sermonId);
    
    /**
     * 설교 좋아요 감소 (원자적 연산)
     */
    void decrementLikeCount(Long sermonId);
    
    /**
     * 인기 급상승 설교 조회 (최근 N일간 조회수 증가율 기준)
     */
    List<Sermon> findTrendingSermons(int days, int limit);
    
    /**
     * 추천 설교 조회 (좋아요 비율 기준)
     */
    List<Sermon> findRecommendedSermons(int limit);
    
    /**
     * 관련 설교 조회 (같은 구절 또는 태그 기준)
     */
    List<Sermon> findRelatedSermons(Sermon sermon, int limit);
    
    /**
     * 설교자의 대표 설교 조회 (조회수 기준)
     */
    List<Sermon> findRepresentativeSermons(String pastorName, int limit);
}