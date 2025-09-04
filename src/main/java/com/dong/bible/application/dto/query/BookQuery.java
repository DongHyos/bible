package com.dong.bible.application.dto.query;

import com.dong.bible.domain.book.Book;
import com.dong.bible.ENUM.Testament;
import lombok.Builder;
import lombok.Getter;

/**
 * 성경책 조회용 Application DTO
 * Use Case: 성경책 관련 모든 API (목록, 상세, 통계 등)
 * Note: Book은 단순한 구조이므로 Detail/Summary 구분 없이 하나로 통합
 */
@Getter
@Builder
public class BookQuery {
    private final Long id;
    private final String name;
    private final String abbreviation;
    private final Testament testament;  // enum 그대로 유지
    private final Integer bookOrder;
    private final Integer totalChapters;
    private final Long categoryId;
    
    public static BookQuery from(Book domain) {
        return BookQuery.builder()
                .id(domain.getId())
                .name(domain.getBookName().getName())
                .abbreviation(domain.getAbbreviation())
                .testament(domain.getTestament())  // enum 그대로
                .bookOrder(domain.getBookOrder())
                .totalChapters(domain.getTotalChapters())
                .categoryId(domain.getCategoryId())
                .build();
    }
}
