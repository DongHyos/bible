package com.dong.bible.web.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BibleBookResponse {
    private Integer id;
    private String name;
    private String abbr;
    private String testament;
    private Integer bookOrder;
    private Integer chapters;
    private Integer categoryId;
    private String categoryName;

    // 선택적 포함 필드들
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BibleCategorySimpleResponse category;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer totalVerses;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime createdAt;
}
