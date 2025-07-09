package com.dong.bible.mapstruct;

import com.dong.bible.web.dto.response.BibleBookDto;
import com.dong.bible.web.dto.response.BibleBookMiniDto;
import com.dong.bible.web.dto.response.BibleCategorySimpleDto;
import com.dong.bible.infrastructure.persistence.entity.BibleCategory;
import com.dong.bible.infrastructure.persistence.entity.KrvBook;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface KrvBookMapper {

    @Named("toBookDtoFull")
    @Mapping(target = "testament", expression = "java(book.getTestament().getValue())")
    @Mapping(target = "category", source = "category", qualifiedByName = "categoryToSimpleDto") // 명시적 매핑
    BibleBookDto toDto(KrvBook book);

    @Named("toBookDtoSimple")
    @Mapping(target = "testament", expression = "java(book.getTestament().getValue())")
    @Mapping(target = "category", ignore = true) // category 정보 제외한 간단 버전
    BibleBookDto toDtoSimple(KrvBook book);

    @Named("toBookMiniDto")
    BibleBookMiniDto toMiniDto(KrvBook book);

    // List 매핑 (명시적 메서드 지정)
    @IterableMapping(qualifiedByName = "toBookDtoFull")
    List<BibleBookDto> toDtoList(List<KrvBook> books);

    @IterableMapping(qualifiedByName = "toBookDtoSimple")
    List<BibleBookDto> toDtoSimpleList(List<KrvBook> books);

    @IterableMapping(qualifiedByName = "toBookMiniDto")
    List<BibleBookMiniDto> toMiniDtoList(List<KrvBook> books);

    // Category 매핑 (순환 참조 방지)
    @Named("categoryToSimpleDto")
    @Mapping(target = "testament", expression = "java(category.getTestament().getValue())")
    @Mapping(target = "bookCount", expression = "java(category.getBooks() != null ? category.getBooks().size() : 0)")
    BibleCategorySimpleDto categoryToSimpleDto(BibleCategory category);
}
