# Bible API Implementation Roadmap

## 📋 Overview
This document contains detailed implementation tasks for completing the Bible API project. Each section includes specific code locations, implementation steps, and code templates.

## 🚨 Priority 1: Critical Issues (Complete First)

### 1.1 SermonRepository Implementation
**Impact**: Entire Sermon functionality is broken  
**Estimated Time**: 2-3 hours total

#### Phase 1: Basic CRUD Operations (30 minutes)

##### 1.1.1 Add findAll() Method
**File**: `src/main/java/com/dong/bible/infrastructure/persistence/repository/SermonRepositoryImpl.java`
**Line**: After line 36

```java
@Override
public List<Sermon> findAll() {
    log.debug("Finding all sermons");
    
    List<SermonEntity> entities = jpaRepository.findAll();
    return mapper.toDomainList(entities);
}
```

##### 1.1.2 Add findAll() to Domain Interface
**File**: `src/main/java/com/dong/bible/domain/sermon/SermonRepository.java`
**Line**: After line 15

```java
/**
 * 모든 설교 조회
 * @return 설교 목록
 */
List<Sermon> findAll();
```

#### Phase 2: Essential Query Methods (1 hour)

##### 1.1.3 Add JPA Repository Methods
**File**: `src/main/java/com/dong/bible/infrastructure/persistence/repository/SermonJpaRepository.java`
**Add these methods**:

```java
// 날짜 범위로 설교 조회
List<SermonEntity> findBySermonDateBetweenOrderBySermonDateDesc(
    LocalDate startDate, 
    LocalDate endDate
);

// 페이징 지원 날짜 범위 조회
Page<SermonEntity> findBySermonDateBetween(
    LocalDate startDate, 
    LocalDate endDate, 
    Pageable pageable
);

// 목사님 이름으로 설교 조회
List<SermonEntity> findByPastorNameContainingOrderBySermonDateDesc(String pastorName);
Page<SermonEntity> findByPastorNameContaining(String pastorName, Pageable pageable);

// 교회 이름으로 설교 조회
List<SermonEntity> findByChurchNameContainingOrderBySermonDateDesc(String churchName);
Page<SermonEntity> findByChurchNameContaining(String churchName, Pageable pageable);

// 태그로 설교 조회
@Query("SELECT s FROM SermonEntity s JOIN s.tags t WHERE t IN :tags ORDER BY s.sermonDate DESC")
List<SermonEntity> findByTagsContaining(@Param("tags") List<String> tags);

// 제목과 날짜로 중복 체크
boolean existsByTitleAndSermonDate(String title, LocalDate sermonDate);

// 조회수 증가
@Modifying
@Query("UPDATE SermonEntity s SET s.viewCount = s.viewCount + 1 WHERE s.id = :id")
void incrementViewCount(@Param("id") Long id);

// 좋아요 수 증가
@Modifying
@Query("UPDATE SermonEntity s SET s.likeCount = s.likeCount + 1 WHERE s.id = :id")
void incrementLikeCount(@Param("id") Long id);

// 좋아요 수 감소
@Modifying
@Query("UPDATE SermonEntity s SET s.likeCount = CASE WHEN s.likeCount > 0 THEN s.likeCount - 1 ELSE 0 END WHERE s.id = :id")
void decrementLikeCount(@Param("id") Long id);
```

##### 1.1.4 Implement Repository Methods
**File**: `src/main/java/com/dong/bible/infrastructure/persistence/repository/SermonRepositoryImpl.java`
**Replace UnsupportedOperationException with**:

```java
@Override
public List<Sermon> findByDateRange(LocalDate startDate, LocalDate endDate) {
    log.debug("Finding sermons by date range: {} ~ {}", startDate, endDate);
    
    if (startDate == null || endDate == null) {
        throw new IllegalArgumentException("Start date and end date must not be null");
    }
    
    if (startDate.isAfter(endDate)) {
        throw new IllegalArgumentException("Start date must be before or equal to end date");
    }
    
    List<SermonEntity> entities = jpaRepository
        .findBySermonDateBetweenOrderBySermonDateDesc(startDate, endDate);
    return mapper.toDomainList(entities);
}

@Override
public Page<Sermon> findByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
    log.debug("Finding sermons by date range with paging: {} ~ {}", startDate, endDate);
    
    if (startDate == null || endDate == null) {
        throw new IllegalArgumentException("Start date and end date must not be null");
    }
    
    Page<SermonEntity> entityPage = jpaRepository
        .findBySermonDateBetween(startDate, endDate, pageable);
    
    List<Sermon> sermons = mapper.toDomainList(entityPage.getContent());
    return new PageImpl<>(sermons, pageable, entityPage.getTotalElements());
}

@Override
public List<Sermon> findByPastor(String pastorName) {
    log.debug("Finding sermons by pastor: {}", pastorName);
    
    if (pastorName == null || pastorName.trim().isEmpty()) {
        return new ArrayList<>();
    }
    
    List<SermonEntity> entities = jpaRepository
        .findByPastorNameContainingOrderBySermonDateDesc(pastorName.trim());
    return mapper.toDomainList(entities);
}

@Override
public Page<Sermon> findByPastor(String pastorName, Pageable pageable) {
    log.debug("Finding sermons by pastor with paging: {}", pastorName);
    
    if (pastorName == null || pastorName.trim().isEmpty()) {
        return Page.empty(pageable);
    }
    
    Page<SermonEntity> entityPage = jpaRepository
        .findByPastorNameContaining(pastorName.trim(), pageable);
    
    List<Sermon> sermons = mapper.toDomainList(entityPage.getContent());
    return new PageImpl<>(sermons, pageable, entityPage.getTotalElements());
}

@Override
public List<Sermon> findByChurch(String churchName) {
    log.debug("Finding sermons by church: {}", churchName);
    
    if (churchName == null || churchName.trim().isEmpty()) {
        return new ArrayList<>();
    }
    
    List<SermonEntity> entities = jpaRepository
        .findByChurchNameContainingOrderBySermonDateDesc(churchName.trim());
    return mapper.toDomainList(entities);
}

@Override
public List<Sermon> findByTag(String tag) {
    log.debug("Finding sermons by tag: {}", tag);
    
    if (tag == null || tag.trim().isEmpty()) {
        return new ArrayList<>();
    }
    
    return findByTagsContaining(Arrays.asList(tag));
}

@Override
public List<Sermon> findByTagsContaining(List<String> tags) {
    log.debug("Finding sermons by tags: {}", tags);
    
    if (tags == null || tags.isEmpty()) {
        return new ArrayList<>();
    }
    
    List<String> cleanedTags = tags.stream()
        .filter(Objects::nonNull)
        .map(String::trim)
        .filter(t -> !t.isEmpty())
        .collect(Collectors.toList());
    
    if (cleanedTags.isEmpty()) {
        return new ArrayList<>();
    }
    
    List<SermonEntity> entities = jpaRepository.findByTagsContaining(cleanedTags);
    return mapper.toDomainList(entities);
}

@Override
public boolean existsSermon(String title, LocalDate sermonDate) {
    log.debug("Checking if sermon exists: title={}, date={}", title, sermonDate);
    
    if (title == null || title.trim().isEmpty() || sermonDate == null) {
        return false;
    }
    
    return jpaRepository.existsByTitleAndSermonDate(title.trim(), sermonDate);
}

@Override
@Transactional
public void incrementViewCount(Long sermonId) {
    log.debug("Incrementing view count for sermon: {}", sermonId);
    
    if (sermonId == null) {
        throw new IllegalArgumentException("Sermon ID must not be null");
    }
    
    jpaRepository.incrementViewCount(sermonId);
}

@Override
@Transactional
public void incrementLikeCount(Long sermonId) {
    log.debug("Incrementing like count for sermon: {}", sermonId);
    
    if (sermonId == null) {
        throw new IllegalArgumentException("Sermon ID must not be null");
    }
    
    jpaRepository.incrementLikeCount(sermonId);
}

@Override
@Transactional
public void decrementLikeCount(Long sermonId) {
    log.debug("Decrementing like count for sermon: {}", sermonId);
    
    if (sermonId == null) {
        throw new IllegalArgumentException("Sermon ID must not be null");
    }
    
    jpaRepository.decrementLikeCount(sermonId);
}
```

#### Phase 3: Statistical Methods (30 minutes)

##### 1.1.5 Add Count Methods to JPA Repository
**File**: `src/main/java/com/dong/bible/infrastructure/persistence/repository/SermonJpaRepository.java`

```java
// 목사별 설교 수 카운트
long countByPastorNameContaining(String pastorName);

// 교회별 설교 수 카운트
long countByChurchNameContaining(String churchName);

// 연도별 설교 수 카운트
@Query("SELECT COUNT(s) FROM SermonEntity s WHERE YEAR(s.sermonDate) = :year")
long countByYear(@Param("year") int year);

// 월별 설교 수 카운트
@Query("SELECT COUNT(s) FROM SermonEntity s WHERE YEAR(s.sermonDate) = :year AND MONTH(s.sermonDate) = :month")
long countByYearAndMonth(@Param("year") int year, @Param("month") int month);

// YouTube 비디오가 있는 설교 수
long countByYoutubeUrlIsNotNull();

// 오디오 파일이 있는 설교 수
long countByAudioUrlIsNotNull();
```

##### 1.1.6 Implement Count Methods
**File**: `src/main/java/com/dong/bible/infrastructure/persistence/repository/SermonRepositoryImpl.java`

```java
@Override
public long countByPastor(String pastorName) {
    log.debug("Counting sermons by pastor: {}", pastorName);
    
    if (pastorName == null || pastorName.trim().isEmpty()) {
        return 0L;
    }
    
    return jpaRepository.countByPastorNameContaining(pastorName.trim());
}

@Override
public long countByChurch(String churchName) {
    log.debug("Counting sermons by church: {}", churchName);
    
    if (churchName == null || churchName.trim().isEmpty()) {
        return 0L;
    }
    
    return jpaRepository.countByChurchNameContaining(churchName.trim());
}

@Override
public long countByYear(int year) {
    log.debug("Counting sermons by year: {}", year);
    
    if (year < 1900 || year > 2100) {
        throw new IllegalArgumentException("Invalid year: " + year);
    }
    
    return jpaRepository.countByYear(year);
}

@Override
public long countByMonth(int year, int month) {
    log.debug("Counting sermons by year and month: {}-{}", year, month);
    
    if (year < 1900 || year > 2100) {
        throw new IllegalArgumentException("Invalid year: " + year);
    }
    
    if (month < 1 || month > 12) {
        throw new IllegalArgumentException("Invalid month: " + month);
    }
    
    return jpaRepository.countByYearAndMonth(year, month);
}

@Override
public long countWithYoutubeVideo() {
    log.debug("Counting sermons with YouTube video");
    
    return jpaRepository.countByYoutubeUrlIsNotNull();
}

@Override
public long countWithAudioFile() {
    log.debug("Counting sermons with audio file");
    
    return jpaRepository.countByAudioUrlIsNotNull();
}
```

#### Phase 4: Advanced Features (1 hour)

##### 1.1.7 Add Advanced Query Methods
**File**: `src/main/java/com/dong/bible/infrastructure/persistence/repository/SermonJpaRepository.java`

```java
// 최근 설교 조회
List<SermonEntity> findTop10ByOrderBySermonDateDesc();

// 인기 설교 조회 (조회수 기준)
List<SermonEntity> findTop10ByOrderByViewCountDesc();

// 좋아요 많은 설교 조회
List<SermonEntity> findTop10ByOrderByLikeCountDesc();

// 트렌딩 설교 조회 (최근 7일 내 조회수 높은 순)
@Query("""
    SELECT s FROM SermonEntity s 
    WHERE s.sermonDate >= :weekAgo 
    ORDER BY s.viewCount DESC
    """)
List<SermonEntity> findTrendingSermons(@Param("weekAgo") LocalDate weekAgo, Pageable pageable);

// 추천 설교 조회 (복합 점수 기반)
@Query("""
    SELECT s FROM SermonEntity s 
    ORDER BY (s.viewCount * 0.5 + s.likeCount * 2) DESC
    """)
List<SermonEntity> findRecommendedSermons(Pageable pageable);

// YouTube 비디오가 있는 설교 조회
List<SermonEntity> findByYoutubeUrlIsNotNullOrderBySermonDateDesc(Pageable pageable);

// 키워드 검색
@Query("""
    SELECT s FROM SermonEntity s 
    WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
       OR LOWER(s.summary) LIKE LOWER(CONCAT('%', :keyword, '%'))
       OR LOWER(s.mainText) LIKE LOWER(CONCAT('%', :keyword, '%'))
    ORDER BY s.sermonDate DESC
    """)
Page<SermonEntity> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
```

##### 1.1.8 Implement Advanced Methods
**File**: `src/main/java/com/dong/bible/infrastructure/persistence/repository/SermonRepositoryImpl.java`

```java
@Override
public List<Sermon> findRecentSermons(int limit) {
    log.debug("Finding recent {} sermons", limit);
    
    if (limit <= 0) {
        throw new IllegalArgumentException("Limit must be positive");
    }
    
    PageRequest pageRequest = PageRequest.of(0, Math.min(limit, 100));
    List<SermonEntity> entities = jpaRepository.findTop10ByOrderBySermonDateDesc();
    return mapper.toDomainList(entities);
}

@Override
public List<Sermon> findPopularSermons(int limit) {
    log.debug("Finding popular {} sermons", limit);
    
    if (limit <= 0) {
        throw new IllegalArgumentException("Limit must be positive");
    }
    
    List<SermonEntity> entities = jpaRepository.findTop10ByOrderByViewCountDesc();
    return mapper.toDomainList(entities);
}

@Override
public List<Sermon> findTrendingSermons(int days, int limit) {
    log.debug("Finding trending sermons from last {} days, limit {}", days, limit);
    
    if (days <= 0 || limit <= 0) {
        throw new IllegalArgumentException("Days and limit must be positive");
    }
    
    LocalDate weekAgo = LocalDate.now().minusDays(days);
    PageRequest pageRequest = PageRequest.of(0, Math.min(limit, 100));
    
    List<SermonEntity> entities = jpaRepository.findTrendingSermons(weekAgo, pageRequest);
    return mapper.toDomainList(entities);
}

@Override
public List<Sermon> findRecommendedSermons(int limit) {
    log.debug("Finding recommended {} sermons", limit);
    
    if (limit <= 0) {
        throw new IllegalArgumentException("Limit must be positive");
    }
    
    PageRequest pageRequest = PageRequest.of(0, Math.min(limit, 100));
    List<SermonEntity> entities = jpaRepository.findRecommendedSermons(pageRequest);
    return mapper.toDomainList(entities);
}

@Override
public List<Sermon> findWithYoutubeVideo(int limit) {
    log.debug("Finding {} sermons with YouTube video", limit);
    
    if (limit <= 0) {
        throw new IllegalArgumentException("Limit must be positive");
    }
    
    PageRequest pageRequest = PageRequest.of(0, Math.min(limit, 100));
    List<SermonEntity> entities = jpaRepository
        .findByYoutubeUrlIsNotNullOrderBySermonDateDesc(pageRequest);
    return mapper.toDomainList(entities);
}

@Override
public Page<Sermon> searchByKeyword(String keyword, Pageable pageable) {
    log.debug("Searching sermons by keyword: {}", keyword);
    
    if (keyword == null || keyword.trim().isEmpty()) {
        return Page.empty(pageable);
    }
    
    Page<SermonEntity> entityPage = jpaRepository.searchByKeyword(keyword.trim(), pageable);
    List<Sermon> sermons = mapper.toDomainList(entityPage.getContent());
    return new PageImpl<>(sermons, pageable, entityPage.getTotalElements());
}
```

### 1.2 BibleVerse Book Name Resolution
**Impact**: All verse displays show bookId instead of book name  
**Estimated Time**: 1 hour

#### Implementation Steps

##### 1.2.1 Create VerseDomainService
**File**: Create `src/main/java/com/dong/bible/domain/verse/VerseDomainService.java`

```java
package com.dong.bible.domain.verse;

import com.dong.bible.domain.book.Book;
import com.dong.bible.domain.book.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Verse 도메인 서비스
 * BibleVerse와 Book 간의 관계를 처리합니다.
 */
@Service
@RequiredArgsConstructor
public class VerseDomainService {
    
    private final BookRepository bookRepository;
    
    /**
     * BibleVerse에 Book 정보를 enrichment합니다.
     */
    public BibleVerse enrichWithBookInfo(BibleVerse verse) {
        if (verse == null) {
            return null;
        }
        
        Book book = bookRepository.findById(verse.getBookId())
            .orElseThrow(() -> new IllegalArgumentException(
                "Book not found for id: " + verse.getBookId()
            ));
        
        return BibleVerse.builder()
            .id(verse.getId())
            .bookId(verse.getBookId())
            .bookName(book.getName())  // Book name 설정
            .bookAbbreviation(book.getAbbreviation())
            .chapter(verse.getChapter())
            .verseNumber(verse.getVerseNumber())
            .verseText(verse.getVerseText())
            .reference(verse.getReference())
            .build();
    }
    
    /**
     * 여러 BibleVerse에 Book 정보를 enrichment합니다.
     */
    public List<BibleVerse> enrichWithBookInfo(List<BibleVerse> verses) {
        if (verses == null || verses.isEmpty()) {
            return verses;
        }
        
        // 성능 최적화: Book ID들을 추출하여 한 번에 조회
        Set<Long> bookIds = verses.stream()
            .map(BibleVerse::getBookId)
            .collect(Collectors.toSet());
        
        Map<Long, Book> bookMap = bookRepository.findByIds(bookIds).stream()
            .collect(Collectors.toMap(Book::getId, Function.identity()));
        
        return verses.stream()
            .map(verse -> {
                Book book = bookMap.get(verse.getBookId());
                if (book == null) {
                    return verse;
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
                    .build();
            })
            .collect(Collectors.toList());
    }
}
```

##### 1.2.2 Update BibleVerse Domain Object
**File**: `src/main/java/com/dong/bible/domain/verse/BibleVerse.java`
**Lines**: 63, 84 - Replace TODO comments

```java
// Line 63 - getBookName() 메서드 수정
public String getBookName() {
    // VerseDomainService를 통해 설정된 bookName 반환
    if (this.bookName != null) {
        return this.bookName;
    }
    // 기본값 반환 (VerseDomainService 사용 전)
    return "Book-" + bookId;
}

// Line 84 - getBookAbbreviation() 메서드 수정  
public String getBookAbbreviation() {
    // VerseDomainService를 통해 설정된 bookAbbreviation 반환
    if (this.bookAbbreviation != null) {
        return this.bookAbbreviation;
    }
    // 기본값 반환 (VerseDomainService 사용 전)
    return "BK" + bookId;
}

// Builder에 필드 추가
@Builder
public static class BibleVerseBuilder {
    private String bookName;
    private String bookAbbreviation;
    // ... 기존 필드들
}
```

##### 1.2.3 Update VerseApplicationService
**File**: `src/main/java/com/dong/bible/application/service/VerseApplicationService.java`
**Inject VerseDomainService and use it**

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class VerseApplicationService {
    
    private final BibleVerseRepository verseRepository;
    private final VerseDomainService verseDomainService;  // 추가
    
    // 모든 조회 메서드에서 enrichment 적용
    
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
    
    public ChapterQueryDto getChapter(Long bookId, Integer chapter) {
        log.info("Getting chapter: bookId={}, chapter={}", bookId, chapter);
        
        List<BibleVerse> verses = verseRepository.findByBookIdAndChapter(bookId, chapter);
        
        if (verses.isEmpty()) {
            throw new EntityNotFoundException(
                String.format("Chapter not found: bookId=%d, chapter=%d", bookId, chapter));
        }
        
        // Book 정보 enrichment
        verses = verseDomainService.enrichWithBookInfo(verses);
        
        return ChapterQueryDto.of(bookId, chapter, verses);
    }
    
    // 다른 조회 메서드들도 동일하게 적용...
}
```

##### 1.2.4 Add findByIds to BookRepository
**File**: `src/main/java/com/dong/bible/domain/book/BookRepository.java`

```java
/**
 * 여러 ID로 Book 조회
 * @param ids Book ID 목록
 * @return Book 목록
 */
List<Book> findByIds(Collection<Long> ids);
```

**File**: `src/main/java/com/dong/bible/infrastructure/persistence/repository/BookRepositoryImpl.java`

```java
@Override
public List<Book> findByIds(Collection<Long> ids) {
    if (ids == null || ids.isEmpty()) {
        return new ArrayList<>();
    }
    
    List<BookEntity> entities = jpaRepository.findAllById(ids);
    return entities.stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
}
```

## 🔥 Priority 2: High Priority Issues

### 2.1 ElasticSearch Advanced Features
**Impact**: Advanced search features not working  
**Estimated Time**: 2-3 hours

#### 2.1.1 Implement Synonym Search
**File**: `src/main/java/com/dong/bible/web/controller/VerseSearchController.java`
**Line**: 247

```java
@GetMapping("/synonym")
public AppResponse<EnhancedVerseSearchResponse> searchWithSynonym(
    @RequestParam String query,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "20") int size
) {
    log.info("Synonym search request: query={}", query);
    
    // Synonym expansion 로직 구현
    Set<String> synonyms = synonymService.expandSynonyms(query);
    
    // 확장된 쿼리로 검색
    String expandedQuery = String.join(" OR ", synonyms);
    EnhancedSearchResult result = searchApplicationService.enhancedSearch(
        expandedQuery, page, size
    );
    
    EnhancedVerseSearchResponse response = searchResponseMapper.toEnhancedResponse(result);
    return AppResponse.success(response);
}
```

#### 2.1.2 Create SynonymService
**File**: Create `src/main/java/com/dong/bible/application/service/SynonymService.java`

```java
package com.dong.bible.application.service;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class SynonymService {
    
    private static final Map<String, Set<String>> SYNONYM_MAP = new HashMap<>();
    
    static {
        // 성경 관련 동의어 매핑
        addSynonyms("사랑", "애정", "love", "charity", "affection");
        addSynonyms("믿음", "신앙", "faith", "belief", "trust");
        addSynonyms("소망", "희망", "hope", "expectation");
        addSynonyms("구원", "salvation", "redemption", "deliverance");
        addSynonyms("은혜", "grace", "favor", "blessing");
        addSynonyms("평화", "평안", "peace", "shalom");
        addSynonyms("기도", "prayer", "supplication", "intercession");
        addSynonyms("찬양", "찬송", "praise", "worship", "glorify");
        addSynonyms("회개", "repentance", "contrition", "penitence");
        addSynonyms("거룩", "성결", "holy", "sacred", "sanctified");
        // 더 많은 동의어 추가...
    }
    
    private static void addSynonyms(String... words) {
        Set<String> synonymSet = new HashSet<>(Arrays.asList(words));
        for (String word : words) {
            SYNONYM_MAP.put(word.toLowerCase(), synonymSet);
        }
    }
    
    public Set<String> expandSynonyms(String query) {
        Set<String> expanded = new HashSet<>();
        expanded.add(query);
        
        String[] words = query.toLowerCase().split("\\s+");
        for (String word : words) {
            Set<String> synonyms = SYNONYM_MAP.get(word);
            if (synonyms != null) {
                expanded.addAll(synonyms);
            }
        }
        
        return expanded;
    }
}
```

#### 2.1.3 Implement Multi-Condition Search
**File**: `src/main/java/com/dong/bible/application/service/VerseSearchApplicationService.java`
**Line**: 242

```java
public EnhancedSearchResult advancedSearch(
    String query,
    Long bookId,
    Integer chapter,
    String dateRange,
    String sortBy,
    int page,
    int size
) {
    log.info("Advanced search with multiple conditions");
    
    // Build complex query
    BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
    
    // Add text query
    if (query != null && !query.isEmpty()) {
        boolQuery.must(QueryBuilders.multiMatchQuery(query)
            .field("verseText", 2.0f)
            .field("reference", 1.5f)
            .field("bookName", 1.0f)
            .type(MultiMatchQueryBuilder.Type.BEST_FIELDS));
    }
    
    // Add book filter
    if (bookId != null) {
        boolQuery.filter(QueryBuilders.termQuery("bookId", bookId));
    }
    
    // Add chapter filter
    if (chapter != null) {
        boolQuery.filter(QueryBuilders.termQuery("chapter", chapter));
    }
    
    // Add date range filter if applicable
    if (dateRange != null && !dateRange.isEmpty()) {
        // Parse date range and add filter
        String[] dates = dateRange.split("~");
        if (dates.length == 2) {
            boolQuery.filter(QueryBuilders.rangeQuery("createdAt")
                .gte(dates[0].trim())
                .lte(dates[1].trim()));
        }
    }
    
    // Build sort
    SortBuilder<?> sort = null;
    if ("relevance".equals(sortBy)) {
        sort = SortBuilders.scoreSort().order(SortOrder.DESC);
    } else if ("book".equals(sortBy)) {
        sort = SortBuilders.fieldSort("bookId").order(SortOrder.ASC);
    } else if ("chapter".equals(sortBy)) {
        sort = SortBuilders.fieldSort("chapter").order(SortOrder.ASC);
    }
    
    // Execute search
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
        .query(boolQuery)
        .from(page * size)
        .size(size)
        .highlighter(createHighlightBuilder());
    
    if (sort != null) {
        searchSourceBuilder.sort(sort);
    }
    
    return searchRepository.searchWithEnhancement(searchSourceBuilder);
}
```

### 2.2 Missing Repository Methods
**Impact**: Content-based search not available  
**Estimated Time**: 1 hour

#### 2.2.1 Add findByTextContaining to BibleVerseRepository
**File**: `src/main/java/com/dong/bible/domain/verse/BibleVerseRepository.java`

```java
/**
 * 텍스트 내용으로 구절 검색
 * @param text 검색할 텍스트
 * @return 검색된 구절 목록
 */
List<BibleVerse> findByTextContaining(String text);

/**
 * 텍스트 내용으로 구절 검색 (페이징)
 * @param text 검색할 텍스트
 * @param pageable 페이징 정보
 * @return 검색된 구절 페이지
 */
Page<BibleVerse> findByTextContaining(String text, Pageable pageable);
```

#### 2.2.2 Add JPA Repository Methods
**File**: `src/main/java/com/dong/bible/infrastructure/persistence/repository/BibleVerseJpaRepository.java`

```java
// 텍스트 내용으로 검색
List<BibleVerseEntity> findByVerseTextContainingIgnoreCase(String text);

// 텍스트 내용으로 검색 (페이징)
Page<BibleVerseEntity> findByVerseTextContainingIgnoreCase(String text, Pageable pageable);

// 참조 구절로 검색
List<BibleVerseEntity> findByReferenceContainingIgnoreCase(String reference);

// 복합 검색
@Query("""
    SELECT v FROM BibleVerseEntity v
    WHERE LOWER(v.verseText) LIKE LOWER(CONCAT('%', :keyword, '%'))
       OR LOWER(v.reference) LIKE LOWER(CONCAT('%', :keyword, '%'))
    ORDER BY v.bookId, v.chapter, v.verseNumber
    """)
Page<BibleVerseEntity> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
```

#### 2.2.3 Implement Repository Methods
**File**: `src/main/java/com/dong/bible/infrastructure/persistence/repository/BibleVerseRepositoryImpl.java`

```java
@Override
public List<BibleVerse> findByTextContaining(String text) {
    log.debug("Finding verses by text containing: {}", text);
    
    if (text == null || text.trim().isEmpty()) {
        return new ArrayList<>();
    }
    
    List<BibleVerseEntity> entities = jpaRepository
        .findByVerseTextContainingIgnoreCase(text.trim());
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
    return new PageImpl<>(verses, pageable, entityPage.getTotalElements());
}
```

## 📊 Priority 3: Medium Priority Issues

### 3.1 Complete Response Mappers
**Impact**: Missing fields in API responses  
**Estimated Time**: 1-2 hours

#### 3.1.1 Fix BibleCategoryMapper
**File**: `src/main/java/com/dong/bible/infrastructure/persistence/mapper/BibleCategoryMapper.java`
**Line**: 63

```java
public BibleCategory createFromEntity(BibleCategoryEntity entity) {
    if (entity == null) {
        return null;
    }
    
    return BibleCategory.builder()
        .id(entity.getId())
        .categoryType(entity.getCategoryType())
        .name(entity.getName())
        .description(entity.getDescription())
        .displayOrder(entity.getDisplayOrder())
        .parentId(entity.getParentId())
        .depth(entity.getDepth())
        .isActive(entity.getIsActive())
        .createdAt(entity.getCreatedAt())
        .updatedAt(entity.getUpdatedAt())
        .build();
}
```

#### 3.1.2 Fix DailyVerseResponseMapper
**File**: `src/main/java/com/dong/bible/web/mapper/DailyVerseResponseMapper.java`
**Lines**: 43, 64

```java
// Line 43 - toSimpleResponse 메서드 수정
public DailyVerseSimpleResponse toSimpleResponse(DailyVerseSummaryDto dto) {
    if (dto == null) {
        return null;
    }
    
    return DailyVerseSimpleResponse.builder()
        .id(dto.getId())
        .date(dto.getDate())
        .verseReference(dto.getVerseReference())
        .verseText(dto.getVerseText())
        .bookName(dto.getBookName())  // Book 정보 추가
        .chapter(dto.getChapter())
        .verseNumber(dto.getVerseNumber())
        .build();
}

// Line 64 - toResponse 메서드 수정
public DailyVerseResponse toResponse(DailyVerseDetailDto dto) {
    if (dto == null) {
        return null;
    }
    
    return DailyVerseResponse.builder()
        .id(dto.getId())
        .date(dto.getDate())
        .verseReference(dto.getVerseReference())
        .verseText(dto.getVerseText())
        .bookName(dto.getBookName())  // Book 정보 추가
        .bookAbbreviation(dto.getBookAbbreviation())
        .chapter(dto.getChapter())
        .verseNumber(dto.getVerseNumber())
        .commentary(dto.getCommentary())
        .prayer(dto.getPrayer())
        .imageUrl(dto.getImageUrl())
        .createdAt(dto.getCreatedAt())
        .updatedAt(dto.getUpdatedAt())
        .build();
}
```

#### 3.1.3 Fix BookResponseMapper
**File**: `src/main/java/com/dong/bible/web/mapper/BookResponseMapper.java`
**Line**: 43

```java
public BibleBookResponse toResponse(BookDto dto) {
    if (dto == null) {
        return null;
    }
    
    return BibleBookResponse.builder()
        .id(dto.getId())
        .name(dto.getName())
        .abbreviation(dto.getAbbreviation())
        .testament(dto.getTestament())
        .bookOrder(dto.getBookOrder())
        .chapterCount(dto.getChapterCount())
        .verseCount(dto.getVerseCount())  // 추가 필드
        .category(dto.getCategory())      // 추가 필드
        .author(dto.getAuthor())          // 추가 필드
        .writtenDate(dto.getWrittenDate()) // 추가 필드
        .description(dto.getDescription())  // 추가 필드
        .build();
}
```

## 🚀 Priority 4: New Features

### 4.1 User Management System
**Estimated Time**: 1 week

#### 4.1.1 Domain Objects
```java
// User.java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;
    private String email;
    private String passwordHash;
    private UserRole role;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
}

// UserFavorite.java
@Entity
@Table(name = "user_favorites")
public class UserFavorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long userId;
    private Long verseId;
    private String note;
    private LocalDateTime createdAt;
}

// ReadingHistory.java
@Entity
@Table(name = "reading_history")
public class ReadingHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long userId;
    private Long bookId;
    private Integer chapter;
    private Integer lastVerse;
    private LocalDateTime readAt;
}
```

### 4.2 Enhanced Analytics
**Estimated Time**: 3-4 days

#### 4.2.1 Analytics Service
```java
@Service
public class AnalyticsService {
    
    // 인기 구절 추적
    public List<PopularVerse> getPopularVerses(int limit) {
        // Implementation
    }
    
    // 사용 패턴 분석
    public UsageStatistics getUsageStatistics(LocalDate from, LocalDate to) {
        // Implementation
    }
    
    // 개인화 추천
    public List<RecommendedContent> getRecommendations(Long userId) {
        // Implementation
    }
}
```

## 📝 Testing Strategy

### Unit Tests to Add
1. SermonRepository methods (all new implementations)
2. VerseDomainService (book enrichment logic)
3. SynonymService (synonym expansion)
4. Response mappers (field mapping)

### Integration Tests
1. Sermon CRUD operations
2. ElasticSearch advanced search
3. Book name resolution in verses
4. End-to-end API tests

## 🔧 Configuration Updates

### application.yml Updates
```yaml
# ElasticSearch synonyms configuration
elasticsearch:
  synonyms:
    enabled: true
    dictionary-path: classpath:synonyms.txt
    
# Cache configuration for book lookups
spring:
  cache:
    type: caffeine
    caffeine:
      spec: maximumSize=100,expireAfterWrite=1h
```

## 📚 Dependencies to Add

### build.gradle Updates
```gradle
dependencies {
    // Caching
    implementation 'org.springframework.boot:spring-boot-starter-cache'
    implementation 'com.github.ben-manes.caffeine:caffeine'
    
    // Security (for user management)
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'io.jsonwebtoken:jjwt-api:0.11.5'
    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.11.5'
    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.11.5'
}
```

## 🎯 Success Metrics

### Phase 1 Completion (2 days)
- [ ] All SermonRepository methods implemented
- [ ] BibleVerse book name resolution working
- [ ] Basic CRUD operations functional

### Phase 2 Completion (1 week)
- [ ] ElasticSearch advanced features complete
- [ ] All response mappers fixed
- [ ] Content-based search working

### Phase 3 Completion (2 weeks)
- [ ] User management system implemented
- [ ] Analytics service operational
- [ ] All tests passing with >80% coverage

## 📌 Notes for Implementation

1. **Always test incrementally** - Test each method after implementation
2. **Use transactions properly** - Mark update methods with @Transactional
3. **Handle null cases** - Always check for null inputs
4. **Log appropriately** - Use debug level for normal operations, error for exceptions
5. **Follow existing patterns** - Maintain consistency with current codebase
6. **Document complex logic** - Add JavaDoc for non-trivial methods
7. **Consider performance** - Use batch operations where possible
8. **Maintain backwards compatibility** - Don't break existing APIs

## 🔄 Daily Implementation Checklist

### Day 1
- [ ] Implement SermonRepository basic CRUD
- [ ] Implement SermonRepository query methods
- [ ] Test Sermon endpoints

### Day 2
- [ ] Implement BibleVerse book name resolution
- [ ] Create VerseDomainService
- [ ] Update VerseApplicationService

### Day 3
- [ ] Implement ElasticSearch synonym search
- [ ] Implement multi-condition search
- [ ] Test search functionality

### Day 4
- [ ] Fix all response mappers
- [ ] Add missing repository methods
- [ ] Comprehensive testing

### Day 5
- [ ] Code review and refactoring
- [ ] Performance optimization
- [ ] Documentation update