package com.dong.bible.web.mapper;

import com.dong.bible.application.dto.query.EnhancedVerseSearchResultQuery;
import com.dong.bible.web.dto.response.EnhancedVerseSearchResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Enhanced 성경 구절 검색 결과 Response Mapper
 * Enhanced Application DTO → Enhanced Web Response DTO 변환
 */
@Component
public class EnhancedVerseSearchResponseMapper {
    
    /**
     * Enhanced Application DTO → Enhanced Web Response DTO 변환
     */
    public EnhancedVerseSearchResponse toResponse(EnhancedVerseSearchResultQuery dto) {
        return EnhancedVerseSearchResponse.builder()
                // 기본 구절 정보
                .id(dto.getId())
                .bookId(dto.getBookId())
                .bookName(dto.getBookName())
                .chapter(dto.getChapter())
                .verse(dto.getVerse())
                .content(dto.getContent())
                .displayReference(dto.getDisplayReference())
                
                // Enhanced 검색 정보
                .highlightedContent(dto.getHighlightedContent())
                .score(dto.getScore())
                .relevanceLevel(dto.getRelevanceLevel())
                .highlightCount(dto.getHighlightCount())
                .hasHighlight(dto.getHasHighlight())
                .searchKeyword(dto.getSearchKeyword())
                
                // 검색 품질 지표
                .isHighQuality(dto.getIsHighQuality())
                .isPerfectMatch(dto.getIsPerfectMatch())
                .isPartialMatch(dto.getIsPartialMatch())
                .highlightQuality(dto.getHighlightQuality())
                
                // 응답 메타데이터 (기본값)
                .processingTimeMs(null) // Controller에서 설정
                .searchType(null) // Controller에서 설정
                .searchOptions(null) // Controller에서 설정
                .build();
    }
    
    /**
     * Enhanced Application DTO 리스트 → Enhanced Web Response DTO 리스트 변환
     */
    public List<EnhancedVerseSearchResponse> toResponseList(List<EnhancedVerseSearchResultQuery> dtoList) {
        return dtoList.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 검색 타입과 처리 시간을 포함한 Response 생성
     */
    public EnhancedVerseSearchResponse toResponse(EnhancedVerseSearchResultQuery dto, 
                                                 String searchType, 
                                                 Long processingTimeMs) {
        EnhancedVerseSearchResponse response = toResponse(dto);
        
        return response.toBuilder()
                .searchType(searchType)
                .processingTimeMs(processingTimeMs)
                .build();
    }
    
    /**
     * 검색 옵션을 포함한 Response 생성 
     */
    public EnhancedVerseSearchResponse toResponse(EnhancedVerseSearchResultQuery dto,
                                                 String searchType,
                                                 Long processingTimeMs,
                                                 EnhancedVerseSearchResponse.SearchOptions searchOptions) {
        EnhancedVerseSearchResponse response = toResponse(dto);
        
        return response.toBuilder()
                .searchType(searchType)
                .processingTimeMs(processingTimeMs)
                .searchOptions(searchOptions)
                .build();
    }
    
    /**
     * 검색 옵션을 포함한 Response 리스트 생성
     */
    public List<EnhancedVerseSearchResponse> toResponseList(List<EnhancedVerseSearchResultQuery> dtoList,
                                                           String searchType,
                                                           Long processingTimeMs,
                                                           EnhancedVerseSearchResponse.SearchOptions searchOptions) {
        return dtoList.stream()
                .map(dto -> toResponse(dto, searchType, processingTimeMs, searchOptions))
                .collect(Collectors.toList());
    }
    
    /**
     * 기본 검색 옵션 생성
     */
    public EnhancedVerseSearchResponse.SearchOptions createDefaultSearchOptions() {
        return EnhancedVerseSearchResponse.SearchOptions.builder()
                .useSynonyms(false)
                .sortBy("score")
                .includeScore(true)
                .fragmentSize(150)
                .maxFragments(3)
                .build();
    }
    
    /**
     * 커스텀 검색 옵션 생성
     */
    public EnhancedVerseSearchResponse.SearchOptions createSearchOptions(Boolean useSynonyms,
                                                                        String sortBy,
                                                                        Boolean includeScore,
                                                                        Integer fragmentSize,
                                                                        Integer maxFragments) {
        return EnhancedVerseSearchResponse.SearchOptions.builder()
                .useSynonyms(useSynonyms != null ? useSynonyms : false)
                .sortBy(sortBy != null ? sortBy : "score")
                .includeScore(includeScore != null ? includeScore : true)
                .fragmentSize(fragmentSize != null ? fragmentSize : 150)
                .maxFragments(maxFragments != null ? maxFragments : 3)
                .build();
    }
}