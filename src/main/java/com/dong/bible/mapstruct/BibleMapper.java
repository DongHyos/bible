package com.dong.bible.mapstruct;

import com.dong.bible.ENUM.Testament;
import com.dong.bible.infrastructure.persistence.entity.BibleCategory;
import com.dong.bible.web.dto.response.BibleCategoryDto;
import com.dong.bible.web.dto.response.BibleCategorySimpleDto;
import org.mapstruct.*;

/**
 * 성경 분류 관련 Entity ↔ DTO 변환 매퍼
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface BibleMapper {
    
    // ==========================================
    // Testament 변환 메서드
    // ==========================================
    @Named("testamentToString")
    default String testamentToString(Testament testament) {
        return testament != null ? testament.getValue() : null;
    }

    @Named("stringToTestament")
    default Testament stringToTestament(String testament) {
        return Testament.fromStringSafe(testament);
    }

    // ==========================================
    // BibleCategory 매핑만 담당
    // ==========================================
    @Mapping(target = "testament", source = "testament", qualifiedByName = "testamentToString")
    @Mapping(target = "bookCount", expression = "java(category.getBooks() != null ? category.getBooks().size() : 0)")
    @Mapping(target = "books", ignore = true) // 순환 참조 방지, Service에서 별도 설정
    BibleCategoryDto toCategoryDto(BibleCategory category);

    @Named("categoryToSimpleDto")
    @Mapping(target = "testament", source = "testament", qualifiedByName = "testamentToString")
    @Mapping(target = "bookCount", expression = "java(category.getBooks() != null ? category.getBooks().size() : 0)")
    BibleCategorySimpleDto categoryToSimpleDto(BibleCategory category);
}
