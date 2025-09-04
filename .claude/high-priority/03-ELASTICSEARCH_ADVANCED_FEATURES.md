# ElasticSearch Advanced Features Implementation Guide

## 📋 Overview
ElasticSearch의 고급 검색 기능을 완성하는 가이드입니다. 현재 기본/향상 검색은 동작하지만, 동의어 검색과 고급 다중 조건 검색이 미완성 상태입니다.

## 🚨 Current Status
- **Status**: ⚠️ High Priority - 고급 검색 기능 미완성
- **Impact**: 동의어 검색, 다중 조건 검색이 placeholder 또는 fallback 상태
- **Estimated Time**: 2-3 hours
- **Files Affected**: Controller, Service, Repository classes

## 📂 Current Implementation Status

### ✅ Working Features
- **Basic Search**: 기본 텍스트 검색 ✅
- **Enhanced Search**: 하이라이팅, 스코어링, 정렬 ✅
- **Index Management**: 생성, 삭제, 재인덱싱 ✅
- **Pagination**: 모든 검색 엔드포인트 페이징 지원 ✅

### ❌ Missing Features
- **Synonym Search**: Placeholder 구현만 존재
- **Advanced Multi-Condition Search**: 기본 검색으로 fallback
- **Search Analytics**: 매치 타입 분석 미완성

## 🎯 Phase 1: Synonym Search Implementation

### 1.1 Create SynonymService
**File**: `src/main/java/com/dong/bible/application/service/SynonymService.java`

```java
package com.dong.bible.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 성경 관련 동의어 확장 서비스
 */
@Service
@Slf4j
public class SynonymService {
    
    // 성경 관련 동의어 매핑 데이터
    private static final Map<String, Set<String>> SYNONYM_MAP = new HashMap<>();
    
    static {
        // 신앙 관련 핵심 용어
        addSynonyms("사랑", "애정", "자비", "긍휼", "love", "charity", "compassion", "mercy");
        addSynonyms("믿음", "신앙", "신뢰", "faith", "belief", "trust", "confidence");
        addSynonyms("소망", "희망", "기대", "hope", "expectation", "anticipation");
        addSynonyms("구원", "구속", "해방", "salvation", "redemption", "deliverance", "liberation");
        addSynonyms("은혜", "자비", "축복", "grace", "favor", "blessing", "mercy");
        addSynonyms("평화", "평안", "안식", "peace", "rest", "tranquility", "shalom");
        
        // 예배 관련 용어
        addSynonyms("기도", "기원", "간구", "prayer", "supplication", "intercession", "petition");
        addSynonyms("찬양", "찬송", "경배", "찬미", "praise", "worship", "adoration", "glorify");
        addSynonyms("예배", "경배", "섬김", "worship", "service", "devotion");
        
        // 회개 관련 용어
        addSynonyms("회개", "뉘우침", "자백", "repentance", "contrition", "penitence", "confession");
        addSynonyms("죄", "허물", "과실", "sin", "transgression", "iniquity", "fault");
        addSynonyms("용서", "사함", "용납", "forgiveness", "pardon", "remission");
        
        // 거룩함 관련 용어
        addSynonyms("거룩", "성결", "정결", "순결", "holy", "sacred", "sanctified", "pure");
        addSynonyms("의", "의로움", "정의", "righteousness", "justice", "uprightness");
        addSynonyms("진리", "참", "진실", "truth", "reality", "honesty");
        
        // 하나님 관련 용어
        addSynonyms("하나님", "하느님", "주", "주님", "여호와", "god", "lord", "jehovah", "yahweh");
        addSynonyms("예수", "그리스도", "주님", "구주", "jesus", "christ", "savior", "lord");
        addSynonyms("성령", "성신", "보혜사", "holy spirit", "spirit", "comforter", "advocate");
        
        // 삶 관련 용어
        addSynonyms("생명", "삶", "인생", "life", "living", "existence");
        addSynonyms("죽음", "사망", "death", "mortality", "passing");
        addSynonyms("부활", "소생", "resurrection", "revival", "renewal");
        
        // 감정 관련 용어
        addSynonyms("기쁨", "즐거움", "환희", "joy", "happiness", "delight", "gladness");
        addSynonyms("슬픔", "근심", "애통", "sorrow", "grief", "mourning", "sadness");
        addSynonyms("두려움", "무서움", "공포", "fear", "terror", "dread", "anxiety");
        addSynonyms("용기", "담대", "힘", "courage", "boldness", "strength", "bravery");
        
        // 관계 관련 용어
        addSynonyms("형제", "형님", "동생", "brother", "sibling");
        addSynonyms("자매", "누나", "동생", "sister", "sibling");
        addSynonyms("아버지", "아빠", "부친", "father", "dad", "papa");
        addSynonyms("어머니", "엄마", "모친", "mother", "mom", "mama");
        
        // 시간 관련 용어
        addSynonyms("영원", "영생", "무궁", "eternal", "everlasting", "forever", "perpetual");
        addSynonyms("오늘", "금일", "today", "this day");
        addSynonyms("내일", "명일", "tomorrow", "next day");
        addSynonyms("어제", "어제께", "yesterday", "past day");
    }
    
    /**
     * 동의어 그룹 추가
     */
    private static void addSynonyms(String... words) {
        Set<String> synonymSet = Arrays.stream(words)
            .map(String::toLowerCase)
            .map(String::trim)
            .filter(StringUtils::hasText)
            .collect(Collectors.toSet());
        
        for (String word : synonymSet) {
            SYNONYM_MAP.put(word, synonymSet);
        }
    }
    
    /**
     * 쿼리를 동의어로 확장
     * @param query 원본 검색어
     * @return 동의어가 포함된 확장된 검색어 집합
     */
    public Set<String> expandSynonyms(String query) {
        if (!StringUtils.hasText(query)) {
            return new HashSet<>();
        }
        
        log.debug("Expanding synonyms for query: {}", query);
        
        Set<String> expandedTerms = new HashSet<>();
        expandedTerms.add(query.trim());
        
        // 공백으로 단어 분리
        String[] words = query.toLowerCase().trim().split("\\s+");
        
        for (String word : words) {
            word = word.trim();
            if (word.isEmpty()) continue;
            
            // 동의어 찾기
            Set<String> synonyms = SYNONYM_MAP.get(word);
            if (synonyms != null && !synonyms.isEmpty()) {
                log.debug("Found synonyms for '{}': {}", word, synonyms);
                
                // 원래 쿼리에서 해당 단어를 동의어로 치환한 버전들 생성
                for (String synonym : synonyms) {
                    if (!synonym.equals(word)) {
                        String expandedQuery = query.toLowerCase().replace(word, synonym);
                        expandedTerms.add(expandedQuery);
                    }
                }
            }
        }
        
        log.debug("Expanded query '{}' to {} terms: {}", query, expandedTerms.size(), expandedTerms);
        return expandedTerms;
    }
    
    /**
     * 단일 단어의 동의어 조회
     * @param word 검색할 단어
     * @return 동의어 집합
     */
    public Set<String> getSynonyms(String word) {
        if (!StringUtils.hasText(word)) {
            return new HashSet<>();
        }
        
        String normalizedWord = word.toLowerCase().trim();
        return SYNONYM_MAP.getOrDefault(normalizedWord, new HashSet<>());
    }
    
    /**
     * ElasticSearch 쿼리용 동의어 OR 조건 생성
     * @param query 원본 검색어
     * @return OR로 연결된 동의어 쿼리
     */
    public String buildSynonymQuery(String query) {
        Set<String> expandedTerms = expandSynonyms(query);
        
        if (expandedTerms.isEmpty()) {
            return query;
        }
        
        // OR 조건으로 연결
        String synonymQuery = expandedTerms.stream()
            .map(term -> "\"" + term + "\"")  // 구문 검색을 위해 따옴표 추가
            .collect(Collectors.joining(" OR "));
        
        log.debug("Built synonym query: {}", synonymQuery);
        return synonymQuery;
    }
    
    /**
     * 동의어 매칭 여부 확인
     * @param word1 첫 번째 단어
     * @param word2 두 번째 단어
     * @return 동의어 관계 여부
     */
    public boolean areSynonyms(String word1, String word2) {
        if (!StringUtils.hasText(word1) || !StringUtils.hasText(word2)) {
            return false;
        }
        
        String normalized1 = word1.toLowerCase().trim();
        String normalized2 = word2.toLowerCase().trim();
        
        if (normalized1.equals(normalized2)) {
            return true;
        }
        
        Set<String> synonyms1 = SYNONYM_MAP.get(normalized1);
        return synonyms1 != null && synonyms1.contains(normalized2);
    }
    
    /**
     * 동의어 통계 정보
     */
    public Map<String, Object> getSynonymStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        Set<Set<String>> uniqueGroups = new HashSet<>(SYNONYM_MAP.values());
        
        stats.put("totalWords", SYNONYM_MAP.size());
        stats.put("synonymGroups", uniqueGroups.size());
        stats.put("averageGroupSize", uniqueGroups.stream()
            .mapToInt(Set::size)
            .average()
            .orElse(0.0));
        
        return stats;
    }
}
```

### 1.2 Update VerseSearchController - Implement Synonym Search
**File**: `src/main/java/com/dong/bible/web/controller/VerseSearchController.java`
**Replace Line 247 placeholder**:

```java
/**
 * 동의어 검색
 * 입력된 검색어의 동의어를 포함하여 검색합니다.
 */
@GetMapping("/synonym")
@Operation(summary = "동의어 검색", description = "검색어의 동의어를 포함하여 구절을 검색합니다.")
public AppResponse<EnhancedVerseSearchResponse> searchWithSynonym(
        @Parameter(description = "검색어", required = true, example = "사랑")
        @RequestParam String query,
        @Parameter(description = "페이지 번호", example = "0")
        @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "페이지 크기", example = "20")
        @RequestParam(defaultValue = "20") int size,
        @Parameter(description = "정렬 기준", example = "relevance")
        @RequestParam(defaultValue = "relevance") String sortBy) {
    
    log.info("Synonym search request: query={}, page={}, size={}", query, page, size);
    
    if (!StringUtils.hasText(query)) {
        return AppResponse.error("INVALID_QUERY", "검색어를 입력해주세요.");
    }
    
    try {
        // 동의어 확장된 검색 실행
        EnhancedSearchResult result = searchApplicationService.synonymSearch(
            query.trim(), sortBy, page, size);
        
        EnhancedVerseSearchResponse response = searchResponseMapper.toEnhancedResponse(result);
        
        return AppResponse.success(response);
        
    } catch (Exception e) {
        log.error("Synonym search failed: query={}, error={}", query, e.getMessage(), e);
        return AppResponse.error("SEARCH_FAILED", "동의어 검색 중 오류가 발생했습니다.");
    }
}

/**
 * 동의어 조회 API
 * 특정 단어의 동의어 목록을 반환합니다.
 */
@GetMapping("/synonyms")
@Operation(summary = "동의어 조회", description = "특정 단어의 동의어 목록을 조회합니다.")
public AppResponse<Map<String, Object>> getSynonyms(
        @Parameter(description = "조회할 단어", required = true, example = "사랑")
        @RequestParam String word) {
    
    log.info("Get synonyms request: word={}", word);
    
    if (!StringUtils.hasText(word)) {
        return AppResponse.error("INVALID_WORD", "단어를 입력해주세요.");
    }
    
    try {
        Set<String> synonyms = synonymService.getSynonyms(word.trim());
        
        Map<String, Object> response = new HashMap<>();
        response.put("word", word.trim());
        response.put("synonyms", synonyms);
        response.put("count", synonyms.size());
        
        return AppResponse.success(response);
        
    } catch (Exception e) {
        log.error("Get synonyms failed: word={}, error={}", word, e.getMessage(), e);
        return AppResponse.error("SYNONYMS_FAILED", "동의어 조회 중 오류가 발생했습니다.");
    }
}
```

### 1.3 Update VerseSearchApplicationService - Add Synonym Search
**File**: `src/main/java/com/dong/bible/application/service/VerseSearchApplicationService.java`
**Add new method**:

```java
// 클래스 상단에 SynonymService 주입 추가
private final SynonymService synonymService;

/**
 * 동의어 검색
 * @param query 검색어
 * @param sortBy 정렬 기준
 * @param page 페이지
 * @param size 크기
 * @return 검색 결과
 */
public EnhancedSearchResult synonymSearch(String query, String sortBy, int page, int size) {
    log.info("Synonym search: query={}, sortBy={}, page={}, size={}", query, sortBy, page, size);
    
    if (!StringUtils.hasText(query)) {
        throw new IllegalArgumentException("Query must not be empty");
    }
    
    try {
        // 동의어 확장
        Set<String> expandedTerms = synonymService.expandSynonyms(query);
        log.debug("Expanded terms: {}", expandedTerms);
        
        if (expandedTerms.isEmpty()) {
            log.warn("No synonym expansion for query: {}", query);
            return enhancedSearch(query, page, size);
        }
        
        // ElasticSearch 복합 쿼리 생성
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        
        // 각 동의어에 대해 should 조건 추가 (OR 조건)
        for (String term : expandedTerms) {
            MultiMatchQueryBuilder termQuery = QueryBuilders.multiMatchQuery(term)
                .field("verseText", 3.0f)      // 구절 본문에 높은 가중치
                .field("reference", 2.0f)      // 참조에 중간 가중치
                .field("bookName", 1.5f)       // 책 이름에 낮은 가중치
                .type(MultiMatchQueryBuilder.Type.BEST_FIELDS)
                .fuzziness(Fuzziness.AUTO);    // 오타 허용
            
            boolQuery.should(termQuery);
        }
        
        // 최소 매치 조건 설정 (동의어 중 최소 1개는 매치되어야 함)
        boolQuery.minimumShouldMatch(1);
        
        // 원본 검색어는 더 높은 점수 부여 (부스팅)
        MultiMatchQueryBuilder originalQuery = QueryBuilders.multiMatchQuery(query)
            .field("verseText", 5.0f)
            .field("reference", 3.0f)
            .field("bookName", 2.0f)
            .type(MultiMatchQueryBuilder.Type.BEST_FIELDS);
        
        boolQuery.should(originalQuery.boost(2.0f)); // 원본 검색어에 부스팅
        
        // 정렬 설정
        SortBuilder<?> sort = createSortBuilder(sortBy);
        
        // 하이라이팅 설정
        HighlightBuilder highlightBuilder = createHighlightBuilder();
        
        // 검색 실행
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
            .query(boolQuery)
            .from(page * size)
            .size(size)
            .sort(sort)
            .highlighter(highlightBuilder)
            .timeout(TimeValue.timeValueSeconds(10)); // 타임아웃 설정
        
        log.debug("Synonym search query: {}", searchSourceBuilder.toString());
        
        EnhancedSearchResult result = searchRepository.searchWithEnhancement(searchSourceBuilder);
        
        // 결과에 동의어 정보 추가
        result = addSynonymMetadata(result, query, expandedTerms);
        
        log.info("Synonym search completed: query={}, totalHits={}", query, result.getTotalHits());
        return result;
        
    } catch (Exception e) {
        log.error("Synonym search failed: query={}", query, e);
        throw new RuntimeException("Synonym search execution failed", e);
    }
}

/**
 * 동의어 검색 결과에 메타데이터 추가
 */
private EnhancedSearchResult addSynonymMetadata(EnhancedSearchResult result, 
                                               String originalQuery, 
                                               Set<String> expandedTerms) {
    Map<String, Object> metadata = new HashMap<>(result.getMetadata());
    metadata.put("originalQuery", originalQuery);
    metadata.put("expandedTerms", expandedTerms);
    metadata.put("synonymCount", expandedTerms.size() - 1); // 원본 제외
    metadata.put("searchType", "synonym");
    
    return EnhancedSearchResult.builder()
        .hits(result.getHits())
        .totalHits(result.getTotalHits())
        .maxScore(result.getMaxScore())
        .took(result.getTook())
        .metadata(metadata)
        .build();
}
```

## 🎯 Phase 2: Advanced Multi-Condition Search

### 2.1 Update VerseSearchApplicationService - Complete Advanced Search
**File**: `src/main/java/com/dong/bible/application/service/VerseSearchApplicationService.java`
**Replace Line 242 fallback implementation**:

```java
/**
 * 고급 다중 조건 검색
 * @param query 텍스트 검색어
 * @param bookId 책 ID 필터
 * @param chapter 장 필터
 * @param verseRange 절 범위 (예: "1-10")
 * @param dateRange 날짜 범위 (예: "2024-01-01~2024-12-31")
 * @param sortBy 정렬 기준
 * @param page 페이지
 * @param size 크기
 * @return 검색 결과
 */
public EnhancedSearchResult advancedSearch(String query, Long bookId, Integer chapter, 
                                          String verseRange, String dateRange, 
                                          String sortBy, int page, int size) {
    log.info("Advanced search: query={}, bookId={}, chapter={}, verseRange={}, dateRange={}", 
        query, bookId, chapter, verseRange, dateRange);
    
    try {
        // 복합 쿼리 빌더 생성
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        
        // 1. 텍스트 검색 조건 (있을 경우)
        if (StringUtils.hasText(query)) {
            // 동의어 포함 검색
            Set<String> expandedTerms = synonymService.expandSynonyms(query);
            
            BoolQueryBuilder textQuery = QueryBuilders.boolQuery();
            for (String term : expandedTerms) {
                MultiMatchQueryBuilder termQuery = QueryBuilders.multiMatchQuery(term)
                    .field("verseText", 3.0f)
                    .field("reference", 2.0f)
                    .field("bookName", 1.5f)
                    .type(MultiMatchQueryBuilder.Type.BEST_FIELDS)
                    .fuzziness(Fuzziness.AUTO);
                    
                textQuery.should(termQuery);
            }
            textQuery.minimumShouldMatch(1);
            boolQuery.must(textQuery);
        }
        
        // 2. 책 ID 필터
        if (bookId != null) {
            boolQuery.filter(QueryBuilders.termQuery("bookId", bookId));
            log.debug("Added book filter: {}", bookId);
        }
        
        // 3. 장 필터  
        if (chapter != null) {
            boolQuery.filter(QueryBuilders.termQuery("chapter", chapter));
            log.debug("Added chapter filter: {}", chapter);
        }
        
        // 4. 절 범위 필터
        if (StringUtils.hasText(verseRange)) {
            try {
                String[] range = verseRange.split("-");
                if (range.length == 2) {
                    int startVerse = Integer.parseInt(range[0].trim());
                    int endVerse = Integer.parseInt(range[1].trim());
                    
                    boolQuery.filter(QueryBuilders.rangeQuery("verseNumber")
                        .gte(startVerse)
                        .lte(endVerse));
                    
                    log.debug("Added verse range filter: {}-{}", startVerse, endVerse);
                }
            } catch (Exception e) {
                log.warn("Invalid verse range format: {}", verseRange);
            }
        }
        
        // 5. 날짜 범위 필터 (구절 생성/수정 날짜)
        if (StringUtils.hasText(dateRange)) {
            try {
                String[] dates = dateRange.split("~");
                if (dates.length == 2) {
                    String startDate = dates[0].trim();
                    String endDate = dates[1].trim();
                    
                    boolQuery.filter(QueryBuilders.rangeQuery("createdAt")
                        .gte(startDate)
                        .lte(endDate));
                    
                    log.debug("Added date range filter: {} ~ {}", startDate, endDate);
                }
            } catch (Exception e) {
                log.warn("Invalid date range format: {}", dateRange);
            }
        }
        
        // 6. 정렬 설정
        SortBuilder<?> sort = createAdvancedSortBuilder(sortBy, StringUtils.hasText(query));
        
        // 7. 하이라이팅 설정
        HighlightBuilder highlightBuilder = createHighlightBuilder();
        
        // 8. 집계(Aggregations) 추가 - 책별, 장별 통계
        AggregationBuilder bookAgg = AggregationBuilders.terms("books")
            .field("bookName.keyword")
            .size(10);
        
        AggregationBuilder chapterAgg = AggregationBuilders.terms("chapters")
            .field("chapter")
            .size(50);
        
        // 검색 실행
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
            .query(boolQuery)
            .from(page * size)
            .size(size)
            .sort(sort)
            .highlighter(highlightBuilder)
            .aggregation(bookAgg)
            .aggregation(chapterAgg)
            .timeout(TimeValue.timeValueSeconds(15));
        
        log.debug("Advanced search query: {}", searchSourceBuilder.toString());
        
        EnhancedSearchResult result = searchRepository.searchWithEnhancement(searchSourceBuilder);
        
        // 결과에 검색 조건 메타데이터 추가
        result = addAdvancedSearchMetadata(result, query, bookId, chapter, verseRange, dateRange);
        
        log.info("Advanced search completed: totalHits={}, conditions={}", 
            result.getTotalHits(), countSearchConditions(query, bookId, chapter, verseRange, dateRange));
        
        return result;
        
    } catch (Exception e) {
        log.error("Advanced search failed", e);
        throw new RuntimeException("Advanced search execution failed", e);
    }
}

/**
 * 고급 검색용 정렬 빌더 생성
 */
private SortBuilder<?> createAdvancedSortBuilder(String sortBy, boolean hasTextQuery) {
    if (sortBy == null) sortBy = "relevance";
    
    switch (sortBy.toLowerCase()) {
        case "relevance":
            // 텍스트 쿼리가 있을 때만 relevance 정렬
            return hasTextQuery ? 
                SortBuilders.scoreSort().order(SortOrder.DESC) :
                SortBuilders.fieldSort("bookId").order(SortOrder.ASC)
                    .missing("_last");
                    
        case "book":
            return SortBuilders.fieldSort("bookId").order(SortOrder.ASC)
                .missing("_last");
                
        case "chapter":
            return SortBuilders.fieldSort("bookId").order(SortOrder.ASC)
                .missing("_last")
                .and(SortBuilders.fieldSort("chapter").order(SortOrder.ASC));
                
        case "verse":
            return SortBuilders.fieldSort("bookId").order(SortOrder.ASC)
                .missing("_last")
                .and(SortBuilders.fieldSort("chapter").order(SortOrder.ASC))
                .and(SortBuilders.fieldSort("verseNumber").order(SortOrder.ASC));
                
        case "created":
            return SortBuilders.fieldSort("createdAt").order(SortOrder.DESC)
                .missing("_last");
                
        default:
            return SortBuilders.scoreSort().order(SortOrder.DESC);
    }
}

/**
 * 고급 검색 결과에 메타데이터 추가
 */
private EnhancedSearchResult addAdvancedSearchMetadata(EnhancedSearchResult result,
                                                      String query, Long bookId, Integer chapter,
                                                      String verseRange, String dateRange) {
    Map<String, Object> metadata = new HashMap<>(result.getMetadata());
    
    Map<String, Object> searchConditions = new HashMap<>();
    if (StringUtils.hasText(query)) searchConditions.put("query", query);
    if (bookId != null) searchConditions.put("bookId", bookId);
    if (chapter != null) searchConditions.put("chapter", chapter);
    if (StringUtils.hasText(verseRange)) searchConditions.put("verseRange", verseRange);
    if (StringUtils.hasText(dateRange)) searchConditions.put("dateRange", dateRange);
    
    metadata.put("searchConditions", searchConditions);
    metadata.put("conditionCount", searchConditions.size());
    metadata.put("searchType", "advanced");
    
    return EnhancedSearchResult.builder()
        .hits(result.getHits())
        .totalHits(result.getTotalHits())
        .maxScore(result.getMaxScore())
        .took(result.getTook())
        .metadata(metadata)
        .build();
}

/**
 * 검색 조건 개수 계산
 */
private int countSearchConditions(String query, Long bookId, Integer chapter, 
                                 String verseRange, String dateRange) {
    int count = 0;
    if (StringUtils.hasText(query)) count++;
    if (bookId != null) count++;
    if (chapter != null) count++;
    if (StringUtils.hasText(verseRange)) count++;
    if (StringUtils.hasText(dateRange)) count++;
    return count;
}
```

### 2.2 Update VerseSearchController - Add Advanced Search Endpoint
**File**: `src/main/java/com/dong/bible/web/controller/VerseSearchController.java`

```java
/**
 * 고급 다중 조건 검색
 */
@GetMapping("/advanced")
@Operation(summary = "고급 다중 조건 검색", description = "여러 조건을 조합하여 구절을 검색합니다.")
public AppResponse<EnhancedVerseSearchResponse> advancedSearch(
        @Parameter(description = "검색어", example = "사랑")
        @RequestParam(required = false) String query,
        @Parameter(description = "책 ID", example = "1")
        @RequestParam(required = false) Long bookId,
        @Parameter(description = "장 번호", example = "1")
        @RequestParam(required = false) Integer chapter,
        @Parameter(description = "절 범위 (예: 1-10)", example = "1-10")
        @RequestParam(required = false) String verseRange,
        @Parameter(description = "날짜 범위 (예: 2024-01-01~2024-12-31)", example = "2024-01-01~2024-12-31")
        @RequestParam(required = false) String dateRange,
        @Parameter(description = "정렬 기준", example = "relevance")
        @RequestParam(defaultValue = "relevance") String sortBy,
        @Parameter(description = "페이지 번호", example = "0")
        @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "페이지 크기", example = "20")
        @RequestParam(defaultValue = "20") int size) {
    
    log.info("Advanced search request: query={}, bookId={}, chapter={}, verseRange={}, dateRange={}", 
        query, bookId, chapter, verseRange, dateRange);
    
    // 최소 하나의 검색 조건 필요
    if (!StringUtils.hasText(query) && bookId == null && chapter == null && 
        !StringUtils.hasText(verseRange) && !StringUtils.hasText(dateRange)) {
        return AppResponse.error("NO_SEARCH_CONDITIONS", "최소 하나의 검색 조건을 입력해주세요.");
    }
    
    try {
        EnhancedSearchResult result = searchApplicationService.advancedSearch(
            query, bookId, chapter, verseRange, dateRange, sortBy, page, size);
        
        EnhancedVerseSearchResponse response = searchResponseMapper.toEnhancedResponse(result);
        
        return AppResponse.success(response);
        
    } catch (Exception e) {
        log.error("Advanced search failed", e);
        return AppResponse.error("SEARCH_FAILED", "고급 검색 중 오류가 발생했습니다.");
    }
}
```

## 🎯 Phase 3: Enhanced Search Analytics

### 3.1 Update EnhancedSearchHit - Complete Match Type Analysis
**File**: `src/main/java/com/dong/bible/infrastructure/search/dto/EnhancedSearchHit.java`
**Replace Lines 122-123 placeholder**:

```java
/**
 * 매치 타입 분석
 * @param query 검색어
 * @return 매치 타입 정보
 */
private Map<String, Object> analyzeMatchType(String query) {
    Map<String, Object> matchInfo = new HashMap<>();
    
    if (query == null || query.trim().isEmpty()) {
        matchInfo.put("type", "none");
        matchInfo.put("confidence", 0.0);
        return matchInfo;
    }
    
    String normalizedQuery = query.toLowerCase().trim();
    String verseText = this.verseText != null ? this.verseText.toLowerCase() : "";
    
    // 1. 완전 일치 (Exact Match)
    if (verseText.contains(normalizedQuery)) {
        matchInfo.put("type", "exact");
        matchInfo.put("confidence", 1.0);
        matchInfo.put("description", "검색어가 구절에 완전히 일치함");
        return matchInfo;
    }
    
    // 2. 구문 일치 (Phrase Match) - 단어 순서 유지
    String[] queryWords = normalizedQuery.split("\\s+");
    if (queryWords.length > 1) {
        boolean phraseMatch = containsPhrase(verseText, queryWords);
        if (phraseMatch) {
            matchInfo.put("type", "phrase");
            matchInfo.put("confidence", 0.8);
            matchInfo.put("description", "검색어 구문이 순서대로 일치함");
            return matchInfo;
        }
    }
    
    // 3. 모든 단어 포함 (All Words Match)
    if (queryWords.length > 1) {
        boolean allWordsMatch = Arrays.stream(queryWords)
            .allMatch(verseText::contains);
        
        if (allWordsMatch) {
            matchInfo.put("type", "all_words");
            matchInfo.put("confidence", 0.7);
            matchInfo.put("description", "검색어의 모든 단어가 포함됨");
            return matchInfo;
        }
    }
    
    // 4. 일부 단어 일치 (Partial Match)
    if (queryWords.length > 1) {
        long matchingWords = Arrays.stream(queryWords)
            .mapToLong(word -> verseText.contains(word) ? 1 : 0)
            .sum();
        
        if (matchingWords > 0) {
            double confidence = (double) matchingWords / queryWords.length;
            matchInfo.put("type", "partial");
            matchInfo.put("confidence", confidence * 0.6);
            matchInfo.put("description", String.format("검색어 중 %d개 단어가 일치함", matchingWords));
            matchInfo.put("matchingWordCount", matchingWords);
            matchInfo.put("totalWordCount", queryWords.length);
            return matchInfo;
        }
    }
    
    // 5. 유사 일치 (Fuzzy Match) - 오타 허용
    if (hasFuzzyMatch(verseText, normalizedQuery)) {
        matchInfo.put("type", "fuzzy");
        matchInfo.put("confidence", 0.4);
        matchInfo.put("description", "유사한 단어가 일치함 (오타 허용)");
        return matchInfo;
    }
    
    // 6. 동의어 일치 (Synonym Match) - 하이라이팅 정보로 판단
    if (hasHighlightedContent() && !verseText.contains(normalizedQuery)) {
        matchInfo.put("type", "synonym");
        matchInfo.put("confidence", 0.6);
        matchInfo.put("description", "동의어가 일치함");
        return matchInfo;
    }
    
    // 7. 일치하지 않음
    matchInfo.put("type", "none");
    matchInfo.put("confidence", 0.0);
    matchInfo.put("description", "일치하는 내용 없음");
    return matchInfo;
}

/**
 * 구문 일치 확인 (단어 순서 유지)
 */
private boolean containsPhrase(String text, String[] words) {
    if (words.length == 0) return false;
    
    String phrase = String.join("\\s+", words);
    return text.matches(".*" + phrase + ".*");
}

/**
 * 퍼지 매치 확인 (간단한 편집 거리 기반)
 */
private boolean hasFuzzyMatch(String text, String query) {
    String[] queryWords = query.split("\\s+");
    
    for (String queryWord : queryWords) {
        if (queryWord.length() < 3) continue; // 짧은 단어는 퍼지 매치 제외
        
        String[] textWords = text.split("\\s+");
        for (String textWord : textWords) {
            if (calculateEditDistance(queryWord, textWord) <= 2 && 
                textWord.length() >= queryWord.length() - 1) {
                return true;
            }
        }
    }
    return false;
}

/**
 * 편집 거리 계산 (Levenshtein Distance)
 */
private int calculateEditDistance(String s1, String s2) {
    int[][] dp = new int[s1.length() + 1][s2.length() + 1];
    
    for (int i = 0; i <= s1.length(); i++) dp[i][0] = i;
    for (int j = 0; j <= s2.length(); j++) dp[0][j] = j;
    
    for (int i = 1; i <= s1.length(); i++) {
        for (int j = 1; j <= s2.length(); j++) {
            if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                dp[i][j] = dp[i - 1][j - 1];
            } else {
                dp[i][j] = 1 + Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]);
            }
        }
    }
    
    return dp[s1.length()][s2.length()];
}

/**
 * 하이라이팅 내용 존재 여부 확인
 */
private boolean hasHighlightedContent() {
    return this.highlightedContent != null && 
           this.highlightedContent.getHighlights() != null && 
           !this.highlightedContent.getHighlights().isEmpty();
}
```

## ✅ Implementation Checklist

### Phase 1: Synonym Search
- [ ] Create `SynonymService.java` with comprehensive synonym mapping
- [ ] Implement synonym expansion logic with Korean/English support
- [ ] Update `VerseSearchController` synonym endpoint (line 247)
- [ ] Add synonym search method to `VerseSearchApplicationService`
- [ ] Add synonym information API endpoint
- [ ] Test synonym expansion with various terms

### Phase 2: Advanced Search
- [ ] Implement `advancedSearch()` method in `VerseSearchApplicationService`
- [ ] Add advanced search endpoint to controller
- [ ] Support multiple filter conditions (book, chapter, verse range, date)
- [ ] Implement complex ElasticSearch query building
- [ ] Add search condition metadata to results
- [ ] Test various condition combinations

### Phase 3: Search Analytics
- [ ] Complete match type analysis in `EnhancedSearchHit`
- [ ] Implement exact, phrase, partial, fuzzy, synonym match detection
- [ ] Add edit distance calculation for fuzzy matching
- [ ] Add search statistics and aggregations
- [ ] Enhance result metadata with analysis information

### Testing & Validation
- [ ] Unit test synonym expansion logic
- [ ] Integration test advanced search combinations
- [ ] Test match type analysis accuracy
- [ ] Performance test with large datasets
- [ ] Validate search result relevance

## 🧪 Testing Strategy

### Unit Tests for SynonymService

```java
@Test
@DisplayName("동의어 확장 테스트")
void expandSynonyms_Success() {
    // Given
    String query = "사랑";
    
    // When
    Set<String> result = synonymService.expandSynonyms(query);
    
    // Then
    assertThat(result).contains("사랑", "애정", "자비");
    assertThat(result.size()).isGreaterThan(1);
}

@Test
@DisplayName("복합어 동의어 확장 테스트")
void expandSynonyms_MultipleWords_Success() {
    // Given
    String query = "하나님 사랑";
    
    // When
    Set<String> result = synonymService.expandSynonyms(query);
    
    // Then
    assertThat(result).hasSizeGreaterThan(4);
    assertThat(result.stream().anyMatch(s -> s.contains("주"))).isTrue();
    assertThat(result.stream().anyMatch(s -> s.contains("애정"))).isTrue();
}
```

### Integration Tests for Advanced Search

```java
@Test
@DisplayName("고급 검색 - 다중 조건 테스트")
void advancedSearch_MultipleConditions_Success() {
    // When
    EnhancedSearchResult result = searchApplicationService.advancedSearch(
        "사랑", 1L, 1, "1-10", null, "relevance", 0, 20
    );
    
    // Then
    assertThat(result.getTotalHits()).isGreaterThan(0);
    assertThat(result.getMetadata().get("searchType")).isEqualTo("advanced");
    assertThat(result.getMetadata().get("conditionCount")).isEqualTo(3);
}
```

## ⚠️ Important Notes

1. **Performance**: 동의어 확장으로 인한 쿼리 복잡도 증가 주의
2. **Relevance**: 원본 검색어에 부스팅 점수를 적용하여 관련성 유지
3. **Timeout**: 복잡한 검색 쿼리의 타임아웃 설정 필수
4. **Caching**: 자주 사용되는 동의어는 캐싱 고려
5. **Monitoring**: 검색 성능과 결과 품질 모니터링 필요

## 🎯 Success Criteria

구현 완료 후:
- ✅ 동의어 검색으로 더 풍부한 검색 결과 제공
- ✅ 다중 조건 검색으로 정밀한 필터링 지원
- ✅ 매치 타입 분석으로 검색 결과 품질 향상
- ✅ 성능 저하 없이 고급 기능 동작
- ✅ 검색 분석 정보로 사용자 경험 개선

이 가이드를 따라 구현하면 ElasticSearch의 모든 고급 기능이 완성됩니다.