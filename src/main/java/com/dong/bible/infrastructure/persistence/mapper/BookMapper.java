package com.dong.bible.infrastructure.persistence.mapper;

import com.dong.bible.ENUM.Testament;
import com.dong.bible.domain.book.Book;
import com.dong.bible.domain.book.BookName;
import com.dong.bible.infrastructure.persistence.entity.KrvBookEntity;
import org.springframework.stereotype.Component;

/**
 * Book Domain과 Infrastructure Entity 간의 매핑을 담당하는 Mapper
 * 순수 DDD 원칙에 따라 Domain과 Infrastructure 계층을 분리합니다.
 */
@Component
public class BookMapper {
    
    /**
     * Domain Book을 Infrastructure Entity로 변환 (저장용)
     * @param book 도메인 Book 객체
     * @return KrvBookEntity Infrastructure 엔티티
     */
    public KrvBookEntity toEntity(Book book) {
        if (book == null) {
            return null;
        }
        
        return KrvBookEntity.builder()
                .id(book.getId() != null ? book.getId().intValue() : null)
                .name(book.getBookName().getName())
                .abbr(book.getAbbreviation())
                .testament(book.getTestament())  // 이미 Testament enum
                .bookOrder(book.getBookOrder())
                .chapters(book.getTotalChapters())
                // category는 별도 조회 필요 (현재는 ID만 있음)
                .build();
    }
    
    /**
     * Infrastructure Entity를 Domain Book으로 변환 (조회용)
     * @param entity KrvBookEntity Infrastructure 엔티티
     * @return Book 도메인 객체
     */
    public Book toDomain(KrvBookEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Long categoryId = entity.getCategory() != null ? 
            entity.getCategory().getId().longValue() : null;
        
        return toDomain(
            entity.getId(),
            entity.getName(),
            entity.getAbbr(),
            entity.getTestament(),
            entity.getBookOrder(),
            entity.getChapters(),
            categoryId
        );
    }
    
    /**
     * DB 조회 결과 원시 데이터를 Domain Book으로 변환
     * Repository에서 직접 쿼리 결과를 매핑할 때 사용
     * @param id 성경책 ID
     * @param name 성경책 이름
     * @param abbr 축약명
     * @param testament 신구약 구분 (Testament enum)
     * @param bookOrder 성경책 순서 (1-66)
     * @param chapters 총 장수
     * @param categoryId 분류 ID
     * @return Book 도메인 객체
     */
    public Book toDomain(Integer id, String name, String abbr, Testament testament,
                        Integer bookOrder, Integer chapters, Long categoryId) {
        if (name == null) {
            return null;
        }
        
        try {
            BookName bookName = BookName.of(name);
            
            if (id != null) {
                // ID가 있는 경우 (DB에서 조회된 기존 엔티티)
                return Book.of(
                    id.longValue(),
                    bookName,
                    abbr,
                    testament,
                    bookOrder,
                    chapters,
                    categoryId
                );
            } else {
                // ID가 없는 경우 (새로운 엔티티)
                return Book.of(
                    bookName,
                    abbr,
                    testament,
                    bookOrder,
                    chapters,
                    categoryId
                );
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Book 도메인 객체 변환 실패: " + name, e);
        }
    }
    
    /**
     * BookName(도메인)을 문자열로 변환
     * @param bookName BookName Value Object
     * @return 성경책 이름 문자열
     */
    public String bookNameToString(BookName bookName) {
        return bookName != null ? bookName.getName() : null;
    }
    
    /**
     * 문자열을 BookName(도메인)으로 변환
     * @param name 성경책 이름 문자열
     * @return BookName Value Object
     */
    public BookName stringToBookName(String name) {
        return name != null ? BookName.of(name) : null;
    }
}
