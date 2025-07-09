package com.dong.bible.mapstruct;

import com.dong.bible.web.dto.response.DailyVerseDto;
import com.dong.bible.web.dto.response.DailyVerseSimpleDto;
import com.dong.bible.infrastructure.persistence.entity.DailyVerse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DailyVerseMapper {

    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookName", source = "book.name")
    @Mapping(target = "bookAbbr", source = "book.abbr")
    @Mapping(target = "verseText", ignore = true) // 서비스에서 별도 설정
    DailyVerseDto toDto(DailyVerse dailyVerse);

    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookName", source = "book.name")
    @Mapping(target = "bookAbbr", source = "book.abbr")
    DailyVerseSimpleDto toSimpleDto(DailyVerse dailyVerse);

    List<DailyVerseDto> toDtoList(List<DailyVerse> dailyVerses);

    List<DailyVerseSimpleDto> toSimpleDtoList(List<DailyVerse> dailyVerses);
}