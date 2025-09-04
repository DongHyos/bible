package com.dong.bible.web.controller;

import com.dong.bible.application.dto.query.VerseSearchResultQuery;
import com.dong.bible.application.dto.query.EnhancedVerseSearchResultQuery;
import com.dong.bible.application.service.VerseSearchApplicationService;
import com.dong.bible.common.response.AppResponse;
import com.dong.bible.web.dto.response.VerseSearchResponse;
import com.dong.bible.web.dto.response.EnhancedVerseSearchResponse;
import com.dong.bible.web.mapper.VerseSearchResponseMapper;
import com.dong.bible.web.mapper.EnhancedVerseSearchResponseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

/**
 * 성경 구절 검색 API Controller
 * ElasticSearch를 활용한 검색 엔드포인트 제공
 */
@RestController
@RequestMapping("/api/verses/search")
@RequiredArgsConstructor
@Slf4j
public class VerseSearchController {
    
    private final VerseSearchApplicationService verseSearchApplicationService;
    private final VerseSearchResponseMapper verseSearchResponseMapper;
    private final EnhancedVerseSearchResponseMapper enhancedVerseSearchResponseMapper;
    
    /**
     * 구절 내용으로 검색
     * GET /api/verses/search?keyword=사랑
     */
    @GetMapping
    public AppResponse<List<VerseSearchResponse>> searchByContent(
            @RequestParam String keyword) {
        
        log.info("구절 내용 검색 API 호출: keyword={}", keyword);
        
        List<VerseSearchResultQuery> searchResults = verseSearchApplicationService.searchByContent(keyword);
        List<VerseSearchResponse> responses = verseSearchResponseMapper.toResponseList(searchResults);
        
        log.info("검색 API 응답: {}개 결과 반환", responses.size());
        return AppResponse.ok(responses);
    }
    
    /**
     * 구절 내용으로 검색 (페이징 지원)
     * GET /api/verses/search/paged?keyword=사랑&page=0&size=10
     */
    @GetMapping("/paged")
    public AppResponse<List<VerseSearchResponse>> searchByContentWithPaging(
            @RequestParam String keyword,
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        
        log.info("구절 내용 검색 (페이징) API 호출: keyword={}, page={}, size={}", keyword, pageable.getPageNumber(), pageable.getPageSize());
        
        Page<VerseSearchResultQuery> searchResults = verseSearchApplicationService.searchByContentWithPaging(keyword, pageable);
        List<VerseSearchResponse> responses = verseSearchResponseMapper.toResponseList(searchResults.getContent());
        
        log.info("검색 (페이징) API 응답: {}개 결과 반환, 전체 {}개", responses.size(), searchResults.getTotalElements());
        return AppResponse.ok(searchResults, responses);
    }
    
    /**
     * 책 이름으로 검색
     * GET /api/verses/search/book/{bookName}
     */
    @GetMapping("/book/{bookName}")
    public AppResponse<List<VerseSearchResponse>> searchByBookName(
            @PathVariable String bookName) {
        
        log.info("책별 검색 API 호출: bookName={}", bookName);
        
        List<VerseSearchResultQuery> searchResults = verseSearchApplicationService.searchByBookName(bookName);
        List<VerseSearchResponse> responses = verseSearchResponseMapper.toResponseList(searchResults);
        
        log.info("책별 검색 API 응답: {}개 결과 반환", responses.size());
        return AppResponse.ok(responses);
    }
    
    /**
     * 특정 책의 특정 장에서 검색
     * GET /api/verses/search/chapter?bookId=1&chapter=1
     */
    @GetMapping("/chapter")
    public AppResponse<List<VerseSearchResponse>> searchByBookAndChapter(
            @RequestParam Integer bookId,
            @RequestParam Integer chapter) {
        
        log.info("장별 검색 API 호출: bookId={}, chapter={}", bookId, chapter);
        
        List<VerseSearchResultQuery> searchResults = verseSearchApplicationService.searchByBookAndChapter(bookId, chapter);
        List<VerseSearchResponse> responses = verseSearchResponseMapper.toResponseList(searchResults);
        
        log.info("장별 검색 API 응답: {}개 결과 반환", responses.size());
        return AppResponse.ok(responses);
    }
    
    // ========================================
    // Enhanced 검색 API (하이라이팅 + 스코어링)
    // ========================================
    
    /**
     * Enhanced 구절 내용 검색 (하이라이팅 + 스코어링)
     * GET /api/verses/search/enhanced?keyword=사랑&sortBy=score
     */
    @GetMapping("/enhanced")
    public AppResponse<List<EnhancedVerseSearchResponse>> searchByContentEnhanced(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "score") String sortBy,
            @RequestParam(defaultValue = "false") boolean useSynonyms) {
        
        long startTime = System.currentTimeMillis();
        log.info("Enhanced 구절 내용 검색 API 호출: keyword={}, sortBy={}, useSynonyms={}", keyword, sortBy, useSynonyms);
        
        // Enhanced 검색 실행
        List<EnhancedVerseSearchResultQuery> searchResults = verseSearchApplicationService.searchByContentEnhanced(keyword);
        
        // 정렬 적용
        List<EnhancedVerseSearchResultQuery> sortedResults = verseSearchApplicationService.sortEnhancedResults(searchResults, sortBy);
        
        // 응답 변환
        long processingTime = System.currentTimeMillis() - startTime;
        EnhancedVerseSearchResponse.SearchOptions searchOptions = enhancedVerseSearchResponseMapper.createSearchOptions(
                useSynonyms, sortBy, true, 150, 3);
        
        List<EnhancedVerseSearchResponse> responses = enhancedVerseSearchResponseMapper.toResponseList(
                sortedResults, "content", processingTime, searchOptions);
        
        log.info("Enhanced 검색 API 응답: {}개 결과 반환, 처리시간={}ms", responses.size(), processingTime);
        return AppResponse.ok(responses);
    }
    
    /**
     * Enhanced 구절 내용 검색 (페이징 + 하이라이팅 + 스코어링)
     * GET /api/verses/search/enhanced/paged?keyword=사랑&page=0&size=10&sortBy=score
     */
    @GetMapping("/enhanced/paged")
    public AppResponse<List<EnhancedVerseSearchResponse>> searchByContentEnhancedWithPaging(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "score") String sortBy,
            @RequestParam(defaultValue = "false") boolean useSynonyms,
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        
        long startTime = System.currentTimeMillis();
        log.info("Enhanced 구절 내용 검색 (페이징) API 호출: keyword={}, sortBy={}, useSynonyms={}, page={}, size={}", 
                keyword, sortBy, useSynonyms, pageable.getPageNumber(), pageable.getPageSize());
        
        // Enhanced 페이징 검색 실행
        Page<EnhancedVerseSearchResultQuery> searchResults = verseSearchApplicationService
                .searchByContentEnhancedWithPaging(keyword, pageable);
        
        // 응답 변환
        long processingTime = System.currentTimeMillis() - startTime;
        EnhancedVerseSearchResponse.SearchOptions searchOptions = enhancedVerseSearchResponseMapper.createSearchOptions(
                useSynonyms, sortBy, true, 150, 3);
        
        List<EnhancedVerseSearchResponse> responses = enhancedVerseSearchResponseMapper.toResponseList(
                searchResults.getContent(), "content", processingTime, searchOptions);
        
        log.info("Enhanced 검색 (페이징) API 응답: {}개 결과 반환, 전체 {}개, 처리시간={}ms", 
                responses.size(), searchResults.getTotalElements(), processingTime);
        return AppResponse.ok(searchResults, responses);
    }
    
    /**
     * Enhanced 책 이름으로 검색 (하이라이팅 + 스코어링)
     * GET /api/verses/search/enhanced/book/{bookName}?sortBy=score
     */
    @GetMapping("/enhanced/book/{bookName}")
    public AppResponse<List<EnhancedVerseSearchResponse>> searchByBookNameEnhanced(
            @PathVariable String bookName,
            @RequestParam(defaultValue = "score") String sortBy) {
        
        long startTime = System.currentTimeMillis();
        log.info("Enhanced 책별 검색 API 호출: bookName={}, sortBy={}", bookName, sortBy);
        
        // Enhanced 책별 검색 실행
        List<EnhancedVerseSearchResultQuery> searchResults = verseSearchApplicationService.searchByBookNameEnhanced(bookName);
        
        // 정렬 적용
        List<EnhancedVerseSearchResultQuery> sortedResults = verseSearchApplicationService.sortEnhancedResults(searchResults, sortBy);
        
        // 응답 변환
        long processingTime = System.currentTimeMillis() - startTime;
        EnhancedVerseSearchResponse.SearchOptions searchOptions = enhancedVerseSearchResponseMapper.createDefaultSearchOptions();
        
        List<EnhancedVerseSearchResponse> responses = enhancedVerseSearchResponseMapper.toResponseList(
                sortedResults, "bookName", processingTime, searchOptions);
        
        log.info("Enhanced 책별 검색 API 응답: {}개 결과 반환, 처리시간={}ms", responses.size(), processingTime);
        return AppResponse.ok(responses);
    }
    
    /**
     * Enhanced 복합 조건 검색 (내용 + 책 + 장)
     * GET /api/verses/search/enhanced/advanced?keyword=사랑&bookName=요한복음&chapter=3&page=0&size=10
     */
    @GetMapping("/enhanced/advanced")
    public AppResponse<List<EnhancedVerseSearchResponse>> searchByMultipleConditionsEnhanced(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String bookName,
            @RequestParam(required = false) Integer chapter,
            @RequestParam(defaultValue = "score") String sortBy,
            @RequestParam(defaultValue = "false") boolean useSynonyms,
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        
        long startTime = System.currentTimeMillis();
        log.info("Enhanced 복합 검색 API 호출: keyword={}, bookName={}, chapter={}, sortBy={}, useSynonyms={}", 
                keyword, bookName, chapter, sortBy, useSynonyms);
        
        // Enhanced 복합 검색 실행
        Page<EnhancedVerseSearchResultQuery> searchResults = verseSearchApplicationService
                .searchByMultipleConditionsEnhanced(keyword, bookName, chapter, pageable);
        
        // 응답 변환
        long processingTime = System.currentTimeMillis() - startTime;
        EnhancedVerseSearchResponse.SearchOptions searchOptions = enhancedVerseSearchResponseMapper.createSearchOptions(
                useSynonyms, sortBy, true, 150, 3);
        
        List<EnhancedVerseSearchResponse> responses = enhancedVerseSearchResponseMapper.toResponseList(
                searchResults.getContent(), "advanced", processingTime, searchOptions);
        
        log.info("Enhanced 복합 검색 API 응답: {}개 결과 반환, 전체 {}개, 처리시간={}ms", 
                responses.size(), searchResults.getTotalElements(), processingTime);
        return AppResponse.ok(searchResults, responses);
    }
    
    /**
     * Enhanced 동의어 검색 (동의어 확장 검색)
     * GET /api/verses/search/enhanced/synonyms?keyword=하나님&page=0&size=10&sortBy=score
     */
    @GetMapping("/enhanced/synonyms")
    public AppResponse<List<EnhancedVerseSearchResponse>> searchWithSynonyms(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "score") String sortBy,
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        
        long startTime = System.currentTimeMillis();
        log.info("Enhanced 동의어 검색 API 호출: keyword={}, sortBy={}", keyword, sortBy);
        
        // TODO: 향후 Repository에 동의어 검색 메서드 구현 시 사용
        // 현재는 일반 Enhanced 검색으로 대체
        Page<EnhancedVerseSearchResultQuery> searchResults = verseSearchApplicationService
                .searchByContentEnhancedWithPaging(keyword, pageable);
        
        // 응답 변환
        long processingTime = System.currentTimeMillis() - startTime;
        EnhancedVerseSearchResponse.SearchOptions searchOptions = enhancedVerseSearchResponseMapper.createSearchOptions(
                true, sortBy, true, 150, 3); // useSynonyms = true
        
        List<EnhancedVerseSearchResponse> responses = enhancedVerseSearchResponseMapper.toResponseList(
                searchResults.getContent(), "synonyms", processingTime, searchOptions);
        
        log.info("Enhanced 동의어 검색 API 응답: {}개 결과 반환, 전체 {}개, 처리시간={}ms", 
                responses.size(), searchResults.getTotalElements(), processingTime);
        return AppResponse.ok(searchResults, responses);
    }
}