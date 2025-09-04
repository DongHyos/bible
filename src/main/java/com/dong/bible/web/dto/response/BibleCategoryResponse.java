package com.dong.bible.web.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Builder
public class BibleCategoryResponse {
    private Integer id;
    private String name;
    private String nameEn;
    private String testament;
    private Integer categoryOrder;
    private String description;
    
    @Setter
    private Integer bookCount; // 해당 분류의 책 수 (Service에서 계산 후 설정)
    
    @Setter
    private List<BibleBookResponse> books; // 해당 분류의 책들 (Service에서 매핑 후 설정)
}
