package com.dong.bible.web.controller;

import com.dong.bible.application.dto.query.SermonDetailQuery;
import com.dong.bible.application.dto.query.SermonSummaryQuery;
import com.dong.bible.application.service.SermonApplicationService;
import com.dong.bible.common.response.AppResponse;
import com.dong.bible.web.dto.response.SermonResponse;
import com.dong.bible.web.dto.response.SermonSimpleResponse;
import com.dong.bible.web.mapper.SermonResponseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sermons")
@Slf4j
@RequiredArgsConstructor
public class SermonController {

    private final SermonApplicationService sermonApplicationService;
    private final SermonResponseMapper sermonResponseMapper;

    /**
     * 특정 구절과 관련된 설교 목록 조회
     * 프론트엔드에서 성경 구절 클릭 시 호출되는 API
     */
    @GetMapping("/verse/{bookId}/{chapter}/{verse}")
    public ResponseEntity<AppResponse<List<SermonSimpleResponse>>> getSermonsByVerse(
            @PathVariable Integer bookId,
            @PathVariable Short chapter,
            @PathVariable Short verse) {
        
        log.info("구절별 설교 검색 API 호출: 책ID={}, 장={}, 절={}", bookId, chapter, verse);
        
        List<SermonSummaryQuery> sermonSummaries = sermonApplicationService.getSermonsByVerse(bookId, chapter, verse);
        List<SermonSimpleResponse> response = sermonResponseMapper.fromSummaryDtoList(sermonSummaries);
        
        return ResponseEntity.ok(AppResponse.of(response));
    }

    /**
     * 설교 상세 정보 조회
     */
    @GetMapping("/{sermonId}")
    public ResponseEntity<AppResponse<SermonResponse>> getSermonDetail(@PathVariable Long sermonId) {
        log.info("설교 상세 조회 API 호출: ID={}", sermonId);
        
        // 조회수 증가
        sermonApplicationService.incrementViewCount(sermonId);
        
        // 설교 상세 조회
        SermonDetailQuery sermonDetail = sermonApplicationService.getSermonById(sermonId);
        
        SermonResponse response = sermonResponseMapper.fromDetailDto(sermonDetail);
        
        return ResponseEntity.ok(AppResponse.of(response));
    }

    /**
     * 설교자별 설교 목록 조회
     */
    @GetMapping("/pastor/{pastorName}")
    public ResponseEntity<AppResponse<List<SermonSimpleResponse>>> getSermonsByPastor(@PathVariable String pastorName) {
        log.info("설교자별 설교 조회 API 호출: {}", pastorName);
        
        List<SermonSummaryQuery> sermonSummaries = sermonApplicationService.getSermonsByPastor(pastorName);
        List<SermonSimpleResponse> response = sermonResponseMapper.fromSummaryDtoList(sermonSummaries);
        
        return ResponseEntity.ok(AppResponse.of(response));
    }

    /**
     * 교회별 설교 목록 조회
     */
    @GetMapping("/church/{churchName}")
    public ResponseEntity<AppResponse<List<SermonSimpleResponse>>> getSermonsByChurch(@PathVariable String churchName) {
        log.info("교회별 설교 조회 API 호출: {}", churchName);
        
        List<SermonSummaryQuery> sermonSummaries = sermonApplicationService.getSermonsByChurch(churchName);
        List<SermonSimpleResponse> response = sermonResponseMapper.fromSummaryDtoList(sermonSummaries);
        
        return ResponseEntity.ok(AppResponse.of(response));
    }

    /**
     * 설교 제목 검색
     */
    @GetMapping("/search")
    public ResponseEntity<AppResponse<List<SermonSimpleResponse>>> searchSermons(@RequestParam String title) {
        log.info("설교 제목 검색 API 호출: {}", title);
        
        List<SermonSummaryQuery> sermonSummaries = sermonApplicationService.searchSermonsByTitle(title);
        List<SermonSimpleResponse> response = sermonResponseMapper.fromSummaryDtoList(sermonSummaries);
        
        return ResponseEntity.ok(AppResponse.of(response));
    }

    /**
     * 인기 설교 목록 조회 (조회수 기준 TOP 10)
     */
    @GetMapping("/popular")
    public ResponseEntity<AppResponse<List<SermonSimpleResponse>>> getPopularSermons() {
        log.info("인기 설교 조회 API 호출");
        
        List<SermonSummaryQuery> sermonSummaries = sermonApplicationService.getPopularSermons();
        List<SermonSimpleResponse> response = sermonResponseMapper.fromSummaryDtoList(sermonSummaries);
        
        return ResponseEntity.ok(AppResponse.of(response));
    }

    /**
     * 최신 설교 목록 조회 (날짜 기준 TOP 10)
     */
    @GetMapping("/latest")
    public ResponseEntity<AppResponse<List<SermonSimpleResponse>>> getLatestSermons() {
        log.info("최신 설교 조회 API 호출");
        
        List<SermonSummaryQuery> sermonSummaries = sermonApplicationService.getLatestSermons();
        List<SermonSimpleResponse> response = sermonResponseMapper.fromSummaryDtoList(sermonSummaries);
        
        return ResponseEntity.ok(AppResponse.of(response));
    }
}
