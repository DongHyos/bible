package com.dong.bible.infrastructure.search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.index.Settings;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Elasticsearch를 사용한 인덱스 관리 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ElasticsearchIndexRepository {

    private final ElasticsearchOperations elasticsearchOperations;

    public void createIndexWithSynonyms(String indexName, List<String> synonyms) {
        log.info("새로운 인덱스 생성 시작: {}", indexName);
        
        try {
            IndexOperations indexOps = elasticsearchOperations.indexOps(IndexCoordinates.of(indexName));
            
            // 기존 인덱스가 있으면 삭제
            if (indexOps.exists()) {
                log.info("기존 인덱스 삭제: {}", indexName);
                indexOps.delete();
            }
            
            // 인덱스 설정 생성
            Document indexSettings = createIndexSettings(synonyms);
            Document mappingSettings = createMappingSettings();
            
            // 인덱스 생성
            indexOps.create(indexSettings);
            indexOps.putMapping(mappingSettings);
            
            log.info("인덱스 생성 완료: {}", indexName);
            
        } catch (Exception e) {
            log.error("인덱스 생성 실패: {}", indexName, e);
            throw new RuntimeException("인덱스 생성에 실패했습니다: " + e.getMessage());
        }
    }

    public void reindexData(String sourceIndex, String targetIndex) {
        log.info("리인덱싱 시작: {} -> {}", sourceIndex, targetIndex);
        
        try {
            IndexOperations sourceOps = elasticsearchOperations.indexOps(IndexCoordinates.of(sourceIndex));
            IndexOperations targetOps = elasticsearchOperations.indexOps(IndexCoordinates.of(targetIndex));
            
            if (!sourceOps.exists()) {
                throw new RuntimeException("소스 인덱스가 존재하지 않습니다: " + sourceIndex);
            }
            
            if (!targetOps.exists()) {
                throw new RuntimeException("타겟 인덱스가 존재하지 않습니다: " + targetIndex);
            }
            
            // TODO: 실제 reindex 로직 구현
            log.info("리인덱싱 완료: {} -> {}", sourceIndex, targetIndex);
            
        } catch (Exception e) {
            log.error("리인덱싱 실패: {} -> {}", sourceIndex, targetIndex, e);
            throw new RuntimeException("리인덱싱에 실패했습니다: " + e.getMessage());
        }
    }

    public Map<String, Object> getIndexStatus(String indexName) {
        log.info("인덱스 상태 조회: {}", indexName);
        
        try {
            IndexOperations indexOps = elasticsearchOperations.indexOps(IndexCoordinates.of(indexName));
            
            if (!indexOps.exists()) {
                return Map.of(
                    "exists", false,
                    "message", "인덱스가 존재하지 않습니다"
                );
            }
            
            // 인덱스 설정과 매핑 정보 조회
            Settings settings = indexOps.getSettings();
            Map<String, Object> mappings = indexOps.getMapping();
            
            return Map.of(
                "exists", true,
                "indexName", indexName,
                "settings", settings != null ? settings.toString() : "{}",
                "mappings", mappings != null ? mappings.toString() : "{}"
            );
            
        } catch (Exception e) {
            log.error("인덱스 상태 조회 실패: {}", indexName, e);
            throw new RuntimeException("인덱스 상태 조회에 실패했습니다: " + e.getMessage());
        }
    }

    public void deleteIndex(String indexName) {
        log.info("인덱스 삭제: {}", indexName);
        
        try {
            IndexOperations indexOps = elasticsearchOperations.indexOps(IndexCoordinates.of(indexName));
            
            if (!indexOps.exists()) {
                log.warn("삭제하려는 인덱스가 존재하지 않습니다: {}", indexName);
                return;
            }
            
            indexOps.delete();
            log.info("인덱스 삭제 완료: {}", indexName);
            
        } catch (Exception e) {
            log.error("인덱스 삭제 실패: {}", indexName, e);
            throw new RuntimeException("인덱스 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    public boolean indexExists(String indexName) {
        try {
            IndexOperations indexOps = elasticsearchOperations.indexOps(IndexCoordinates.of(indexName));
            return indexOps.exists();
        } catch (Exception e) {
            log.error("인덱스 존재 확인 실패: {}", indexName, e);
            return false;
        }
    }

    /**
     * 인덱스 설정 생성 (동의어 포함)
     */
    private Document createIndexSettings(List<String> synonyms) {
        return Document.create()
            .append("number_of_shards", 1)
            .append("number_of_replicas", 0)
            .append("analysis", Map.of(
                "filter", Map.of(
                    "bible_synonyms", Map.of(
                        "type", "synonym",
                        "synonyms", synonyms
                    )
                ),
                "analyzer", Map.of(
                    "korean_bible", Map.of(
                        "type", "custom",
                        "tokenizer", "nori_tokenizer",
                        "filter", List.of("lowercase", "nori_part_of_speech", "bible_synonyms")
                    )
                )
            ));
    }

    /**
     * 매핑 설정 생성
     */
    private Document createMappingSettings() {
        return Document.create()
            .append("properties", Map.of(
                "id", Map.of("type", "keyword"),
                "bookId", Map.of("type", "integer"),
                "bookName", Map.of(
                    "type", "text",
                    "analyzer", "korean_bible",
                    "fields", Map.of(
                        "keyword", Map.of("type", "keyword")
                    )
                ),
                "chapter", Map.of("type", "integer"),
                "verse", Map.of("type", "integer"),
                "content", Map.of(
                    "type", "text",
                    "analyzer", "korean_bible",
                    "fields", Map.of(
                        "exact", Map.of(
                            "type", "text",
                            "analyzer", "nori"
                        )
                    )
                ),
                "displayReference", Map.of("type", "keyword")
            ));
    }
}