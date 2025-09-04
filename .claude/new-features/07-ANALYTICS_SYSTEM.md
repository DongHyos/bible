# Analytics System Implementation Guide

## 📋 Overview
사용 패턴 분석, 인기 구절 추적, 개인화 추천 등 데이터 기반 인사이트를 제공하는 분석 시스템 구현 가이드입니다.

## 🎯 System Features

### Core Analytics
- **Usage Statistics**: API 호출, 검색 패턴, 사용자 행동 분석
- **Popular Content**: 인기 구절, 설교, 일일 구절 순위
- **Reading Patterns**: 읽기 진행 패턴, 선호 성경 분석
- **Search Analytics**: 검색어 분석, 검색 성공률, 트렌드

### Personalization
- **Recommended Verses**: 개인 읽기 이력 기반 추천
- **Personalized Content**: 관심사 기반 콘텐츠 큐레이션
- **Reading Goals**: 개인화된 읽기 목표 및 진행률

### Business Intelligence
- **User Engagement**: 활성 사용자, 리텐션, 세션 분석
- **Content Performance**: 콘텐츠별 참여도, 공유율
- **Growth Metrics**: 신규 사용자, 성장 트렌드

## 📂 Directory Structure

```
src/main/java/com/dong/bible/
├── domain/
│   ├── analytics/
│   │   ├── UserActivity.java
│   │   ├── SearchLog.java
│   │   ├── PopularContent.java
│   │   └── AnalyticsRepository.java
│   └── recommendation/
│       ├── UserPreference.java
│       ├── RecommendationEngine.java
│       └── RecommendationRepository.java
├── application/
│   ├── service/
│   │   ├── AnalyticsApplicationService.java
│   │   ├── RecommendationApplicationService.java
│   │   └── UserEngagementService.java
│   └── dto/
│       ├── query/
│       │   ├── UsageStatisticsQuery.java
│       │   ├── PopularContentQuery.java
│       │   └── RecommendationQuery.java
│       └── command/
│           └── LogUserActivityCommand.java
├── infrastructure/
│   ├── analytics/
│   │   ├── AnalyticsCollector.java
│   │   ├── UserActivityLogger.java
│   │   └── SearchAnalyzer.java
│   └── recommendation/
│       ├── CollaborativeFiltering.java
│       ├── ContentBasedFiltering.java
│       └── HybridRecommendation.java
└── web/
    ├── controller/
    │   ├── AnalyticsController.java
    │   └── RecommendationController.java
    └── dto/
        └── response/
            ├── AnalyticsResponse.java
            └── RecommendationResponse.java
```

## 🎯 Phase 1: Domain Models

### 1.1 UserActivity Domain Object
**File**: `src/main/java/com/dong/bible/domain/analytics/UserActivity.java`

```java
package com.dong.bible.domain.analytics;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 사용자 활동 추적 도메인 객체
 */
@Getter
@Builder(toBuilder = true)
public class UserActivity {
    
    private final Long id;
    private final Long userId;
    private final String sessionId;
    private final ActivityType activityType;
    private final String endpoint;
    private final Long resourceId;
    private final String resourceType;
    private final Map<String, Object> metadata;
    private final String userAgent;
    private final String ipAddress;
    private final Integer responseTime;
    private final Boolean isSuccess;
    private final LocalDateTime timestamp;
    
    /**
     * 새 사용자 활동 기록
     */
    public static UserActivity createNew(Long userId, String sessionId, 
                                        ActivityType activityType, String endpoint) {
        return UserActivity.builder()
            .userId(userId)
            .sessionId(sessionId)
            .activityType(activityType)
            .endpoint(endpoint)
            .timestamp(LocalDateTime.now())
            .isSuccess(true)
            .build();
    }
    
    /**
     * 리소스 정보 추가
     */
    public UserActivity withResource(Long resourceId, String resourceType) {
        return this.toBuilder()
            .resourceId(resourceId)
            .resourceType(resourceType)
            .build();
    }
    
    /**
     * 메타데이터 추가
     */
    public UserActivity withMetadata(Map<String, Object> metadata) {
        return this.toBuilder()
            .metadata(metadata)
            .build();
    }
    
    /**
     * 요청 정보 추가
     */
    public UserActivity withRequestInfo(String userAgent, String ipAddress, Integer responseTime) {
        return this.toBuilder()
            .userAgent(userAgent)
            .ipAddress(ipAddress)
            .responseTime(responseTime)
            .build();
    }
    
    /**
     * 실패 상태로 표시
     */
    public UserActivity markAsFailed() {
        return this.toBuilder()
            .isSuccess(false)
            .build();
    }
}

/**
 * 활동 유형 enum
 */
public enum ActivityType {
    VIEW_VERSE,
    VIEW_CHAPTER, 
    VIEW_SERMON,
    VIEW_DAILY_VERSE,
    SEARCH_VERSE,
    SEARCH_SERMON,
    ADD_FAVORITE,
    REMOVE_FAVORITE,
    UPDATE_READING_PROGRESS,
    LOGIN,
    LOGOUT,
    REGISTER
}
```

### 1.2 SearchLog Domain Object
**File**: `src/main/java/com/dong/bible/domain/analytics/SearchLog.java`

```java
package com.dong.bible.domain.analytics;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 검색 로그 도메인 객체
 */
@Getter
@Builder(toBuilder = true)
public class SearchLog {
    
    private final Long id;
    private final Long userId;
    private final String sessionId;
    private final String searchQuery;
    private final SearchType searchType;
    private final Long resultCount;
    private final Boolean hasResults;
    private final String searchEngine; // elastic, database
    private final Integer responseTime;
    private final LocalDateTime timestamp;
    
    /**
     * 새 검색 로그 생성
     */
    public static SearchLog create(Long userId, String sessionId, String searchQuery, SearchType searchType) {
        return SearchLog.builder()
            .userId(userId)
            .sessionId(sessionId) 
            .searchQuery(searchQuery)
            .searchType(searchType)
            .timestamp(LocalDateTime.now())
            .build();
    }
    
    /**
     * 검색 결과 정보 추가
     */
    public SearchLog withResults(Long resultCount, Integer responseTime, String searchEngine) {
        return this.toBuilder()
            .resultCount(resultCount)
            .hasResults(resultCount > 0)
            .responseTime(responseTime)
            .searchEngine(searchEngine)
            .build();
    }
}

/**
 * 검색 유형 enum
 */
public enum SearchType {
    BASIC_VERSE,
    ENHANCED_VERSE,
    SYNONYM_VERSE,
    ADVANCED_VERSE,
    SERMON_SEARCH,
    DAILY_VERSE_SEARCH
}
```

### 1.3 PopularContent Domain Object
**File**: `src/main/java/com/dong/bible/domain/analytics/PopularContent.java`

```java
package com.dong.bible.domain.analytics;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 인기 콘텐츠 집계 도메인 객체
 */
@Getter
@Builder(toBuilder = true)
public class PopularContent {
    
    private final Long id;
    private final String contentType; // verse, sermon, daily_verse
    private final Long contentId;
    private final String contentTitle;
    private final Long viewCount;
    private final Long favoriteCount;
    private final Long shareCount;
    private final Double popularityScore;
    private final LocalDate aggregationDate;
    private final String period; // daily, weekly, monthly
    private final LocalDateTime updatedAt;
    
    /**
     * 인기 콘텐츠 집계 생성
     */
    public static PopularContent create(String contentType, Long contentId, String contentTitle,
                                       Long viewCount, Long favoriteCount, LocalDate aggregationDate, String period) {
        double popularityScore = calculatePopularityScore(viewCount, favoriteCount, 0L);
        
        return PopularContent.builder()
            .contentType(contentType)
            .contentId(contentId)
            .contentTitle(contentTitle)
            .viewCount(viewCount)
            .favoriteCount(favoriteCount)
            .shareCount(0L)
            .popularityScore(popularityScore)
            .aggregationDate(aggregationDate)
            .period(period)
            .updatedAt(LocalDateTime.now())
            .build();
    }
    
    /**
     * 통계 업데이트
     */
    public PopularContent updateStats(Long newViewCount, Long newFavoriteCount, Long newShareCount) {
        double newScore = calculatePopularityScore(newViewCount, newFavoriteCount, newShareCount);
        
        return this.toBuilder()
            .viewCount(newViewCount)
            .favoriteCount(newFavoriteCount)
            .shareCount(newShareCount)
            .popularityScore(newScore)
            .updatedAt(LocalDateTime.now())
            .build();
    }
    
    /**
     * 인기도 점수 계산
     * 조회수 * 1 + 즐겨찾기 * 3 + 공유수 * 5
     */
    private static double calculatePopularityScore(Long viewCount, Long favoriteCount, Long shareCount) {
        return (viewCount != null ? viewCount : 0) * 1.0 +
               (favoriteCount != null ? favoriteCount : 0) * 3.0 +
               (shareCount != null ? shareCount : 0) * 5.0;
    }
}
```

## 🎯 Phase 2: Analytics Application Services

### 2.1 Analytics Application Service
**File**: `src/main/java/com/dong/bible/application/service/AnalyticsApplicationService.java`

```java
package com.dong.bible.application.service;

import com.dong.bible.application.dto.query.UsageStatisticsQuery;
import com.dong.bible.application.dto.query.PopularContentQuery;
import com.dong.bible.domain.analytics.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 분석 관련 애플리케이션 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AnalyticsApplicationService {
    
    private final AnalyticsRepository analyticsRepository;
    private final UserActivityLogger activityLogger;
    private final SearchAnalyzer searchAnalyzer;
    
    /**
     * 사용자 활동 로깅
     */
    @Transactional
    public void logUserActivity(Long userId, String sessionId, ActivityType activityType, 
                               String endpoint, Long resourceId, String resourceType) {
        log.debug("Logging user activity: userId={}, type={}, endpoint={}", 
            userId, activityType, endpoint);
        
        UserActivity activity = UserActivity.createNew(userId, sessionId, activityType, endpoint)
            .withResource(resourceId, resourceType);
        
        analyticsRepository.saveUserActivity(activity);
    }
    
    /**
     * 검색 로그 기록
     */
    @Transactional
    public void logSearch(Long userId, String sessionId, String searchQuery, 
                         SearchType searchType, Long resultCount, Integer responseTime) {
        log.debug("Logging search: userId={}, query={}, type={}, results={}", 
            userId, searchQuery, searchType, resultCount);
        
        SearchLog searchLog = SearchLog.create(userId, sessionId, searchQuery, searchType)
            .withResults(resultCount, responseTime, "elasticsearch");
        
        analyticsRepository.saveSearchLog(searchLog);
    }
    
    /**
     * 사용 통계 조회
     */
    public UsageStatisticsQuery getUsageStatistics(LocalDate startDate, LocalDate endDate) {
        log.info("Getting usage statistics: {} ~ {}", startDate, endDate);
        
        // 전체 활동 수
        long totalActivities = analyticsRepository.countActivitiesByDateRange(startDate, endDate);
        
        // 활성 사용자 수
        long activeUsers = analyticsRepository.countActiveUsersByDateRange(startDate, endDate);
        
        // 검색 통계
        long totalSearches = analyticsRepository.countSearchesByDateRange(startDate, endDate);
        long successfulSearches = analyticsRepository.countSuccessfulSearchesByDateRange(startDate, endDate);
        double searchSuccessRate = totalSearches > 0 ? 
            (double) successfulSearches / totalSearches * 100 : 0.0;
        
        // 일별 활동 분포
        Map<LocalDate, Long> dailyActivities = analyticsRepository
            .getDailyActivityCounts(startDate, endDate);
        
        return UsageStatisticsQuery.builder()
            .startDate(startDate)
            .endDate(endDate)
            .totalActivities(totalActivities)
            .activeUsers(activeUsers)
            .totalSearches(totalSearches)
            .searchSuccessRate(searchSuccessRate)
            .dailyActivities(dailyActivities)
            .build();
    }
    
    /**
     * 인기 콘텐츠 조회
     */
    public List<PopularContentQuery> getPopularContent(String contentType, String period, int limit) {
        log.info("Getting popular content: type={}, period={}, limit={}", 
            contentType, period, limit);
        
        LocalDate aggregationDate = LocalDate.now();
        
        List<PopularContent> popularContents = analyticsRepository
            .findPopularContent(contentType, period, aggregationDate, limit);
        
        return popularContents.stream()
            .map(PopularContentQuery::from)
            .collect(Collectors.toList());
    }
    
    /**
     * 사용자별 활동 통계
     */
    public Map<String, Object> getUserActivityStats(Long userId, int days) {
        log.info("Getting user activity stats: userId={}, days={}", userId, days);
        
        LocalDate startDate = LocalDate.now().minusDays(days);
        LocalDate endDate = LocalDate.now();
        
        Map<String, Object> stats = new HashMap<>();
        
        // 총 활동 수
        long totalActivities = analyticsRepository.countUserActivitiesByDateRange(
            userId, startDate, endDate);
        stats.put("totalActivities", totalActivities);
        
        // 활동 유형별 분포
        Map<ActivityType, Long> activityByType = analyticsRepository
            .getUserActivityByType(userId, startDate, endDate);
        stats.put("activityByType", activityByType);
        
        // 일별 활동 패턴
        Map<LocalDate, Long> dailyPattern = analyticsRepository
            .getUserDailyActivityPattern(userId, startDate, endDate);
        stats.put("dailyPattern", dailyPattern);
        
        // 평균 세션 시간 (근사치)
        double avgSessionTime = analyticsRepository.getAverageSessionTime(userId, days);
        stats.put("averageSessionTime", avgSessionTime);
        
        return stats;
    }
    
    /**
     * 검색 분석 결과
     */
    public Map<String, Object> getSearchAnalytics(int days) {
        log.info("Getting search analytics for {} days", days);
        
        LocalDate startDate = LocalDate.now().minusDays(days);
        LocalDate endDate = LocalDate.now();
        
        Map<String, Object> analytics = new HashMap<>();
        
        // 인기 검색어 Top 10
        List<Map<String, Object>> popularQueries = analyticsRepository
            .getPopularSearchQueries(startDate, endDate, 10);
        analytics.put("popularQueries", popularQueries);
        
        // 검색 타입별 분포
        Map<SearchType, Long> searchByType = analyticsRepository
            .getSearchCountByType(startDate, endDate);
        analytics.put("searchByType", searchByType);
        
        // 평균 응답 시간
        Double avgResponseTime = analyticsRepository
            .getAverageSearchResponseTime(startDate, endDate);
        analytics.put("averageResponseTime", avgResponseTime);
        
        // 무결과 검색어 Top 10
        List<String> noResultQueries = analyticsRepository
            .getNoResultSearchQueries(startDate, endDate, 10);
        analytics.put("noResultQueries", noResultQueries);
        
        return analytics;
    }
    
    /**
     * 인기 콘텐츠 집계 (배치 작업용)
     */
    @Transactional
    public void aggregatePopularContent(LocalDate aggregationDate, String period) {
        log.info("Aggregating popular content: date={}, period={}", aggregationDate, period);
        
        // 구절별 인기도 집계
        List<PopularContent> popularVerses = searchAnalyzer.aggregatePopularVerses(
            aggregationDate, period);
        
        // 설교별 인기도 집계  
        List<PopularContent> popularSermons = searchAnalyzer.aggregatePopularSermons(
            aggregationDate, period);
        
        // 일일 구절 인기도 집계
        List<PopularContent> popularDailyVerses = searchAnalyzer.aggregatePopularDailyVerses(
            aggregationDate, period);
        
        // 기존 데이터 삭제 후 새로 저장
        analyticsRepository.deletePopularContentByDateAndPeriod(aggregationDate, period);
        
        popularVerses.forEach(analyticsRepository::savePopularContent);
        popularSermons.forEach(analyticsRepository::savePopularContent);
        popularDailyVerses.forEach(analyticsRepository::savePopularContent);
        
        log.info("Popular content aggregation completed: verses={}, sermons={}, dailyVerses={}",
            popularVerses.size(), popularSermons.size(), popularDailyVerses.size());
    }
}
```

### 2.2 Recommendation Application Service  
**File**: `src/main/java/com/dong/bible/application/service/RecommendationApplicationService.java`

```java
package com.dong.bible.application.service;

import com.dong.bible.application.dto.query.RecommendationQuery;
import com.dong.bible.domain.recommendation.RecommendationEngine;
import com.dong.bible.domain.recommendation.UserPreference;
import com.dong.bible.domain.user.User;
import com.dong.bible.domain.verse.BibleVerse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 추천 시스템 애플리케이션 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationApplicationService {
    
    private final RecommendationEngine recommendationEngine;
    private final UserApplicationService userService;
    private final VerseApplicationService verseService;
    private final ReadingHistoryApplicationService historyService;
    private final FavoriteApplicationService favoriteService;
    
    /**
     * 사용자별 구절 추천
     */
    public List<RecommendationQuery> getRecommendedVerses(Long userId, int limit) {
        log.info("Getting verse recommendations for user: {}, limit: {}", userId, limit);
        
        // 사용자 선호도 분석
        UserPreference preference = analyzeUserPreference(userId);
        
        // 추천 엔진을 통한 구절 추천
        List<BibleVerse> recommendedVerses = recommendationEngine
            .recommendVerses(preference, limit);
        
        return recommendedVerses.stream()
            .map(verse -> RecommendationQuery.fromVerse(verse, calculateRelevanceScore(preference, verse)))
            .collect(Collectors.toList());
    }
    
    /**
     * 유사한 사용자 기반 추천 (Collaborative Filtering)
     */
    public List<RecommendationQuery> getSimilarUserRecommendations(Long userId, int limit) {
        log.info("Getting similar user recommendations for user: {}, limit: {}", userId, limit);
        
        List<Long> similarUsers = recommendationEngine.findSimilarUsers(userId, 10);
        
        List<BibleVerse> recommendations = recommendationEngine
            .recommendBasedOnSimilarUsers(userId, similarUsers, limit);
        
        return recommendations.stream()
            .map(verse -> RecommendationQuery.fromVerse(verse, calculateCollaborativeScore(userId, verse)))
            .collect(Collectors.toList());
    }
    
    /**
     * 읽기 이력 기반 추천
     */
    public List<RecommendationQuery> getReadingHistoryBasedRecommendations(Long userId, int limit) {
        log.info("Getting reading history based recommendations for user: {}, limit: {}", userId, limit);
        
        // 사용자 읽기 패턴 분석
        List<Long> preferredBooks = historyService.getMostReadBooks(userId, 5);
        List<String> preferredTopics = historyService.getPreferredTopics(userId);
        
        // 패턴 기반 추천
        List<BibleVerse> recommendations = recommendationEngine
            .recommendBasedOnReadingPattern(preferredBooks, preferredTopics, limit);
        
        return recommendations.stream()
            .map(verse -> RecommendationQuery.fromVerse(verse, calculateContentScore(verse, preferredTopics)))
            .collect(Collectors.toList());
    }
    
    /**
     * 인기 기반 추천 (Trending)
     */
    public List<RecommendationQuery> getTrendingRecommendations(int limit) {
        log.info("Getting trending recommendations, limit: {}", limit);
        
        List<BibleVerse> trendingVerses = recommendationEngine.getTrendingVerses(limit);
        
        return trendingVerses.stream()
            .map(verse -> RecommendationQuery.fromVerse(verse, calculateTrendingScore(verse)))
            .collect(Collectors.toList());
    }
    
    /**
     * 하이브리드 추천 (여러 방식 조합)
     */
    public List<RecommendationQuery> getHybridRecommendations(Long userId, int limit) {
        log.info("Getting hybrid recommendations for user: {}, limit: {}", userId, limit);
        
        // 각 방식별로 일부씩 추천
        int perMethod = limit / 3;
        
        List<RecommendationQuery> contentBased = getRecommendedVerses(userId, perMethod);
        List<RecommendationQuery> collaborative = getSimilarUserRecommendations(userId, perMethod);  
        List<RecommendationQuery> trending = getTrendingRecommendations(perMethod);
        
        // 결과 합치기 및 중복 제거
        List<RecommendationQuery> combined = new ArrayList<>();
        combined.addAll(contentBased);
        combined.addAll(collaborative);
        combined.addAll(trending);
        
        return combined.stream()
            .distinct()
            .sorted((r1, r2) -> Double.compare(r2.getRelevanceScore(), r1.getRelevanceScore()))
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    /**
     * 사용자 선호도 분석
     */
    private UserPreference analyzeUserPreference(Long userId) {
        // 즐겨찾기한 구절 분석
        List<Long> favoriteVerseIds = favoriteService.getFavoriteVerseIds(userId);
        
        // 읽기 이력 분석
        List<Long> readBooks = historyService.getReadBookIds(userId);
        
        // 검색 이력 분석 (최근 검색어에서 관심사 추출)
        List<String> recentSearches = getRecentSearchQueries(userId, 20);
        
        return UserPreference.builder()
            .userId(userId)
            .favoriteVerseIds(favoriteVerseIds)
            .readBookIds(readBooks)
            .searchKeywords(recentSearches)
            .build();
    }
    
    /**
     * 관련성 점수 계산 (Content-based)
     */
    private double calculateRelevanceScore(UserPreference preference, BibleVerse verse) {
        double score = 0.0;
        
        // 선호 책에 포함되면 가점
        if (preference.getReadBookIds().contains(verse.getBookId())) {
            score += 0.3;
        }
        
        // 검색 키워드와 매칭되면 가점
        String verseText = verse.getVerseText().toLowerCase();
        for (String keyword : preference.getSearchKeywords()) {
            if (verseText.contains(keyword.toLowerCase())) {
                score += 0.2;
                break;
            }
        }
        
        // 기본 점수
        score += 0.5;
        
        return Math.min(score, 1.0);
    }
    
    /**
     * 협업 필터링 점수 계산
     */
    private double calculateCollaborativeScore(Long userId, BibleVerse verse) {
        // 유사 사용자들의 이 구절에 대한 관심도
        return recommendationEngine.calculateSimilarUserInterest(userId, verse.getId());
    }
    
    /**
     * 콘텐츠 기반 점수 계산
     */
    private double calculateContentScore(BibleVerse verse, List<String> preferredTopics) {
        double score = 0.5; // 기본 점수
        
        String verseText = verse.getVerseText().toLowerCase();
        for (String topic : preferredTopics) {
            if (verseText.contains(topic.toLowerCase())) {
                score += 0.1;
            }
        }
        
        return Math.min(score, 1.0);
    }
    
    /**
     * 트렌딩 점수 계산
     */
    private double calculateTrendingScore(BibleVerse verse) {
        // 최근 조회수, 즐겨찾기 수 등을 기반으로 점수 계산
        return recommendationEngine.calculateTrendingScore(verse.getId());
    }
    
    /**
     * 최근 검색어 조회
     */
    private List<String> getRecentSearchQueries(Long userId, int limit) {
        // AnalyticsRepository에서 최근 검색어 조회
        return analyticsRepository.getRecentSearchQueries(userId, limit);
    }
}
```

## ✅ Implementation Checklist

### Domain Layer
- [ ] Create UserActivity domain object with activity tracking logic
- [ ] Create SearchLog domain object for search analytics
- [ ] Create PopularContent domain object for content ranking
- [ ] Create UserPreference domain object for recommendations
- [ ] Define AnalyticsRepository and RecommendationRepository interfaces

### Application Layer
- [ ] Create AnalyticsApplicationService for usage analytics
- [ ] Create RecommendationApplicationService for personalization
- [ ] Create UserEngagementService for engagement metrics
- [ ] Create all query and command DTOs
- [ ] Add activity logging to existing services

### Infrastructure Layer
- [ ] Implement analytics data collection infrastructure
- [ ] Create recommendation engines (collaborative, content-based, hybrid)
- [ ] Add analytics database schema and repositories
- [ ] Implement caching for frequently accessed analytics
- [ ] Create batch jobs for data aggregation

### Web Layer
- [ ] Create AnalyticsController for analytics APIs
- [ ] Create RecommendationController for recommendation APIs
- [ ] Add analytics middleware for automatic activity logging
- [ ] Create analytics dashboard endpoints
- [ ] Add proper authentication and authorization

### Data Processing
- [ ] Create scheduled jobs for popular content aggregation
- [ ] Implement user preference learning algorithms
- [ ] Add real-time analytics pipeline
- [ ] Create data export/import functionality
- [ ] Add analytics data retention policies

## 🧪 Testing Strategy

### Unit Tests
```java
@Test
@DisplayName("사용자 활동 로깅 테스트")
void logUserActivity_Success() {
    // Given
    Long userId = 1L;
    String sessionId = "session-123";
    ActivityType activityType = ActivityType.VIEW_VERSE;
    
    // When
    analyticsService.logUserActivity(userId, sessionId, activityType, "/api/verses/1", 1L, "verse");
    
    // Then
    verify(analyticsRepository).saveUserActivity(any(UserActivity.class));
}

@Test
@DisplayName("개인화 추천 테스트")
void getRecommendedVerses_Success() {
    // Given
    Long userId = 1L;
    int limit = 5;
    
    // When
    List<RecommendationQuery> recommendations = recommendationService.getRecommendedVerses(userId, limit);
    
    // Then
    assertThat(recommendations).isNotEmpty();
    assertThat(recommendations).hasSize(limit);
    assertThat(recommendations).allSatisfy(r -> 
        assertThat(r.getRelevanceScore()).isBetween(0.0, 1.0));
}
```

## 🎯 Success Criteria

구현 완료 후:
- ✅ 모든 사용자 활동이 자동으로 추적되고 분석됨
- ✅ 인기 콘텐츠가 실시간으로 집계되고 순위 제공
- ✅ 개인화된 구절 및 콘텐츠 추천 시스템 동작
- ✅ 사용 패턴 분석을 통한 비즈니스 인사이트 제공
- ✅ 검색 분석으로 사용자 경험 개선 데이터 확보
- ✅ 실시간 대시보드로 서비스 운영 지표 모니터링

이 가이드를 따라 구현하면 데이터 기반의 지능형 성경 서비스가 완성됩니다.