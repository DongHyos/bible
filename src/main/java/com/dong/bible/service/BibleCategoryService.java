package com.dong.bible.service;

import com.dong.bible.web.dto.response.BibleBookDto;
import com.dong.bible.web.dto.response.BibleCategoryDto;

import java.util.List;
import java.util.Map;

public interface BibleCategoryService {
    List<BibleCategoryDto> getCategoriesByTestament(String testamentStr);

    Map<String, List<BibleCategoryDto>> getBibleStructure();

    List<BibleBookDto> getBooksByCategory(Integer categoryId);
}
