package com.dong.bible.web.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class BibleCategoryDto {
    private Integer id;
    private String name;
    private String nameEn;
    private String testament;
    private Integer categoryOrder;
    private String description;
    private Integer bookCount; // 해당 분류의 책 수
    private List<BibleBookDto> books; // 해당 분류의 책들
}
