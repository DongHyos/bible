package com.dong.bible.web.controller;

import com.dong.bible.application.service.BookQueryService;
import com.dong.bible.common.response.AppResponse;
import com.dong.bible.domain.book.Book;
import com.dong.bible.ENUM.Testament;
import com.dong.bible.web.dto.response.BibleBookDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 성경책 조회 REST Controller
 *
 * DDD 구조 적용:
 * - BookQueryService 활용
 * - 도메인 객체를 Web DTO로 변환
 */
@RestController
@RequestMapping("/api/bible")
@Slf4j
@RequiredArgsConstructor
public class KrvBookController {

    private final BookQueryService bookQueryService;

    /**
     * 전체 성경 목록
     * GET /api/bible/books
     */
    @GetMapping("/books")
    public ResponseEntity<AppResponse<List<BibleBookDto>>> getAllBooks() {
        log.info("Getting all books");

        List<Book> books = bookQueryService.getAllBooks();
        List<BibleBookDto> response = books.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(AppResponse.of(response));
    }

    /**
     * 구약/신약별 조회
     * GET /api/bible/books/구약 또는 GET /api/bible/books/신약
     */
    @GetMapping("/books/{testament}")
    public ResponseEntity<AppResponse<List<BibleBookDto>>> getBooksByTestament(
            @PathVariable String testament) {
        log.info("Getting books by testament: {}", testament);

        List<Book> books = bookQueryService.getBooksByTestament(testament); // String 그대로 넘김
        List<BibleBookDto> response = books.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(AppResponse.of(response));
    }

    /**
     * 특정 성경책 ID로 조회
     * GET /api/bible/books/1 (창세기)
     */
    @GetMapping("/books/{id}")
    public ResponseEntity<AppResponse<BibleBookDto>> findById(@PathVariable Integer id) {
        log.info("Getting book by id: {}", id);

        Book book = bookQueryService.getBookById(id); // 예외는 Service에서 처리
        BibleBookDto response = toDto(book);

        return ResponseEntity.ok(AppResponse.of(response));
    }

    /**
     * 그룹핑된 데이터 (프론트에서 탭 구성용)
     * GET /api/bible/books/grouped
     */
    @GetMapping("/books/grouped")
    public ResponseEntity<AppResponse<Map<String, List<BibleBookDto>>>> getBooksByTestamentGrouped() {
        log.info("Getting books grouped by testament");

        List<Book> oldTestamentBooks = bookQueryService.getOldTestamentBooks();
        List<Book> newTestamentBooks = bookQueryService.getNewTestamentBooks();

        Map<String, List<BibleBookDto>> grouped = new HashMap<>();
        grouped.put("구약", oldTestamentBooks.stream().map(this::toDto).collect(Collectors.toList()));
        grouped.put("신약", newTestamentBooks.stream().map(this::toDto).collect(Collectors.toList()));

        return ResponseEntity.ok(AppResponse.of(grouped));
    }

    /**
     * 성경 통계
     * GET /api/bible/books/statistics
     */
    @GetMapping("/books/statistics")
    public ResponseEntity<AppResponse<Map<String, Integer>>> getBibleStatistics() {
        log.info("Getting bible statistics");

        long totalBooks = bookQueryService.getTotalBookCount();
        List<Book> oldTestamentBooks = bookQueryService.getOldTestamentBooks();
        List<Book> newTestamentBooks = bookQueryService.getNewTestamentBooks();

        Map<String, Integer> statistics = new HashMap<>();
        statistics.put("totalBooks", (int) totalBooks);
        statistics.put("oldTestamentBooks", oldTestamentBooks.size());
        statistics.put("newTestamentBooks", newTestamentBooks.size());

        // 총 장수 계산
        int totalChapters = bookQueryService.getAllBooks().stream()
                .mapToInt(Book::getTotalChapters)
                .sum();
        statistics.put("totalChapters", totalChapters);

        return ResponseEntity.ok(AppResponse.of(statistics));
    }

    /**
     * Domain Book → Web BibleBookDto 변환
     */
    private BibleBookDto toDto(Book book) {
        BibleBookDto dto = new BibleBookDto();
        dto.setId(book.getId() != null ? book.getId().intValue() : null);
        dto.setName(book.getBookName().getName());
        dto.setAbbr(book.getAbbreviation());
        dto.setTestament(book.getTestament() == Testament.신약 ? "신약" : "구약");
        dto.setBookOrder(book.getBookOrder());
        dto.setChapters(book.getTotalChapters());

        // TODO: category, totalVerses 등은 필요시 추가 구현

        return dto;
    }
}
