package com.dong.bible.application.service;

import com.dong.bible.application.dto.BibleCategoryDto;
import com.dong.bible.application.dto.BookDto;
import com.dong.bible.ENUM.Testament;
import com.dong.bible.domain.category.BibleCategory;
import com.dong.bible.domain.category.BibleCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 성경 분류 관련 Application Service
 * DDD 원칙 준수:
 * - Domain Repository 사용 (새로 구현된 BibleCategoryRepositoryImpl 활용)
 * - Application DTO 반환
 * - 도메인 로직은 Domain 객체에 위임
 * - Optional 활용으로 안전성 강화
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BibleCategoryApplicationService {
    
    private final BibleCategoryRepository categoryRepository;
    private final BookQueryService bookQueryService;  // 기존 Service 재사용

    /**
     * 구약/신약별 분류 조회
     */
    public List<BibleCategoryDto> getCategoriesByTestament(String testamentStr) {
        log.debug("Getting categories by testament: {}", testamentStr);
        
        Testament testament = Testament.fromString(testamentStr);
        List<BibleCategory> categories = categoryRepository.findByTestamentOrderByCategoryOrder(testament);

        return categories.stream()
                .map(this::buildCategoryDtoWithBooks)
                .collect(Collectors.toList());
    }

    /**
     * 전체 성경 구조 (분류별로 그룹핑)
     */
    public Map<String, List<BibleCategoryDto>> getBibleStructure() {
        log.debug("Getting bible structure");
        
        List<BibleCategory> allCategories = categoryRepository.findAllByOrderByCategoryOrder();

        return allCategories.stream()
                .map(this::buildCategoryDtoWithBooks)
                .collect(Collectors.groupingBy(dto -> dto.getTestament()));
    }

    /**
     * 특정 분류의 책들 (ID로 조회)
     */
    public List<BookDto> getBooksByCategory(Integer categoryId) {
        log.debug("Getting books by category: {}", categoryId);
        
        // Repository의 Optional 반환을 안전하게 처리
        BibleCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + categoryId));
        
        // 카테고리의 책들을 BookQueryService를 통해 조회
        return category.getBookIds().stream()
                .map(bookQueryService::getBookById)
                .collect(Collectors.toList());
    }

    /**
     * 카테고리명으로 조회
     */
    public BibleCategoryDto getCategoryByName(String categoryName) {
        log.debug("Getting category by name: {}", categoryName);
        
        BibleCategory category = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with name: " + categoryName));
        
        return buildCategoryDtoWithBooks(category);
    }

    /**
     * 전체 카테고리 개수 조회
     */
    public long getTotalCategoryCount() {
        log.debug("Getting total category count");
        return categoryRepository.count();
    }

    /**
     * 구약/신약별 카테고리 개수 조회
     */
    public long getCategoryCountByTestament(String testamentStr) {
        log.debug("Getting category count by testament: {}", testamentStr);
        
        Testament testament = Testament.fromString(testamentStr);
        return categoryRepository.countByTestament(testament);
    }

    /**
     * 카테고리 존재 여부 확인
     */
    public boolean existsCategoryByName(String categoryName) {
        log.debug("Checking if category exists by name: {}", categoryName);
        return categoryRepository.existsByName(categoryName);
    }

    /**
     * 간단한 카테고리 목록 (책 정보 없이)
     */
    public List<BibleCategoryDto> getCategoriesSimple() {
        log.debug("Getting simple categories list");
        
        List<BibleCategory> categories = categoryRepository.findAllByOrderByCategoryOrder();
        
        return categories.stream()
                .map(category -> BibleCategoryDto.from(category, List.of())) // 책 목록 없이
                .collect(Collectors.toList());
    }

    /**
     * 특정 구약/신약의 간단한 카테고리 목록 (책 정보 없이)
     */
    public List<BibleCategoryDto> getCategoriesSimpleByTestament(String testamentStr) {
        log.debug("Getting simple categories by testament: {}", testamentStr);
        
        Testament testament = Testament.fromString(testamentStr);
        List<BibleCategory> categories = categoryRepository.findByTestamentOrderByCategoryOrder(testament);
        
        return categories.stream()
                .map(category -> BibleCategoryDto.from(category, List.of())) // 책 목록 없이
                .collect(Collectors.toList());
    }

    /**
     * Domain 객체를 Application DTO로 변환하는 헬퍼 메서드
     * 책 목록도 함께 조회하여 완전한 DTO 생성
     */
    private BibleCategoryDto buildCategoryDtoWithBooks(BibleCategory category) {
        // 카테고리 내 책들 조회
        List<BookDto> books = category.getBookIds().stream()
                .map(bookQueryService::getBookById)
                .collect(Collectors.toList());
        
        // DTO 정적 팩토리 메서드 사용 (변환은 DTO가 담당)
        return BibleCategoryDto.from(category, books);
    }
}