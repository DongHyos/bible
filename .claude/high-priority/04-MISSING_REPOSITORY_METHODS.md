# Missing Repository Methods Implementation Guide

## 📋 Overview
누락된 Repository 메서드들을 구현하여 콘텐츠 기반 검색 및 기타 필수 기능을 완성하는 가이드입니다.

## 🚨 Current Issues
- **BibleVerseRepository**: `findByTextContaining` 메서드 누락 (Line 170 in VerseApplicationService)
- **BookRepository**: `findByIds` 메서드 구현 완료 (02-BIBLEVERSE_BOOK_NAME_RESOLUTION.md 참고)
- **기타 Repository**: 성능 최적화를 위한 추가 메서드들

## 📂 Files to Modify

### 1. BibleVerse Repository Methods
- `src/main/java/com/dong/bible/domain/verse/BibleVerseRepository.java`
- `src/main/java/com/dong/bible/infrastructure/persistence/repository/BibleVerseJpaRepository.java`
- `src/main/java/com/dong/bible/infrastructure/persistence/repository/BibleVerseRepositoryImpl.java`

### 2. Other Repository Enhancements
- Book, Category, DailyVerse, Sermon repositories

## 🎯 Phase 1: BibleVerse Text Search Methods

### 1.1 Update Domain Repository Interface
**File**: `src/main/java/com/dong/bible/domain/verse/BibleVerseRepository.java`
**Add these methods**:

```java
/**
 * 구절 내용으로 검색
 * @param text 검색할 텍스트
 * @return 일치하는 구절 목록
 */
List<BibleVerse> findByTextContaining(String text);

/**
 * 구절 내용으로 검색 (페이징)
 * @param text 검색할 텍스트
 * @param pageable 페이징 정보
 * @return 일치하는 구절 페이지
 */
Page<BibleVerse> findByTextContaining(String text, Pageable pageable);

/**
 * 참조로 구절 검색
 * @param reference 참조 문자열
 * @return 일치하는 구절 목록
 */
List<BibleVerse> findByReferenceContaining(String reference);

/**
 * 키워드로 구절 검색 (텍스트 + 참조)
 * @param keyword 키워드
 * @param pageable 페이징 정보
 * @return 검색 결과 페이지
 */
Page<BibleVerse> searchByKeyword(String keyword, Pageable pageable);

/**
 * 여러 책에서 구절 검색
 * @param bookIds 책 ID 목록
 * @return 구절 목록
 */
List<BibleVerse> findByBookIds(Collection<Long> bookIds);

/**
 * 여러 책에서 구절 검색 (페이징)
 * @param bookIds 책 ID 목록
 * @param pageable 페이징 정보
 * @return 구절 페이지
 */
Page<BibleVerse> findByBookIds(Collection<Long> bookIds, Pageable pageable);

/**
 * 책과 장 범위로 구절 검색
 * @param bookId 책 ID
 * @param startChapter 시작 장
 * @param endChapter 끝 장
 * @return 구절 목록
 */
List<BibleVerse> findByBookIdAndChapterBetween(Long bookId, Integer startChapter, Integer endChapter);

/**
 * 구절 번호 범위로 검색 (여러 장 포함)
 * @param bookId 책 ID
 * @param startChapter 시작 장
 * @param startVerse 시작 절
 * @param endChapter 끝 장  
 * @param endVerse 끝 절
 * @return 구절 목록
 */
List<BibleVerse> findByBookIdAndChapterVerseRange(Long bookId, Integer startChapter, Integer startVerse,
                                                  Integer endChapter, Integer endVerse);

/**
 * 특정 텍스트가 포함된 구절 수 조회
 * @param text 검색할 텍스트
 * @return 일치하는 구절 수
 */
long countByTextContaining(String text);

/**
 * 책별 구절 수 조회
 * @param bookId 책 ID
 * @return 구절 수
 */
long countByBookId(Long bookId);
```

### 1.2 Add JPA Repository Methods
**File**: `src/main/java/com/dong/bible/infrastructure/persistence/repository/BibleVerseJpaRepository.java`

```java
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Collection;
import java.util.List;

// ===== 텍스트 검색 메서드 =====

/**
 * 구절 텍스트로 검색 (대소문자 무시)
 */
List<BibleVerseEntity> findByVerseTextContainingIgnoreCaseOrderByBookIdAscChapterAscVerseNumberAsc(String verseText);

/**
 * 구절 텍스트로 검색 (페이징)
 */
Page<BibleVerseEntity> findByVerseTextContainingIgnoreCase(String verseText, Pageable pageable);

/**
 * 참조로 검색
 */
List<BibleVerseEntity> findByReferenceContainingIgnoreCaseOrderByBookIdAscChapterAscVerseNumberAsc(String reference);

/**
 * 키워드로 복합 검색 (텍스트 + 참조)
 */
@Query("""
    SELECT v FROM BibleVerseEntity v
    WHERE LOWER(v.verseText) LIKE LOWER(CONCAT('%', :keyword, '%'))
       OR LOWER(v.reference) LIKE LOWER(CONCAT('%', :keyword, '%'))
    ORDER BY v.bookId, v.chapter, v.verseNumber
    """)
Page<BibleVerseEntity> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

/**
 * 정확한 구문 검색
 */
@Query("""
    SELECT v FROM BibleVerseEntity v
    WHERE v.verseText LIKE CONCAT('%', :phrase, '%')
    ORDER BY v.bookId, v.chapter, v.verseNumber
    """)
List<BibleVerseEntity> findByExactPhrase(@Param("phrase") String phrase);

// ===== 여러 책 검색 메서드 =====

/**
 * 여러 책에서 구절 조회
 */
List<BibleVerseEntity> findByBookIdInOrderByBookIdAscChapterAscVerseNumberAsc(Collection<Long> bookIds);

/**
 * 여러 책에서 구절 조회 (페이징)
 */
Page<BibleVerseEntity> findByBookIdIn(Collection<Long> bookIds, Pageable pageable);

// ===== 범위 검색 메서드 =====

/**
 * 장 범위로 구절 검색
 */
List<BibleVerseEntity> findByBookIdAndChapterBetweenOrderByChapterAscVerseNumberAsc(
    Long bookId, Integer startChapter, Integer endChapter);

/**
 * 복합 범위 검색 (장과 절 모두 고려)
 */
@Query("""
    SELECT v FROM BibleVerseEntity v
    WHERE v.bookId = :bookId
    AND (
        (v.chapter > :startChapter AND v.chapter < :endChapter)
        OR (v.chapter = :startChapter AND v.verseNumber >= :startVerse)
        OR (v.chapter = :endChapter AND v.verseNumber <= :endVerse)
        OR (v.chapter = :startChapter AND v.chapter = :endChapter 
            AND v.verseNumber >= :startVerse AND v.verseNumber <= :endVerse)
    )
    ORDER BY v.chapter, v.verseNumber
    """)
List<BibleVerseEntity> findByBookIdAndChapterVerseRange(
    @Param("bookId") Long bookId,
    @Param("startChapter") Integer startChapter,
    @Param("startVerse") Integer startVerse,
    @Param("endChapter") Integer endChapter,
    @Param("endVerse") Integer endVerse
);

// ===== 카운트 메서드 =====

/**
 * 텍스트 포함 구절 수
 */
long countByVerseTextContainingIgnoreCase(String verseText);

/**
 * 키워드 검색 결과 수
 */
@Query("""
    SELECT COUNT(v) FROM BibleVerseEntity v
    WHERE LOWER(v.verseText) LIKE LOWER(CONCAT('%', :keyword, '%'))
       OR LOWER(v.reference) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
long countByKeyword(@Param("keyword") String keyword);

/**
 * 책별 구절 수
 */
long countByBookId(Long bookId);

/**
 * 여러 책의 총 구절 수
 */
long countByBookIdIn(Collection<Long> bookIds);

// ===== 통계/분석 메서드 =====

/**
 * 가장 긴 구절 조회
 */
@Query("""
    SELECT v FROM BibleVerseEntity v
    WHERE LENGTH(v.verseText) = (
        SELECT MAX(LENGTH(v2.verseText)) FROM BibleVerseEntity v2
    )
    """)
List<BibleVerseEntity> findLongestVerses();

/**
 * 가장 짧은 구절 조회
 */
@Query("""
    SELECT v FROM BibleVerseEntity v
    WHERE LENGTH(v.verseText) = (
        SELECT MIN(LENGTH(v2.verseText)) FROM BibleVerseEntity v2 
        WHERE LENGTH(v2.verseText) > 0
    )
    """)
List<BibleVerseEntity> findShortestVerses();

/**
 * 책별 평균 구절 길이
 */
@Query("""
    SELECT v.bookId, AVG(LENGTH(v.verseText)) 
    FROM BibleVerseEntity v
    GROUP BY v.bookId
    ORDER BY v.bookId
    """)
List<Object[]> findAverageVerseLengthByBook();
```

### 1.3 Implement Repository Methods
**File**: `src/main/java/com/dong/bible/infrastructure/persistence/repository/BibleVerseRepositoryImpl.java`

```java
// 추가 import 문
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

// ===== 텍스트 검색 메서드 구현 =====

@Override
public List<BibleVerse> findByTextContaining(String text) {
    log.debug("Finding verses by text containing: {}", text);
    
    if (text == null || text.trim().isEmpty()) {
        return new ArrayList<>();
    }
    
    List<BibleVerseEntity> entities = jpaRepository
        .findByVerseTextContainingIgnoreCaseOrderByBookIdAscChapterAscVerseNumberAsc(text.trim());
    
    log.debug("Found {} verses containing text: {}", entities.size(), text);
    return mapper.toDomainList(entities);
}

@Override
public Page<BibleVerse> findByTextContaining(String text, Pageable pageable) {
    log.debug("Finding verses by text containing with paging: {}", text);
    
    if (text == null || text.trim().isEmpty()) {
        return Page.empty(pageable);
    }
    
    Page<BibleVerseEntity> entityPage = jpaRepository
        .findByVerseTextContainingIgnoreCase(text.trim(), pageable);
    
    List<BibleVerse> verses = mapper.toDomainList(entityPage.getContent());
    
    log.debug("Found {} verses (page {}/{}) containing text: {}", 
        verses.size(), entityPage.getNumber(), entityPage.getTotalPages(), text);
    
    return new PageImpl<>(verses, pageable, entityPage.getTotalElements());
}

@Override
public List<BibleVerse> findByReferenceContaining(String reference) {
    log.debug("Finding verses by reference containing: {}", reference);
    
    if (reference == null || reference.trim().isEmpty()) {
        return new ArrayList<>();
    }
    
    List<BibleVerseEntity> entities = jpaRepository
        .findByReferenceContainingIgnoreCaseOrderByBookIdAscChapterAscVerseNumberAsc(reference.trim());
    
    log.debug("Found {} verses with reference containing: {}", entities.size(), reference);
    return mapper.toDomainList(entities);
}

@Override
public Page<BibleVerse> searchByKeyword(String keyword, Pageable pageable) {
    log.debug("Searching verses by keyword: {}", keyword);
    
    if (keyword == null || keyword.trim().isEmpty()) {
        return Page.empty(pageable);
    }
    
    Page<BibleVerseEntity> entityPage = jpaRepository
        .searchByKeyword(keyword.trim(), pageable);
    
    List<BibleVerse> verses = mapper.toDomainList(entityPage.getContent());
    
    log.debug("Found {} verses (total: {}) for keyword: {}", 
        verses.size(), entityPage.getTotalElements(), keyword);
    
    return new PageImpl<>(verses, pageable, entityPage.getTotalElements());
}

// ===== 여러 책 검색 메서드 구현 =====

@Override
public List<BibleVerse> findByBookIds(Collection<Long> bookIds) {
    log.debug("Finding verses by book IDs: {}", bookIds);
    
    if (bookIds == null || bookIds.isEmpty()) {
        return new ArrayList<>();
    }
    
    // null 값 필터링 및 중복 제거
    Collection<Long> cleanBookIds = bookIds.stream()
        .filter(Objects::nonNull)
        .distinct()
        .collect(Collectors.toList());
    
    if (cleanBookIds.isEmpty()) {
        return new ArrayList<>();
    }
    
    List<BibleVerseEntity> entities = jpaRepository
        .findByBookIdInOrderByBookIdAscChapterAscVerseNumberAsc(cleanBookIds);
    
    log.debug("Found {} verses from {} books", entities.size(), cleanBookIds.size());
    return mapper.toDomainList(entities);
}

@Override
public Page<BibleVerse> findByBookIds(Collection<Long> bookIds, Pageable pageable) {
    log.debug("Finding verses by book IDs with paging: {}", bookIds);
    
    if (bookIds == null || bookIds.isEmpty()) {
        return Page.empty(pageable);
    }
    
    Collection<Long> cleanBookIds = bookIds.stream()
        .filter(Objects::nonNull)
        .distinct()
        .collect(Collectors.toList());
    
    if (cleanBookIds.isEmpty()) {
        return Page.empty(pageable);
    }
    
    Page<BibleVerseEntity> entityPage = jpaRepository
        .findByBookIdIn(cleanBookIds, pageable);
    
    List<BibleVerse> verses = mapper.toDomainList(entityPage.getContent());
    
    log.debug("Found {} verses (total: {}) from {} books", 
        verses.size(), entityPage.getTotalElements(), cleanBookIds.size());
    
    return new PageImpl<>(verses, pageable, entityPage.getTotalElements());
}

// ===== 범위 검색 메서드 구현 =====

@Override
public List<BibleVerse> findByBookIdAndChapterBetween(Long bookId, Integer startChapter, Integer endChapter) {
    log.debug("Finding verses by book {} and chapters {}-{}", bookId, startChapter, endChapter);
    
    if (bookId == null || startChapter == null || endChapter == null) {
        throw new IllegalArgumentException("Book ID, start chapter, and end chapter must not be null");
    }
    
    if (startChapter > endChapter) {
        throw new IllegalArgumentException("Start chapter must be less than or equal to end chapter");
    }
    
    if (startChapter < 1 || endChapter < 1) {
        throw new IllegalArgumentException("Chapter numbers must be positive");
    }
    
    List<BibleVerseEntity> entities = jpaRepository
        .findByBookIdAndChapterBetweenOrderByChapterAscVerseNumberAsc(bookId, startChapter, endChapter);
    
    log.debug("Found {} verses in book {} chapters {}-{}", 
        entities.size(), bookId, startChapter, endChapter);
    
    return mapper.toDomainList(entities);
}

@Override
public List<BibleVerse> findByBookIdAndChapterVerseRange(Long bookId, Integer startChapter, Integer startVerse,
                                                         Integer endChapter, Integer endVerse) {
    log.debug("Finding verses by book {} range {}:{} to {}:{}", 
        bookId, startChapter, startVerse, endChapter, endVerse);
    
    if (bookId == null || startChapter == null || startVerse == null || 
        endChapter == null || endVerse == null) {
        throw new IllegalArgumentException("All range parameters must not be null");
    }
    
    if (startChapter < 1 || endChapter < 1 || startVerse < 1 || endVerse < 1) {
        throw new IllegalArgumentException("All numbers must be positive");
    }
    
    // 시작 위치가 끝 위치보다 뒤에 있는지 확인
    if (startChapter > endChapter || 
        (startChapter.equals(endChapter) && startVerse > endVerse)) {
        throw new IllegalArgumentException("Start position must be before or equal to end position");
    }
    
    List<BibleVerseEntity> entities = jpaRepository
        .findByBookIdAndChapterVerseRange(bookId, startChapter, startVerse, endChapter, endVerse);
    
    log.debug("Found {} verses in range {}:{} to {}:{}", 
        entities.size(), startChapter, startVerse, endChapter, endVerse);
    
    return mapper.toDomainList(entities);
}

// ===== 카운트 메서드 구현 =====

@Override
public long countByTextContaining(String text) {
    log.debug("Counting verses by text containing: {}", text);
    
    if (text == null || text.trim().isEmpty()) {
        return 0L;
    }
    
    long count = jpaRepository.countByVerseTextContainingIgnoreCase(text.trim());
    
    log.debug("Found {} verses containing text: {}", count, text);
    return count;
}

@Override
public long countByBookId(Long bookId) {
    log.debug("Counting verses by book ID: {}", bookId);
    
    if (bookId == null) {
        return 0L;
    }
    
    long count = jpaRepository.countByBookId(bookId);
    
    log.debug("Book {} has {} verses", bookId, count);
    return count;
}

// ===== 유틸리티 메서드 추가 =====

/**
 * 정확한 구문으로 구절 검색
 */
public List<BibleVerse> findByExactPhrase(String phrase) {
    log.debug("Finding verses by exact phrase: {}", phrase);
    
    if (phrase == null || phrase.trim().isEmpty()) {
        return new ArrayList<>();
    }
    
    List<BibleVerseEntity> entities = jpaRepository.findByExactPhrase(phrase.trim());
    
    log.debug("Found {} verses with exact phrase: {}", entities.size(), phrase);
    return mapper.toDomainList(entities);
}

/**
 * 키워드 검색 결과 수 조회
 */
public long countByKeyword(String keyword) {
    log.debug("Counting verses by keyword: {}", keyword);
    
    if (keyword == null || keyword.trim().isEmpty()) {
        return 0L;
    }
    
    long count = jpaRepository.countByKeyword(keyword.trim());
    
    log.debug("Found {} verses for keyword: {}", count, keyword);
    return count;
}

/**
 * 여러 책의 총 구절 수 조회
 */
public long countByBookIds(Collection<Long> bookIds) {
    log.debug("Counting verses by book IDs: {}", bookIds);
    
    if (bookIds == null || bookIds.isEmpty()) {
        return 0L;
    }
    
    Collection<Long> cleanBookIds = bookIds.stream()
        .filter(Objects::nonNull)
        .distinct()
        .collect(Collectors.toList());
    
    if (cleanBookIds.isEmpty()) {
        return 0L;
    }
    
    long count = jpaRepository.countByBookIdIn(cleanBookIds);
    
    log.debug("Found {} verses in {} books", count, cleanBookIds.size());
    return count;
}
```

## 🎯 Phase 2: Update VerseApplicationService

### 2.1 Fix Line 170 TODO Comment
**File**: `src/main/java/com/dong/bible/application/service/VerseApplicationService.java`
**Replace the TODO comment with actual implementation**:

```java
/**
 * 텍스트로 구절 검색
 * @param text 검색할 텍스트
 * @param pageable 페이징 정보
 * @return 검색 결과
 */
public Page<VerseSearchResultDto> searchVersesByText(String text, Pageable pageable) {
    log.info("Searching verses by text: {}", text);
    
    if (text == null || text.trim().isEmpty()) {
        throw new IllegalArgumentException("Search text must not be empty");
    }
    
    // 이제 BibleVerseRepository에 findByTextContaining 메서드가 있음
    Page<BibleVerse> versePage = verseRepository.findByTextContaining(text.trim(), pageable);
    
    // Book 정보 enrichment (VerseDomainService 사용)
    List<BibleVerse> enrichedVerses = verseDomainService.enrichWithBookInfo(versePage.getContent());
    
    List<VerseSearchResultDto> dtos = enrichedVerses.stream()
        .map(VerseSearchResultDto::from)
        .collect(Collectors.toList());
    
    log.info("Found {} verses containing text: {}", versePage.getTotalElements(), text);
    
    return new PageImpl<>(dtos, pageable, versePage.getTotalElements());
}

/**
 * 키워드로 구절 검색 (텍스트 + 참조)
 * @param keyword 검색 키워드
 * @param pageable 페이징 정보
 * @return 검색 결과
 */
public Page<VerseSearchResultDto> searchVersesByKeyword(String keyword, Pageable pageable) {
    log.info("Searching verses by keyword: {}", keyword);
    
    if (keyword == null || keyword.trim().isEmpty()) {
        throw new IllegalArgumentException("Search keyword must not be empty");
    }
    
    Page<BibleVerse> versePage = verseRepository.searchByKeyword(keyword.trim(), pageable);
    
    // Book 정보 enrichment
    List<BibleVerse> enrichedVerses = verseDomainService.enrichWithBookInfo(versePage.getContent());
    
    List<VerseSearchResultDto> dtos = enrichedVerses.stream()
        .map(VerseSearchResultDto::from)
        .collect(Collectors.toList());
    
    log.info("Found {} verses for keyword: {}", versePage.getTotalElements(), keyword);
    
    return new PageImpl<>(dtos, pageable, versePage.getTotalElements());
}

/**
 * 여러 책에서 구절 검색
 * @param bookIds 책 ID 목록
 * @param pageable 페이징 정보
 * @return 검색 결과
 */
public Page<VerseQueryDto> getVersesByBooks(Collection<Long> bookIds, Pageable pageable) {
    log.info("Getting verses from books: {}", bookIds);
    
    if (bookIds == null || bookIds.isEmpty()) {
        throw new IllegalArgumentException("Book IDs must not be empty");
    }
    
    Page<BibleVerse> versePage = verseRepository.findByBookIds(bookIds, pageable);
    
    // Book 정보 enrichment
    List<BibleVerse> enrichedVerses = verseDomainService.enrichWithBookInfo(versePage.getContent());
    
    List<VerseQueryDto> dtos = enrichedVerses.stream()
        .map(VerseQueryDto::from)
        .collect(Collectors.toList());
    
    log.info("Found {} verses from {} books", versePage.getTotalElements(), bookIds.size());
    
    return new PageImpl<>(dtos, pageable, versePage.getTotalElements());
}

/**
 * 확장된 범위로 구절 검색 (여러 장에 걸쳐)
 * @param bookId 책 ID
 * @param startChapter 시작 장
 * @param startVerse 시작 절
 * @param endChapter 끝 장
 * @param endVerse 끝 절
 * @return 범위 내 구절 목록
 */
public List<VerseQueryDto> getVersesByExtendedRange(Long bookId, Integer startChapter, Integer startVerse,
                                                   Integer endChapter, Integer endVerse) {
    log.info("Getting verses by extended range: {}:{} to {}:{} in book {}", 
        startChapter, startVerse, endChapter, endVerse, bookId);
    
    List<BibleVerse> verses = verseRepository.findByBookIdAndChapterVerseRange(
        bookId, startChapter, startVerse, endChapter, endVerse);
    
    if (verses.isEmpty()) {
        throw new EntityNotFoundException(
            String.format("No verses found in range %d:%d to %d:%d for book %d", 
                startChapter, startVerse, endChapter, endVerse, bookId));
    }
    
    // Book 정보 enrichment
    verses = verseDomainService.enrichWithBookInfo(verses);
    
    List<VerseQueryDto> dtos = verses.stream()
        .map(VerseQueryDto::from)
        .collect(Collectors.toList());
    
    log.info("Found {} verses in extended range", dtos.size());
    
    return dtos;
}
```

## 🎯 Phase 3: Other Repository Enhancements

### 3.1 BookRepository Enhancements
**File**: `src/main/java/com/dong/bible/domain/book/BookRepository.java`
**Add additional methods**:

```java
/**
 * 구약/신약으로 책 조회
 * @param testament 구약/신약 구분
 * @return 해당 구약/신약의 책 목록
 */
List<Book> findByTestament(String testament);

/**
 * 책 이름으로 검색 (부분 일치)
 * @param name 책 이름
 * @return 일치하는 책 목록
 */
List<Book> findByNameContaining(String name);

/**
 * 책 순서 범위로 조회
 * @param startOrder 시작 순서
 * @param endOrder 끝 순서
 * @return 해당 순서 범위의 책 목록
 */
List<Book> findByBookOrderBetween(Integer startOrder, Integer endOrder);
```

### 3.2 CategoryRepository Enhancements  
**File**: `src/main/java/com/dong/bible/domain/category/BibleCategoryRepository.java`

```java
/**
 * 활성화된 카테고리만 조회
 * @return 활성 카테고리 목록
 */
List<BibleCategory> findByIsActiveTrue();

/**
 * 부모 카테고리로 하위 카테고리 조회
 * @param parentId 부모 카테고리 ID
 * @return 하위 카테고리 목록
 */
List<BibleCategory> findByParentId(Long parentId);

/**
 * 카테고리 타입별 조회
 * @param categoryType 카테고리 타입
 * @return 해당 타입의 카테고리 목록
 */
List<BibleCategory> findByCategoryType(String categoryType);

/**
 * 깊이별 카테고리 조회
 * @param depth 카테고리 깊이
 * @return 해당 깊이의 카테고리 목록
 */
List<BibleCategory> findByDepth(Integer depth);
```

### 3.3 DailyVerseRepository Enhancements
**File**: `src/main/java/com/dong/bible/domain/dailyverse/DailyVerseRepository.java`

```java
/**
 * 날짜 범위로 일일 구절 조회
 * @param startDate 시작 날짜
 * @param endDate 끝 날짜
 * @return 해당 기간의 일일 구절 목록
 */
List<DailyVerse> findByDateBetween(LocalDate startDate, LocalDate endDate);

/**
 * 월별 일일 구절 조회
 * @param year 연도
 * @param month 월
 * @return 해당 월의 일일 구절 목록
 */
List<DailyVerse> findByYearAndMonth(int year, int month);

/**
 * 특정 책의 일일 구절 조회
 * @param bookId 책 ID
 * @return 해당 책의 일일 구절 목록
 */
List<DailyVerse> findByVerseBookId(Long bookId);

/**
 * 활성화된 일일 구절만 조회
 * @return 활성 일일 구절 목록
 */
List<DailyVerse> findByIsActiveTrue();
```

## ✅ Implementation Checklist

### Core BibleVerse Methods
- [ ] Add `findByTextContaining` methods to domain repository
- [ ] Add JPA repository methods for text search
- [ ] Implement repository methods with proper validation
- [ ] Add keyword search combining text and reference
- [ ] Add multi-book search methods
- [ ] Add extended range search methods

### VerseApplicationService Updates  
- [ ] Fix Line 170 TODO comment with actual implementation
- [ ] Add `searchVersesByText` method
- [ ] Add `searchVersesByKeyword` method
- [ ] Add `getVersesByBooks` method
- [ ] Add `getVersesByExtendedRange` method
- [ ] Integrate with VerseDomainService for book enrichment

### Other Repository Enhancements
- [ ] Add BookRepository search methods
- [ ] Add CategoryRepository filtering methods
- [ ] Add DailyVerseRepository date range methods
- [ ] Implement all JPA repository interfaces

### Testing & Validation
- [ ] Unit test all new repository methods
- [ ] Integration test with actual database
- [ ] Test search performance with large datasets
- [ ] Validate search result accuracy

## 🧪 Testing Strategy

### Unit Tests for BibleVerseRepository

```java
@Test
@DisplayName("텍스트 포함 구절 검색 성공")
void findByTextContaining_Success() {
    // Given
    String searchText = "사랑";
    
    // When
    List<BibleVerse> result = verseRepository.findByTextContaining(searchText);
    
    // Then
    assertThat(result).isNotEmpty();
    assertThat(result).allSatisfy(verse -> 
        assertThat(verse.getVerseText().toLowerCase()).contains(searchText.toLowerCase()));
}

@Test
@DisplayName("키워드 검색 - 텍스트와 참조 모두 검색")
void searchByKeyword_Success() {
    // Given
    String keyword = "요한";
    PageRequest pageRequest = PageRequest.of(0, 10);
    
    // When
    Page<BibleVerse> result = verseRepository.searchByKeyword(keyword, pageRequest);
    
    // Then
    assertThat(result.getContent()).isNotEmpty();
    assertThat(result.getContent()).allSatisfy(verse -> {
        String text = verse.getVerseText().toLowerCase();
        String reference = verse.getReference().toLowerCase();
        assertThat(text.contains(keyword.toLowerCase()) || 
                  reference.contains(keyword.toLowerCase())).isTrue();
    });
}

@Test
@DisplayName("확장된 범위 검색 성공")
void findByBookIdAndChapterVerseRange_Success() {
    // Given
    Long bookId = 1L;
    Integer startChapter = 1, startVerse = 1;
    Integer endChapter = 2, endVerse = 5;
    
    // When
    List<BibleVerse> result = verseRepository.findByBookIdAndChapterVerseRange(
        bookId, startChapter, startVerse, endChapter, endVerse);
    
    // Then
    assertThat(result).isNotEmpty();
    assertThat(result).allSatisfy(verse -> {
        assertThat(verse.getBookId()).isEqualTo(bookId);
        // 범위 내에 있는지 확인
        boolean inRange = (verse.getChapter() > startChapter && verse.getChapter() < endChapter) ||
                         (verse.getChapter().equals(startChapter) && verse.getVerseNumber() >= startVerse) ||
                         (verse.getChapter().equals(endChapter) && verse.getVerseNumber() <= endVerse);
        assertThat(inRange).isTrue();
    });
}
```

### Integration Tests for VerseApplicationService

```java
@Test
@DisplayName("텍스트 검색으로 구절 조회 - Book 이름 포함")
void searchVersesByText_WithBookName() {
    // Given
    String searchText = "하나님";
    PageRequest pageRequest = PageRequest.of(0, 5);
    
    // When
    Page<VerseSearchResultDto> result = verseApplicationService
        .searchVersesByText(searchText, pageRequest);
    
    // Then
    assertThat(result.getContent()).isNotEmpty();
    assertThat(result.getContent()).allSatisfy(dto -> {
        assertThat(dto.getBookName()).isNotNull();
        assertThat(dto.getBookName()).doesNotStartWith("Book-");
        assertThat(dto.getVerseText().toLowerCase()).contains(searchText.toLowerCase());
    });
}
```

## ⚠️ Important Notes

1. **Performance**: 텍스트 검색은 인덱스를 활용할 수 있도록 데이터베이스 인덱스 설정 필요
2. **Memory Usage**: 대량 결과 조회 시 페이징 사용 권장
3. **Search Accuracy**: 대소문자 무시 검색으로 사용자 편의성 증대
4. **Error Handling**: 모든 입력 파라미터에 대한 유효성 검증 필수
5. **Integration**: VerseDomainService와 연동하여 Book 정보 자동 추가

## 🎯 Success Criteria

구현 완료 후:
- ✅ `findByTextContaining` 메서드로 콘텐츠 기반 검색 가능
- ✅ 키워드 검색으로 텍스트와 참조 동시 검색
- ✅ 여러 책에서 구절 검색 가능  
- ✅ 확장된 범위 검색으로 여러 장에 걸친 구절 조회
- ✅ 모든 검색 결과에 올바른 Book 이름 표시
- ✅ 페이징 지원으로 성능 최적화
- ✅ VerseApplicationService의 TODO 주석 완전 해결

이 가이드를 따라 구현하면 누락된 Repository 메서드들이 모두 완성됩니다.