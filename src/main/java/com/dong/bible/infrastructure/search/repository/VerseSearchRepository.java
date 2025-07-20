package com.dong.bible.infrastructure.search.repository;

import com.dong.bible.infrastructure.search.document.VerseSearchDocument;
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
     * 구절 내용으로 검색 (기본 검색)
     */
    List<VerseSearchDocument> findByContentContaining(String content);
    
    /**
     * 책 이름으로 검색
     */
    List<VerseSearchDocument> findByBookName(String bookName);
    
    /**
     * 특정 책의 특정 장 검색
     */
    List<VerseSearchDocument> findByBookIdAndChapter(Integer bookId, Integer chapter);
}