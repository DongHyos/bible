package com.dong.bible.application.service;

import com.dong.bible.application.dto.query.VerseSearchResultQuery;
import com.dong.bible.application.dto.query.EnhancedVerseSearchResultQuery;
import com.dong.bible.infrastructure.search.document.VerseSearchDocument;
import com.dong.bible.infrastructure.search.repository.VerseSearchRepository;
import com.dong.bible.infrastructure.search.dto.EnhancedSearchHit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 성경 구절 검색 Application Service
 * ElasticSearch를 활용한 검색 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VerseSearchApplicationService {
    
    private final VerseSearchRepository verseSearchRepository;
    
    /**
     * 구절 내용으로 검색
     */
    public List<VerseSearchResultQuery> searchByContent(String keyword) {
        log.info("구절 내용 검색 시작: keyword={}", keyword);
        
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("검색 키워드는 비어있을 수 없습니다");
        }
        
        List<VerseSearchDocument> searchResults = verseSearchRepository.findByContentContaining(keyword.trim());
        log.info("검색 결과: {}개", searchResults.size());
        
        return searchResults.stream()
                .map(VerseSearchResultQuery::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 책 이름으로 검색
     */
    public List<VerseSearchResultQuery> searchByBookName(String bookName) {
        log.info("책 이름 검색 시작: bookName={}", bookName);
        
        if (bookName == null || bookName.trim().isEmpty()) {
            throw new IllegalArgumentException("책 이름은 비어있을 수 없습니다");
        }
        
        List<VerseSearchDocument> searchResults = verseSearchRepository.findByBookName(bookName.trim());
        log.info("검색 결과: {}개", searchResults.size());
        
        return searchResults.stream()
                .map(VerseSearchResultQuery::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 특정 책의 특정 장에서 검색
     */
    public List<VerseSearchResultQuery> searchByBookAndChapter(Integer bookId, Integer chapter) {
        log.info("책별 장별 검색 시작: bookId={}, chapter={}", bookId, chapter);
        
        if (bookId == null || bookId <= 0) {
            throw new IllegalArgumentException("올바른 책 ID를 입력해주세요");
        }
        if (chapter == null || chapter <= 0) {
            throw new IllegalArgumentException("올바른 장 번호를 입력해주세요");
        }
        
        List<VerseSearchDocument> searchResults = verseSearchRepository.findByBookIdAndChapter(bookId, chapter);
        log.info("검색 결과: {}개", searchResults.size());
        
        return searchResults.stream()
                .map(VerseSearchResultQuery::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 구절 내용으로 검색 (페이징 지원)
     */
    public Page<VerseSearchResultQuery> searchByContentWithPaging(String keyword, Pageable pageable) {
        log.info("구절 내용 검색 (페이징) 시작: keyword={}, page={}, size={}", keyword, pageable.getPageNumber(), pageable.getPageSize());
        
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("검색 키워드는 비어있을 수 없습니다");
        }
        
        Page<VerseSearchDocument> searchResults = verseSearchRepository.findByContentContaining(keyword.trim(), pageable);
        log.info("검색 결과: 현재 페이지 {}개, 전체 {}개", searchResults.getNumberOfElements(), searchResults.getTotalElements());
        
        return searchResults.map(VerseSearchResultQuery::from);
    }
    
    /**
     * 책 이름으로 검색 (페이징 지원)
     */
    public Page<VerseSearchResultQuery> searchByBookNameWithPaging(String bookName, Pageable pageable) {
        log.info("책 이름 검색 (페이징) 시작: bookName={}, page={}, size={}", bookName, pageable.getPageNumber(), pageable.getPageSize());
        
        if (bookName == null || bookName.trim().isEmpty()) {
            throw new IllegalArgumentException("책 이름은 비어있을 수 없습니다");
        }
        
        Page<VerseSearchDocument> searchResults = verseSearchRepository.findByBookName(bookName.trim(), pageable);
        log.info("검색 결과: 현재 페이지 {}개, 전체 {}개", searchResults.getNumberOfElements(), searchResults.getTotalElements());
        
        return searchResults.map(VerseSearchResultQuery::from);
    }
    
    /**
     * 특정 책의 특정 장에서 검색 (페이징 지원)
     */
    public Page<VerseSearchResultQuery> searchByBookAndChapterWithPaging(Integer bookId, Integer chapter, Pageable pageable) {
        log.info("책별 장별 검색 (페이징) 시작: bookId={}, chapter={}, page={}, size={}", bookId, chapter, pageable.getPageNumber(), pageable.getPageSize());
        
        if (bookId == null || bookId <= 0) {
            throw new IllegalArgumentException("올바른 책 ID를 입력해주세요");
        }
        if (chapter == null || chapter <= 0) {
            throw new IllegalArgumentException("올바른 장 번호를 입력해주세요");
        }
        
        Page<VerseSearchDocument> searchResults = verseSearchRepository.findByBookIdAndChapter(bookId, chapter, pageable);
        log.info("검색 결과: 현재 페이지 {}개, 전체 {}개", searchResults.getNumberOfElements(), searchResults.getTotalElements());
        
        return searchResults.map(VerseSearchResultQuery::from);
    }
    
    // ========================================
    // Enhanced 검색 메서드 (하이라이팅 + 스코어링)
    // ========================================
    
    /**
     * Enhanced 구절 내용 검색 (하이라이팅 + 스코어링 지원)
     * 
     * @param keyword 검색 키워드
     * @return 하이라이팅과 스코어가 포함된 검색 결과
     */
    public List<EnhancedVerseSearchResultQuery> searchByContentEnhanced(String keyword) {
        log.info("Enhanced 구절 내용 검색 시작: keyword={}", keyword);
        
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("검색 키워드는 비어있을 수 없습니다");
        }
        
        // Enhanced Repository 메서드 사용 (하이라이팅 + 스코어링)
        List<EnhancedSearchHit> searchHits = verseSearchRepository.findByContentWithHighlight(keyword.trim());
        log.info("Enhanced 검색 결과: {}개", searchHits.size());
        
        return searchHits.stream()
                .map(hit -> convertToEnhancedDto(hit, keyword))
                .collect(Collectors.toList());
    }
    
    /**
     * Enhanced 구절 내용 검색 (페이징 + 하이라이팅 + 스코어링)
     * 
     * @param keyword 검색 키워드
     * @param pageable 페이징 정보
     * @return 페이징된 Enhanced 검색 결과
     */
    public Page<EnhancedVerseSearchResultQuery> searchByContentEnhancedWithPaging(String keyword, Pageable pageable) {
        log.info("Enhanced 구절 내용 검색 (페이징) 시작: keyword={}, page={}, size={}", 
                keyword, pageable.getPageNumber(), pageable.getPageSize());
        
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("검색 키워드는 비어있을 수 없습니다");
        }
        
        // Enhanced Repository 메서드 사용 (페이징 + 하이라이팅)
        Page<EnhancedSearchHit> searchHits = verseSearchRepository.findByContentWithHighlight(keyword.trim(), pageable);
        log.info("Enhanced 검색 결과: 현재 페이지 {}개, 전체 {}개", 
                searchHits.getNumberOfElements(), searchHits.getTotalElements());
        
        return searchHits.map(hit -> convertToEnhancedDto(hit, keyword));
    }
    
    /**
     * Enhanced 책 이름으로 검색 (하이라이팅 + 스코어링)
     * 
     * @param bookName 책 이름
     * @return 하이라이팅과 스코어가 포함된 검색 결과
     */
    public List<EnhancedVerseSearchResultQuery> searchByBookNameEnhanced(String bookName) {
        log.info("Enhanced 책 이름 검색 시작: bookName={}", bookName);
        
        if (bookName == null || bookName.trim().isEmpty()) {
            throw new IllegalArgumentException("책 이름은 비어있을 수 없습니다");
        }
        
        // TODO: Repository에 하이라이팅 지원 메서드 구현 필요
        List<VerseSearchDocument> searchResults = verseSearchRepository.findByBookName(bookName.trim());
        log.info("Enhanced 검색 결과: {}개", searchResults.size());
        
        return searchResults.stream()
                .map(doc -> 
                    EnhancedVerseSearchResultQuery.fromElasticSearchResult(
                        doc.getId(),
                        doc.getBookId(),
                        doc.getBookName(),
                        doc.getChapter(),
                        doc.getVerse(),
                        doc.getContent(),
                        doc.getDisplayReference(),
                        null, // 하이라이팅 조각들
                        1.0, // 임시 스코어
                        bookName
                    )
                )
                .collect(Collectors.toList());
    }
    
    /**
     * Enhanced 복합 검색 (내용 + 책 + 장 조합)
     * 
     * @param keyword 검색 키워드 (선택)
     * @param bookName 책 이름 (선택)
     * @param chapter 장 번호 (선택)
     * @param pageable 페이징 정보
     * @return 복합 조건 Enhanced 검색 결과
     */
    public Page<EnhancedVerseSearchResultQuery> searchByMultipleConditionsEnhanced(
            String keyword, String bookName, Integer chapter, Pageable pageable) {
        
        log.info("Enhanced 복합 검색 시작: keyword={}, bookName={}, chapter={}", 
                keyword, bookName, chapter);
        
        // 최소 하나의 검색 조건은 필요
        if ((keyword == null || keyword.trim().isEmpty()) &&
            (bookName == null || bookName.trim().isEmpty()) &&
            chapter == null) {
            throw new IllegalArgumentException("최소 하나의 검색 조건을 입력해주세요");
        }
        
        // TODO: Repository에 복합 검색 메서드 구현 필요
        // 임시로 키워드 검색만 수행
        if (keyword != null && !keyword.trim().isEmpty()) {
            return searchByContentEnhancedWithPaging(keyword, pageable);
        }
        
        // 빈 결과 반환
        return Page.empty(pageable);
    }
    
    /**
     * 검색 결과 정렬 옵션 적용
     * 
     * @param results Enhanced 검색 결과 리스트
     * @param sortBy 정렬 기준 ("score", "reference", "highlightCount")
     * @return 정렬된 검색 결과
     */
    public List<EnhancedVerseSearchResultQuery> sortEnhancedResults(
            List<EnhancedVerseSearchResultQuery> results, String sortBy) {
        
        if (results == null || results.isEmpty()) {
            return results;
        }
        
        return switch (sortBy != null ? sortBy.toLowerCase() : "score") {
            case "reference" -> results.stream()
                    .sorted((a, b) -> {
                        int bookCompare = a.getBookId().compareTo(b.getBookId());
                        if (bookCompare != 0) return bookCompare;
                        int chapterCompare = a.getChapter().compareTo(b.getChapter());
                        if (chapterCompare != 0) return chapterCompare;
                        return a.getVerse().compareTo(b.getVerse());
                    })
                    .collect(Collectors.toList());
            
            case "highlightcount" -> results.stream()
                    .sorted((a, b) -> b.getHighlightCount().compareTo(a.getHighlightCount()))
                    .collect(Collectors.toList());
            
            default -> // "score" 또는 기본값
                results.stream()
                    .sorted((a, b) -> b.getScore().compareTo(a.getScore()))
                    .collect(Collectors.toList());
        };
    }
    
    // ========================================
    // Private 헬퍼 메서드들
    // ========================================
    
    /**
     * EnhancedSearchHit을 EnhancedVerseSearchResultQuery로 변환
     */
    private EnhancedVerseSearchResultQuery convertToEnhancedDto(EnhancedSearchHit hit, String searchKeyword) {
        VerseSearchDocument doc = hit.getDocument();
        
        return EnhancedVerseSearchResultQuery.fromElasticSearchResult(
            doc.getId(),
            doc.getBookId(), 
            doc.getBookName(),
            doc.getChapter(),
            doc.getVerse(),
            doc.getContent(),
            doc.getDisplayReference(),
            hit.getContentHighlights(), // 실제 하이라이팅 조각들
            (double) hit.getScore(), // 실제 검색 점수
            searchKeyword
        );
    }
}