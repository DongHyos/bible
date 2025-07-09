package com.dong.bible.web.controller;

import com.dong.bible.common.response.AppResponse;
import com.dong.bible.common.response.ResponseCode;
import com.dong.bible.web.dto.request.SearchRequestDto;
import com.dong.bible.web.dto.response.ChapterDto;
import com.dong.bible.web.dto.response.VerseDto;
import com.dong.bible.web.dto.response.VerseSearchResultDto;
import com.dong.bible.service.KrvVerseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/bible")
@Slf4j
@RequiredArgsConstructor
public class KrvVerseController {

    private final KrvVerseService verseService;

    // 특정 장의 모든 구절 조회 (GET으로 변경)
    @GetMapping("/books/{bookId}/chapters/{chapter}")
    public ResponseEntity<AppResponse<ChapterDto>> getChapter(
            @PathVariable Integer bookId,
            @PathVariable Integer chapter) {
        return ResponseEntity.ok(AppResponse.of(
                verseService.getChapter(bookId, chapter)
        ));
    }

    // 특정 구절 조회 (GET으로 변경)
    @GetMapping("/books/{bookId}/chapters/{chapter}/verses/{verse}")
    public ResponseEntity<AppResponse<VerseDto>> getVerse(
            @PathVariable Integer bookId,
            @PathVariable Integer chapter,
            @PathVariable Integer verse) {
        return ResponseEntity.ok(AppResponse.of(
                verseService.getVerse(bookId, chapter, verse)
        ));
    }

    // 구절 범위 조회 (GET으로 변경)
    @GetMapping("/verses")
    public ResponseEntity<AppResponse<List<VerseDto>>> getVerseRange(
            @RequestParam Integer bookId,
            @RequestParam Integer chapter,
            @RequestParam Integer fromVerse,
            @RequestParam Integer toVerse) {

        // 범위 유효성 검증
        if (fromVerse > toVerse) {
            return ResponseEntity.badRequest()
                    .body(AppResponse.error(null,
                            ResponseCode.REQ_BAD_REQUEST,
                            "시작 절이 끝 절보다 클 수 없습니다"));
        }

        return ResponseEntity.ok(AppResponse.of(
                verseService.getVerseRange(bookId, chapter, fromVerse, toVerse)
        ));
    }

    // 구절 검색 (POST 유지 - 복잡한 검색 조건)
    @PostMapping("/search")
    public ResponseEntity<AppResponse<List<VerseSearchResultDto>>> searchVerses(@Valid @RequestBody SearchRequestDto request) {
        return ResponseEntity.ok(AppResponse.of(verseService.searchVerses(request.getKeyword())));
    }
}