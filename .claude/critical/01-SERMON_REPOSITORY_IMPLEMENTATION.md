# SermonRepository Implementation Guide

## 📋 Overview
SermonRepository의 22개 미구현 메서드를 완성하는 상세 가이드입니다. 전체 Sermon 기능이 이 구현에 따라 동작하게 됩니다.

## 🚨 Current Status
- **Status**: 🚨 Critical - Sermon 기능 전체 마비
- **Impact**: 22개 메서드가 `UnsupportedOperationException` 발생
- **Estimated Time**: 2-3 hours
- **Priority**: Highest (모든 작업 중 최우선)

## 📂 Files to Modify

### 1. Domain Repository Interface
**File**: `src/main/java/com/dong/bible/domain/sermon/SermonRepository.java`

### 2. JPA Repository Interface  
**File**: `src/main/java/com/dong/bible/infrastructure/persistence/repository/SermonJpaRepository.java`

### 3. Repository Implementation
**File**: `src/main/java/com/dong/bible/infrastructure/persistence/repository/SermonRepositoryImpl.java`

## 🎯 Phase 1: Basic CRUD (30 minutes)

### 1.1 Add Missing findAll() Method

#### Domain Repository Interface
**File**: `src/main/java/com/dong/bible/domain/sermon/SermonRepository.java`
**Add after line 15**:

```java
/**
 * 모든 설교 조회
 * @return 설교 목록
 */
List<Sermon> findAll();
```

#### Repository Implementation
**File**: `src/main/java/com/dong/bible/infrastructure/persistence/repository/SermonRepositoryImpl.java`
**Add after line 36**:

```java
@Override
public List<Sermon> findAll() {
    log.debug("Finding all sermons");
    
    List<SermonEntity> entities = jpaRepository.findAll();
    return mapper.toDomainList(entities);
}
```

## 🔍 Phase 2: Essential Query Methods (1 hour)

### 2.1 Add JPA Repository Methods
**File**: `src/main/java/com/dong/bible/infrastructure/persistence/repository/SermonJpaRepository.java`
**Add these methods**:

```java
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

// ===== 날짜 범위 검색 =====
/**
 * 날짜 범위로 설교 조회 (정렬)
 */
List<SermonEntity> findBySermonDateBetweenOrderBySermonDateDesc(
    LocalDate startDate, 
    LocalDate endDate
);

/**
 * 날짜 범위로 설교 조회 (페이징)
 */
Page<SermonEntity> findBySermonDateBetween(
    LocalDate startDate, 
    LocalDate endDate, 
    Pageable pageable
);

// ===== 목사별 검색 =====
/**
 * 목사 이름으로 설교 조회
 */
List<SermonEntity> findByPastorNameContainingIgnoreCaseOrderBySermonDateDesc(String pastorName);

Page<SermonEntity> findByPastorNameContainingIgnoreCase(String pastorName, Pageable pageable);

// ===== 교회별 검색 =====
/**
 * 교회 이름으로 설교 조회
 */
List<SermonEntity> findByChurchNameContainingIgnoreCaseOrderBySermonDateDesc(String churchName);

Page<SermonEntity> findByChurchNameContainingIgnoreCase(String churchName, Pageable pageable);

// ===== 태그 검색 =====
/**
 * 태그로 설교 조회
 */
@Query("""
    SELECT s FROM SermonEntity s 
    WHERE LOWER(s.tags) LIKE LOWER(CONCAT('%', :tag, '%'))
    ORDER BY s.sermonDate DESC
    """)
List<SermonEntity> findByTagContaining(@Param("tag") String tag);

/**
 * 여러 태그로 설교 조회
 */
@Query("""
    SELECT DISTINCT s FROM SermonEntity s 
    WHERE EXISTS (
        SELECT 1 FROM SermonEntity s2 
        WHERE s2.id = s.id 
        AND (
            SELECT COUNT(tag) FROM (
                SELECT TRIM(LOWER(value)) as tag 
                FROM STRING_SPLIT(s2.tags, ',')
            ) tags 
            WHERE tag IN (:tags)
        ) > 0
    )
    ORDER BY s.sermonDate DESC
    """)
List<SermonEntity> findByTagsContaining(@Param("tags") List<String> tags);

// ===== 존재 확인 =====
/**
 * 제목과 날짜로 설교 존재 확인
 */
boolean existsByTitleAndSermonDate(String title, LocalDate sermonDate);

// ===== 카운트 메서드 =====
/**
 * 목사별 설교 수
 */
long countByPastorNameContainingIgnoreCase(String pastorName);

/**
 * 교회별 설교 수
 */
long countByChurchNameContainingIgnoreCase(String churchName);

/**
 * 연도별 설교 수
 */
@Query("SELECT COUNT(s) FROM SermonEntity s WHERE YEAR(s.sermonDate) = :year")
long countByYear(@Param("year") int year);

/**
 * 월별 설교 수
 */
@Query("""
    SELECT COUNT(s) FROM SermonEntity s 
    WHERE YEAR(s.sermonDate) = :year AND MONTH(s.sermonDate) = :month
    """)
long countByYearAndMonth(@Param("year") int year, @Param("month") int month);

/**
 * YouTube 비디오 있는 설교 수
 */
long countByYoutubeUrlIsNotNull();

/**
 * 오디오 파일 있는 설교 수
 */  
long countByAudioUrlIsNotNull();

// ===== 업데이트 메서드 =====
/**
 * 조회수 증가
 */
@Modifying
@Query("UPDATE SermonEntity s SET s.viewCount = s.viewCount + 1 WHERE s.id = :id")
void incrementViewCount(@Param("id") Long id);

/**
 * 좋아요 수 증가
 */
@Modifying
@Query("UPDATE SermonEntity s SET s.likeCount = s.likeCount + 1 WHERE s.id = :id")
void incrementLikeCount(@Param("id") Long id);

/**
 * 좋아요 수 감소
 */
@Modifying
@Query("""
    UPDATE SermonEntity s 
    SET s.likeCount = CASE 
        WHEN s.likeCount > 0 THEN s.likeCount - 1 
        ELSE 0 
    END 
    WHERE s.id = :id
    """)
void decrementLikeCount(@Param("id") Long id);
```

### 2.2 Implement Repository Methods
**File**: `src/main/java/com/dong/bible/infrastructure/persistence/repository/SermonRepositoryImpl.java`
**Replace all `UnsupportedOperationException` methods**:

```java
// ===== 기본 조회 메서드 =====

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
    
    if (startDate.isAfter(endDate)) {
        throw new IllegalArgumentException("Start date must be before or equal to end date");
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
        .findByPastorNameContainingIgnoreCaseOrderBySermonDateDesc(pastorName.trim());
    return mapper.toDomainList(entities);
}

@Override
public Page<Sermon> findByPastor(String pastorName, Pageable pageable) {
    log.debug("Finding sermons by pastor with paging: {}", pastorName);
    
    if (pastorName == null || pastorName.trim().isEmpty()) {
        return Page.empty(pageable);
    }
    
    Page<SermonEntity> entityPage = jpaRepository
        .findByPastorNameContainingIgnoreCase(pastorName.trim(), pageable);
    
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
        .findByChurchNameContainingIgnoreCaseOrderBySermonDateDesc(churchName.trim());
    return mapper.toDomainList(entities);
}

@Override
public Page<Sermon> findByChurch(String churchName, Pageable pageable) {
    log.debug("Finding sermons by church with paging: {}", churchName);
    
    if (churchName == null || churchName.trim().isEmpty()) {
        return Page.empty(pageable);
    }
    
    Page<SermonEntity> entityPage = jpaRepository
        .findByChurchNameContainingIgnoreCase(churchName.trim(), pageable);
    
    List<Sermon> sermons = mapper.toDomainList(entityPage.getContent());
    return new PageImpl<>(sermons, pageable, entityPage.getTotalElements());
}

@Override
public List<Sermon> findByTag(String tag) {
    log.debug("Finding sermons by tag: {}", tag);
    
    if (tag == null || tag.trim().isEmpty()) {
        return new ArrayList<>();
    }
    
    List<SermonEntity> entities = jpaRepository.findByTagContaining(tag.trim());
    return mapper.toDomainList(entities);
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
        .map(String::toLowerCase)
        .filter(t -> !t.isEmpty())
        .collect(Collectors.toList());
    
    if (cleanedTags.isEmpty()) {
        return new ArrayList<>();
    }
    
    List<SermonEntity> entities = jpaRepository.findByTagsContaining(cleanedTags);
    return mapper.toDomainList(entities);
}

// ===== 존재 확인 메서드 =====

@Override
public boolean existsSermon(String title, LocalDate sermonDate) {
    log.debug("Checking if sermon exists: title={}, date={}", title, sermonDate);
    
    if (title == null || title.trim().isEmpty() || sermonDate == null) {
        return false;
    }
    
    return jpaRepository.existsByTitleAndSermonDate(title.trim(), sermonDate);
}

// ===== 카운트 메서드 =====

@Override
public long countByPastor(String pastorName) {
    log.debug("Counting sermons by pastor: {}", pastorName);
    
    if (pastorName == null || pastorName.trim().isEmpty()) {
        return 0L;
    }
    
    return jpaRepository.countByPastorNameContainingIgnoreCase(pastorName.trim());
}

@Override
public long countByChurch(String churchName) {
    log.debug("Counting sermons by church: {}", churchName);
    
    if (churchName == null || churchName.trim().isEmpty()) {
        return 0L;
    }
    
    return jpaRepository.countByChurchNameContainingIgnoreCase(churchName.trim());
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

// ===== 업데이트 메서드 =====

@Override
@Transactional
public void incrementViewCount(Long sermonId) {
    log.debug("Incrementing view count for sermon: {}", sermonId);
    
    if (sermonId == null) {
        throw new IllegalArgumentException("Sermon ID must not be null");
    }
    
    // 설교 존재 여부 확인
    if (!jpaRepository.existsById(sermonId)) {
        throw new EntityNotFoundException("Sermon not found: id=" + sermonId);
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
    
    // 설교 존재 여부 확인
    if (!jpaRepository.existsById(sermonId)) {
        throw new EntityNotFoundException("Sermon not found: id=" + sermonId);
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
    
    // 설교 존재 여부 확인
    if (!jpaRepository.existsById(sermonId)) {
        throw new EntityNotFoundException("Sermon not found: id=" + sermonId);
    }
    
    jpaRepository.decrementLikeCount(sermonId);
}
```

## 🚀 Phase 3: Advanced Features (1 hour)

### 3.1 Add Advanced JPA Repository Methods

```java
// ===== 인기/추천 설교 조회 =====

/**
 * 최근 설교 조회
 */
List<SermonEntity> findTop10ByOrderBySermonDateDesc();

/**
 * 인기 설교 조회 (조회수 기준)
 */
List<SermonEntity> findTop10ByOrderByViewCountDesc();

/**
 * 좋아요 많은 설교 조회
 */
List<SermonEntity> findTop10ByOrderByLikeCountDesc();

/**
 * 트렌딩 설교 조회 (최근 N일 내 조회수 높은 순)
 */
@Query("""
    SELECT s FROM SermonEntity s 
    WHERE s.sermonDate >= :dateFrom 
    ORDER BY s.viewCount DESC
    """)
List<SermonEntity> findTrendingSermons(
    @Param("dateFrom") LocalDate dateFrom, 
    Pageable pageable
);

/**
 * 추천 설교 조회 (복합 점수 기반)
 */
@Query("""
    SELECT s FROM SermonEntity s 
    ORDER BY (s.viewCount * 0.5 + s.likeCount * 2) DESC
    """)
List<SermonEntity> findRecommendedSermons(Pageable pageable);

/**
 * YouTube 비디오가 있는 설교 조회
 */
List<SermonEntity> findByYoutubeUrlIsNotNullOrderBySermonDateDesc(Pageable pageable);

/**
 * 키워드 검색
 */
@Query("""
    SELECT s FROM SermonEntity s 
    WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
       OR LOWER(s.summary) LIKE LOWER(CONCAT('%', :keyword, '%'))
       OR LOWER(s.mainText) LIKE LOWER(CONCAT('%', :keyword, '%'))
    ORDER BY s.sermonDate DESC
    """)
Page<SermonEntity> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
```

### 3.2 Implement Advanced Methods

```java
@Override
public List<Sermon> findRecentSermons(int limit) {
    log.debug("Finding recent {} sermons", limit);
    
    if (limit <= 0) {
        throw new IllegalArgumentException("Limit must be positive");
    }
    
    List<SermonEntity> entities = jpaRepository.findTop10ByOrderBySermonDateDesc();
    return mapper.toDomainList(entities).stream()
        .limit(Math.min(limit, 100))
        .collect(Collectors.toList());
}

@Override
public List<Sermon> findPopularSermons(int limit) {
    log.debug("Finding popular {} sermons", limit);
    
    if (limit <= 0) {
        throw new IllegalArgumentException("Limit must be positive");
    }
    
    List<SermonEntity> entities = jpaRepository.findTop10ByOrderByViewCountDesc();
    return mapper.toDomainList(entities).stream()
        .limit(Math.min(limit, 100))
        .collect(Collectors.toList());
}

@Override
public List<Sermon> findTrendingSermons(int days, int limit) {
    log.debug("Finding trending sermons from last {} days, limit {}", days, limit);
    
    if (days <= 0 || limit <= 0) {
        throw new IllegalArgumentException("Days and limit must be positive");
    }
    
    LocalDate dateFrom = LocalDate.now().minusDays(days);
    PageRequest pageRequest = PageRequest.of(0, Math.min(limit, 100));
    
    List<SermonEntity> entities = jpaRepository.findTrendingSermons(dateFrom, pageRequest);
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

## ✅ Implementation Checklist

### Phase 1: Basic CRUD
- [ ] Add `findAll()` to domain repository interface
- [ ] Implement `findAll()` in repository implementation

### Phase 2: Essential Queries
- [ ] Add all JPA repository query methods (dates, pastor, church, tags)
- [ ] Add count methods for statistics
- [ ] Add increment/decrement methods for counters
- [ ] Replace all UnsupportedOperationException methods
- [ ] Add proper validation and error handling

### Phase 3: Advanced Features
- [ ] Add trending/popular sermon queries
- [ ] Add keyword search functionality
- [ ] Add YouTube video filtering
- [ ] Implement recommendation algorithm

### Testing
- [ ] Unit test each method with various scenarios
- [ ] Integration test with actual database
- [ ] Test error cases and validation

## 🧪 Testing Strategy

### Unit Tests to Create

```java
@Test
@DisplayName("날짜 범위로 설교 조회 성공")
void findByDateRange_Success() {
    // Given
    LocalDate startDate = LocalDate.of(2024, 1, 1);
    LocalDate endDate = LocalDate.of(2024, 12, 31);
    
    // When & Then
    List<Sermon> sermons = sermonRepository.findByDateRange(startDate, endDate);
    assertThat(sermons).isNotNull();
}

@Test
@DisplayName("잘못된 날짜 범위로 조회 시 예외 발생")
void findByDateRange_InvalidRange_ThrowsException() {
    // Given
    LocalDate startDate = LocalDate.of(2024, 12, 31);
    LocalDate endDate = LocalDate.of(2024, 1, 1);
    
    // When & Then
    assertThatThrownBy(() -> sermonRepository.findByDateRange(startDate, endDate))
        .isInstanceOf(IllegalArgumentException.class);
}
```

## ⚠️ Important Notes

1. **Import Statements**: 다음 import들을 추가해야 합니다:
   ```java
   import org.springframework.data.domain.Page;
   import org.springframework.data.domain.PageImpl;
   import org.springframework.data.domain.Pageable;
   import org.springframework.data.domain.PageRequest;
   import org.springframework.transaction.annotation.Transactional;
   import jakarta.persistence.EntityNotFoundException;
   import java.time.LocalDate;
   import java.util.stream.Collectors;
   import java.util.Objects;
   ```

2. **Transaction Management**: 모든 업데이트 메서드는 `@Transactional` 어노테이션 필요

3. **Validation**: 모든 입력 파라미터에 대한 null 체크와 유효성 검증 필수

4. **Performance**: 페이징을 사용하여 대용량 데이터 처리

5. **Error Handling**: 명확한 에러 메시지와 적절한 예외 타입 사용

## 🎯 Success Criteria

구현 완료 후 다음이 가능해야 합니다:
- ✅ 모든 Sermon API 엔드포인트가 정상 동작
- ✅ 날짜별, 목사별, 교회별, 태그별 설교 검색
- ✅ 조회수, 좋아요 수 증가/감소 기능
- ✅ 인기, 추천, 트렌딩 설교 조회
- ✅ 키워드 검색 및 페이징 지원
- ✅ 모든 통계 정보 제공

이 가이드를 따라 구현하면 Sermon 기능이 완전히 복구됩니다.