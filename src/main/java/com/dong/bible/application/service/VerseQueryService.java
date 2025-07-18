package com.dong.bible.application.service;

import com.dong.bible.application.dto.ChapterQueryDto;
import com.dong.bible.application.dto.VerseQueryDto;
import com.dong.bible.application.dto.VerseRangeQueryDto;
import com.dong.bible.application.dto.VerseSearchDto;
import com.dong.bible.domain.book.Book;
import com.dong.bible.domain.verse.BibleVerse;
import com.dong.bible.domain.verse.BibleVerseRepository;
import com.dong.bible.domain.verse.VerseReference;
import com.dong.bible.domain.verse.VerseReferenceRange;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 구절 조회 관련 Application Service (UseCase)
 * 
 * 순수 DDD 원칙 적용:
 * - BookQueryService와 조합하여 완전한 도메인 중심 구조
 * - Book 도메인을 활용한 검증 로직 강화
 * - Infrastructure 세부사항 완전 격리
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VerseQueryService {

    private final BibleVerseRepository bibleVerseRepository;
    private final BookQueryService bookQueryService;  // 🆕 Book 도메인 서비스 주입

    /**
     * 특정 장의 모든 구절 조회 UseCase (완전 도메인 중심)
     */
    @Cacheable(value = "chapterVerses", key = "#bookName + '_' + #chapter")
    public ChapterQueryDto getChapter(String bookName, Integer chapter) {
        log.debug("Getting chapter: book='{}', chapter={}", bookName, chapter);

        // 1. 입력 검증 및 Book 도메인 조회
        Book book = validateAndGetBook(bookName);

        // 2. Book 도메인으로 장 유효성 검증
        book.validateChapter(chapter);

        // 3. Domain Repository로 구절들 조회 (도메인 언어 사용)
        List<BibleVerse> verses = bibleVerseRepository.findByChapter(book.getBookName().getName(), chapter);

        if (verses.isEmpty()) {
            log.warn("No verses found for book='{}', chapter={}", bookName, chapter);
            throw new IllegalArgumentException("Chapter not found: " + bookName + " " + chapter);
        }

        // 4. BookQueryService로 bookId 조회 (Web Layer 호환성)
        Integer bookId = bookQueryService.getBookIdByName(book.getBookName().getName()).orElse(null);

        // 5. DTO 변환 및 반환
        return ChapterQueryDto.of(bookId, book.getBookName().getName(), chapter, verses);
    }

    /**
     * 특정 구절 조회 UseCase (Web Layer에서 호출)
     */
    public VerseQueryDto getVerse(Integer bookId, Integer chapter, Integer verse) {
        log.debug("Getting verse: bookId={}, chapter={}, verse={}", bookId, chapter, verse);

        // 1. BookQueryService로 bookId → bookName 변환 + 검증
        String bookName = bookQueryService.getBookNameById(bookId);

        // 2. 도메인 중심 메서드 호출
        return getVerseByName(bookName, chapter, verse);
    }

    /**
     * 특정 구절 조회 UseCase (완전 도메인 중심) - 내부 메서드
     */
    private VerseQueryDto getVerseByName(String bookName, Integer chapter, Integer verse) {
        log.debug("Getting verse: book='{}', chapter={}, verse={}", bookName, chapter, verse);

        // 1. 입력 검증 및 Book 도메인 조회
        Book book = validateAndGetBook(bookName);

        // 2. Book 도메인으로 장 유효성 검증
        book.validateChapter(chapter);

        // 3. VerseReference로 도메인 객체 생성
        VerseReference reference = VerseReference.of(book.getBookName().getName(), chapter, verse);

        // 4. Domain Repository로 구절 조회 (도메인 객체 사용)
        BibleVerse bibleVerse = bibleVerseRepository.findByReference(reference)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Verse not found: " + reference.toDisplayString()));

        // 5. DTO 변환 및 반환
        return VerseQueryDto.of(bibleVerse);
    }

    /**
     * 구절 범위 조회 UseCase (Web Layer에서 호출)
     */
    public VerseRangeQueryDto getVerseRange(Integer bookId, Integer chapter, Integer fromVerse, Integer toVerse) {
        log.debug("Getting verse range: bookId={}, chapter={}, verses={}-{}",
                 bookId, chapter, fromVerse, toVerse);

        // 1. BookQueryService로 bookId → bookName 변환 + 검증
        String bookName = bookQueryService.getBookNameById(bookId);

        // 2. 도메인 중심 메서드 호출
        return getVerseRangeByName(bookName, chapter, fromVerse, toVerse);
    }

    /**
     * 구절 범위 조회 UseCase (완전 도메인 중심) - 내부 메서드
     */
    private VerseRangeQueryDto getVerseRangeByName(String bookName, Integer chapter, Integer startVerse, Integer endVerse) {
        log.debug("Getting verse range: book='{}', chapter={}, verses={}-{}",
                 bookName, chapter, startVerse, endVerse);

        // 1. Book 도메인 검증
        Book book = validateAndGetBook(bookName);
        book.validateChapter(chapter);

        // 2. VerseReferenceRange로 범위 검증 (도메인 로직)
        VerseReferenceRange verseRange = VerseReferenceRange.of(book.getBookName().getName(), chapter, startVerse, endVerse);

        // 3. Domain Repository로 구절 범위 조회 (도메인 언어 사용)
        List<BibleVerse> verses = bibleVerseRepository.findByChapterRange(
                book.getBookName().getName(), chapter, startVerse, endVerse);

        if (verses.isEmpty()) {
            log.warn("No verses found in range: book='{}', chapter={}, verses={}-{}",
                    bookName, chapter, startVerse, endVerse);
        }

        // 4. BookQueryService로 bookId 조회 (Web Layer 호환성)
        Integer bookId = bookQueryService.getBookIdByName(book.getBookName().getName()).orElse(null);

        // 5. DTO 변환 및 반환
        return VerseRangeQueryDto.of(bookId, chapter, startVerse, endVerse, verses);
    }



    /**
     * 텍스트 검색 UseCase
     */
    public VerseSearchDto searchVerses(String keyword) {
        log.debug("Searching verses with keyword: '{}'", keyword);

        // 1. 입력 검증
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Search keyword cannot be empty");
        }

        String trimmedKeyword = keyword.trim();

        // 2. Domain Repository로 검색
        // TODO: 현재 BibleVerseRepository에 findByTextContaining 메서드 없음
        // 임시로 모든 구절을 조회 후 필터링 (성능상 좋지 않음 - 학습용)
        List<BibleVerse> allVerses = bibleVerseRepository.findAll();
        List<BibleVerse> matchedVerses = allVerses.stream()
                .filter(verse -> verse.getContent().getText().contains(trimmedKeyword))
                .toList();

        log.info("Found {} verses for keyword: '{}'", matchedVerses.size(), trimmedKeyword);

        // 3. DTO 변환 및 반환
        return VerseSearchDto.of(trimmedKeyword, matchedVerses);
    }

    /**
     * ID 기반 구절 조회 (하위 호환성 - Web Layer에서 사용)
     */
    public VerseQueryDto getVerseById(Long id) {
        log.debug("Getting verse by id: {}", id);

        if (id == null) {
            throw new IllegalArgumentException("Verse ID cannot be null");
        }

        BibleVerse bibleVerse = bibleVerseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Verse not found with id: " + id));

        return VerseQueryDto.of(bibleVerse);
    }

    /**
     * 책의 모든 구절 조회 UseCase (Web Layer에서 호출)
     */
    public List<VerseQueryDto> getBookVerses(Integer bookId) {
        log.debug("Getting book verses: bookId={}", bookId);

        // 1. BookQueryService로 bookId → bookName 변환 + 검증
        String bookName = bookQueryService.getBookNameById(bookId);

        // 2. 도메인 중심 메서드 호출
        return getBookVersesByName(bookName);
    }

    /**
     * 책의 모든 구절 조회 UseCase (도메인 검증 강화) - 내부 메서드
     */
    private List<VerseQueryDto> getBookVersesByName(String bookName) {
        log.debug("Getting all verses for book: '{}'", bookName);

        // 1. Book 도메인 검증
        Book book = validateAndGetBook(bookName);

        // 2. Domain Repository로 구절 조회
        List<BibleVerse> verses = bibleVerseRepository.findByBook(book.getBookName().getName());

        log.info("Found {} verses for book: '{}'", verses.size(), book.getBookName().getName());

        return verses.stream()
                .map(VerseQueryDto::of)
                .toList();
    }

    /**
     * 신약/구약 구절 조회 UseCase
     */
    public List<VerseQueryDto> getTestamentVerses(boolean isNewTestament) {
        log.debug("Getting {} verses", isNewTestament ? "New Testament" : "Old Testament");

        List<BibleVerse> verses = bibleVerseRepository.findByTestament(isNewTestament);

        return verses.stream()
                .map(VerseQueryDto::of)
                .toList();
    }

    /**
     * bookId로 bookName 조회 후 장 조회 (Web Layer에서 호출)
     */
    public ChapterQueryDto getChapterById(Integer bookId, Integer chapter) {
        log.debug("Getting chapter by bookId: bookId={}, chapter={}", bookId, chapter);

        // 1. BookQueryService로 bookId → bookName 변환 + 검증
        String bookName = bookQueryService.getBookNameById(bookId);

        // 2. 기존 도메인 중심 메서드 재사용
        return getChapter(bookName, chapter);
    }

    /**
     * 공통 검증 메서드: Book 존재성 확인 및 반환
     */
    private Book validateAndGetBook(String bookName) {
        if (bookName == null || bookName.trim().isEmpty()) {
            throw new IllegalArgumentException("Book name cannot be empty");
        }

        return bookQueryService.getBookDomainByName(bookName.trim())
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookName));
    }
}
