package com.dong.bible.web.controller;

import com.dong.bible.application.service.VerseApplicationService;
import com.dong.bible.application.dto.command.*;
import com.dong.bible.application.dto.query.ChapterQuery;
import com.dong.bible.application.dto.query.VerseQuery;
import com.dong.bible.application.dto.query.VerseRangeQuery;
import com.dong.bible.application.dto.query.VerseSearchQuery;
import com.dong.bible.common.response.AppResponse;
import com.dong.bible.web.dto.request.SearchRequest;
import com.dong.bible.web.dto.response.ChapterResponse;
import com.dong.bible.web.dto.response.VerseResponse;
import com.dong.bible.web.dto.response.VerseSearchResponse;
import com.dong.bible.web.mapper.VerseResponseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

/**
 * 성경 구절 조회 REST Controller
 * 
 * DDD 구조 적용:
 * - Web API는 ID 기반으로 클라이언트 친화적 설계
 * - 내부에서 BookApplicationService로 ID → 도메인 언어 변환
 * - Application Service에서 완전 DDD 적용
 */
@RestController
@RequestMapping("/api/bible")
@Slf4j
@RequiredArgsConstructor
public class KrvVerseController {

    private final VerseApplicationService verseApplicationService;
    private final VerseResponseMapper verseResponseMapper;

    /**
     * 특정 장의 모든 구절 조회
     * GET /api/bible/books/43/chapters/3
     */
    @GetMapping("/books/{bookId}/chapters/{chapter}")
    public ResponseEntity<AppResponse<ChapterResponse>> getChapter(
            @PathVariable Integer bookId,
            @PathVariable Integer chapter) {
        
        log.info("Getting chapter by bookId: {}, chapter: {}", bookId, chapter);
        
        // DDD: BookApplicationService로 bookId → bookName 변환 후 도메인 중심 메서드 호출
        ChapterQuery chapterQuery = verseApplicationService.getChapterById(bookId, chapter);
        ChapterResponse response = verseResponseMapper.toChapterResponse(chapterQuery);
        
        return ResponseEntity.ok(AppResponse.of(response));
    }

    /**
     * 특정 구절 조회
     * GET /api/bible/books/43/chapters/3/verses/16
     */
    @GetMapping("/books/{bookId}/chapters/{chapter}/verses/{verse}")
    public ResponseEntity<AppResponse<VerseResponse>> getVerse(
            @PathVariable Integer bookId,
            @PathVariable Integer chapter,
            @PathVariable Integer verse) {
        
        log.info("Getting verse by bookId: {}, chapter: {}, verse: {}", bookId, chapter, verse);
        
        // DDD: Web DTO → Application Command DTO 변환
        VerseQueryCommand command = VerseQueryCommand.of(bookId, chapter, verse);
        VerseQuery verseQuery = verseApplicationService.getVerse(command);
        VerseResponse response = verseResponseMapper.toVerseResponse(verseQuery);
        
        return ResponseEntity.ok(AppResponse.of(response));
    }

    /**
     * 구절 범위 조회
     * GET /api/bible/verses?bookId=43&chapter=3&fromVerse=16&toVerse=17
     */
    @GetMapping("/verses")
    public ResponseEntity<AppResponse<List<VerseResponse>>> getVerseRange(
            @RequestParam Integer bookId,
            @RequestParam Integer chapter,
            @RequestParam Integer fromVerse,
            @RequestParam Integer toVerse) {

        log.info("Getting verse range by bookId: {}, chapter: {}, from: {}, to: {}", 
                bookId, chapter, fromVerse, toVerse);

        // DDD: Application Service에서 모든 검증 처리 (bookId 변환 + 범위 검증)
        VerseRangeQuery rangeQuery = verseApplicationService.getVerseRange(bookId, chapter, fromVerse, toVerse);
        List<VerseResponse> response = rangeQuery.getVerses().stream()
                .map(verseResponseMapper::toVerseResponse)
                .toList();

        return ResponseEntity.ok(AppResponse.of(response));
    }

    /**
     * 구절 검색
     * POST /api/bible/search
     */
    @PostMapping("/search")
    public ResponseEntity<AppResponse<List<VerseSearchResponse>>> searchVerses(
            @Valid @RequestBody SearchRequest request) {
        
        log.info("Searching verses with keyword: '{}'", request.getKeyword());
        
        // DDD: Web DTO → Application Command DTO 변환
        VerseSearchCommand command = VerseSearchCommand.of(request.getKeyword());
        VerseSearchQuery searchResult = verseApplicationService.searchVerses(command);
        List<VerseSearchResponse> response = verseResponseMapper.toSearchResultDtoList(searchResult);
        
        return ResponseEntity.ok(AppResponse.of(response));
    }

    /**
     * ID로 특정 구절 조회 (단일 구절 빠른 조회용)
     * GET /api/bible/verses/12345
     */
    @GetMapping("/verses/{id}")
    public ResponseEntity<AppResponse<VerseResponse>> findById(@PathVariable Long id) {
        log.info("Getting verse by id: {}", id);
        
        VerseQuery verseQuery = verseApplicationService.getVerseById(id);
        VerseResponse response = verseResponseMapper.toVerseResponse(verseQuery);
        
        return ResponseEntity.ok(AppResponse.of(response));
    }

    /**
     * 특정 책의 모든 구절 조회
     * GET /api/bible/books/43/verses
     */
    @GetMapping("/books/{bookId}/verses")
    public ResponseEntity<AppResponse<List<VerseResponse>>> getBookVerses(
            @PathVariable Integer bookId) {
        
        log.info("Getting all verses for bookId: {}", bookId);
        
        // DDD: Application Service에서 모든 검증 처리 (bookId 변환 + 도메인 검증)
        List<VerseQuery> verseQueries = verseApplicationService.getBookVerses(bookId);
        List<VerseResponse> response = verseQueries.stream()
                .map(verseResponseMapper::toVerseResponse)
                .toList();
        
        return ResponseEntity.ok(AppResponse.of(response));
    }
}
