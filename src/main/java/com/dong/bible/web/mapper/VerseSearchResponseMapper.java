package com.dong.bible.web.mapper;

import com.dong.bible.application.dto.VerseSearchResultDto;
import com.dong.bible.web.dto.response.VerseSearchResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 성경 구절 검색 결과 Response Mapper
 * Application DTO → Web Response DTO 변환
 */
@Component
public class VerseSearchResponseMapper {
    
    /**
     * Application DTO → Web Response DTO 변환
     */
    public VerseSearchResponse toResponse(VerseSearchResultDto dto) {
        return VerseSearchResponse.builder()
                .id(dto.getId())
                .bookId(dto.getBookId())
                .bookName(dto.getBookName())
                .chapter(dto.getChapter())
                .verse(dto.getVerse())
                .content(dto.getContent())
                .displayReference(dto.getDisplayReference())
                .build();
    }
    
    /**
     * Application DTO 리스트 → Web Response DTO 리스트 변환
     */
    public List<VerseSearchResponse> toResponseList(List<VerseSearchResultDto> dtoList) {
        return dtoList.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}