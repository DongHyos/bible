package com.dong.bible.web.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KrvBookResponse {
    private Integer id;
    private String name;
    private String abbr;
    private String testament; // "구약" or "신약"
    private Integer bookOrder;
    private Integer chapters;
    private LocalDateTime createdAt;
}
