package com.dong.bible.application.service;

import com.dong.bible.application.dto.ChapterQueryDto;
import com.dong.bible.application.dto.VerseQueryDto;
import com.dong.bible.application.dto.VerseRangeQueryDto;
import com.dong.bible.application.dto.VerseSearchDto;
import com.dong.bible.domain.verse.BibleVerse;
import com.dong.bible.domain.verse.BibleVerseRepository;
import com.dong.bible.domain.verse.VerseReference;
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
 * - 도메인 언어 중심 (bookName 사용)
 * - Infrastructure 세부사항 제거 (bookId 제거)
 * - 비즈니스 유스케이스 조합
 * - 트랜잭션 경계 관리
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VerseQueryService {
    
    private final BibleVerseRepository bibleVerseRepository;

    /**
     * 특정 장의 모든 구절 조회 UseCase (도메인 중심)
     */
    @Cacheable(value = "chapterVerses", key = "#bookName + '_' + #chapter")
    public ChapterQueryDto getChapter(String bookName, Integer chapter) {
        log.debug("Getting chapter: book='{}', chapter={}", bookName, chapter);
        
        // 1. 입력 검증
        if (bookName == null || bookName.trim().isEmpty()) {
            throw new IllegalArgumentException("Book name cannot be empty");
        }
        
        // 2. Domain Repository로 구절들 조회 (도메인 언어 사용)
        List<BibleVerse> verses = bibleVerseRepository.findByChapter(bookName.trim(), chapter);
        
        if (verses.isEmpty()) {
            log.warn("No verses found for book='{}', chapter={}", bookName, chapter);
            throw new IllegalArgumentException("Chapter not found: " + bookName + " " + chapter);
        }
        
        // 3. DTO 변환 및 반환 (bookId는 임시로 null)
        return ChapterQueryDto.of(null, bookName.trim(), chapter, verses);
    }

    /**
     * 특정 구절 조회 UseCase (도메인 중심)
     */
    public VerseQueryDto getVerse(String bookName, Integer chapter, Integer verse) {
        log.debug("Getting verse: book='{}', chapter={}, verse={}", bookName, chapter, verse);
        
        // 1. 입력 검증
        if (bookName == null || bookName.trim().isEmpty()) {
            throw new IllegalArgumentException("Book name cannot be empty");
        }
        
        // 2. VerseReference로 도메인 객체 생성
        VerseReference reference = VerseReference.of(bookName.trim(), chapter, verse);
        
        // 3. Domain Repository로 구절 조회 (도메인 객체 사용)
        BibleVerse bibleVerse = bibleVerseRepository.findByReference(reference)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Verse not found: " + reference.toDisplayString()));
        
        // 4. DTO 변환 및 반환
        return VerseQueryDto.of(bibleVerse);
    }

    /**
     * 구절 범위 조회 UseCase (도메인 중심)
     */
    public VerseRangeQueryDto getVerseRange(String bookName, Integer chapter, Integer startVerse, Integer endVerse) {
        log.debug("Getting verse range: book='{}', chapter={}, verses={}-{}", 
                 bookName, chapter, startVerse, endVerse);
        
        // 1. 입력 검증
        if (bookName == null || bookName.trim().isEmpty()) {
            throw new IllegalArgumentException("Book name cannot be empty");
        }
        if (startVerse > endVerse) {
            throw new IllegalArgumentException("Start verse cannot be greater than end verse");
        }
        
        // 2. Domain Repository로 구절 범위 조회 (도메인 언어 사용)
        List<BibleVerse> verses = bibleVerseRepository.findByChapterRange(
                bookName.trim(), chapter, startVerse, endVerse);
        
        if (verses.isEmpty()) {
            log.warn("No verses found in range: book='{}', chapter={}, verses={}-{}", 
                    bookName, chapter, startVerse, endVerse);
        }
        
        // 3. DTO 변환 및 반환 (bookId는 임시로 null)
        return VerseRangeQueryDto.of(null, chapter, startVerse, endVerse, verses);
    }

    /**
     * 텍스트 검색 UseCase
     * 
     * 참고: 실제로는 Elasticsearch 등 전문 검색 엔진을 사용해야 하지만,
     * 학습 목적으로 간단한 텍스트 검색 구현
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
     * 
     * 참고: 순수 DDD에서는 기술적 ID보다 도메인 식별자를 선호하지만,
     * 기존 API 호환성을 위해 제공
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
     * 책의 모든 구절 조회 UseCase
     */
    public List<VerseQueryDto> getBookVerses(String bookName) {
        log.debug("Getting all verses for book: '{}'", bookName);
        
        if (bookName == null || bookName.trim().isEmpty()) {
            throw new IllegalArgumentException("Book name cannot be empty");
        }
        
        List<BibleVerse> verses = bibleVerseRepository.findByBook(bookName.trim());
        
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
}
