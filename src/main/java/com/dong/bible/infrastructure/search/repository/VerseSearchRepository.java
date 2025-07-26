package com.dong.bible.infrastructure.search.repository;

import com.dong.bible.infrastructure.search.document.VerseSearchDocument;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ElasticSearch용 성경 구절 검색 Repository
 * Infrastructure Layer - 검색 데이터 접근을 담당
 */
@Repository
public interface VerseSearchRepository extends ElasticsearchRepository<VerseSearchDocument, String> {
    
    /**
     * 구절 내용으로 검색 (match 쿼리 사용)
     */
    @Query("{\"match\": {\"content\": \"?0\"}}")
    List<VerseSearchDocument> findByContentContaining(String content);
    
    /**
     * 책 이름으로 검색 (keyword 필드 사용)
     */
    @Query("{\"match\": {\"bookName.keyword\": \"?0\"}}")
    List<VerseSearchDocument> findByBookName(String bookName);
    
    /**
     * 특정 책의 특정 장 검색
     */
    @Query("{\"bool\": {\"must\": [{\"term\": {\"bookId\": ?0}}, {\"term\": {\"chapter\": ?1}}]}}")
    List<VerseSearchDocument> findByBookIdAndChapter(Integer bookId, Integer chapter);
}