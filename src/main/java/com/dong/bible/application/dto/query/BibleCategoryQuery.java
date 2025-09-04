package com.dong.bible.application.dto.query;

import com.dong.bible.domain.category.BibleCategory;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 성경 분류 조회용 Application DTO
 * Use Case: 성경 분류 관련 모든 API (구약/신약별 분류, 전체 구조 등)
 */
@Getter
@Builder
public class BibleCategoryQuery {
    private final Integer id;
    private final String name;
    private final String nameEn;
    private final String testament;
    private final Integer categoryOrder;
    private final String description;
    private final Integer bookCount;
    private final List<BookQuery> books;

    /**
     * Domain 객체로부터 Application DTO 생성 (기존 패턴 따름)
     */
    public static BibleCategoryQuery from(BibleCategory domain, List<BookQuery> books) {
        if (domain == null) {
            throw new IllegalArgumentException("BibleCategory domain cannot be null");
        }
        
        if (books == null) {
            throw new IllegalArgumentException("Books list cannot be null");
        }

        return BibleCategoryQuery.builder()
                .id(domain.getId())
                .name(domain.getName())
                .nameEn(domain.getNameEn())
                .testament(domain.getTestament().name()) // enum을 String으로 변환
                .categoryOrder(domain.getCategoryOrder())
                .description(domain.getDescription())
                .bookCount(books.size())
                .books(books)
                .build();
    }

    /**
     * 책 목록 없이 기본 정보만으로 생성 (필요시)
     */
    public static BibleCategoryQuery from(BibleCategory domain) {
        return from(domain, List.of());
    }
}