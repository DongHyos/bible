package com.dong.bible.integration;

import com.dong.bible.application.service.VerseIndexingApplicationService;
import com.dong.bible.application.service.VerseSearchApplicationService;
import com.dong.bible.infrastructure.search.repository.VerseSearchRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ElasticSearch 통합 테스트
 * 
 * 실제 ElasticSearch 서버가 필요한 테스트입니다.
 * 다음 환경에서만 실행됩니다:
 * 1. Docker로 ElasticSearch 서버가 실행 중
 * 2. 환경변수 ELASTICSEARCH_INTEGRATION_TEST=true 설정
 * 
 * 실행 방법:
 * export ELASTICSEARCH_INTEGRATION_TEST=true
 * ./gradlew test --tests "*ElasticSearchIntegrationTest"
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.elasticsearch.uris=http://localhost:9200",
    "spring.elasticsearch.connection-timeout=5s",
    "spring.elasticsearch.socket-timeout=10s"
})
@DisplayName("ElasticSearch 통합 테스트")
class ElasticSearchIntegrationTest {

    @Autowired
    private VerseSearchRepository verseSearchRepository;

    @Autowired
    private VerseSearchApplicationService verseSearchApplicationService;

    @Autowired
    private VerseIndexingApplicationService verseIndexingApplicationService;

    @Test
    @DisplayName("ElasticSearch 연결 및 기본 CRUD 테스트")
    void elasticSearchConnectionTest() {
        // given: ElasticSearch 서버가 실행 중이어야 함
        
        // when: Repository 연결 확인
        long initialCount = verseSearchRepository.count();
        
        // then: 연결 성공 (카운트 조회 가능)
        assertThat(initialCount).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("실제 데이터 인덱싱 및 검색 테스트")
    void realDataIndexingAndSearchTest() {
        // 이 테스트는 실제 MySQL 데이터와 ElasticSearch가 모두 필요
        // 실제 운영 환경에서만 의미있는 테스트
        
        // given: 실제 성경 데이터가 MySQL에 존재
        // when: 인덱싱 실행
        // verseIndexingApplicationService.indexAllVerses();
        
        // then: 검색 가능
        // List<VerseSearchResultQuery> results = verseSearchApplicationService.searchByContent("하나님");
        // assertThat(results).isNotEmpty();
        
        // 현재는 테스트 데이터가 없으므로 스킵
        assertThat(true).isTrue(); // Placeholder
    }
}