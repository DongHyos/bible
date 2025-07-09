package com.dong.bible.service;

import com.dong.bible.web.dto.response.BibleBookDto;

import java.util.List;
import java.util.Map;

public interface KrvBookService {
    // 전체 성경 목록
    List<BibleBookDto> getAllBooks();

    // 구약/신약별 조회
    List<BibleBookDto> getBooksByTestament(String testament);

    // 구약/신약으로 그룹핑 (프론트 탭용)
    Map<String, List<BibleBookDto>> getBooksByTestamentGrouped();

    // 성경 통계
    Map<String, Integer> getBibleStatistics();
}
