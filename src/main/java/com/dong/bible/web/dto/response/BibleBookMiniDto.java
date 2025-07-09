package com.dong.bible.web.dto.response;

import lombok.Data;

// 최소 정보만 필요한 경우
@Data
public class BibleBookMiniDto {
    private Integer id;
    private String name;
    private String abbr;
}
