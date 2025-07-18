package com.dong.bible.web.controller;

import com.dong.bible.application.dto.BibleStatisticsDto;
import com.dong.bible.application.dto.BookDto;
import com.dong.bible.application.service.BookApplicationService;
import com.dong.bible.common.response.AppResponse;
import com.dong.bible.web.dto.response.BibleBookDto;
import com.dong.bible.web.mapper.BookResponseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 성경책 조회 REST Controller
 * DDD 구조 완전 적용:
 * - BookApplicationService에서 Application DTO 반환
 * - BookResponseMapper로 Application DTO → Web DTO 변환
 * - Controller는 HTTP 처리만 담당
 */
@RestController
@RequestMapping("/api/bible")
@Slf4j
@RequiredArgsConstructor
public class KrvBookController {

    private final BookApplicationService bookApplicationService;
    private final BookResponseMapper bookResponseMapper;

    /**
     * 전체 성경 목록
     * GET /api/bible/books
     */
    @GetMapping("/books")
    public ResponseEntity<AppResponse<List<BibleBookDto>>> getAllBooks() {
        log.info("Getting all books");
        List<BookDto> books = bookApplicationService.getAllBooks();
        List<BibleBookDto> response = bookResponseMapper.fromBookDtoList(books);
        return ResponseEntity.ok(AppResponse.of(response));
    }

    /**
     * 구약/신약별 조회 (경로 충돌 해결)
     * GET /api/bible/books/testament/구약 또는 GET /api/bible/books/testament/신약
     */
    @GetMapping("/books/testament/{testament}")
    public ResponseEntity<AppResponse<List<BibleBookDto>>> getBooksByTestament(
            @PathVariable String testament) {
        log.info("Getting books by testament: {}", testament);
        List<BookDto> books = bookApplicationService.getBooksByTestament(testament);
        List<BibleBookDto> response = bookResponseMapper.fromBookDtoList(books);
        return ResponseEntity.ok(AppResponse.of(response));
    }

    /**
     * 특정 성경책 ID로 조회
     * GET /api/bible/books/1 (창세기)
     */
    @GetMapping("/books/{id}")
    public ResponseEntity<AppResponse<BibleBookDto>> getBookById(@PathVariable Integer id) {
        log.info("Getting book by id: {}", id);
        BookDto book = bookApplicationService.getBookById(id);
        BibleBookDto response = bookResponseMapper.fromBookDto(book);
        return ResponseEntity.ok(AppResponse.of(response));
    }

    /**
     * 그룹핑된 데이터 (프론트에서 탭 구성용)
     * GET /api/bible/books/grouped
     */
    @GetMapping("/books/grouped")
    public ResponseEntity<AppResponse<Map<String, List<BibleBookDto>>>> getBooksByTestamentGrouped() {
        log.info("Getting books grouped by testament");
        Map<String, List<BookDto>> groupedBooks = bookApplicationService.getGroupedBooksByTestament();
        Map<String, List<BibleBookDto>> response = bookResponseMapper.fromGroupedBooks(groupedBooks);
        return ResponseEntity.ok(AppResponse.of(response));
    }

    /**
     * 성경 통계
     * GET /api/bible/books/statistics
     */
    @GetMapping("/books/statistics")
    public ResponseEntity<AppResponse<Map<String, Integer>>> getBibleStatistics() {
        log.info("Getting bible statistics");
        BibleStatisticsDto statistics = bookApplicationService.getBibleStatistics();
        Map<String, Integer> response = bookResponseMapper.fromStatisticsDto(statistics);
        return ResponseEntity.ok(AppResponse.of(response));
    }
}