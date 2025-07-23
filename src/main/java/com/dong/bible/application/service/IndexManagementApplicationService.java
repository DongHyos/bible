package com.dong.bible.application.service;

import com.dong.bible.infrastructure.search.ElasticsearchIndexRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 검색 인덱스 관리 Application Service
 * 
 * 주요 기능:
 * - 인덱스 생성/삭제
 * - 동의어 설정 관리
 * - 리인덱싱 실행
 * - 인덱스 상태 조회
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IndexManagementApplicationService {

    private final ElasticsearchIndexRepository elasticsearchIndexRepository;

    /**
     * 동의어 설정이 포함된 새로운 인덱스 생성
     */
    public void createIndexWithSynonyms(String indexName, List<String> synonyms) {
        log.info("새로운 인덱스 생성 요청: {}", indexName);
        elasticsearchIndexRepository.createIndexWithSynonyms(indexName, synonyms);
        log.info("인덱스 생성 완료: {}", indexName);
    }

    /**
     * 기존 인덱스에서 새 인덱스로 데이터 복사 (리인덱싱)
     */
    public void reindexData(String sourceIndex, String targetIndex) {
        log.info("리인덱싱 요청: {} -> {}", sourceIndex, targetIndex);
        elasticsearchIndexRepository.reindexData(sourceIndex, targetIndex);
        log.info("리인덱싱 완료: {} -> {}", sourceIndex, targetIndex);
    }

    /**
     * 인덱스 상태 조회
     */
    public Map<String, Object> getIndexStatus(String indexName) {
        log.info("인덱스 상태 조회 요청: {}", indexName);
        return elasticsearchIndexRepository.getIndexStatus(indexName);
    }

    /**
     * 인덱스 삭제
     */
    public void deleteIndex(String indexName) {
        log.info("인덱스 삭제 요청: {}", indexName);
        elasticsearchIndexRepository.deleteIndex(indexName);
        log.info("인덱스 삭제 완료: {}", indexName);
    }
}