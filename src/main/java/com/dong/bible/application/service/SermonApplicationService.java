package com.dong.bible.application.service;

import com.dong.bible.application.dto.query.SermonDetailQuery;
import com.dong.bible.application.dto.query.SermonSummaryQuery;
import com.dong.bible.domain.sermon.Sermon;
import com.dong.bible.domain.sermon.SermonDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Sermon Application Service
 * Domain Service를 사용하여 비즈니스 로직을 처리하고 DTO로 변환합니다.
 * 트랜잭션 관리와 계층 간 데이터 변환을 담당합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SermonApplicationService {
    
    private final SermonDomainService sermonDomainService;
    
    // === 조회 메서드들 ===
    
    /**
     * 설교 ID로 상세 조회
     * Use Case: 설교 상세 페이지 표시
     */
    public SermonDetailQuery getSermonById(Long sermonId) {
        log.info("설교 상세 조회: ID={}", sermonId);
        
        Sermon sermon = sermonDomainService.getSermonById(sermonId)
                .orElseThrow(() -> new IllegalArgumentException("설교를 찾을 수 없습니다: " + sermonId));
        
        return SermonDetailQuery.from(sermon);
    }
    
    /**
     * 특정 구절과 관련된 설교 목록 조회
     * Use Case: 성경 구절 클릭 시 관련 설교 목록 표시
     */
    public List<SermonSummaryQuery> getSermonsByVerse(Integer bookId, Short chapter, Short verse) {
        log.info("특정 구절 설교 조회: 책ID={}, 장={}, 절={}", bookId, chapter, verse);
        
        List<Sermon> sermons = sermonDomainService.getSermonsByVerse(bookId, chapter, verse);
        log.info("조회된 설교 수: {}", sermons.size());
        
        return sermons.stream()
                .map(SermonSummaryQuery::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 설교자별 설교 목록 조회
     * Use Case: 설교자별 설교 목록 페이지 표시
     */
    public List<SermonSummaryQuery> getSermonsByPastor(String pastorName) {
        log.info("설교자별 설교 조회: {}", pastorName);
        
        List<Sermon> sermons = sermonDomainService.getSermonsByPastor(pastorName);
        log.info("조회된 설교 수: {}", sermons.size());
        
        return sermons.stream()
                .map(SermonSummaryQuery::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 교회별 설교 목록 조회
     * Use Case: 교회별 설교 목록 페이지 표시
     */
    public List<SermonSummaryQuery> getSermonsByChurch(String churchName) {
        log.info("교회별 설교 조회: {}", churchName);
        
        List<Sermon> sermons = sermonDomainService.getSermonsByChurch(churchName);
        log.info("조회된 설교 수: {}", sermons.size());
        
        return sermons.stream()
                .map(SermonSummaryQuery::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 제목으로 설교 검색
     * Use Case: 설교 검색 결과 목록 표시
     */
    public List<SermonSummaryQuery> searchSermonsByTitle(String title) {
        log.info("제목으로 설교 검색: {}", title);
        
        List<Sermon> sermons = sermonDomainService.searchSermonsByTitle(title);
        log.info("검색된 설교 수: {}", sermons.size());
        
        return sermons.stream()
                .map(SermonSummaryQuery::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 인기 설교 목록 조회 (조회수 기준)
     * Use Case: 인기 설교 목록 페이지 표시
     */
    public List<SermonSummaryQuery> getPopularSermons() {
        log.info("인기 설교 조회");
        
        List<Sermon> popularSermons = sermonDomainService.getPopularSermons();
        log.info("조회된 인기 설교 수: {}", popularSermons.size());
        
        return popularSermons.stream()
                .map(SermonSummaryQuery::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 최신 설교 목록 조회
     * Use Case: 최신 설교 목록 페이지 표시
     */
    public List<SermonSummaryQuery> getLatestSermons() {
        log.info("최신 설교 조회");
        
        List<Sermon> latestSermons = sermonDomainService.getLatestSermons();
        log.info("조회된 최신 설교 수: {}", latestSermons.size());
        
        return latestSermons.stream()
                .map(SermonSummaryQuery::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 조회수 증가
     * Use Case: 설교 상세 페이지 조회 시 호출
     */
    @Transactional
    public void incrementViewCount(Long sermonId) {
        log.info("설교 조회수 증가: ID={}", sermonId);
        
        sermonDomainService.incrementViewCount(sermonId);
        log.info("조회수 증가 완료: ID={}", sermonId);
    }
    
    /**
     * 관련 설교 추천 조회
     * Use Case: 설교 상세 페이지에서 관련 설교 추천
     */
    public List<SermonSummaryQuery> getRelatedSermons(Long baseSermonId) {
        log.info("관련 설교 추천 조회: baseSermonId={}", baseSermonId);
        
        List<Sermon> relatedSermons = sermonDomainService.getRelatedSermons(baseSermonId);
        log.info("조회된 관련 설교 수: {}", relatedSermons.size());
        
        return relatedSermons.stream()
                .map(SermonSummaryQuery::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 트렌딩 설교 추천 조회
     * Use Case: 트렌딩 설교 목록 페이지 표시
     */
    public List<SermonSummaryQuery> getTrendingSermons() {
        log.info("트렌딩 설교 조회");
        
        List<Sermon> trendingSermons = sermonDomainService.getTrendingSermons();
        log.info("조회된 트렌딩 설교 수: {}", trendingSermons.size());
        
        return trendingSermons.stream()
                .map(SermonSummaryQuery::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 베스트 설교 추천 조회 (종합 점수 기준)
     * Use Case: 베스트 설교 목록 페이지 표시
     */
    public List<SermonSummaryQuery> getBestSermons() {
        log.info("베스트 설교 조회");
        
        List<Sermon> bestSermons = sermonDomainService.getBestSermons();
        log.info("조회된 베스트 설교 수: {}", bestSermons.size());
        
        return bestSermons.stream()
                .map(SermonSummaryQuery::from)
                .collect(Collectors.toList());
    }
}
