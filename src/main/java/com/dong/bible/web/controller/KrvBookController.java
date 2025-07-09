package com.dong.bible.web.controller;

import com.dong.bible.common.response.AppResponse;
import com.dong.bible.web.dto.response.BibleBookDto;
import com.dong.bible.service.KrvBookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/bible")
@Slf4j
@RequiredArgsConstructor
public class KrvBookController {
    private final KrvBookService krvBookService;

    // 전체 성경 목록
    @GetMapping("/books")
    public ResponseEntity<AppResponse<List<BibleBookDto>>> getAllBooks() {
        return ResponseEntity.ok(AppResponse.of(krvBookService.getAllBooks()));
    }

    // 구약/신약별 조회
    @GetMapping("/books/{testament}")
    public ResponseEntity<AppResponse<List<BibleBookDto>>> getBooksByTestament(
            @PathVariable String testament) {
        return ResponseEntity.ok(AppResponse.of(krvBookService.getBooksByTestament(testament)));
    }

    // 그룹핑된 데이터 (프론트에서 탭 구성용)
    @GetMapping("/books/grouped")
    public ResponseEntity<AppResponse<Map<String, List<BibleBookDto>>>> getBooksByTestamentGrouped() {
        return ResponseEntity.ok(AppResponse.of(krvBookService.getBooksByTestamentGrouped()));
    }

    // 성경 통계
    @GetMapping("/books/statistics")
    public ResponseEntity<AppResponse<Map<String, Integer>>> getBibleStatistics() {
        return ResponseEntity.ok(AppResponse.of(krvBookService.getBibleStatistics()));
    }
}
