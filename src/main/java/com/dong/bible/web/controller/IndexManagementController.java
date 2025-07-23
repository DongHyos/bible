package com.dong.bible.web.controller;

import com.dong.bible.application.service.IndexManagementApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * ElasticSearch 인덱스 관리 컨트롤러
 * 
 * 인덱스 구조(스키마) 관리를 위한 관리자 API
 */
@RestController
@RequestMapping("/api/admin/elasticsearch")
@RequiredArgsConstructor
@Slf4j
public class IndexManagementController {

    private final IndexManagementApplicationService indexManagementApplicationService;

    /**
     * 동의어 설정이 포함된 인덱스 생성
     * 성경 전용 동의어가 설정된 새로운 인덱스를 생성합니다
     */
    @PostMapping("/index/{indexName}/create")
    public ResponseEntity<Map<String, Object>> createIndexWithSynonyms(
            @PathVariable String indexName,
            @RequestBody CreateIndexRequest request) {
        
        try {
            log.info("인덱스 생성 요청: {}, 동의어 개수: {}", indexName, 
                    request.getSynonyms() != null ? request.getSynonyms().size() : 0);
            
            indexManagementApplicationService.createIndexWithSynonyms(indexName, request.getSynonyms());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "인덱스가 성공적으로 생성되었습니다",
                "indexName", indexName,
                "synonymCount", request.getSynonyms() != null ? request.getSynonyms().size() : 0
            ));
            
        } catch (Exception e) {
            log.error("인덱스 생성 실패: {}", indexName, e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "인덱스 생성 실패: " + e.getMessage(),
                "indexName", indexName,
                "error", e.getClass().getSimpleName()
            ));
        }
    }

    /**
     * 인덱스 간 데이터 복사
     * 소스 인덱스의 모든 데이터를 타겟 인덱스로 복사합니다
     */
    @PostMapping("/reindex")
    public ResponseEntity<Map<String, Object>> reindexData(@RequestBody ReindexRequest request) {
        
        try {
            log.info("리인덱싱 요청: {} -> {}", request.getSourceIndex(), request.getTargetIndex());
            
            indexManagementApplicationService.reindexData(request.getSourceIndex(), request.getTargetIndex());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "리인덱싱이 성공적으로 완료되었습니다",
                "sourceIndex", request.getSourceIndex(),
                "targetIndex", request.getTargetIndex()
            ));
            
        } catch (Exception e) {
            log.error("리인덱싱 실패: {} -> {}", request.getSourceIndex(), request.getTargetIndex(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "리인덱싱 실패: " + e.getMessage(),
                "sourceIndex", request.getSourceIndex(),
                "targetIndex", request.getTargetIndex(),
                "error", e.getClass().getSimpleName()
            ));
        }
    }

    /**
     * 인덱스 상태 조회
     * 인덱스의 설정과 매핑 정보를 조회합니다
     */
    @GetMapping("/index/{indexName}/status")
    public ResponseEntity<Map<String, Object>> getIndexStatus(@PathVariable String indexName) {
        
        try {
            log.info("인덱스 상태 조회: {}", indexName);
            
            Map<String, Object> status = indexManagementApplicationService.getIndexStatus(indexName);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", status
            ));
            
        } catch (Exception e) {
            log.error("인덱스 상태 조회 실패: {}", indexName, e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "인덱스 상태 조회 실패: " + e.getMessage(),
                "indexName", indexName,
                "error", e.getClass().getSimpleName()
            ));
        }
    }

    /**
     * 인덱스 삭제
     * 지정된 인덱스를 삭제합니다
     */
    @DeleteMapping("/index/{indexName}")
    public ResponseEntity<Map<String, Object>> deleteIndex(@PathVariable String indexName) {
        
        try {
            log.info("인덱스 삭제 요청: {}", indexName);
            
            indexManagementApplicationService.deleteIndex(indexName);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "인덱스가 성공적으로 삭제되었습니다",
                "indexName", indexName
            ));
            
        } catch (Exception e) {
            log.error("인덱스 삭제 실패: {}", indexName, e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "인덱스 삭제 실패: " + e.getMessage(),
                "indexName", indexName,
                "error", e.getClass().getSimpleName()
            ));
        }
    }

    /**
     * 기본 성경 구절 인덱스 생성
     * 미리 정의된 동의어 설정으로 성경 구절 인덱스를 생성합니다
     */
    @PostMapping("/index/bible-verses/create-default")
    public ResponseEntity<Map<String, Object>> createDefaultBibleVersesIndex() {
        
        try {
            // 기본 성경 동의어 설정
            List<String> defaultSynonyms = List.of(
                "하나님,하느님,여호와,주님,주",
                "예수,그리스도,메시아,구주,구세주",
                "성령,성신,보혜사",
                "말씀,언약,율법",
                "천국,하늘나라",
                "믿음,신앙,신뢰",
                "구원,구속,해방",
                "사랑,애정,자비",
                "평안,평화,안식",
                "영생,영원"
            );
            
            String indexName = "bible_verses_v" + System.currentTimeMillis();
            
            log.info("기본 성경 구절 인덱스 생성: {}", indexName);
            
            indexManagementApplicationService.createIndexWithSynonyms(indexName, defaultSynonyms);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "기본 성경 구절 인덱스가 생성되었습니다",
                "indexName", indexName,
                "synonymGroups", defaultSynonyms.size(),
                "recommendation", "이제 VerseIndexing API로 데이터를 인덱싱하세요"
            ));
            
        } catch (Exception e) {
            log.error("기본 성경 구절 인덱스 생성 실패", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "기본 인덱스 생성 실패: " + e.getMessage(),
                "error", e.getClass().getSimpleName()
            ));
        }
    }

    // Request DTOs
    public static class CreateIndexRequest {
        private List<String> synonyms;
        
        public List<String> getSynonyms() {
            return synonyms;
        }
        
        public void setSynonyms(List<String> synonyms) {
            this.synonyms = synonyms;
        }
    }

    public static class ReindexRequest {
        private String sourceIndex;
        private String targetIndex;
        
        public String getSourceIndex() {
            return sourceIndex;
        }
        
        public void setSourceIndex(String sourceIndex) {
            this.sourceIndex = sourceIndex;
        }
        
        public String getTargetIndex() {
            return targetIndex;
        }
        
        public void setTargetIndex(String targetIndex) {
            this.targetIndex = targetIndex;
        }
    }
}