package com.dong.bible.mapstruct;

import com.dong.bible.web.dto.response.SermonDto;
import com.dong.bible.web.dto.response.SermonSimpleDto;
import com.dong.bible.web.dto.response.SermonVerseDto;
import com.dong.bible.infrastructure.persistence.entity.Sermon;
import com.dong.bible.infrastructure.persistence.entity.SermonVerse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SermonMapper {

    @Mapping(target = "tags", source = "tags", qualifiedByName = "jsonToStringList")
    @Mapping(target = "verses", source = "sermonVerses")
    SermonDto toDto(Sermon sermon);

    @Mapping(target = "tags", source = "tags", qualifiedByName = "jsonToStringList")
    SermonSimpleDto toSimpleDto(Sermon sermon);

    List<SermonSimpleDto> toSimpleDtoList(List<Sermon> sermons);

    @Mapping(target = "bookName", source = "book.name")
    @Mapping(target = "bookAbbr", source = "book.abbr")
    SermonVerseDto toVerseDto(SermonVerse sermonVerse);

    @Named("jsonToStringList")
    default List<String> jsonToStringList(String json) {
        if (json == null || json.trim().isEmpty()) {
            return List.of();
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }
}