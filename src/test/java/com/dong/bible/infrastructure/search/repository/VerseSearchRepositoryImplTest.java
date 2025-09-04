package com.dong.bible.infrastructure.search.repository;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.dong.bible.infrastructure.search.document.VerseSearchDocument;
import com.dong.bible.infrastructure.search.dto.EnhancedSearchHit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.TotalHitsRelation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("VerseSearchRepositoryImpl 테스트")
class VerseSearchRepositoryImplTest {

    @Mock
    private ElasticsearchTemplate elasticsearchTemplate;

    @InjectMocks
    private VerseSearchRepositoryImpl verseSearchRepositoryImpl;

    @Captor
    private ArgumentCaptor<NativeQuery> queryCaptor;

    private VerseSearchDocument sampleDocument1;
    private VerseSearchDocument sampleDocument2;
    private SearchHit<VerseSearchDocument> searchHit1;
    private SearchHit<VerseSearchDocument> searchHit2;
    private SearchHits<VerseSearchDocument> mockSearchHits;

    @BeforeEach
    void setUp() {
        sampleDocument1 = VerseSearchDocument.builder()
                .id("1:1:1")
                .bookId(1)
                .bookName("창세기")
                .chapter(1)
                .verse(1)
                .content("태초에 하나님이 천지를 창조하시니라")
                .displayReference("창세기 1:1")
                .build();

        sampleDocument2 = VerseSearchDocument.builder()
                .id("43:3:16")
                .bookId(43)
                .bookName("요한복음")
                .chapter(3)
                .verse(16)
                .content("하나님이 세상을 이처럼 사랑하사 독생자를 주셨으니")
                .displayReference("요한복음 3:16")
                .build();

        // Mock SearchHit with highlights
        searchHit1 = createMockSearchHit(sampleDocument1, 0.85f, 
                Map.of("content", List.of("태초에 <mark>하나님</mark>이 천지를 창조하시니라")));
        
        searchHit2 = createMockSearchHit(sampleDocument2, 0.92f,
                Map.of("content", List.of("<mark>하나님</mark>이 세상을 이처럼 <mark>사랑</mark>하사")));
    }

    @SuppressWarnings("unchecked")
    private SearchHit<VerseSearchDocument> createMockSearchHit(VerseSearchDocument document, 
                                                              float score, 
                                                              Map<String, List<String>> highlights) {
        SearchHit<VerseSearchDocument> hit = mock(SearchHit.class);
        when(hit.getContent()).thenReturn(document);
        when(hit.getScore()).thenReturn(score);
        when(hit.getHighlightFields()).thenReturn(highlights);
        return hit;
    }

    private SearchHits<VerseSearchDocument> createMockSearchHits(List<SearchHit<VerseSearchDocument>> hits, 
                                                                long totalHits) {
        SearchHits<VerseSearchDocument> searchHits = mock(SearchHits.class);
        when(searchHits.getSearchHits()).thenReturn(hits);
        when(searchHits.getTotalHits()).thenReturn(totalHits);
        when(searchHits.getTotalHitsRelation()).thenReturn(TotalHitsRelation.EQUAL_TO);
        return searchHits;
    }

    // ========================================
    // findByContentWithHighlight (단일) 테스트
    // ========================================

    @Test
    @DisplayName("Enhanced 구절 내용 검색 - 성공")
    void findByContentWithHighlight_Success() {
        // given
        String keyword = "하나님";
        List<SearchHit<VerseSearchDocument>> hits = Arrays.asList(searchHit1, searchHit2);
        mockSearchHits = createMockSearchHits(hits, 2);
        
        when(elasticsearchTemplate.search(any(NativeQuery.class), eq(VerseSearchDocument.class)))
                .thenReturn(mockSearchHits);

        // when
        List<EnhancedSearchHit> results = verseSearchRepositoryImpl.findByContentWithHighlight(keyword);

        // then
        assertThat(results).hasSize(2);
        
        // 첫 번째 결과 검증
        EnhancedSearchHit result1 = results.get(0);
        assertThat(result1.getDocument().getId()).isEqualTo("1:1:1");
        assertThat(result1.getDocument().getBookName()).isEqualTo("창세기");
        assertThat(result1.getScore()).isEqualTo(0.85f);
        assertThat(result1.getHighlightFields()).containsKey("content");
        assertThat(result1.getHighlightFields().get("content")[0]).contains("<mark>하나님</mark>");

        // 두 번째 결과 검증
        EnhancedSearchHit result2 = results.get(1);
        assertThat(result2.getDocument().getBookName()).isEqualTo("요한복음");
        assertThat(result2.getScore()).isEqualTo(0.92f);
        assertThat(result2.getHighlightFields().get("content")[0]).contains("<mark>사랑</mark>");

        // ElasticsearchTemplate 호출 검증
        verify(elasticsearchTemplate).search(queryCaptor.capture(), eq(VerseSearchDocument.class));
        
        NativeQuery capturedQuery = queryCaptor.getValue();
        assertThat(capturedQuery).isNotNull();
        assertThat(capturedQuery.getHighlightQuery()).isNotNull();
    }

    @Test
    @DisplayName("Enhanced 구절 내용 검색 - 검색결과 없음")
    void findByContentWithHighlight_NoResults() {
        // given
        String keyword = "존재하지않는키워드";
        mockSearchHits = createMockSearchHits(Collections.emptyList(), 0);
        
        when(elasticsearchTemplate.search(any(NativeQuery.class), eq(VerseSearchDocument.class)))
                .thenReturn(mockSearchHits);

        // when
        List<EnhancedSearchHit> results = verseSearchRepositoryImpl.findByContentWithHighlight(keyword);

        // then
        assertThat(results).isEmpty();
        verify(elasticsearchTemplate).search(any(NativeQuery.class), eq(VerseSearchDocument.class));
    }

    @Test
    @DisplayName("Enhanced 구절 내용 검색 - ElasticsearchTemplate 예외")
    void findByContentWithHighlight_ElasticsearchException() {
        // given
        String keyword = "하나님";
        when(elasticsearchTemplate.search(any(NativeQuery.class), eq(VerseSearchDocument.class)))
                .thenThrow(new RuntimeException("ElasticSearch 연결 실패"));

        // when & then
        assertThatThrownBy(() -> verseSearchRepositoryImpl.findByContentWithHighlight(keyword))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("ElasticSearch 연결 실패");

        verify(elasticsearchTemplate).search(any(NativeQuery.class), eq(VerseSearchDocument.class));
    }

    // ========================================
    // findByContentWithHighlight (페이징) 테스트
    // ========================================

    @Test
    @DisplayName("Enhanced 구절 내용 검색 (페이징) - 성공")
    void findByContentWithHighlight_WithPaging_Success() {
        // given
        String keyword = "하나님";
        Pageable pageable = PageRequest.of(0, 10);
        List<SearchHit<VerseSearchDocument>> hits = Arrays.asList(searchHit1);
        mockSearchHits = createMockSearchHits(hits, 1);
        
        when(elasticsearchTemplate.search(any(NativeQuery.class), eq(VerseSearchDocument.class)))
                .thenReturn(mockSearchHits);

        // when
        Page<EnhancedSearchHit> results = verseSearchRepositoryImpl.findByContentWithHighlight(keyword, pageable);

        // then
        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getTotalElements()).isEqualTo(1);
        assertThat(results.getTotalPages()).isEqualTo(1);
        assertThat(results.getNumber()).isEqualTo(0);
        assertThat(results.getSize()).isEqualTo(10);
        
        EnhancedSearchHit result = results.getContent().get(0);
        assertThat(result.getDocument().getBookName()).isEqualTo("창세기");
        assertThat(result.getScore()).isEqualTo(0.85f);

        // 페이징 쿼리 검증
        verify(elasticsearchTemplate).search(queryCaptor.capture(), eq(VerseSearchDocument.class));
        NativeQuery capturedQuery = queryCaptor.getValue();
        assertThat(capturedQuery.getPageable()).isEqualTo(pageable);
    }

    @Test
    @DisplayName("Enhanced 구절 내용 검색 (페이징) - 빈 페이지")
    void findByContentWithHighlight_WithPaging_EmptyPage() {
        // given
        String keyword = "하나님";
        Pageable pageable = PageRequest.of(1, 10); // 두 번째 페이지
        mockSearchHits = createMockSearchHits(Collections.emptyList(), 5); // 전체 5개 결과, 하지만 이 페이지는 비어있음
        
        when(elasticsearchTemplate.search(any(NativeQuery.class), eq(VerseSearchDocument.class)))
                .thenReturn(mockSearchHits);

        // when
        Page<EnhancedSearchHit> results = verseSearchRepositoryImpl.findByContentWithHighlight(keyword, pageable);

        // then
        assertThat(results.getContent()).isEmpty();
        assertThat(results.getTotalElements()).isEqualTo(5);
        assertThat(results.getNumber()).isEqualTo(1);
        assertThat(results.getSize()).isEqualTo(10);
    }

    // ========================================
    // findByBookNameWithHighlight 테스트
    // ========================================

    @Test
    @DisplayName("Enhanced 책 이름 검색 - 성공")
    void findByBookNameWithHighlight_Success() {
        // given
        String bookName = "창세기";
        SearchHit<VerseSearchDocument> bookSearchHit = createMockSearchHit(sampleDocument1, 0.95f,
                Map.of("bookName", List.of("<mark>창세기</mark>")));
        
        List<SearchHit<VerseSearchDocument>> hits = Arrays.asList(bookSearchHit);
        mockSearchHits = createMockSearchHits(hits, 1);
        
        when(elasticsearchTemplate.search(any(NativeQuery.class), eq(VerseSearchDocument.class)))
                .thenReturn(mockSearchHits);

        // when
        List<EnhancedSearchHit> results = verseSearchRepositoryImpl.findByBookNameWithHighlight(bookName);

        // then
        assertThat(results).hasSize(1);
        
        EnhancedSearchHit result = results.get(0);
        assertThat(result.getDocument().getBookName()).isEqualTo("창세기");
        assertThat(result.getScore()).isEqualTo(0.95f);
        assertThat(result.getHighlightFields()).containsKey("bookName");
        assertThat(result.getHighlightFields().get("bookName")[0]).contains("<mark>창세기</mark>");

        verify(elasticsearchTemplate).search(any(NativeQuery.class), eq(VerseSearchDocument.class));
    }

    // ========================================
    // searchByMultipleConditionsWithHighlight 테스트
    // ========================================

    @Test
    @DisplayName("Enhanced 복합 조건 검색 - 모든 조건 포함")
    void searchByMultipleConditionsWithHighlight_AllConditions_Success() {
        // given
        String keyword = "하나님";
        String bookName = "창세기";
        Integer chapter = 1;
        Pageable pageable = PageRequest.of(0, 5);
        
        List<SearchHit<VerseSearchDocument>> hits = Arrays.asList(searchHit1);
        mockSearchHits = createMockSearchHits(hits, 1);
        
        when(elasticsearchTemplate.search(any(NativeQuery.class), eq(VerseSearchDocument.class)))
                .thenReturn(mockSearchHits);

        // when
        Page<EnhancedSearchHit> results = verseSearchRepositoryImpl
                .searchByMultipleConditionsWithHighlight(keyword, bookName, chapter, pageable);

        // then
        assertThat(results.getContent()).hasSize(1);
        
        EnhancedSearchHit result = results.getContent().get(0);
        assertThat(result.getDocument().getBookName()).isEqualTo("창세기");
        assertThat(result.getDocument().getChapter()).isEqualTo(1);
        assertThat(result.getScore()).isEqualTo(0.85f);

        verify(elasticsearchTemplate).search(queryCaptor.capture(), eq(VerseSearchDocument.class));
        NativeQuery capturedQuery = queryCaptor.getValue();
        assertThat(capturedQuery.getPageable()).isEqualTo(pageable);
    }

    @Test
    @DisplayName("Enhanced 복합 조건 검색 - 키워드만 제공")
    void searchByMultipleConditionsWithHighlight_KeywordOnly_Success() {
        // given
        String keyword = "하나님";
        String bookName = null;
        Integer chapter = null;
        Pageable pageable = PageRequest.of(0, 10);
        
        List<SearchHit<VerseSearchDocument>> hits = Arrays.asList(searchHit1, searchHit2);
        mockSearchHits = createMockSearchHits(hits, 2);
        
        when(elasticsearchTemplate.search(any(NativeQuery.class), eq(VerseSearchDocument.class)))
                .thenReturn(mockSearchHits);

        // when
        Page<EnhancedSearchHit> results = verseSearchRepositoryImpl
                .searchByMultipleConditionsWithHighlight(keyword, bookName, chapter, pageable);

        // then
        assertThat(results.getContent()).hasSize(2);
        assertThat(results.getTotalElements()).isEqualTo(2);

        verify(elasticsearchTemplate).search(any(NativeQuery.class), eq(VerseSearchDocument.class));
    }

    @Test
    @DisplayName("Enhanced 복합 조건 검색 - 빈 키워드 처리")
    void searchByMultipleConditionsWithHighlight_EmptyKeyword_Success() {
        // given
        String keyword = ""; // 빈 키워드
        String bookName = "창세기";
        Integer chapter = 1;
        Pageable pageable = PageRequest.of(0, 10);
        
        List<SearchHit<VerseSearchDocument>> hits = Arrays.asList(searchHit1);
        mockSearchHits = createMockSearchHits(hits, 1);
        
        when(elasticsearchTemplate.search(any(NativeQuery.class), eq(VerseSearchDocument.class)))
                .thenReturn(mockSearchHits);

        // when
        Page<EnhancedSearchHit> results = verseSearchRepositoryImpl
                .searchByMultipleConditionsWithHighlight(keyword, bookName, chapter, pageable);

        // then
        assertThat(results.getContent()).hasSize(1);
        
        // 빈 키워드는 제외되고 책이름과 장번호 조건만 적용됨
        EnhancedSearchHit result = results.getContent().get(0);
        assertThat(result.getDocument().getBookName()).isEqualTo("창세기");
        assertThat(result.getDocument().getChapter()).isEqualTo(1);

        verify(elasticsearchTemplate).search(any(NativeQuery.class), eq(VerseSearchDocument.class));
    }

    // ========================================
    // searchWithSynonyms 테스트
    // ========================================

    @Test
    @DisplayName("동의어 기반 Enhanced 검색 - 동의어 사용")
    void searchWithSynonyms_UseSynonyms_Success() {
        // given
        String keyword = "주님";
        boolean useSynonyms = true;
        Pageable pageable = PageRequest.of(0, 10);
        
        List<SearchHit<VerseSearchDocument>> hits = Arrays.asList(searchHit1, searchHit2);
        mockSearchHits = createMockSearchHits(hits, 2);
        
        when(elasticsearchTemplate.search(any(NativeQuery.class), eq(VerseSearchDocument.class)))
                .thenReturn(mockSearchHits);

        // when
        Page<EnhancedSearchHit> results = verseSearchRepositoryImpl
                .searchWithSynonyms(keyword, useSynonyms, pageable);

        // then
        assertThat(results.getContent()).hasSize(2);
        assertThat(results.getTotalElements()).isEqualTo(2);
        
        // 동의어 검색으로 "하나님" 관련 결과도 포함됨
        assertThat(results.getContent().get(0).getDocument().getContent()).contains("하나님");
        assertThat(results.getContent().get(1).getDocument().getContent()).contains("하나님");

        verify(elasticsearchTemplate).search(queryCaptor.capture(), eq(VerseSearchDocument.class));
        NativeQuery capturedQuery = queryCaptor.getValue();
        assertThat(capturedQuery.getPageable()).isEqualTo(pageable);
    }

    @Test
    @DisplayName("동의어 기반 Enhanced 검색 - 동의어 미사용")
    void searchWithSynonyms_NoSynonyms_Success() {
        // given
        String keyword = "하나님";
        boolean useSynonyms = false;
        Pageable pageable = PageRequest.of(0, 10);
        
        List<SearchHit<VerseSearchDocument>> hits = Arrays.asList(searchHit1);
        mockSearchHits = createMockSearchHits(hits, 1);
        
        when(elasticsearchTemplate.search(any(NativeQuery.class), eq(VerseSearchDocument.class)))
                .thenReturn(mockSearchHits);

        // when
        Page<EnhancedSearchHit> results = verseSearchRepositoryImpl
                .searchWithSynonyms(keyword, useSynonyms, pageable);

        // then
        assertThat(results.getContent()).hasSize(1);
        
        EnhancedSearchHit result = results.getContent().get(0);
        assertThat(result.getDocument().getContent()).contains("하나님");
        assertThat(result.getScore()).isEqualTo(0.85f);

        verify(elasticsearchTemplate).search(any(NativeQuery.class), eq(VerseSearchDocument.class));
    }

    // ========================================
    // 하이라이팅 설정 테스트
    // ========================================

    @Test
    @DisplayName("하이라이트 필드 변환 로직 검증")
    void convertToEnhancedHit_HighlightConversion_Success() {
        // given
        String keyword = "하나님";
        
        // 복잡한 하이라이팅 결과
        SearchHit<VerseSearchDocument> complexHit = createMockSearchHit(sampleDocument1, 0.75f,
                Map.of(
                    "content", List.of("태초에 <mark>하나님</mark>이", "천지를 <mark>창조</mark>하시니라"),
                    "bookName", List.of("<mark>창세</mark>기")
                ));
        
        List<SearchHit<VerseSearchDocument>> hits = Arrays.asList(complexHit);
        mockSearchHits = createMockSearchHits(hits, 1);
        
        when(elasticsearchTemplate.search(any(NativeQuery.class), eq(VerseSearchDocument.class)))
                .thenReturn(mockSearchHits);

        // when
        List<EnhancedSearchHit> results = verseSearchRepositoryImpl.findByContentWithHighlight(keyword);

        // then
        assertThat(results).hasSize(1);
        
        EnhancedSearchHit result = results.get(0);
        assertThat(result.getHighlightFields()).containsKeys("content", "bookName");
        assertThat(result.getHighlightFields().get("content")).hasSize(2);
        assertThat(result.getHighlightFields().get("content")[0]).contains("<mark>하나님</mark>");
        assertThat(result.getHighlightFields().get("content")[1]).contains("<mark>창조</mark>");
        assertThat(result.getHighlightFields().get("bookName")[0]).contains("<mark>창세</mark>");
    }

    @Test
    @DisplayName("하이라이팅 없는 검색 결과 처리")
    void convertToEnhancedHit_NoHighlights_Success() {
        // given
        String keyword = "하나님";
        
        // 하이라이팅이 없는 결과
        SearchHit<VerseSearchDocument> noHighlightHit = createMockSearchHit(sampleDocument1, 0.45f,
                Collections.emptyMap());
        
        List<SearchHit<VerseSearchDocument>> hits = Arrays.asList(noHighlightHit);
        mockSearchHits = createMockSearchHits(hits, 1);
        
        when(elasticsearchTemplate.search(any(NativeQuery.class), eq(VerseSearchDocument.class)))
                .thenReturn(mockSearchHits);

        // when
        List<EnhancedSearchHit> results = verseSearchRepositoryImpl.findByContentWithHighlight(keyword);

        // then
        assertThat(results).hasSize(1);
        
        EnhancedSearchHit result = results.get(0);
        assertThat(result.getDocument().getId()).isEqualTo("1:1:1");
        assertThat(result.getScore()).isEqualTo(0.45f);
        assertThat(result.getHighlightFields()).isEmpty();
    }

    // ========================================
    // 쿼리 생성 검증 테스트
    // ========================================

    @Test
    @DisplayName("NativeQuery 생성 검증 - 하이라이팅 포함")
    void verifyNativeQueryCreation_WithHighlighting() {
        // given
        String keyword = "하나님";
        mockSearchHits = createMockSearchHits(Collections.emptyList(), 0);
        
        when(elasticsearchTemplate.search(any(NativeQuery.class), eq(VerseSearchDocument.class)))
                .thenReturn(mockSearchHits);

        // when
        verseSearchRepositoryImpl.findByContentWithHighlight(keyword);

        // then
        verify(elasticsearchTemplate).search(queryCaptor.capture(), eq(VerseSearchDocument.class));
        
        NativeQuery capturedQuery = queryCaptor.getValue();
        assertThat(capturedQuery).isNotNull();
        assertThat(capturedQuery.getQuery()).isNotNull();
        assertThat(capturedQuery.getHighlightQuery()).isNotNull();
        // 페이징 없는 메서드에서도 기본 Pageable이 설정될 수 있음
    }

    @Test
    @DisplayName("NativeQuery 생성 검증 - 페이징 포함")
    void verifyNativeQueryCreation_WithPaging() {
        // given
        String keyword = "하나님";
        Pageable pageable = PageRequest.of(1, 20);
        mockSearchHits = createMockSearchHits(Collections.emptyList(), 0);
        
        when(elasticsearchTemplate.search(any(NativeQuery.class), eq(VerseSearchDocument.class)))
                .thenReturn(mockSearchHits);

        // when
        verseSearchRepositoryImpl.findByContentWithHighlight(keyword, pageable);

        // then
        verify(elasticsearchTemplate).search(queryCaptor.capture(), eq(VerseSearchDocument.class));
        
        NativeQuery capturedQuery = queryCaptor.getValue();
        assertThat(capturedQuery).isNotNull();
        assertThat(capturedQuery.getQuery()).isNotNull();
        assertThat(capturedQuery.getHighlightQuery()).isNotNull();
        assertThat(capturedQuery.getPageable()).isEqualTo(pageable);
    }

    // ========================================
    // 예외 처리 테스트
    // ========================================

    @Test
    @DisplayName("ElasticsearchTemplate null 결과 처리")
    void handleNullSearchHitsFromElasticsearchTemplate() {
        // given
        String keyword = "하나님";
        when(elasticsearchTemplate.search(any(NativeQuery.class), eq(VerseSearchDocument.class)))
                .thenReturn(null);

        // when & then
        assertThatThrownBy(() -> verseSearchRepositoryImpl.findByContentWithHighlight(keyword))
                .isInstanceOf(NullPointerException.class);

        verify(elasticsearchTemplate).search(any(NativeQuery.class), eq(VerseSearchDocument.class));
    }

    @Test
    @DisplayName("ElasticsearchTemplate 연결 시간 초과 예외")
    void handleElasticsearchTimeoutException() {
        // given
        String keyword = "하나님";
        when(elasticsearchTemplate.search(any(NativeQuery.class), eq(VerseSearchDocument.class)))
                .thenThrow(new RuntimeException("Connection timeout"));

        // when & then
        assertThatThrownBy(() -> verseSearchRepositoryImpl.findByContentWithHighlight(keyword))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Connection timeout");
    }
}