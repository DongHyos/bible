package com.dong.bible.web.mapper;

import com.dong.bible.application.dto.query.BibleCategoryQuery;
import com.dong.bible.application.dto.query.BookQuery;
import com.dong.bible.infrastructure.persistence.entity.BibleCategoryEntity;
import com.dong.bible.web.dto.response.BibleBookResponse;
import com.dong.bible.web.dto.response.BibleCategoryResponse;
import com.dong.bible.web.mapper.BookResponseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * BibleCategory Entity → Response DTO 매핑
 * Web Layer 전용 매핑 클래스
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class BibleCategoryResponseMapper {
    
    private final BookResponseMapper bookResponseMapper;
    
    /**
     * BibleCategory → BibleCategoryResponse 변환
     */
    public BibleCategoryResponse toDto(BibleCategoryEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return BibleCategoryResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .nameEn(entity.getNameEn())
                .testament(entity.getTestament() != null ? entity.getTestament().name() : null)
                .categoryOrder(entity.getCategoryOrder())
                .description(entity.getDescription())
                // books와 bookCount는 Service에서 별도 설정
                .build();
    }
    
    /**
     * List<BibleCategory> → List<BibleCategoryResponse> 변환
     */
    public List<BibleCategoryResponse> toDtoList(List<BibleCategoryEntity> entities) {
        if (entities == null) {
            return List.of();
        }
        
        return entities.stream()
                .map(this::toDto)
                .filter(dto -> dto != null)
                .toList();
    }
    
    /**
     * Application DTO → Web DTO 변환
     */
    public BibleCategoryResponse fromApplicationDto(BibleCategoryQuery appDto) {
        if (appDto == null) {
            return null;
        }
        
        List<BibleBookResponse> webBooks = bookResponseMapper.fromBookQueryList(appDto.getBooks());
        
        return BibleCategoryResponse.builder()
                .id(appDto.getId())
                .name(appDto.getName())
                .nameEn(appDto.getNameEn())
                .testament(appDto.getTestament())
                .categoryOrder(appDto.getCategoryOrder())
                .description(appDto.getDescription())
                .books(webBooks)
                .bookCount(appDto.getBookCount())
                .build();
    }
    
    /**
     * List<Application DTO> → List<Web DTO> 변환
     */
    public List<BibleCategoryResponse> fromApplicationDtoList(List<BibleCategoryQuery> appDtos) {
        if (appDtos == null) {
            return List.of();
        }
        
        return appDtos.stream()
                .map(this::fromApplicationDto)
                .filter(dto -> dto != null)
                .toList();
    }
    
    /**
     * Map<String, List<Application DTO>> → Map<String, List<Web DTO>> 변환
     */
    public Map<String, List<BibleCategoryResponse>> fromApplicationDtoMap(Map<String, List<BibleCategoryQuery>> appDtoMap) {
        if (appDtoMap == null) {
            return Map.of();
        }
        
        return appDtoMap.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> fromApplicationDtoList(entry.getValue())
                ));
    }
}