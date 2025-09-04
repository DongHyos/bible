package com.dong.bible.application.service;

import com.dong.bible.application.dto.query.BookQuery;
import com.dong.bible.application.dto.query.BibleStatisticsQuery;
import com.dong.bible.ENUM.Testament;
import com.dong.bible.domain.book.Book;
import com.dong.bible.domain.book.BookName;
import com.dong.bible.domain.book.BookRepository;
import com.dong.bible.domain.statistics.BibleStatistics;
import com.dong.bible.domain.statistics.BibleStatisticsDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 성경책 조회를 담당하는 Application Service
 * Application DTO를 반환하여 계층별 책임을 분리합니다.
 * 도메인 로직은 도메인 객체에서 처리하고, Application은 조합만 담당합니다.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookApplicationService {
    
    private final BookRepository bookRepository;
    private final BibleStatisticsDomainService statisticsDomainService;
    
    // ========================================
    // Public API 메서드들 (모두 Application DTO 반환)
    // ========================================
    
    /**
     * 모든 성경책 조회 (순서대로)
     * Use Case: 성경책 목록 페이지 표시
     */
    public List<BookQuery> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return books.stream()
                .map(BookQuery::from)
                .collect(Collectors.toList());
    }
    
    /**
     * DB ID로 Book 조회
     * Use Case: 성경책 상세 페이지 표시 + VerseQueryService 지원
     */
    public BookQuery getBookById(Integer bookId) {
        if (bookId == null) {
            throw new IllegalArgumentException("Book id must not be null");
        }

        Book book = findBookByIdDomain(bookId);
        return BookQuery.from(book);
    }
    
    /**
     * 성경책 이름으로 Book 조회
     * Use Case: VerseQueryService 지원 + 검색 기능
     */
    public Optional<BookQuery> getBookByName(String bookName) {
        if (bookName == null || bookName.trim().isEmpty()) {
            return Optional.empty();
        }
        
        return getBookByNameDomain(bookName)
                .map(BookQuery::from);
    }
    
    /**
     * BookName Value Object로 Book 조회
     * Use Case: VerseQueryService 지원
     */
    public Optional<BookQuery> getBookByName(BookName bookName) {
        if (bookName == null) {
            return Optional.empty();
        }
        
        return bookRepository.findByName(bookName)
                .map(BookQuery::from);
    }
    
    /**
     * 성경책 이름으로 DB ID 조회
     * Use Case: VerseQueryService 지원
     */
    public Optional<Integer> getBookIdByName(String bookName) {
        return getBookByNameDomain(bookName)
                .map(Book::getId)
                .map(Long::intValue);
    }

    /**
     * DB ID로 성경책 이름 조회
     * Use Case: VerseQueryService 지원
     */
    public String getBookNameById(Integer bookId) {
        Book book = findBookByIdDomain(bookId); // 여기서 예외 발생 가능
        return book.getBookName().getName();
    }
    
    /**
     * 신구약별 성경책 조회
     * Use Case: 신구약별 성경책 목록 페이지 표시
     */
    public List<BookQuery> getBooksByTestament(String testament) {
        Testament testamentEnum = Testament.fromString(testament);
        List<Book> books = bookRepository.findByTestament(testamentEnum);
        return books.stream()
                .map(BookQuery::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 구약 성경책 목록 조회
     * Use Case: 구약 성경책 목록 페이지 표시
     */
    public List<BookQuery> getOldTestamentBooks() {
        List<Book> books = bookRepository.findOldTestamentBooks();
        return books.stream()
                .map(BookQuery::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 신약 성경책 목록 조회
     * Use Case: 신약 성경책 목록 페이지 표시
     */
    public List<BookQuery> getNewTestamentBooks() {
        List<Book> books = bookRepository.findNewTestamentBooks();
        return books.stream()
                .map(BookQuery::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 성경책 존재 여부 확인
     * Use Case: VerseQueryService 지원 + 검증
     */
    public boolean existsBook(String bookName) {
        if (bookName == null || bookName.trim().isEmpty()) {
            return false;
        }
        
        try {
            BookName bookNameObj = BookName.of(bookName.trim());
            return bookRepository.existsByName(bookNameObj);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * 특정 장이 유효한지 검증
     * Use Case: VerseQueryService 지원 + 검증
     */
    public boolean isValidChapter(String bookName, int chapter) {
        return getBookByNameDomain(bookName)
                .map(book -> book.hasChapter(chapter))  // ✅ 도메인 로직 사용
                .orElse(false);
    }
    
    /**
     * 성경책의 총 장수 조회
     * Use Case: VerseQueryService 지원 + 통계
     */
    public Optional<Integer> getTotalChapters(String bookName) {
        return getBookByNameDomain(bookName)
                .map(Book::getTotalChapters);  // ✅ 도메인 메서드 사용
    }
    
    /**
     * 전체 성경책 개수 (66권 확인용)
     * Use Case: 성경책 통계 표시
     */
    public long getTotalBookCount() {
        return bookRepository.count();
    }
    
    /**
     * 신구약별 그룹핑된 성경책 조회
     * Use Case: 프론트엔드 탭 구성용 데이터 제공
     */
    public Map<String, List<BookQuery>> getGroupedBooksByTestament() {
        log.info("Getting books grouped by testament");
        
        List<BookQuery> oldTestamentBooks = getOldTestamentBooks();
        List<BookQuery> newTestamentBooks = getNewTestamentBooks();
        
        Map<String, List<BookQuery>> grouped = new HashMap<>();
        grouped.put("구약", oldTestamentBooks);
        grouped.put("신약", newTestamentBooks);
        
        return grouped;
    }

    /**
     * 성경 통계 정보 조회
     * Use Case: 성경 통계 대시보드 표시
     */
    public BibleStatisticsQuery getBibleStatistics() {
        log.info("Getting bible statistics");
        
        // 1. 데이터 조회 (Application Service 역할)
        List<Book> allBooks = bookRepository.findAll();
        List<Book> oldTestamentBooks = bookRepository.findOldTestamentBooks();
        List<Book> newTestamentBooks = bookRepository.findNewTestamentBooks();
        
        // 2. 도메인 서비스 호출 (비즈니스 로직 위임)
        BibleStatistics statistics = statisticsDomainService.calculateStatistics(
            allBooks, oldTestamentBooks, newTestamentBooks
        );
        
        // 3. DTO 변환 (Application Service 역할)
        return BibleStatisticsQuery.builder()
                .totalBooks(statistics.getTotalBooks())
                .oldTestamentBooks(statistics.getOldTestamentBooks())
                .newTestamentBooks(statistics.getNewTestamentBooks())
                .totalChapters(statistics.getTotalChapters())
                .build();
    }
    
    // ========================================
    // Service 간 호출용 메서드들 (도메인 객체 반환)
    // ========================================
    
    /**
     * Service 간 호출용: 도메인 객체 반환
     * VerseQueryService에서 도메인 로직 호출을 위해 필요
     */
    public Optional<Book> getBookDomainByName(String bookName) {
        return getBookByNameDomain(bookName);
    }
    
    // ========================================
    // Private 헬퍼 메서드들 (도메인 객체 조회)
    // ========================================
    
    /**
     * DB ID로 도메인 객체 조회
     */
    private Book findBookByIdDomain(Integer bookId) {
        return bookRepository.findAll()
                .stream()
                .filter(book -> book.getId() != null && book.getId().equals(bookId.longValue()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Book not found with id: " + bookId));
    }
    
    /**
     * 성경책 이름으로 도메인 객체 조회
     */
    private Optional<Book> getBookByNameDomain(String bookName) {
        return bookRepository.findByName(bookName.trim());
    }
}
