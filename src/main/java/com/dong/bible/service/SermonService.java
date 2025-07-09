package com.dong.bible.service;

import com.dong.bible.web.dto.response.SermonDto;
import com.dong.bible.web.dto.response.SermonSimpleDto;

import java.util.List;

public interface SermonService {
    
    // 특정 구절과 관련된 설교 조회
    List<SermonSimpleDto> getSermonsByVerse(Integer bookId, Short chapter, Short verse);
    
    // 설교 상세 조회
    SermonDto getSermonDetail(Long sermonId);
    
    // 설교자별 설교 조회
    List<SermonSimpleDto> getSermonsByPastor(String pastorName);
    
    // 교회별 설교 조회
    List<SermonSimpleDto> getSermonsByChurch(String churchName);
    
    // 제목으로 설교 검색
    List<SermonSimpleDto> searchSermonsByTitle(String title);
    
    // 인기 설교 조회
    List<SermonSimpleDto> getPopularSermons();
    
    // 최신 설교 조회
    List<SermonSimpleDto> getLatestSermons();
    
    // 설교 조회수 증가
    void incrementViewCount(Long sermonId);
}