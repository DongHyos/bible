package com.dong.bible.domain.book;

import com.dong.bible.ENUM.Testament;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Objects;

/**
 * 성경책을 나타내는 Entity
 * DB의 krv_books 테이블과 매핑되는 도메인 객체
 */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Book {
    
    private final Long id;              // krv_books.id
    private final BookName bookName;    // krv_books.name (Value Object)
    private final String abbreviation;  // krv_books.abbr
    private final Testament testament;  // krv_books.testament (구약/신약)
    private final int bookOrder;        // krv_books.book_order (1-66)
    private final int totalChapters;    // krv_books.chapters
    private final Long categoryId;      // krv_books.category_id
    
    /**
     * 정적 팩토리 메서드 - 새로운 Book 생성 (ID 없음)
     */
    public static Book of(BookName bookName, String abbreviation, Testament testament, 
                         int bookOrder, int totalChapters, Long categoryId) {
        validateParameters(bookName, bookOrder, totalChapters);
        
        return Book.builder()
                .bookName(bookName)
                .abbreviation(abbreviation)
                .testament(testament)
                .bookOrder(bookOrder)
                .totalChapters(totalChapters)
                .categoryId(categoryId)
                .build();
    }
    
    /**
     * 정적 팩토리 메서드 - 기존 Book 생성 (ID 있음, Infrastructure에서 사용)
     */
    public static Book of(Long id, BookName bookName, String abbreviation, Testament testament,
                         int bookOrder, int totalChapters, Long categoryId) {
        validateParameters(bookName, bookOrder, totalChapters);
        
        if (id == null) {
            throw new IllegalArgumentException("ID는 null일 수 없습니다.");
        }
        
        return Book.builder()
                .id(id)
                .bookName(bookName)
                .abbreviation(abbreviation)
                .testament(testament)
                .bookOrder(bookOrder)
                .totalChapters(totalChapters)
                .categoryId(categoryId)
                .build();
    }
    
    /**
     * Infrastructure에서 사용하는 팩토리 메서드 (DB 조회 결과를 Domain으로 변환)
     */
    public static Book from(Integer id, String name, String abbr, String testament,
                           Integer bookOrder, Integer chapters, Integer categoryId) {
        BookName bookName = BookName.of(name);
        Testament testamentEnum = "신약".equals(testament) ? Testament.NEW : Testament.OLD;
        
        return of(
            id != null ? id.longValue() : null,
            bookName,
            abbr,
            testamentEnum,
            bookOrder,
            chapters,
            categoryId != null ? categoryId.longValue() : null
        );
    }
    
    /**
     * 파라미터 유효성 검증
     */
    private static void validateParameters(BookName bookName, int bookOrder, int totalChapters) {
        if (bookName == null) {
            throw new IllegalArgumentException("성경책 이름은 null일 수 없습니다.");
        }
        if (bookOrder < 1 || bookOrder > 66) {
            throw new IllegalArgumentException("성경책 순서는 1-66 사이여야 합니다: " + bookOrder);
        }
        if (totalChapters < 1) {
            throw new IllegalArgumentException("총 장수는 1 이상이어야 합니다: " + totalChapters);
        }
    }
    
    /**
     * 특정 장이 이 책에 속하는지 확인
     */
    public boolean hasChapter(int chapter) {
        return chapter > 0 && chapter <= totalChapters;
    }
    
    /**
     * 장 번호 유효성 검증 (비즈니스 로직)
     */
    public void validateChapter(int chapter) {
        if (!hasChapter(chapter)) {
            throw new IllegalArgumentException(
                String.format("%s는 %d장까지만 있습니다. 요청된 장: %d", 
                    bookName.getName(), totalChapters, chapter)
            );
        }
    }
    
    /**
     * 이 책이 신약인지 확인
     */
    public boolean isNewTestament() {
        return testament == Testament.NEW;
    }
    
    /**
     * 이 책이 구약인지 확인
     */
    public boolean isOldTestament() {
        return testament == Testament.OLD;
    }
    
    /**
     * 새로운 엔티티인지 확인 (ID가 없으면 새로운 엔티티)
     */
    public boolean isNew() {
        return id == null;
    }
    
    /**
     * 영속화된 엔티티인지 확인
     */
    public boolean isPersisted() {
        return id != null;
    }
    
    /**
     * 표시용 문자열 생성
     */
    public String toDisplayString() {
        return String.format("%s (%s, %d장)", 
            bookName.getName(), 
            testament == Testament.NEW ? "신약" : "구약", 
            totalChapters);
    }
    
    /**
     * 축약형 표시 문자열
     */
    public String toShortDisplayString() {
        return String.format("%s (%s)", abbreviation, totalChapters + "장");
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Book book = (Book) o;
        
        // ID가 있으면 ID로 비교, 없으면 bookName으로 비교
        if (id != null && book.id != null) {
            return Objects.equals(id, book.id);
        }
        
        return Objects.equals(bookName, book.bookName);
    }
    
    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : Objects.hash(bookName);
    }
    
    @Override
    public String toString() {
        return bookName.getName();
    }
}
