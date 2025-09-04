package com.dong.bible.infrastructure.search.repository;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.dong.bible.infrastructure.search.document.VerseSearchDocument;
import com.dong.bible.infrastructure.search.dto.EnhancedSearchHit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightParameters;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ElasticSearch 고급 검색 기능 Custom Repository 구현
 * ElasticsearchTemplate을 사용하여 하이라이팅, 스코어링 등 고급 기능 구현
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class VerseSearchRepositoryImpl implements VerseSearchRepositoryCustom {
    
    private final ElasticsearchTemplate elasticsearchTemplate;
    
    /**
     * Enhanced 구절 내용 검색 (하이라이팅 + 스코어 포함)
     */
    @Override
    public List<EnhancedSearchHit> findByContentWithHighlight(String keyword) {
        log.debug("Enhanced 검색 시작: keyword={}", keyword);
        
        // 검색 쿼리 생성
        Query matchQuery = Query.of(q -> q
                .match(m -> m
                        .field("content")
                        .query(keyword)
                        .analyzer("nori") // 한국어 분석기 사용
                )
        );
        
        // 하이라이팅 설정
        Highlight highlight = createHighlight("content");
        
        // Native Query 생성
        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(matchQuery)
                .withHighlightQuery(new HighlightQuery(highlight, VerseSearchDocument.class))
                .build();
        
        // 검색 실행
        SearchHits<VerseSearchDocument> searchHits = elasticsearchTemplate.search(
                searchQuery, 
                VerseSearchDocument.class
        );
        
        // 결과 변환
        return convertToEnhancedHits(searchHits);
    }
    
    /**
     * Enhanced 구절 내용 검색 (페이징 + 하이라이팅 + 스코어)
     */
    @Override
    public Page<EnhancedSearchHit> findByContentWithHighlight(String keyword, Pageable pageable) {
        log.debug("Enhanced 페이징 검색 시작: keyword={}, page={}, size={}", 
                keyword, pageable.getPageNumber(), pageable.getPageSize());
        
        // 검색 쿼리 생성
        Query matchQuery = Query.of(q -> q
                .match(m -> m
                        .field("content")
                        .query(keyword)
                        .analyzer("nori")
                )
        );
        
        // 하이라이팅 설정
        Highlight highlight = createHighlight("content");
        
        // Native Query 생성 (페이징 포함)
        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(matchQuery)
                .withHighlightQuery(new HighlightQuery(highlight, VerseSearchDocument.class))
                .withPageable(pageable)
                .build();
        
        // 검색 실행
        SearchHits<VerseSearchDocument> searchHits = elasticsearchTemplate.search(
                searchQuery, 
                VerseSearchDocument.class
        );
        
        // 결과 변환
        List<EnhancedSearchHit> enhancedHits = convertToEnhancedHits(searchHits);
        
        return new PageImpl<>(enhancedHits, pageable, searchHits.getTotalHits());
    }
    
    /**
     * Enhanced 책 이름 검색 (하이라이팅 + 스코어 포함)
     */
    @Override
    public List<EnhancedSearchHit> findByBookNameWithHighlight(String bookName) {
        log.debug("Enhanced 책 이름 검색 시작: bookName={}", bookName);
        
        // 책 이름은 keyword 필드로 정확히 매칭
        Query matchQuery = Query.of(q -> q
                .match(m -> m
                        .field("bookName.keyword")
                        .query(bookName)
                )
        );
        
        // 하이라이팅 설정 (bookName 필드)
        Highlight highlight = createHighlight("bookName");
        
        // Native Query 생성
        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(matchQuery)
                .withHighlightQuery(new HighlightQuery(highlight, VerseSearchDocument.class))
                .build();
        
        // 검색 실행
        SearchHits<VerseSearchDocument> searchHits = elasticsearchTemplate.search(
                searchQuery, 
                VerseSearchDocument.class
        );
        
        // 결과 변환
        return convertToEnhancedHits(searchHits);
    }
    
    /**
     * Enhanced 복합 조건 검색 (내용 + 책 + 장)
     */
    @Override
    public Page<EnhancedSearchHit> searchByMultipleConditionsWithHighlight(
            String keyword, String bookName, Integer chapter, Pageable pageable) {
        
        log.debug("Enhanced 복합 검색 시작: keyword={}, bookName={}, chapter={}", 
                keyword, bookName, chapter);
        
        // Bool Query 빌더
        BoolQuery.Builder boolBuilder = new BoolQuery.Builder();
        List<String> highlightFields = new ArrayList<>();
        
        // 키워드 조건
        if (keyword != null && !keyword.trim().isEmpty()) {
            boolBuilder.must(Query.of(q -> q
                    .match(m -> m
                            .field("content")
                            .query(keyword)
                            .analyzer("nori")
                    )
            ));
            highlightFields.add("content");
        }
        
        // 책 이름 조건
        if (bookName != null && !bookName.trim().isEmpty()) {
            boolBuilder.filter(Query.of(q -> q
                    .term(t -> t
                            .field("bookName.keyword")
                            .value(bookName)
                    )
            ));
            highlightFields.add("bookName");
        }
        
        // 장 번호 조건
        if (chapter != null && chapter > 0) {
            boolBuilder.filter(Query.of(q -> q
                    .term(t -> t
                            .field("chapter")
                            .value(chapter)
                    )
            ));
        }
        
        // 최종 쿼리
        Query finalQuery = Query.of(q -> q.bool(boolBuilder.build()));
        
        // 하이라이팅 설정 (여러 필드)
        Highlight highlight = createMultiFieldHighlight(highlightFields);
        
        // Native Query 생성
        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(finalQuery)
                .withHighlightQuery(new HighlightQuery(highlight, VerseSearchDocument.class))
                .withPageable(pageable)
                .build();
        
        // 검색 실행
        SearchHits<VerseSearchDocument> searchHits = elasticsearchTemplate.search(
                searchQuery, 
                VerseSearchDocument.class
        );
        
        // 결과 변환
        List<EnhancedSearchHit> enhancedHits = convertToEnhancedHits(searchHits);
        
        return new PageImpl<>(enhancedHits, pageable, searchHits.getTotalHits());
    }
    
    /**
     * 동의어 기반 Enhanced 검색
     */
    @Override
    public Page<EnhancedSearchHit> searchWithSynonyms(String keyword, boolean useSynonyms, Pageable pageable) {
        log.debug("동의어 검색 시작: keyword={}, useSynonyms={}", keyword, useSynonyms);
        
        // 동의어 사용 시 다른 분석기 사용 (인덱스 설정에 따라)
        String analyzer = useSynonyms ? "synonym_analyzer" : "nori";
        
        Query matchQuery = Query.of(q -> q
                .match(m -> m
                        .field("content")
                        .query(keyword)
                        .analyzer(analyzer)
                )
        );
        
        // 하이라이팅 설정
        Highlight highlight = createHighlight("content");
        
        // Native Query 생성
        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(matchQuery)
                .withHighlightQuery(new HighlightQuery(highlight, VerseSearchDocument.class))
                .withPageable(pageable)
                .build();
        
        // 검색 실행
        SearchHits<VerseSearchDocument> searchHits = elasticsearchTemplate.search(
                searchQuery, 
                VerseSearchDocument.class
        );
        
        // 결과 변환
        List<EnhancedSearchHit> enhancedHits = convertToEnhancedHits(searchHits);
        
        return new PageImpl<>(enhancedHits, pageable, searchHits.getTotalHits());
    }
    
    // ========================================
    // Private 헬퍼 메서드들
    // ========================================
    
    /**
     * 단일 필드 하이라이팅 설정 생성
     */
    private Highlight createHighlight(String field) {
        return new Highlight(
                HighlightParameters.builder()
                        .withPreTags("<mark>")
                        .withPostTags("</mark>")
                        .withFragmentSize(150) // 하이라이트 조각 크기
                        .withNumberOfFragments(3) // 최대 조각 수
                        .build(),
                List.of(new HighlightField(field))
        );
    }
    
    /**
     * 다중 필드 하이라이팅 설정 생성
     */
    private Highlight createMultiFieldHighlight(List<String> fields) {
        List<HighlightField> highlightFields = fields.stream()
                .map(HighlightField::new)
                .collect(Collectors.toList());
        
        return new Highlight(
                HighlightParameters.builder()
                        .withPreTags("<mark>")
                        .withPostTags("</mark>")
                        .withFragmentSize(150)
                        .withNumberOfFragments(3)
                        .build(),
                highlightFields
        );
    }
    
    /**
     * SearchHits를 EnhancedSearchHit 리스트로 변환
     */
    private List<EnhancedSearchHit> convertToEnhancedHits(SearchHits<VerseSearchDocument> searchHits) {
        return searchHits.getSearchHits().stream()
                .map(this::convertToEnhancedHit)
                .collect(Collectors.toList());
    }
    
    /**
     * 단일 SearchHit을 EnhancedSearchHit으로 변환
     */
    private EnhancedSearchHit convertToEnhancedHit(SearchHit<VerseSearchDocument> searchHit) {
        VerseSearchDocument document = searchHit.getContent();
        float score = searchHit.getScore();
        
        // 하이라이트 필드 변환
        Map<String, String[]> highlightFields = new HashMap<>();
        searchHit.getHighlightFields().forEach((field, highlights) -> {
            highlightFields.put(field, highlights.toArray(new String[0]));
        });
        
        return EnhancedSearchHit.from(document, score, highlightFields);
    }
}