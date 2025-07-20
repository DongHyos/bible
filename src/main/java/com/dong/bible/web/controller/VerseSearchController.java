package com.dong.bible.web.controller;

import com.dong.bible.application.dto.VerseSearchResultDto;
import com.dong.bible.application.service.VerseSearchApplicationService;
import com.dong.bible.web.dto.response.VerseSearchResponse;
import com.dong.bible.web.mapper.VerseSearchResponseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    
    /**
     * 구절 내용으로 검색
     * GET /api/verses/search?keyword=사랑
     */
    @GetMapping
    public ResponseEntity<List<VerseSearchResponse>> searchByContent(
            @RequestParam String keyword) {
        
        log.info("구절 내용 검색 API 호출: keyword={}", keyword);
        
        List<VerseSearchResultDto> searchResults = verseSearchApplicationService.searchByContent(keyword);
        List<VerseSearchResponse> responses = verseSearchResponseMapper.toResponseList(searchResults);
        
        log.info("검색 API 응답: {}개 결과 반환", responses.size());
        return ResponseEntity.ok(responses);
    }
    
    /**
     * 책 이름으로 검색
     * GET /api/verses/search/book/{bookName}
     */
    @GetMapping("/book/{bookName}")
    public ResponseEntity<List<VerseSearchResponse>> searchByBookName(
            @PathVariable String bookName) {
        
        log.info("책별 검색 API 호출: bookName={}", bookName);
        
        List<VerseSearchResultDto> searchResults = verseSearchApplicationService.searchByBookName(bookName);
        List<VerseSearchResponse> responses = verseSearchResponseMapper.toResponseList(searchResults);
        
        log.info("책별 검색 API 응답: {}개 결과 반환", responses.size());
        return ResponseEntity.ok(responses);
    }
    
    /**
     * 특정 책의 특정 장에서 검색
     * GET /api/verses/search/chapter?bookId=1&chapter=1
     */
    @GetMapping("/chapter")
    public ResponseEntity<List<VerseSearchResponse>> searchByBookAndChapter(
            @RequestParam Integer bookId,
            @RequestParam Integer chapter) {
        
        log.info("장별 검색 API 호출: bookId={}, chapter={}", bookId, chapter);
        
        List<VerseSearchResultDto> searchResults = verseSearchApplicationService.searchByBookAndChapter(bookId, chapter);
        List<VerseSearchResponse> responses = verseSearchResponseMapper.toResponseList(searchResults);
        
        log.info("장별 검색 API 응답: {}개 결과 반환", responses.size());
        return ResponseEntity.ok(responses);
    }
}