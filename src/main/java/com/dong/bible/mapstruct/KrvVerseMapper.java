package com.dong.bible.mapstruct;

import com.dong.bible.web.dto.response.ChapterDto;
import com.dong.bible.web.dto.response.VerseDto;
import com.dong.bible.web.dto.response.VerseSearchResultDto;
import com.dong.bible.web.dto.response.VerseSimpleDto;
import com.dong.bible.infrastructure.persistence.entity.KrvBook;
import com.dong.bible.infrastructure.persistence.entity.KrvVerse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface KrvVerseMapper {
    
    // 기본 구절 매핑
    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.name", target = "bookName")
    @Mapping(source = "book.abbr", target = "bookAbbr")
    VerseDto toDto(KrvVerse verse);
    
    // 간단한 구절 매핑
    VerseSimpleDto toSimpleDto(KrvVerse verse);
    
    // 검색 결과 매핑
    @Mapping(source = "book.name", target = "bookName")
    @Mapping(target = "reference", expression = "java(buildReference(verse))")
    VerseSearchResultDto toSearchResultDto(KrvVerse verse);
    
    // List 매핑
    List<VerseDto> toDtoList(List<KrvVerse> verses);
    List<VerseSimpleDto> toSimpleDtoList(List<KrvVerse> verses);
    List<VerseSearchResultDto> toSearchResultDtoList(List<KrvVerse> verses);
    
    // ChapterDto 매핑 (복합)
    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookName", source = "book.name")
    @Mapping(target = "bookAbbr", source = "book.abbr")
    @Mapping(target = "chapter", source = "chapter")
    @Mapping(target = "totalVerses", expression = "java(verses.size())")
    @Mapping(target = "verses", source = "verses")
    ChapterDto toChapterDto(KrvBook book, Integer chapter, List<KrvVerse> verses);
    
    // 구절 참조 문자열 생성 ("창세기 1:1" 형태)
    default String buildReference(KrvVerse verse) {
        if (verse == null || verse.getBook() == null) return "";
        return verse.getBook().getName() + " " + verse.getChapter() + ":" + verse.getVerse();
    }
}
