package com.dong.bible.web.controller;

import com.dong.bible.common.response.AppResponse;
import com.dong.bible.web.dto.response.BibleBookDto;
import com.dong.bible.web.dto.response.BibleCategoryDto;
import com.dong.bible.service.BibleCategoryService;
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
    
    private final BibleCategoryService bibleCategoryService;
    
    // 구약/신약별 분류 조회
    @GetMapping("/categories/{testament}")
    public ResponseEntity<AppResponse<List<BibleCategoryDto>>> getCategoriesByTestament(@PathVariable String testament) {
        return ResponseEntity.ok(AppResponse.of(bibleCategoryService.getCategoriesByTestament(testament)));
    }
    
    // 전체 성경 구조 (분류별) - 목차용 최고 추천!
    @GetMapping("/structure")
    public ResponseEntity<AppResponse<Map<String, List<BibleCategoryDto>>>> getBibleStructure() {
        return ResponseEntity.ok(AppResponse.of(bibleCategoryService.getBibleStructure()));
    }
    
    // 특정 분류의 책들
    @GetMapping("/categories/{categoryId}/books")
    public ResponseEntity<AppResponse<List<BibleBookDto>>> getBooksByCategory(@PathVariable Integer categoryId) {
        return ResponseEntity.ok(AppResponse.of(bibleCategoryService.getBooksByCategory(categoryId)));
    }
}
