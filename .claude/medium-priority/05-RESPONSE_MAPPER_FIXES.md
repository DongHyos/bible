# Response Mapper Fixes Guide

## 📋 Overview
Response Mapper들의 누락된 필드 매핑을 완성하여 API 응답에서 모든 필요한 정보가 올바르게 표시되도록 하는 가이드입니다.

## 🚨 Current Issues
- **BibleCategoryMapper**: Line 63 - Factory method 미완성
- **DailyVerseResponseMapper**: Lines 43, 64 - Book 관련 필드 누락
- **BookResponseMapper**: Line 43 - 메타데이터 필드 누락
- **기타 Mapper**: 일관성 없는 필드 매핑

## 📂 Files to Fix

### 1. Category Mapper
- `src/main/java/com/dong/bible/infrastructure/persistence/mapper/BibleCategoryMapper.java`

### 2. Daily Verse Response Mapper  
- `src/main/java/com/dong/bible/web/mapper/DailyVerseResponseMapper.java`

### 3. Book Response Mapper
- `src/main/java/com/dong/bible/web/mapper/BookResponseMapper.java`

### 4. Other Response Mappers
- Various response mappers requiring consistency improvements

## 🎯 Phase 1: Fix BibleCategoryMapper

### 1.1 Complete Factory Method
**File**: `src/main/java/com/dong/bible/infrastructure/persistence/mapper/BibleCategoryMapper.java`
**Fix Line 63**:

```java
/**
 * Entity에서 Domain 객체 생성하는 팩토리 메서드
 * @param entity BibleCategoryEntity
 * @return BibleCategory domain 객체
 */
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

/**
 * Domain 객체에서 Entity 생성
 * @param domain BibleCategory domain 객체
 * @return BibleCategoryEntity
 */
public BibleCategoryEntity toEntity(BibleCategory domain) {
    if (domain == null) {
        return null;
    }
    
    return BibleCategoryEntity.builder()
        .id(domain.getId())
        .categoryType(domain.getCategoryType())
        .name(domain.getName())
        .description(domain.getDescription())
        .displayOrder(domain.getDisplayOrder())
        .parentId(domain.getParentId())
        .depth(domain.getDepth())
        .isActive(domain.getIsActive())
        .createdAt(domain.getCreatedAt())
        .updatedAt(domain.getUpdatedAt())
        .build();
}

/**
 * Entity 리스트를 Domain 리스트로 변환
 * @param entities Entity 리스트
 * @return Domain 리스트
 */
public List<BibleCategory> toDomainList(List<BibleCategoryEntity> entities) {
    if (entities == null || entities.isEmpty()) {
        return new ArrayList<>();
    }
    
    return entities.stream()
        .map(this::toDomain)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
}

/**
 * Domain 리스트를 Entity 리스트로 변환
 * @param domains Domain 리스트
 * @return Entity 리스트
 */
public List<BibleCategoryEntity> toEntityList(List<BibleCategory> domains) {
    if (domains == null || domains.isEmpty()) {
        return new ArrayList<>();
    }
    
    return domains.stream()
        .map(this::toEntity)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
}

/**
 * 카테고리 계층 구조 검증
 * @param domain BibleCategory
 * @return 유효성 여부
 */
public boolean validateCategoryHierarchy(BibleCategory domain) {
    if (domain == null) {
        return false;
    }
    
    // 루트 카테고리 (parentId가 null)는 depth가 0이어야 함
    if (domain.getParentId() == null && domain.getDepth() != 0) {
        return false;
    }
    
    // 하위 카테고리는 depth가 0보다 커야 함
    if (domain.getParentId() != null && domain.getDepth() <= 0) {
        return false;
    }
    
    // displayOrder는 양수여야 함
    if (domain.getDisplayOrder() != null && domain.getDisplayOrder() < 0) {
        return false;
    }
    
    return true;
}
```

## 🎯 Phase 2: Fix DailyVerseResponseMapper

### 2.1 Add Missing Book Fields
**File**: `src/main/java/com/dong/bible/web/mapper/DailyVerseResponseMapper.java`
**Fix Lines 43, 64**:

```java
/**
 * DailyVerseSummaryDto를 DailyVerseSimpleResponse로 변환
 * @param dto DailyVerseSummaryDto
 * @return DailyVerseSimpleResponse
 */
public DailyVerseSimpleResponse toSimpleResponse(DailyVerseSummaryDto dto) {
    if (dto == null) {
        return null;
    }
    
    return DailyVerseSimpleResponse.builder()
        .id(dto.getId())
        .date(dto.getDate())
        .verseReference(dto.getVerseReference())
        .verseText(dto.getVerseText())
        // Book 정보 추가
        .bookId(dto.getBookId())
        .bookName(dto.getBookName())
        .bookAbbreviation(dto.getBookAbbreviation())
        .chapter(dto.getChapter())
        .verseNumber(dto.getVerseNumber())
        // 추가 정보
        .imageUrl(dto.getImageUrl())
        .isActive(dto.getIsActive())
        .createdAt(dto.getCreatedAt())
        .build();
}

/**
 * DailyVerseDetailDto를 DailyVerseResponse로 변환
 * @param dto DailyVerseDetailDto
 * @return DailyVerseResponse
 */
public DailyVerseResponse toResponse(DailyVerseDetailDto dto) {
    if (dto == null) {
        return null;
    }
    
    return DailyVerseResponse.builder()
        .id(dto.getId())
        .date(dto.getDate())
        .verseReference(dto.getVerseReference())
        .verseText(dto.getVerseText())
        // Book 정보 추가 (Line 64 수정)
        .bookId(dto.getBookId())
        .bookName(dto.getBookName())
        .bookAbbreviation(dto.getBookAbbreviation())
        .chapter(dto.getChapter())
        .verseNumber(dto.getVerseNumber())
        // 상세 정보
        .commentary(dto.getCommentary())
        .prayer(dto.getPrayer())
        .meditation(dto.getMeditation())
        .applicationPoint(dto.getApplicationPoint())
        // 미디어 정보
        .imageUrl(dto.getImageUrl())
        .audioUrl(dto.getAudioUrl())
        .videoUrl(dto.getVideoUrl())
        // 메타 정보
        .isActive(dto.getIsActive())
        .viewCount(dto.getViewCount())
        .likeCount(dto.getLikeCount())
        .shareCount(dto.getShareCount())
        .createdAt(dto.getCreatedAt())
        .updatedAt(dto.getUpdatedAt())
        .build();
}

/**
 * 일일 구절 목록을 간단한 응답으로 변환
 * @param dtos DTO 목록
 * @return 응답 목록
 */
public List<DailyVerseSimpleResponse> toSimpleResponseList(List<DailyVerseSummaryDto> dtos) {
    if (dtos == null || dtos.isEmpty()) {
        return new ArrayList<>();
    }
    
    return dtos.stream()
        .map(this::toSimpleResponse)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
}

/**
 * 일일 구절 목록을 상세 응답으로 변환
 * @param dtos DTO 목록
 * @return 응답 목록
 */
public List<DailyVerseResponse> toResponseList(List<DailyVerseDetailDto> dtos) {
    if (dtos == null || dtos.isEmpty()) {
        return new ArrayList<>();
    }
    
    return dtos.stream()
        .map(this::toResponse)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
}

/**
 * 페이지 변환
 * @param dtoPage DTO 페이지
 * @return 응답 페이지
 */
public Page<DailyVerseSimpleResponse> toSimpleResponsePage(Page<DailyVerseSummaryDto> dtoPage) {
    if (dtoPage == null) {
        return Page.empty();
    }
    
    List<DailyVerseSimpleResponse> responses = toSimpleResponseList(dtoPage.getContent());
    return new PageImpl<>(responses, dtoPage.getPageable(), dtoPage.getTotalElements());
}

/**
 * 일일 구절 응답 검증
 * @param response 응답 객체
 * @return 유효성 여부
 */
public boolean validateDailyVerseResponse(DailyVerseResponse response) {
    if (response == null) {
        return false;
    }
    
    // 필수 필드 검증
    if (response.getId() == null || response.getDate() == null || 
        response.getVerseText() == null || response.getVerseText().trim().isEmpty()) {
        return false;
    }
    
    // Book 정보 검증
    if (response.getBookId() == null || response.getBookName() == null ||
        response.getChapter() == null || response.getVerseNumber() == null) {
        return false;
    }
    
    // 숫자 필드 검증
    if (response.getChapter() < 1 || response.getVerseNumber() < 1) {
        return false;
    }
    
    return true;
}
```

### 2.2 Update DTO Classes (if needed)
**Ensure DTOs have all required fields**:

```java
// DailyVerseSummaryDto.java - 필요한 경우 필드 추가
@Getter
@Builder
@ToString
public class DailyVerseSummaryDto {
    private final Long id;
    private final LocalDate date;
    private final String verseReference;
    private final String verseText;
    
    // Book 정보 필드
    private final Long bookId;
    private final String bookName;
    private final String bookAbbreviation;
    private final Integer chapter;
    private final Integer verseNumber;
    
    // 추가 필드
    private final String imageUrl;
    private final Boolean isActive;
    private final LocalDateTime createdAt;
    
    public static DailyVerseSummaryDto from(DailyVerse dailyVerse) {
        // BibleVerse 정보가 enrichment된 상태여야 함
        return DailyVerseSummaryDto.builder()
            .id(dailyVerse.getId())
            .date(dailyVerse.getDate())
            .verseReference(dailyVerse.getVerseReference())
            .verseText(dailyVerse.getVerseText())
            .bookId(dailyVerse.getBookId())
            .bookName(dailyVerse.getBookName())
            .bookAbbreviation(dailyVerse.getBookAbbreviation())
            .chapter(dailyVerse.getChapter())
            .verseNumber(dailyVerse.getVerseNumber())
            .imageUrl(dailyVerse.getImageUrl())
            .isActive(dailyVerse.getIsActive())
            .createdAt(dailyVerse.getCreatedAt())
            .build();
    }
}
```

## 🎯 Phase 3: Fix BookResponseMapper

### 3.1 Add Missing Metadata Fields
**File**: `src/main/java/com/dong/bible/web/mapper/BookResponseMapper.java`
**Fix Line 43**:

```java
/**
 * BookDto를 BibleBookResponse로 변환
 * @param dto BookDto
 * @return BibleBookResponse
 */
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
        // 추가 메타데이터 필드 (Line 43 수정)
        .verseCount(dto.getVerseCount())
        .category(dto.getCategory())
        .author(dto.getAuthor())
        .writtenDate(dto.getWrittenDate())
        .writtenLocation(dto.getWrittenLocation())
        .description(dto.getDescription())
        .keyThemes(dto.getKeyThemes())
        .keyVerses(dto.getKeyVerses())
        // 언어별 정보
        .nameKorean(dto.getNameKorean())
        .nameEnglish(dto.getNameEnglish())
        .nameHebrew(dto.getNameHebrew())
        .nameGreek(dto.getNameGreek())
        // 통계 정보
        .totalWords(dto.getTotalWords())
        .averageVersesPerChapter(dto.getAverageVersesPerChapter())
        // 시스템 정보
        .createdAt(dto.getCreatedAt())
        .updatedAt(dto.getUpdatedAt())
        .build();
}

/**
 * BookDto를 BibleBookMiniResponse로 변환 (간소화된 정보)
 * @param dto BookDto
 * @return BibleBookMiniResponse
 */
public BibleBookMiniResponse toMiniResponse(BookDto dto) {
    if (dto == null) {
        return null;
    }
    
    return BibleBookMiniResponse.builder()
        .id(dto.getId())
        .name(dto.getName())
        .abbreviation(dto.getAbbreviation())
        .testament(dto.getTestament())
        .bookOrder(dto.getBookOrder())
        .chapterCount(dto.getChapterCount())
        .verseCount(dto.getVerseCount())
        .build();
}

/**
 * 책 목록을 응답 목록으로 변환
 * @param dtos DTO 목록
 * @return 응답 목록
 */
public List<BibleBookResponse> toResponseList(List<BookDto> dtos) {
    if (dtos == null || dtos.isEmpty()) {
        return new ArrayList<>();
    }
    
    return dtos.stream()
        .map(this::toResponse)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
}

/**
 * 책 목록을 간소화된 응답 목록으로 변환
 * @param dtos DTO 목록
 * @return 간소화된 응답 목록
 */
public List<BibleBookMiniResponse> toMiniResponseList(List<BookDto> dtos) {
    if (dtos == null || dtos.isEmpty()) {
        return new ArrayList<>();
    }
    
    return dtos.stream()
        .map(this::toMiniResponse)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
}

/**
 * 구약/신약별로 책 목록 그룹화
 * @param dtos 전체 책 DTO 목록
 * @return 구약/신약별 그룹화된 응답
 */
public Map<String, List<BibleBookMiniResponse>> groupByTestament(List<BookDto> dtos) {
    if (dtos == null || dtos.isEmpty()) {
        return new HashMap<>();
    }
    
    return dtos.stream()
        .collect(Collectors.groupingBy(
            BookDto::getTestament,
            Collectors.mapping(
                this::toMiniResponse,
                Collectors.filtering(Objects::nonNull, Collectors.toList())
            )
        ));
}

/**
 * 책 정보 응답 검증
 * @param response 응답 객체
 * @return 유효성 여부
 */
public boolean validateBookResponse(BibleBookResponse response) {
    if (response == null) {
        return false;
    }
    
    // 필수 필드 검증
    if (response.getId() == null || response.getName() == null || 
        response.getName().trim().isEmpty()) {
        return false;
    }
    
    // 구약/신약 검증
    if (response.getTestament() == null || 
        (!response.getTestament().equals("구약") && !response.getTestament().equals("신약"))) {
        return false;
    }
    
    // 숫자 필드 검증
    if (response.getBookOrder() == null || response.getBookOrder() < 1 ||
        response.getChapterCount() == null || response.getChapterCount() < 1) {
        return false;
    }
    
    return true;
}
```

### 3.2 Update BookDto (if needed)
**Ensure BookDto has all required fields**:

```java
// BookDto.java - 추가 필드가 필요한 경우
@Getter
@Builder
@ToString
public class BookDto {
    // 기본 정보
    private final Long id;
    private final String name;
    private final String abbreviation;
    private final String testament;
    private final Integer bookOrder;
    private final Integer chapterCount;
    
    // 추가 메타데이터
    private final Long verseCount;
    private final String category;
    private final String author;
    private final String writtenDate;
    private final String writtenLocation;
    private final String description;
    private final String keyThemes;
    private final String keyVerses;
    
    // 다국어 이름
    private final String nameKorean;
    private final String nameEnglish;
    private final String nameHebrew;
    private final String nameGreek;
    
    // 통계 정보
    private final Long totalWords;
    private final Double averageVersesPerChapter;
    
    // 시스템 정보
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    
    public static BookDto from(Book book) {
        return BookDto.builder()
            .id(book.getId())
            .name(book.getName())
            .abbreviation(book.getAbbreviation())
            .testament(book.getTestament())
            .bookOrder(book.getBookOrder())
            .chapterCount(book.getChapterCount())
            .verseCount(book.getVerseCount())
            .category(book.getCategory())
            .author(book.getAuthor())
            .writtenDate(book.getWrittenDate())
            .writtenLocation(book.getWrittenLocation())
            .description(book.getDescription())
            .keyThemes(book.getKeyThemes())
            .keyVerses(book.getKeyVerses())
            .nameKorean(book.getNameKorean())
            .nameEnglish(book.getNameEnglish())
            .nameHebrew(book.getNameHebrew())
            .nameGreek(book.getNameGreek())
            .totalWords(book.getTotalWords())
            .averageVersesPerChapter(book.getAverageVersesPerChapter())
            .createdAt(book.getCreatedAt())
            .updatedAt(book.getUpdatedAt())
            .build();
    }
}
```

## 🎯 Phase 4: Other Response Mapper Improvements

### 4.1 SermonResponseMapper Enhancements
**File**: `src/main/java/com/dong/bible/web/mapper/SermonResponseMapper.java`

```java
/**
 * 설교 응답 매퍼 개선
 */
@Component
public class SermonResponseMapper {
    
    /**
     * SermonDto를 SermonResponse로 변환
     */
    public SermonResponse toResponse(SermonDto dto) {
        if (dto == null) {
            return null;
        }
        
        return SermonResponse.builder()
            .id(dto.getId())
            .title(dto.getTitle())
            .summary(dto.getSummary())
            .mainText(dto.getMainText())
            .pastorName(dto.getPastorName())
            .churchName(dto.getChurchName())
            .sermonDate(dto.getSermonDate())
            .sermonSeries(dto.getSermonSeries())
            .tags(dto.getTags())
            // 미디어 정보
            .audioUrl(dto.getAudioUrl())
            .videoUrl(dto.getVideoUrl())
            .youtubeUrl(dto.getYoutubeUrl())
            .thumbnailUrl(dto.getThumbnailUrl())
            // 성경 참조
            .bibleReferences(dto.getBibleReferences())
            .mainVerse(dto.getMainVerse())
            // 통계 정보
            .viewCount(dto.getViewCount())
            .likeCount(dto.getLikeCount())
            .shareCount(dto.getShareCount())
            .downloadCount(dto.getDownloadCount())
            // 메타 정보
            .duration(dto.getDuration())
            .language(dto.getLanguage())
            .isActive(dto.getIsActive())
            .createdAt(dto.getCreatedAt())
            .updatedAt(dto.getUpdatedAt())
            .build();
    }
    
    /**
     * 간소화된 설교 응답 변환
     */
    public SermonSimpleResponse toSimpleResponse(SermonDto dto) {
        if (dto == null) {
            return null;
        }
        
        return SermonSimpleResponse.builder()
            .id(dto.getId())
            .title(dto.getTitle())
            .summary(dto.getSummary())
            .pastorName(dto.getPastorName())
            .churchName(dto.getChurchName())
            .sermonDate(dto.getSermonDate())
            .thumbnailUrl(dto.getThumbnailUrl())
            .mainVerse(dto.getMainVerse())
            .viewCount(dto.getViewCount())
            .duration(dto.getDuration())
            .build();
    }
}
```

### 4.2 VerseResponseMapper Enhancements
**File**: `src/main/java/com/dong/bible/web/mapper/VerseResponseMapper.java`

```java
/**
 * 구절 응답 매퍼 개선
 */
@Component
public class VerseResponseMapper {
    
    /**
     * VerseQueryDto를 VerseResponse로 변환
     */
    public VerseResponse toResponse(VerseQueryDto dto) {
        if (dto == null) {
            return null;
        }
        
        return VerseResponse.builder()
            .id(dto.getId())
            .bookId(dto.getBookId())
            .bookName(dto.getBookName())
            .bookAbbreviation(dto.getBookAbbreviation())
            .chapter(dto.getChapter())
            .verseNumber(dto.getVerseNumber())
            .verseText(dto.getVerseText())
            .reference(dto.getReference())
            .fullReference(dto.getFullReference())
            .shortReference(dto.getShortReference())
            .testament(dto.getTestament())
            .createdAt(dto.getCreatedAt())
            .updatedAt(dto.getUpdatedAt())
            .build();
    }
    
    /**
     * 간소화된 구절 응답 변환
     */
    public VerseSimpleResponse toSimpleResponse(VerseQueryDto dto) {
        if (dto == null) {
            return null;
        }
        
        return VerseSimpleResponse.builder()
            .id(dto.getId())
            .bookName(dto.getBookName())
            .chapter(dto.getChapter())
            .verseNumber(dto.getVerseNumber())
            .verseText(dto.getVerseText())
            .fullReference(dto.getFullReference())
            .build();
    }
    
    /**
     * 검색 결과 응답 변환
     */
    public VerseSearchResponse toSearchResponse(VerseSearchResultDto dto) {
        if (dto == null) {
            return null;
        }
        
        return VerseSearchResponse.builder()
            .id(dto.getId())
            .bookName(dto.getBookName())
            .chapter(dto.getChapter())
            .verseNumber(dto.getVerseNumber())
            .verseText(dto.getVerseText())
            .fullReference(dto.getFullReference())
            .highlightedText(dto.getHighlightedText())
            .matchScore(dto.getMatchScore())
            .matchType(dto.getMatchType())
            .build();
    }
}
```

## ✅ Implementation Checklist

### BibleCategoryMapper
- [ ] Complete factory method `createFromEntity()`
- [ ] Add `toEntity()` method for reverse mapping
- [ ] Add list conversion methods
- [ ] Add hierarchy validation method
- [ ] Test all mapping methods

### DailyVerseResponseMapper
- [ ] Fix Line 43 - Add book fields to `toSimpleResponse()`
- [ ] Fix Line 64 - Add book fields to `toResponse()`
- [ ] Add list conversion methods
- [ ] Add page conversion methods
- [ ] Add response validation method
- [ ] Update DTOs with missing fields

### BookResponseMapper  
- [ ] Fix Line 43 - Add metadata fields to `toResponse()`
- [ ] Add `toMiniResponse()` for simplified responses
- [ ] Add list conversion methods
- [ ] Add testament grouping method
- [ ] Add response validation method
- [ ] Update DTOs with missing fields

### Other Mappers
- [ ] Enhance SermonResponseMapper with all fields
- [ ] Enhance VerseResponseMapper with search support
- [ ] Ensure consistent mapping patterns across all mappers
- [ ] Add validation methods to all mappers

### Testing & Validation
- [ ] Unit test all mapper methods
- [ ] Test null input handling
- [ ] Test validation methods
- [ ] Integration test with updated DTOs
- [ ] Verify API responses contain all fields

## 🧪 Testing Strategy

### Unit Tests for Mappers

```java
@ExtendWith(MockitoExtension.class)
class DailyVerseResponseMapperTest {
    
    @InjectMocks
    private DailyVerseResponseMapper mapper;
    
    @Test
    @DisplayName("DailyVerseSummaryDto를 SimpleResponse로 변환 - Book 정보 포함")
    void toSimpleResponse_WithBookInfo_Success() {
        // Given
        DailyVerseSummaryDto dto = DailyVerseSummaryDto.builder()
            .id(1L)
            .date(LocalDate.of(2024, 1, 1))
            .verseReference("창세기 1:1")
            .verseText("태초에 하나님이 천지를 창조하시니라")
            .bookId(1L)
            .bookName("창세기")
            .bookAbbreviation("창")
            .chapter(1)
            .verseNumber(1)
            .build();
        
        // When
        DailyVerseSimpleResponse result = mapper.toSimpleResponse(dto);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getBookName()).isEqualTo("창세기");
        assertThat(result.getBookAbbreviation()).isEqualTo("창");
        assertThat(result.getChapter()).isEqualTo(1);
        assertThat(result.getVerseNumber()).isEqualTo(1);
    }
    
    @Test
    @DisplayName("null DTO 처리")
    void toSimpleResponse_NullDto_ReturnsNull() {
        // When
        DailyVerseSimpleResponse result = mapper.toSimpleResponse(null);
        
        // Then
        assertThat(result).isNull();
    }
}

@ExtendWith(MockitoExtension.class)
class BookResponseMapperTest {
    
    @InjectMocks
    private BookResponseMapper mapper;
    
    @Test
    @DisplayName("BookDto를 Response로 변환 - 메타데이터 포함")
    void toResponse_WithMetadata_Success() {
        // Given
        BookDto dto = BookDto.builder()
            .id(1L)
            .name("창세기")
            .abbreviation("창")
            .testament("구약")
            .bookOrder(1)
            .chapterCount(50)
            .verseCount(1533L)
            .category("모세오경")
            .author("모세")
            .writtenDate("BC 1450-1410")
            .description("하나님의 창조와 인류의 시작을 다루는 책")
            .build();
        
        // When
        BibleBookResponse result = mapper.toResponse(dto);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("창세기");
        assertThat(result.getVerseCount()).isEqualTo(1533L);
        assertThat(result.getCategory()).isEqualTo("모세오경");
        assertThat(result.getAuthor()).isEqualTo("모세");
        assertThat(result.getDescription()).isNotNull();
    }
    
    @Test
    @DisplayName("책 목록을 구약/신약별로 그룹화")
    void groupByTestament_Success() {
        // Given
        List<BookDto> dtos = Arrays.asList(
            createBookDto(1L, "창세기", "구약"),
            createBookDto(2L, "출애굽기", "구약"),
            createBookDto(40L, "마태복음", "신약")
        );
        
        // When
        Map<String, List<BibleBookMiniResponse>> result = mapper.groupByTestament(dtos);
        
        // Then
        assertThat(result).containsKeys("구약", "신약");
        assertThat(result.get("구약")).hasSize(2);
        assertThat(result.get("신약")).hasSize(1);
    }
}
```

### Integration Tests

```java
@SpringBootTest
@AutoConfigureTestDatabase
class ResponseMapperIntegrationTest {
    
    @Autowired
    private DailyVerseApplicationService dailyVerseService;
    
    @Autowired
    private DailyVerseResponseMapper responseMapper;
    
    @Test
    @DisplayName("일일 구절 조회 API - 완전한 Book 정보 포함")
    void getDailyVerse_CompleteBookInfo() {
        // Given
        LocalDate date = LocalDate.of(2024, 1, 1);
        
        // When
        DailyVerseDetailDto dto = dailyVerseService.getDailyVerse(date);
        DailyVerseResponse response = responseMapper.toResponse(dto);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getBookName()).isNotNull();
        assertThat(response.getBookName()).doesNotStartWith("Book-");
        assertThat(response.getBookAbbreviation()).isNotNull();
        assertThat(response.getChapter()).isPositive();
        assertThat(response.getVerseNumber()).isPositive();
        
        // 응답 검증
        assertThat(responseMapper.validateDailyVerseResponse(response)).isTrue();
    }
}
```

## ⚠️ Important Notes

1. **Null Safety**: 모든 mapper에서 null 입력 처리 필수
2. **Validation**: 응답 객체 검증 메서드로 데이터 무결성 보장
3. **Performance**: 리스트 변환 시 스트림 최적화
4. **Consistency**: 모든 mapper에서 동일한 패턴 사용
5. **Book Integration**: VerseDomainService와 연동하여 Book 정보 자동 추가

## 🎯 Success Criteria

구현 완료 후:
- ✅ 모든 Response Mapper의 누락된 필드 완성
- ✅ API 응답에서 Book 정보 올바르게 표시
- ✅ 메타데이터 필드들이 모든 응답에 포함
- ✅ 일관된 매핑 패턴으로 코드 품질 향상
- ✅ 검증 메서드로 응답 데이터 품질 보장
- ✅ null 처리 및 예외상황 안전 처리

이 가이드를 따라 구현하면 모든 Response Mapper의 누락된 필드 문제가 해결됩니다.