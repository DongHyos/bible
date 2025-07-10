package com.dong.bible.application.service;

import com.dong.bible.ENUM.Testament;
import com.dong.bible.domain.book.Book;
import com.dong.bible.domain.book.BookName;
import com.dong.bible.domain.book.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 성경책 조회를 담당하는 Application Service
 * VerseQueryService에서 bookId ↔ bookName 변환 시 사용됩니다.
 * 순수 DDD 원칙에 따라 Domain Repository를 조합하여 유스케이스를 구현합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookQueryService {
    
    private final BookRepository bookRepository;
    
    /**
     * 성경책 이름으로 Book 조회
     * @param bookName 성경책 이름
     * @return Book 도메인 객체
     */
    public Optional<Book> getBookByName(String bookName) {
        if (bookName == null || bookName.trim().isEmpty()) {
            return Optional.empty();
        }
        
        return bookRepository.findByName(bookName.trim());
    }
    
    /**
     * BookName Value Object로 Book 조회
     * @param bookName BookName Value Object
     * @return Book 도메인 객체
     */
    public Optional<Book> getBookByName(BookName bookName) {
        if (bookName == null) {
            return Optional.empty();
        }
        
        return bookRepository.findByName(bookName);
    }
    
    /**
     * 성경책 이름으로 DB ID 조회 (VerseQueryService 지원용)
     * Infrastructure의 기술적 ID를 Domain Service에서 제공
     * @param bookName 성경책 이름
     * @return DB의 기술적 ID (krv_books.id)
     */
    public Optional<Integer> getBookIdByName(String bookName) {
        return getBookByName(bookName)
                .map(Book::getId)
                .map(Long::intValue);
    }

    /**
     * DB ID로 Book 조회 (Web Layer에서 사용)
     * @param bookId DB의 기술적 ID
     * @return Book 도메인 객체
     */
    public Book getBookById(Integer bookId) {
        if (bookId == null) {
            throw new IllegalArgumentException("Book id must not be null");
        }

        // 현재 순수 DDD Repository는 ID 조회를 지원하지 않음
        // 모든 Book을 조회해서 ID로 필터링 (학습용이므로 성능은 무시)
        return bookRepository.findAll()
                .stream()
                .filter(book -> book.getId() != null && book.getId().equals(bookId.longValue()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Book not found with id: " + bookId));
    }

    /**
     * DB ID로 성경책 이름 조회 (VerseQueryService 지원용)
     * @param bookId DB의 기술적 ID
     * @return 성경책 이름
     */
    public String getBookNameById(Integer bookId) {
        Book book = getBookById(bookId); // 여기서 예외 발생 가능
        return book.getBookName().getName();
    }
    
    /**
     * 모든 성경책 조회 (순서대로)
     * @return 66권 성경책 전체
     */
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    
    /**
     * 신구약별 성경책 조회
     * @param testament 신약/구약 구분
     * @return 해당 신구약의 성경책들
     */
    public List<Book> getBooksByTestament(String testament) {
        Testament testamentEnum = Testament.fromString(testament); // 변환 및 검증 책임
        return bookRepository.findByTestament(testamentEnum);
    }
    
    /**
     * 구약 성경책 목록 조회
     * @return 구약 39권
     */
    public List<Book> getOldTestamentBooks() {
        return bookRepository.findOldTestamentBooks();
    }
    
    /**
     * 신약 성경책 목록 조회
     * @return 신약 27권
     */
    public List<Book> getNewTestamentBooks() {
        return bookRepository.findNewTestamentBooks();
    }
    
    /**
     * 성경책 존재 여부 확인
     * @param bookName 성경책 이름
     * @return 존재하면 true
     */
    public boolean existsBook(String bookName) {
        if (bookName == null || bookName.trim().isEmpty()) {
            return false;
        }
        
        try {
            BookName bookNameObj = BookName.of(bookName.trim());
            return bookRepository.existsByName(bookNameObj);
        } catch (IllegalArgumentException e) {
            return false; // 유효하지 않은 성경책 이름
        }
    }
    
    /**
     * 특정 장이 유효한지 검증
     * @param bookName 성경책 이름
     * @param chapter 장 번호
     * @return 유효하면 true
     */
    public boolean isValidChapter(String bookName, int chapter) {
        return getBookByName(bookName)
                .map(book -> book.hasChapter(chapter))
                .orElse(false);
    }
    
    /**
     * 성경책의 총 장수 조회
     * @param bookName 성경책 이름
     * @return 총 장수
     */
    public Optional<Integer> getTotalChapters(String bookName) {
        return getBookByName(bookName)
                .map(Book::getTotalChapters);
    }
    
    /**
     * 전체 성경책 개수 (66권 확인용)
     * @return 성경책 총 개수
     */
    public long getTotalBookCount() {
        return bookRepository.count();
    }
}
