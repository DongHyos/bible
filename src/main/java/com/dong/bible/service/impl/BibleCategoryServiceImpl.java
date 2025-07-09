package com.dong.bible.service.impl;

import com.dong.bible.ENUM.Testament;
import com.dong.bible.web.dto.response.BibleBookDto;
import com.dong.bible.web.dto.response.BibleCategoryDto;
import com.dong.bible.infrastructure.persistence.entity.BibleCategory;
import com.dong.bible.infrastructure.persistence.entity.KrvBook;
import com.dong.bible.mapstruct.BibleMapper;
import com.dong.bible.mapstruct.KrvBookMapper; // KrvBookMapper 추가
import com.dong.bible.infrastructure.persistence.jpa.BibleCategoryRepository;
import com.dong.bible.infrastructure.persistence.jpa.KrvBookRepository;
import com.dong.bible.service.BibleCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BibleCategoryServiceImpl implements BibleCategoryService {
    private final BibleCategoryRepository categoryRepository;
    private final KrvBookRepository bookRepository;
    private final BibleMapper bibleMapper; // Category 담당
    private final KrvBookMapper bookMapper; // Book 담당

    // 구약/신약별 분류 조회
    @Override
    public List<BibleCategoryDto> getCategoriesByTestament(String testamentStr) {
        Testament testament = Testament.fromString(testamentStr);
        List<BibleCategory> categories = categoryRepository.findByTestamentOrderByCategoryOrder(testament);

        return categories.stream()
                .map(category -> {
                    BibleCategoryDto dto = bibleMapper.toCategoryDto(category);
                    // KrvBookMapper 사용
                    dto.setBooks(bookMapper.toDtoList(category.getBooks()));
                    dto.setBookCount(category.getBooks().size());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 전체 성경 구조 (분류별로 그룹핑)
    @Override
    public Map<String, List<BibleCategoryDto>> getBibleStructure() {
        List<BibleCategory> allCategories = categoryRepository.findAllByOrderByCategoryOrder();

        return allCategories.stream()
                .map(category -> {
                    BibleCategoryDto dto = bibleMapper.toCategoryDto(category);
                    // KrvBookMapper 사용
                    dto.setBooks(bookMapper.toDtoList(category.getBooks()));
                    dto.setBookCount(category.getBooks().size());
                    return dto;
                })
                .collect(Collectors.groupingBy(dto -> dto.getTestament()));
    }

    // 특정 분류의 책들
    @Override
    public List<BibleBookDto> getBooksByCategory(Integer categoryId) {
        List<KrvBook> books = bookRepository.findByCategoryIdOrderByBookOrder(categoryId);
        // KrvBookMapper 사용
        return bookMapper.toDtoList(books);
    }
}
