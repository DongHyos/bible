package com.dong.bible.domain.sermon;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Sermon Domain Service
 * Sermon 도메인의 모든 비즈니스 로직을 담당합니다.
 * 순수한 Domain 객체들만 다루고, Repository를 통해 데이터에 접근합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SermonDomainService {
    
    private final SermonRepository sermonRepository;
    
    // === 조회 메서드들 ===
    
    /**
     * 설교 ID로 상세 조회
     */
    public Optional<Sermon> getSermonById(Long sermonId) {
        log.debug("Domain: 설교 상세 조회 - ID={}", sermonId);
        
        if (sermonId == null) {
            throw new IllegalArgumentException("설교 ID는 null일 수 없습니다");
        }
        
        return sermonRepository.getById(sermonId);
    }
    
    /**
     * 특정 구절과 관련된 설교 목록 조회
     */
    public List<Sermon> getSermonsByVerse(Integer bookId, Short chapter, Short verse) {
        log.debug("Domain: 특정 구절 설교 조회 - 책ID={}, 장={}, 절={}", bookId, chapter, verse);
        
        validateVerseParameters(bookId, chapter, verse);
        
        return sermonRepository.findByVerse(bookId, chapter, verse);
    }
    
    /**
     * 설교자별 설교 목록 조회
     */
    public List<Sermon> getSermonsByPastor(String pastorName) {
        log.debug("Domain: 설교자별 설교 조회 - {}", pastorName);
        
        validatePastorName(pastorName);
        
        return sermonRepository.findByPastorNameContaining(pastorName.trim());
    }
    
    /**
     * 교회별 설교 목록 조회
     */
    public List<Sermon> getSermonsByChurch(String churchName) {
        log.debug("Domain: 교회별 설교 조회 - {}", churchName);
        
        validateChurchName(churchName);
        
        return sermonRepository.findByChurchNameContaining(churchName.trim());
    }
    
    /**
     * 제목으로 설교 검색
     */
    public List<Sermon> searchSermonsByTitle(String title) {
        log.debug("Domain: 제목으로 설교 검색 - {}", title);
        
        validateTitle(title);
        
        return sermonRepository.findByTitleContaining(title.trim());
    }
    
    /**
     * 인기 설교 목록 조회 (조회수 기준)
     */
    public List<Sermon> getPopularSermons() {
        log.debug("Domain: 인기 설교 조회");
        
        return sermonRepository.findTopByViewCount(10);
    }
    
    /**
     * 최신 설교 목록 조회
     */
    public List<Sermon> getLatestSermons() {
        log.debug("Domain: 최신 설교 조회");
        
        return sermonRepository.findLatestSermons(10);
    }
    
    /**
     * 관련 설교 추천 조회
     */
    public List<Sermon> getRelatedSermons(Long baseSermonId) {
        log.debug("Domain: 관련 설교 추천 조회 - baseSermonId={}", baseSermonId);
        
        if (baseSermonId == null) {
            throw new IllegalArgumentException("기준 설교 ID는 null일 수 없습니다");
        }
        
        Sermon baseSermon = sermonRepository.getById(baseSermonId)
                .orElseThrow(() -> new IllegalArgumentException("기준 설교를 찾을 수 없습니다: " + baseSermonId));
        
        return sermonRepository.findRelatedSermons(baseSermon, 5);
    }
    
    /**
     * 트렌딩 설교 추천 조회
     */
    public List<Sermon> getTrendingSermons() {
        log.debug("Domain: 트렌딩 설교 조회");
        
        return sermonRepository.findTrendingSermons(30, 10);
    }
    
    /**
     * 베스트 설교 추천 조회 (종합 점수 기준)
     */
    public List<Sermon> getBestSermons() {
        log.debug("Domain: 베스트 설교 조회");
        
        return sermonRepository.findRecommendedSermons(10);
    }
    
    /**
     * 특정 기간의 설교 조회
     */
    public List<Sermon> getSermonsByDateRange(LocalDate startDate, LocalDate endDate) {
        log.debug("Domain: 특정 기간 설교 조회 - {} ~ {}", startDate, endDate);
        
        validateDateRange(startDate, endDate);
        
        return sermonRepository.findByDateRange(startDate, endDate);
    }
    
    /**
     * 특정 연도의 설교 조회
     */
    public List<Sermon> getSermonsByYear(int year) {
        log.debug("Domain: 특정 연도 설교 조회 - {}", year);
        
        validateYear(year);
        
        return sermonRepository.findByYear(year);
    }
    
    /**
     * 태그로 설교 검색
     */
    public List<Sermon> getSermonsByTag(String tag) {
        log.debug("Domain: 태그로 설교 검색 - {}", tag);
        
        validateTag(tag);
        
        return sermonRepository.findByTag(tag.trim());
    }
    
    /**
     * 유튜브 영상이 있는 설교만 조회
     */
    public List<Sermon> getSermonsWithYoutubeVideo() {
        log.debug("Domain: 유튜브 영상이 있는 설교 조회");
        
        return sermonRepository.findWithYoutubeVideo();
    }
    
    /**
     * 특정 설교자의 최신 설교 조회
     */
    public List<Sermon> getLatestSermonsByPastor(String pastorName, int limit) {
        log.debug("Domain: 특정 설교자의 최신 설교 조회 - {}, limit={}", pastorName, limit);
        
        validatePastorName(pastorName);
        validateLimit(limit);
        
        return sermonRepository.findLatestByPastor(pastorName.trim(), limit);
    }
    
    // === 명령 메서드들 ===
    
    /**
     * 설교 저장
     */
    public Sermon saveSermon(Sermon sermon) {
        if (sermon == null) {
            throw new IllegalArgumentException("설교 정보는 null일 수 없습니다");
        }
        
        log.debug("Domain: 설교 저장 - {}", sermon.getId());
        
        return sermonRepository.store(sermon);
    }
    
    /**
     * 설교 삭제
     */
    public void deleteSermon(Long sermonId) {
        log.debug("Domain: 설교 삭제 - ID={}", sermonId);
        
        if (sermonId == null) {
            throw new IllegalArgumentException("설교 ID는 null일 수 없습니다");
        }
        
        if (!sermonRepository.exists(sermonId)) {
            throw new IllegalArgumentException("설교를 찾을 수 없습니다: " + sermonId);
        }
        
        sermonRepository.removeById(sermonId);
    }
    
    /**
     * 조회수 증가
     */
    public void incrementViewCount(Long sermonId) {
        log.debug("Domain: 설교 조회수 증가 - ID={}", sermonId);
        
        if (sermonId == null) {
            throw new IllegalArgumentException("설교 ID는 null일 수 없습니다");
        }
        
        if (!sermonRepository.exists(sermonId)) {
            throw new IllegalArgumentException("설교를 찾을 수 없습니다: " + sermonId);
        }
        
        try {
            sermonRepository.incrementViewCount(sermonId);
        } catch (UnsupportedOperationException e) {
            log.warn("조회수 증가 기능이 아직 구현되지 않았습니다: {}", e.getMessage());
        }
    }
    
    // === 통계 메서드들 ===
    
    /**
     * 전체 설교 개수
     */
    public long getTotalSermonCount() {
        log.debug("Domain: 전체 설교 개수 조회");
        
        return sermonRepository.getTotalCount();
    }
    
    /**
     * 특정 설교자의 설교 개수
     */
    public long getSermonCountByPastor(String pastorName) {
        log.debug("Domain: 특정 설교자의 설교 개수 조회 - {}", pastorName);
        
        validatePastorName(pastorName);
        
        return sermonRepository.countByPastor(pastorName.trim());
    }
    
    /**
     * 특정 교회의 설교 개수
     */
    public long getSermonCountByChurch(String churchName) {
        log.debug("Domain: 특정 교회의 설교 개수 조회 - {}", churchName);
        
        validateChurchName(churchName);
        
        return sermonRepository.countByChurch(churchName.trim());
    }
    
    // === 유효성 검증 메서드들 ===
    
    private void validateVerseParameters(Integer bookId, Short chapter, Short verse) {
        if (bookId == null || bookId <= 0) {
            throw new IllegalArgumentException("올바른 책 ID를 입력해주세요: " + bookId);
        }
        if (chapter == null || chapter <= 0) {
            throw new IllegalArgumentException("올바른 장 번호를 입력해주세요: " + chapter);
        }
        if (verse == null || verse <= 0) {
            throw new IllegalArgumentException("올바른 절 번호를 입력해주세요: " + verse);
        }
    }
    
    private void validatePastorName(String pastorName) {
        if (pastorName == null || pastorName.trim().isEmpty()) {
            throw new IllegalArgumentException("설교자명은 비어있을 수 없습니다");
        }
    }
    
    private void validateChurchName(String churchName) {
        if (churchName == null || churchName.trim().isEmpty()) {
            throw new IllegalArgumentException("교회명은 비어있을 수 없습니다");
        }
    }
    
    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("검색할 제목은 비어있을 수 없습니다");
        }
    }
    
    private void validateTag(String tag) {
        if (tag == null || tag.trim().isEmpty()) {
            throw new IllegalArgumentException("태그는 비어있을 수 없습니다");
        }
    }
    
    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("시작일과 종료일은 null일 수 없습니다");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("시작일은 종료일보다 이후일 수 없습니다");
        }
    }
    
    private void validateYear(int year) {
        if (year < 1900 || year > LocalDate.now().getYear() + 1) {
            throw new IllegalArgumentException("올바른 연도를 입력해주세요: " + year);
        }
    }
    
    private void validateLimit(int limit) {
        if (limit <= 0 || limit > 100) {
            throw new IllegalArgumentException("조회 제한 개수는 1-100 사이여야 합니다: " + limit);
        }
    }
}