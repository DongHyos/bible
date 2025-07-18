package com.dong.bible.application.service;

import com.dong.bible.application.dto.SermonDetailDto;
import com.dong.bible.application.dto.SermonSummaryDto;
import com.dong.bible.domain.sermon.Sermon;
import com.dong.bible.domain.sermon.SermonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Sermon Application Service
 * 비즈니스 로직을 조합하고 트랜잭션을 관리합니다.
 * Application DTO를 반환하여 계층별 책임을 분리합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SermonApplicationService {
    
    private final SermonRepository sermonRepository;
    
    // === 조회 메서드들 ===
    
    /**
     * 설교 ID로 상세 조회
     * Use Case: 설교 상세 페이지 표시
     */
    public SermonDetailDto getSermonById(Long sermonId) {
        log.info("설교 상세 조회: ID={}", sermonId);
        
        if (sermonId == null) {
            throw new IllegalArgumentException("설교 ID는 null일 수 없습니다");
        }
        
        Sermon sermon = sermonRepository.getById(sermonId)
                .orElseThrow(() -> new IllegalArgumentException("설교를 찾을 수 없습니다: " + sermonId));
        
        return SermonDetailDto.from(sermon);
    }
    
    /**
     * 특정 구절과 관련된 설교 목록 조회
     * Use Case: 성경 구절 클릭 시 관련 설교 목록 표시
     */
    public List<SermonSummaryDto> getSermonsByVerse(Integer bookId, Short chapter, Short verse) {
        log.info("특정 구절 설교 조회: 책ID={}, 장={}, 절={}", bookId, chapter, verse);
        
        if (bookId == null || bookId <= 0) {
            throw new IllegalArgumentException("올바른 책 ID를 입력해주세요: " + bookId);
        }
        if (chapter == null || chapter <= 0) {
            throw new IllegalArgumentException("올바른 장 번호를 입력해주세요: " + chapter);
        }
        if (verse == null || verse <= 0) {
            throw new IllegalArgumentException("올바른 절 번호를 입력해주세요: " + verse);
        }
        
        List<Sermon> sermons = sermonRepository.findByVerse(bookId, chapter, verse);
        log.info("조회된 설교 수: {}", sermons.size());
        
        return sermons.stream()
                .map(SermonSummaryDto::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 설교자별 설교 목록 조회
     * Use Case: 설교자별 설교 목록 페이지 표시
     */
    public List<SermonSummaryDto> getSermonsByPastor(String pastorName) {
        log.info("설교자별 설교 조회: {}", pastorName);
        
        if (pastorName == null || pastorName.trim().isEmpty()) {
            throw new IllegalArgumentException("설교자명은 비어있을 수 없습니다");
        }
        
        List<Sermon> sermons = sermonRepository.findByPastorNameContaining(pastorName.trim());
        log.info("조회된 설교 수: {}", sermons.size());
        
        return sermons.stream()
                .map(SermonSummaryDto::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 교회별 설교 목록 조회
     * Use Case: 교회별 설교 목록 페이지 표시
     */
    public List<SermonSummaryDto> getSermonsByChurch(String churchName) {
        log.info("교회별 설교 조회: {}", churchName);
        
        if (churchName == null || churchName.trim().isEmpty()) {
            throw new IllegalArgumentException("교회명은 비어있을 수 없습니다");
        }
        
        List<Sermon> sermons = sermonRepository.findByChurchNameContaining(churchName.trim());
        log.info("조회된 설교 수: {}", sermons.size());
        
        return sermons.stream()
                .map(SermonSummaryDto::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 제목으로 설교 검색
     * Use Case: 설교 검색 결과 목록 표시
     */
    public List<SermonSummaryDto> searchSermonsByTitle(String title) {
        log.info("제목으로 설교 검색: {}", title);
        
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("검색할 제목은 비어있을 수 없습니다");
        }
        
        List<Sermon> sermons = sermonRepository.findByTitleContaining(title.trim());
        log.info("검색된 설교 수: {}", sermons.size());
        
        return sermons.stream()
                .map(SermonSummaryDto::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 인기 설교 목록 조회 (조회수 기준)
     * Use Case: 인기 설교 목록 페이지 표시
     */
    public List<SermonSummaryDto> getPopularSermons() {
        log.info("인기 설교 조회");
        
        List<Sermon> sermons = sermonRepository.findTopByViewCount(10);
        log.info("조회된 인기 설교 수: {}", sermons.size());
        
        return sermons.stream()
                .map(SermonSummaryDto::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 최신 설교 목록 조회
     * Use Case: 최신 설교 목록 페이지 표시
     */
    public List<SermonSummaryDto> getLatestSermons() {
        log.info("최신 설교 조회");
        
        List<Sermon> sermons = sermonRepository.findLatestSermons(10);
        log.info("조회된 최신 설교 수: {}", sermons.size());
        
        return sermons.stream()
                .map(SermonSummaryDto::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 조회수 증가
     * Use Case: 설교 상세 페이지 조회 시 호출
     */
    @Transactional
    public void incrementViewCount(Long sermonId) {
        log.info("설교 조회수 증가: ID={}", sermonId);
        
        if (sermonId == null) {
            throw new IllegalArgumentException("설교 ID는 null일 수 없습니다");
        }
        
        // 비즈니스 규칙: 존재하는 설교만 조회수 증가 가능
        if (!sermonRepository.exists(sermonId)) {
            throw new IllegalArgumentException("설교를 찾을 수 없습니다: " + sermonId);
        }
        
        try {
            sermonRepository.incrementViewCount(sermonId);
            log.info("조회수 증가 완료: ID={}", sermonId);
        } catch (UnsupportedOperationException e) {
            log.warn("조회수 증가 기능이 아직 구현되지 않았습니다: {}", e.getMessage());
        }
    }
}
