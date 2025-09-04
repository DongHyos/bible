package com.dong.bible.web.controller;

import com.dong.bible.application.dto.query.BibleCategoryQuery;
import com.dong.bible.application.dto.query.BookQuery;
import com.dong.bible.application.service.BibleCategoryApplicationService;
import com.dong.bible.common.response.AppResponse;
import com.dong.bible.web.dto.response.BibleBookResponse;
import com.dong.bible.web.dto.response.BibleCategoryResponse;
import com.dong.bible.web.mapper.BibleCategoryResponseMapper;
import com.dong.bible.web.mapper.BookResponseMapper;
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
public class BibleCategoryController {
    
    private final BibleCategoryApplicationService bibleCategoryApplicationService;
    private final BibleCategoryResponseMapper categoryResponseMapper;
    private final BookResponseMapper bookResponseMapper;
    
    // 구약/신약별 분류 조회
    @GetMapping("/categories/{testament}")
    public ResponseEntity<AppResponse<List<BibleCategoryResponse>>> getCategoriesByTestament(@PathVariable String testament) {
        List<BibleCategoryQuery> categories = bibleCategoryApplicationService.getCategoriesByTestament(testament);
        List<BibleCategoryResponse> responses = categoryResponseMapper.fromApplicationDtoList(categories);
        return ResponseEntity.ok(AppResponse.of(responses));
    }
    
    // 전체 성경 구조 (분류별) - 목차용 최고 추천!
    @GetMapping("/structure")
    public ResponseEntity<AppResponse<Map<String, List<BibleCategoryResponse>>>> getBibleStructure() {
        Map<String, List<BibleCategoryQuery>> structure = bibleCategoryApplicationService.getBibleStructure();
        Map<String, List<BibleCategoryResponse>> responses = categoryResponseMapper.fromApplicationDtoMap(structure);
        return ResponseEntity.ok(AppResponse.of(responses));
    }
    
    // 특정 분류의 책들
    @GetMapping("/categories/{categoryId}/books")
    public ResponseEntity<AppResponse<List<BibleBookResponse>>> getBooksByCategory(@PathVariable Integer categoryId) {
        List<BookQuery> books = bibleCategoryApplicationService.getBooksByCategory(categoryId);
        List<BibleBookResponse> responses = bookResponseMapper.fromBookQueryList(books);
        return ResponseEntity.ok(AppResponse.of(responses));
    }
}
