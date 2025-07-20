package com.dong.bible.web.controller;

import com.dong.bible.application.service.VerseIndexingApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 성경 구절 인덱싱 관리 API Controller
 * ElasticSearch 인덱싱 작업을 관리하는 관리자용 API
 */
@RestController
@RequestMapping("/api/admin/index/verses")
@RequiredArgsConstructor
@Slf4j
public class VerseIndexingController {
    
    private final VerseIndexingApplicationService verseIndexingApplicationService;
    
    /**
     * 전체 성경 구절 인덱싱
     * POST /api/admin/index/verses/full
     */
    @PostMapping("/full")
    public ResponseEntity<Map<String, Object>> indexAllVerses() {
        log.info("전체 인덱싱 API 호출");
        
        try {
            verseIndexingApplicationService.indexAllVerses();
            
            // 인덱싱 완료 후 상태 확인
            long indexedCount = verseIndexingApplicationService.getIndexedVerseCount();
            
            Map<String, Object> response = Map.of(
                "success", true,
                "message", "전체 성경 구절 인덱싱이 완료되었습니다",
                "indexedCount", indexedCount
            );
            
            log.info("전체 인덱싱 API 완료: {}개 구절", indexedCount);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("전체 인덱싱 API 오류", e);
            
            Map<String, Object> errorResponse = Map.of(
                "success", false,
                "message", "인덱싱 작업 실패: " + e.getMessage(),
                "error", e.getClass().getSimpleName()
            );
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * 특정 책의 구절 인덱싱
     * POST /api/admin/index/verses/book/{bookId}?bookName=창세기
     */
    @PostMapping("/book/{bookId}")
    public ResponseEntity<Map<String, Object>> indexVersesByBook(
            @PathVariable Integer bookId,
            @RequestParam String bookName) {
        
        log.info("책별 인덱싱 API 호출: bookId={}, bookName={}", bookId, bookName);
        
        try {
            int indexedCount = verseIndexingApplicationService.indexVersesByBook(bookId, bookName);
            
            Map<String, Object> response = Map.of(
                "success", true,
                "message", "책 '" + bookName + "' 인덱싱이 완료되었습니다",
                "bookId", bookId,
                "bookName", bookName,
                "indexedCount", indexedCount
            );
            
            log.info("책별 인덱싱 API 완료: {} - {}개 구절", bookName, indexedCount);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("책별 인덱싱 API 오류: bookId={}, bookName={}", bookId, bookName, e);
            
            Map<String, Object> errorResponse = Map.of(
                "success", false,
                "message", "책별 인덱싱 실패: " + e.getMessage(),
                "bookId", bookId,
                "bookName", bookName,
                "error", e.getClass().getSimpleName()
            );
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * 인덱스 전체 삭제
     * DELETE /api/admin/index/verses
     */
    @DeleteMapping
    public ResponseEntity<Map<String, Object>> deleteAllIndex() {
        log.info("인덱스 삭제 API 호출");
        
        try {
            verseIndexingApplicationService.deleteAllIndex();
            
            Map<String, Object> response = Map.of(
                "success", true,
                "message", "ElasticSearch 인덱스가 삭제되었습니다"
            );
            
            log.info("인덱스 삭제 API 완료");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("인덱스 삭제 API 오류", e);
            
            Map<String, Object> errorResponse = Map.of(
                "success", false,
                "message", "인덱스 삭제 실패: " + e.getMessage(),
                "error", e.getClass().getSimpleName()
            );
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * 인덱스 상태 조회
     * GET /api/admin/index/verses/status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getIndexStatus() {
        log.info("인덱스 상태 조회 API 호출");
        
        try {
            long indexedCount = verseIndexingApplicationService.getIndexedVerseCount();
            
            Map<String, Object> response = Map.of(
                "success", true,
                "indexedCount", indexedCount,
                "message", indexedCount > 0 ? 
                    "현재 " + indexedCount + "개의 구절이 인덱싱되어 있습니다" : 
                    "인덱싱된 데이터가 없습니다"
            );
            
            log.info("인덱스 상태 조회 API 완료: {}개 구절", indexedCount);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("인덱스 상태 조회 API 오류", e);
            
            Map<String, Object> errorResponse = Map.of(
                "success", false,
                "message", "상태 조회 실패: " + e.getMessage(),
                "error", e.getClass().getSimpleName()
            );
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}