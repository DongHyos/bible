# BibleVerse Book Name Resolution Guide

## 📋 Overview
BibleVerse 도메인 객체의 bookId → bookName 변환 로직을 구현하여 모든 구절 표시에서 올바른 책 이름이 나타나도록 하는 가이드입니다.

## 🚨 Current Issue
- **Status**: 🚨 Critical - 모든 verse 표시에서 책 이름 누락
- **File**: `src/main/java/com/dong/bible/domain/verse/BibleVerse.java`
- **Lines**: 63, 84 - TODO 주석으로 변환 로직 필요 표시
- **Impact**: 모든 구절 조회 API에서 bookName이 "Book-{id}" 형태로 표시됨
- **Estimated Time**: 1 hour

## 🎯 Solution Architecture

### Current State
```java
// BibleVerse.java - Line 63, 84
// TODO: bookId -> bookName 변환 로직 필요
public String getBookName() {
    return "Book-" + bookId; // 임시 구현
}
```

### Target State
```java
public String getBookName() {
    return this.bookName != null ? this.bookName : "Book-" + bookId;
}
```

## 📂 Files to Create/Modify

### 1. Create Domain Service
**File**: `src/main/java/com/dong/bible/domain/verse/VerseDomainService.java`

### 2. Update BibleVerse Domain Object
**File**: `src/main/java/com/dong/bible/domain/verse/BibleVerse.java`

### 3. Update VerseApplicationService
**File**: `src/main/java/com/dong/bible/application/service/VerseApplicationService.java`

### 4. Add BookRepository Method
**File**: `src/main/java/com/dong/bible/domain/book/BookRepository.java`
**File**: `src/main/java/com/dong/bible/infrastructure/persistence/repository/BookRepositoryImpl.java`

## 🏗️ Step 1: Create VerseDomainService

### 1.1 Create Domain Service
**File**: `src/main/java/com/dong/bible/domain/verse/VerseDomainService.java`

```java
package com.dong.bible.domain.verse;

import com.dong.bible.domain.book.Book;
import com.dong.bible.domain.book.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Verse 도메인 서비스
 * BibleVerse와 Book 간의 관계를 처리합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VerseDomainService {
    
    private final BookRepository bookRepository;
    
    /**
     * BibleVerse에 Book 정보를 enrichment합니다.
     * @param verse 원본 BibleVerse
     * @return Book 정보가 포함된 BibleVerse
     */
    public BibleVerse enrichWithBookInfo(BibleVerse verse) {
        if (verse == null) {
            return null;
        }
        
        // 이미 책 정보가 있다면 그대로 반환
        if (verse.getBookName() != null && !verse.getBookName().startsWith("Book-")) {
            return verse;
        }
        
        log.debug("Enriching verse with book info: bookId={}", verse.getBookId());
        
        try {
            Book book = getBookById(verse.getBookId());
            
            return BibleVerse.builder()
                .id(verse.getId())
                .bookId(verse.getBookId())
                .bookName(book.getName())
                .bookAbbreviation(book.getAbbreviation())
                .chapter(verse.getChapter())
                .verseNumber(verse.getVerseNumber())
                .verseText(verse.getVerseText())
                .reference(verse.getReference())
                .createdAt(verse.getCreatedAt())
                .updatedAt(verse.getUpdatedAt())
                .build();
                
        } catch (Exception e) {
            log.warn("Failed to enrich verse with book info: bookId={}, error={}", 
                verse.getBookId(), e.getMessage());
            return verse; // 실패 시 원본 반환
        }
    }
    
    /**
     * 여러 BibleVerse에 Book 정보를 enrichment합니다.
     * 성능 최적화를 위해 한 번에 Book 정보를 조회합니다.
     * 
     * @param verses 원본 BibleVerse 목록
     * @return Book 정보가 포함된 BibleVerse 목록
     */
    public List<BibleVerse> enrichWithBookInfo(List<BibleVerse> verses) {
        if (verses == null || verses.isEmpty()) {
            return verses != null ? verses : new ArrayList<>();
        }
        
        log.debug("Enriching {} verses with book info", verses.size());
        
        // 이미 모든 verse에 책 정보가 있는지 확인
        boolean allHaveBookInfo = verses.stream()
            .allMatch(v -> v.getBookName() != null && !v.getBookName().startsWith("Book-"));
        
        if (allHaveBookInfo) {
            log.debug("All verses already have book info, returning as is");
            return verses;
        }
        
        try {
            // Book ID들을 추출하여 한 번에 조회 (성능 최적화)
            Set<Long> bookIds = verses.stream()
                .map(BibleVerse::getBookId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
            
            log.debug("Loading book info for {} unique book IDs", bookIds.size());
            
            Map<Long, Book> bookMap = getBooksByIds(bookIds).stream()
                .collect(Collectors.toMap(Book::getId, Function.identity()));
            
            return verses.stream()
                .map(verse -> enrichWithBookInfoFromMap(verse, bookMap))
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.warn("Failed to batch enrich verses with book info, error={}", e.getMessage());
            return verses; // 실패 시 원본 반환
        }
    }
    
    /**
     * Book ID로 Book 정보 조회 (캐시 적용)
     */
    @Cacheable(value = "books", key = "#bookId")
    private Book getBookById(Long bookId) {
        return bookRepository.findById(bookId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Book not found for id: " + bookId));
    }
    
    /**
     * 여러 Book ID로 Book 목록 조회
     */
    private List<Book> getBooksByIds(Collection<Long> bookIds) {
        if (bookIds == null || bookIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        return bookRepository.findByIds(bookIds);
    }
    
    /**
     * 미리 로드된 Book Map을 사용하여 BibleVerse enrichment
     */
    private BibleVerse enrichWithBookInfoFromMap(BibleVerse verse, Map<Long, Book> bookMap) {
        if (verse == null) {
            return null;
        }
        
        // 이미 책 정보가 있다면 그대로 반환
        if (verse.getBookName() != null && !verse.getBookName().startsWith("Book-")) {
            return verse;
        }
        
        Book book = bookMap.get(verse.getBookId());
        if (book == null) {
            log.warn("Book not found in map: bookId={}", verse.getBookId());
            return verse; // Book을 찾을 수 없으면 원본 반환
        }
        
        return BibleVerse.builder()
            .id(verse.getId())
            .bookId(verse.getBookId())
            .bookName(book.getName())
            .bookAbbreviation(book.getAbbreviation())
            .chapter(verse.getChapter())
            .verseNumber(verse.getVerseNumber())
            .verseText(verse.getVerseText())
            .reference(verse.getReference())
            .createdAt(verse.getCreatedAt())
            .updatedAt(verse.getUpdatedAt())
            .build();
    }
    
    /**
     * BibleVerse의 완전한 참조 문자열 생성
     * 예: "창세기 1:1", "시편 23:1"
     */
    public String createFullReference(BibleVerse verse) {
        if (verse == null) {
            return "";
        }
        
        String bookName = verse.getBookName();
        if (bookName == null || bookName.startsWith("Book-")) {
            // Book 정보가 없다면 enrichment 시도
            BibleVerse enrichedVerse = enrichWithBookInfo(verse);
            bookName = enrichedVerse.getBookName();
        }
        
        return String.format("%s %d:%d", bookName, verse.getChapter(), verse.getVerseNumber());
    }
}
```

## 🔧 Step 2: Update BibleVerse Domain Object

### 2.1 Update BibleVerse Builder
**File**: `src/main/java/com/dong/bible/domain/verse/BibleVerse.java`
**Update the @Builder and add fields**:

```java
@Builder
@Getter
public class BibleVerse {
    private final Long id;
    private final Long bookId;
    private final String bookName;        // 추가
    private final String bookAbbreviation; // 추가
    private final Integer chapter;
    private final Integer verseNumber;
    private final String verseText;
    private final String reference;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    
    // ... 기존 코드는 그대로 유지 ...
}
```

### 2.2 Fix TODO Methods
**Replace the TODO methods at lines 63, 84**:

```java
// Line 63 근처 - getBookName() 메서드 수정
public String getBookName() {
    // VerseDomainService를 통해 설정된 bookName 반환
    if (this.bookName != null && !this.bookName.startsWith("Book-")) {
        return this.bookName;
    }
    // 기본값 반환 (VerseDomainService 사용 전 또는 실패 시)
    return "Book-" + bookId;
}

// Line 84 근처 - getBookAbbreviation() 메서드 수정  
public String getBookAbbreviation() {
    // VerseDomainService를 통해 설정된 bookAbbreviation 반환
    if (this.bookAbbreviation != null && !this.bookAbbreviation.startsWith("BK")) {
        return this.bookAbbreviation;
    }
    // 기본값 반환 (VerseDomainService 사용 전 또는 실패 시)
    return "BK" + bookId;
}

/**
 * 완전한 참조 문자열 반환
 * 예: "창세기 1:1"
 */
public String getFullReference() {
    return String.format("%s %d:%d", getBookName(), chapter, verseNumber);
}

/**
 * 축약된 참조 문자열 반환
 * 예: "창 1:1"
 */
public String getShortReference() {
    return String.format("%s %d:%d", getBookAbbreviation(), chapter, verseNumber);
}
```

## 🔗 Step 3: Add BookRepository Methods

### 3.1 Update Domain Repository Interface
**File**: `src/main/java/com/dong/bible/domain/book/BookRepository.java`
**Add this method**:

```java
/**
 * 여러 ID로 Book 조회
 * @param ids Book ID 목록
 * @return Book 목록
 */
List<Book> findByIds(Collection<Long> ids);
```

### 3.2 Implement Repository Method
**File**: `src/main/java/com/dong/bible/infrastructure/persistence/repository/BookRepositoryImpl.java`
**Add this implementation**:

```java
@Override
public List<Book> findByIds(Collection<Long> ids) {
    log.debug("Finding books by IDs: {}", ids);
    
    if (ids == null || ids.isEmpty()) {
        return new ArrayList<>();
    }
    
    // 중복 제거 및 null 필터링
    Set<Long> uniqueIds = ids.stream()
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
    
    if (uniqueIds.isEmpty()) {
        return new ArrayList<>();
    }
    
    List<BookEntity> entities = jpaRepository.findAllById(uniqueIds);
    return entities.stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
}
```

**Add import if needed**:
```java
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
```

## 🔄 Step 4: Update Application Services

### 4.1 Update VerseApplicationService
**File**: `src/main/java/com/dong/bible/application/service/VerseApplicationService.java`
**Inject VerseDomainService and use it in all query methods**:

```java
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class VerseApplicationService {
    
    private final BibleVerseRepository verseRepository;
    private final VerseDomainService verseDomainService;  // 추가
    
    // 기존 생성자는 @RequiredArgsConstructor로 자동 생성
    
    /**
     * 단일 구절 조회 - Book 정보 포함
     */
    public VerseQueryDto getVerse(Long bookId, Integer chapter, Integer verseNumber) {
        log.info("Getting verse: bookId={}, chapter={}, verse={}", bookId, chapter, verseNumber);
        
        BibleVerse verse = verseRepository.findByBookIdAndChapterAndVerseNumber(
                bookId, chapter, verseNumber)
            .orElseThrow(() -> new EntityNotFoundException(
                String.format("Verse not found: bookId=%d, chapter=%d, verse=%d", 
                    bookId, chapter, verseNumber)));
        
        // Book 정보 enrichment
        verse = verseDomainService.enrichWithBookInfo(verse);
        
        return VerseQueryDto.from(verse);
    }
    
    /**
     * 챕터별 구절 조회 - Book 정보 포함
     */
    public ChapterQueryDto getChapter(Long bookId, Integer chapter) {
        log.info("Getting chapter: bookId={}, chapter={}", bookId, chapter);
        
        List<BibleVerse> verses = verseRepository.findByBookIdAndChapter(bookId, chapter);
        
        if (verses.isEmpty()) {
            throw new EntityNotFoundException(
                String.format("Chapter not found: bookId=%d, chapter=%d", bookId, chapter));
        }
        
        // Book 정보 enrichment (배치 처리로 성능 최적화)
        verses = verseDomainService.enrichWithBookInfo(verses);
        
        return ChapterQueryDto.of(bookId, chapter, verses);
    }
    
    /**
     * 구절 범위 조회 - Book 정보 포함
     */
    public VerseRangeQueryDto getVerseRange(Long bookId, Integer chapter, 
                                           Integer startVerse, Integer endVerse) {
        log.info("Getting verse range: bookId={}, chapter={}, verses={}-{}", 
            bookId, chapter, startVerse, endVerse);
        
        List<BibleVerse> verses = verseRepository.findByBookIdAndChapterAndVerseNumberBetween(
            bookId, chapter, startVerse, endVerse);
        
        if (verses.isEmpty()) {
            throw new EntityNotFoundException(
                String.format("Verse range not found: bookId=%d, chapter=%d, verses=%d-%d", 
                    bookId, chapter, startVerse, endVerse));
        }
        
        // Book 정보 enrichment
        verses = verseDomainService.enrichWithBookInfo(verses);
        
        return VerseRangeQueryDto.of(bookId, chapter, startVerse, endVerse, verses);
    }
    
    /**
     * 구절 검색 - Book 정보 포함
     */
    public Page<VerseSearchResultDto> searchVerses(String keyword, Pageable pageable) {
        log.info("Searching verses: keyword={}", keyword);
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return Page.empty(pageable);
        }
        
        Page<BibleVerse> versePage = verseRepository.findByVerseTextContaining(
            keyword.trim(), pageable);
        
        // Book 정보 enrichment (배치 처리)
        List<BibleVerse> enrichedVerses = verseDomainService.enrichWithBookInfo(
            versePage.getContent());
        
        List<VerseSearchResultDto> dtos = enrichedVerses.stream()
            .map(VerseSearchResultDto::from)
            .collect(Collectors.toList());
        
        return new PageImpl<>(dtos, pageable, versePage.getTotalElements());
    }
    
    /**
     * 북별 전체 구절 조회 - Book 정보 포함
     */
    public List<VerseQueryDto> getVersesByBook(Long bookId) {
        log.info("Getting all verses by book: bookId={}", bookId);
        
        List<BibleVerse> verses = verseRepository.findByBookId(bookId);
        
        if (verses.isEmpty()) {
            log.warn("No verses found for bookId: {}", bookId);
            return new ArrayList<>();
        }
        
        // Book 정보 enrichment (배치 처리)
        verses = verseDomainService.enrichWithBookInfo(verses);
        
        return verses.stream()
            .map(VerseQueryDto::from)
            .collect(Collectors.toList());
    }
}
```

### 4.2 Update Other Application Services
**If there are other services using BibleVerse**, apply the same pattern:

```java
// DailyVerseApplicationService.java 예시
public DailyVerseDetailDto getDailyVerse(LocalDate date) {
    // ... 기존 로직 ...
    
    // BibleVerse 조회 후 enrichment
    BibleVerse verse = verseRepository.findById(dailyVerse.getVerseId())
        .orElseThrow(() -> new EntityNotFoundException("Verse not found"));
    
    verse = verseDomainService.enrichWithBookInfo(verse);
    
    // ... 나머지 로직 ...
}
```

## ⚙️ Step 5: Configure Caching

### 5.1 Add Cache Configuration
**File**: `src/main/java/com/dong/bible/common/config/CacheConfig.java`

```java
package com.dong.bible.common.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .maximumSize(500)  // 최대 500개 Book 캐시
            .expireAfterWrite(1, TimeUnit.HOURS)  // 1시간 후 만료
            .recordStats());
        return cacheManager;
    }
}
```

### 5.2 Add Cache Dependency
**File**: `build.gradle`
**Add to dependencies section**:

```gradle
dependencies {
    // 기존 dependencies...
    
    // Cache support
    implementation 'org.springframework.boot:spring-boot-starter-cache'
    implementation 'com.github.ben-manes.caffeine:caffeine'
}
```

## ✅ Implementation Checklist

### Core Implementation
- [ ] Create `VerseDomainService.java` with enrichment logic
- [ ] Update `BibleVerse.java` builder to include bookName/bookAbbreviation fields  
- [ ] Fix TODO methods in `BibleVerse.java` (lines 63, 84)
- [ ] Add `findByIds()` method to `BookRepository` interface
- [ ] Implement `findByIds()` in `BookRepositoryImpl`

### Service Integration
- [ ] Update `VerseApplicationService` to use `VerseDomainService`
- [ ] Update all verse query methods to enrichment verses
- [ ] Update other services that use BibleVerse (DailyVerseApplicationService, etc.)

### Performance & Caching
- [ ] Add cache configuration for Book lookups
- [ ] Add cache dependency to build.gradle
- [ ] Test batch enrichment performance with large datasets

### Testing
- [ ] Unit test `VerseDomainService` enrichment methods
- [ ] Test single verse enrichment
- [ ] Test batch verse enrichment
- [ ] Test cache effectiveness
- [ ] Integration test with updated application services

## 🧪 Testing Examples

### Unit Test for VerseDomainService

```java
@ExtendWith(MockitoExtension.class)
class VerseDomainServiceTest {
    
    @Mock
    private BookRepository bookRepository;
    
    @InjectMocks
    private VerseDomainService verseDomainService;
    
    @Test
    @DisplayName("단일 구절 Book 정보 enrichment 성공")
    void enrichWithBookInfo_SingleVerse_Success() {
        // Given
        BibleVerse verse = createVerse(1L, 1L, 1, 1);
        Book book = createBook(1L, "창세기", "창");
        
        given(bookRepository.findById(1L)).willReturn(Optional.of(book));
        
        // When
        BibleVerse result = verseDomainService.enrichWithBookInfo(verse);
        
        // Then
        assertThat(result.getBookName()).isEqualTo("창세기");
        assertThat(result.getBookAbbreviation()).isEqualTo("창");
        assertThat(result.getBookId()).isEqualTo(1L);
    }
    
    @Test
    @DisplayName("배치 구절 Book 정보 enrichment 성공")
    void enrichWithBookInfo_BatchVerses_Success() {
        // Given
        List<BibleVerse> verses = Arrays.asList(
            createVerse(1L, 1L, 1, 1),
            createVerse(2L, 1L, 1, 2),
            createVerse(3L, 2L, 1, 1)
        );
        
        List<Book> books = Arrays.asList(
            createBook(1L, "창세기", "창"),
            createBook(2L, "출애굽기", "출")
        );
        
        given(bookRepository.findByIds(any())).willReturn(books);
        
        // When
        List<BibleVerse> result = verseDomainService.enrichWithBookInfo(verses);
        
        // Then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getBookName()).isEqualTo("창세기");
        assertThat(result.get(2).getBookName()).isEqualTo("출애굽기");
    }
}
```

### Integration Test

```java
@SpringBootTest
@Transactional
class VerseApplicationServiceIntegrationTest {
    
    @Autowired
    private VerseApplicationService verseApplicationService;
    
    @Test
    @DisplayName("구절 조회 시 Book 이름이 올바르게 표시됨")
    void getVerse_ReturnsCorrectBookName() {
        // When
        VerseQueryDto result = verseApplicationService.getVerse(1L, 1, 1);
        
        // Then
        assertThat(result.getBookName()).isNotNull();
        assertThat(result.getBookName()).doesNotStartWith("Book-");
        assertThat(result.getBookName()).isIn("창세기", "Genesis"); // 실제 데이터에 따라
    }
}
```

## ⚠️ Important Notes

1. **Performance Consideration**: 대량의 구절을 조회할 때는 반드시 배치 enrichment를 사용
2. **Cache Strategy**: Book 정보는 자주 변경되지 않으므로 캐싱 적극 활용
3. **Error Handling**: Book을 찾을 수 없을 때 원본 BibleVerse를 반환하여 서비스 중단 방지
4. **Backward Compatibility**: 기존 API 응답 구조는 변경하지 않고 bookName 필드만 추가

## 🎯 Success Criteria

구현 완료 후:
- ✅ 모든 구절 API에서 올바른 책 이름 표시 (예: "창세기", "시편")
- ✅ "Book-{id}" 형태의 임시 이름 완전 제거
- ✅ 성능 저하 없이 Book 정보 조회 (캐싱 적용)
- ✅ 기존 API 호환성 유지
- ✅ 에러 상황에서도 서비스 정상 동작

이 가이드를 따라 구현하면 모든 구절 표시에서 올바른 책 이름을 확인할 수 있습니다.