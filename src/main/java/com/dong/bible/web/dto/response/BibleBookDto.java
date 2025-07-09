package com.dong.bible.web.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BibleBookDto {
    private Integer id;
    private String name;
    private String abbr;
    private String testament;
    private Integer bookOrder;
    private Integer chapters;

    // 선택적 포함 필드들
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BibleCategorySimpleDto category;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer totalVerses;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime createdAt;
}
