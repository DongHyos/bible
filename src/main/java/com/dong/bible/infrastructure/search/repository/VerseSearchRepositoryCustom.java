package com.dong.bible.infrastructure.search.repository;

import com.dong.bible.infrastructure.search.document.VerseSearchDocument;
import com.dong.bible.infrastructure.search.dto.EnhancedSearchHit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * ElasticSearch 고급 검색 기능을 위한 Custom Repository
 * 하이라이팅, 스코어링 등 Spring Data가 직접 지원하지 않는 기능 구현
 */
public interface VerseSearchRepositoryCustom {
    
    /**
     * Enhanced 구절 내용 검색 (하이라이팅 + 스코어 포함)
     * 
     * @param keyword 검색 키워드
     * @return 하이라이팅과 스코어가 포함된 검색 결과
     */
    List<EnhancedSearchHit> findByContentWithHighlight(String keyword);
    
    /**
     * Enhanced 구절 내용 검색 (페이징 + 하이라이팅 + 스코어)
     * 
     * @param keyword 검색 키워드
     * @param pageable 페이징 정보
     * @return 페이징된 Enhanced 검색 결과
     */
    Page<EnhancedSearchHit> findByContentWithHighlight(String keyword, Pageable pageable);
    
    /**
     * Enhanced 책 이름 검색 (하이라이팅 + 스코어 포함)
     * 
     * @param bookName 책 이름
     * @return 하이라이팅과 스코어가 포함된 검색 결과
     */
    List<EnhancedSearchHit> findByBookNameWithHighlight(String bookName);
    
    /**
     * Enhanced 복합 조건 검색 (내용 + 책 + 장)
     * 
     * @param keyword 검색 키워드 (선택)
     * @param bookName 책 이름 (선택)
     * @param chapter 장 번호 (선택)
     * @param pageable 페이징 정보
     * @return 복합 조건 Enhanced 검색 결과
     */
    Page<EnhancedSearchHit> searchByMultipleConditionsWithHighlight(
            String keyword, String bookName, Integer chapter, Pageable pageable);
    
    /**
     * 동의어 기반 Enhanced 검색
     * 설정된 동의어를 활용한 확장 검색
     * 
     * @param keyword 검색 키워드
     * @param useSynonyms 동의어 사용 여부
     * @param pageable 페이징 정보
     * @return 동의어 확장 검색 결과
     */
    Page<EnhancedSearchHit> searchWithSynonyms(String keyword, boolean useSynonyms, Pageable pageable);
}