package com.dong.bible.common.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Getter
@Builder
public class ResPaging {
    private int totalPages;
    private long totalElements;
    private int numberOfElements;
    private int number;
    private boolean last;
    private boolean first;
    private boolean empty;
    private Sort sort;
    
    private Pageable pageable;

    public static ResPaging of(Page<?> page){
        return ResPaging.builder()
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .numberOfElements(page.getNumberOfElements())
                .number(page.getNumber())
                .last(page.isLast())
                .first(page.isFirst())
                .empty(page.isEmpty())
                .sort(page.getSort())
                .pageable(page.getPageable())
                .build();
    }
}
