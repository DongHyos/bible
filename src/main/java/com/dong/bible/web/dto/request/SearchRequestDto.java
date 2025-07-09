package com.dong.bible.web.dto.request;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class SearchRequestDto {
    
    @NotBlank(message = "검색어는 필수입니다")
    @Size(min = 1, max = 100, message = "검색어는 1자 이상 100자 이하여야 합니다")
    private String keyword;
    
    // 옵션: 검색 범위 제한
    private Integer bookId; // 특정 책에서만 검색
    private String testament; // 구약/신약에서만 검색
    
    // 옵션: 페이징
    private Integer page = 0;
    private Integer size = 20;
    
    // 옵션: 정렬
    private String sortBy = "bookOrder"; // bookOrder, relevance
    private String sortDirection = "asc"; // asc, desc
}
