package com.dong.bible.web.dto.response;

import lombok.Data;

@Data
public class BibleCategorySimpleResponse {
    private Integer id;
    private String name;
    private String nameEn;
    private String testament; // 추가
    private Integer categoryOrder;
    private Integer bookCount;
}
