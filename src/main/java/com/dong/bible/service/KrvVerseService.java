package com.dong.bible.service;

import com.dong.bible.web.dto.response.ChapterDto;
import com.dong.bible.web.dto.response.VerseDto;
import com.dong.bible.web.dto.response.VerseSearchResultDto;

import java.util.List;

public interface KrvVerseService {
    
    // 특정 장의 모든 구절 조회
    ChapterDto getChapter(Integer bookId, Integer chapter);
    
    // 특정 구절 조회
    VerseDto getVerse(Integer bookId, Integer chapter, Integer verse);
    
    // 구절 범위 조회
    List<VerseDto> getVerseRange(Integer bookId, Integer chapter, Integer startVerse, Integer endVerse);
    
    // 텍스트 검색
    List<VerseSearchResultDto> searchVerses(String keyword);
}
