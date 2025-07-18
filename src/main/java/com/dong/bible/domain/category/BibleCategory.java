package com.dong.bible.domain.category;

import com.dong.bible.ENUM.Testament;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

/**
 * 성경 분류를 나타내는 Domain Entity
 * 예: 모세오경, 역사서, 시가서, 예언서, 복음서, 서신서, 묵시서 등
 */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BibleCategory {
    
    private final Integer id;
    private final String name;
    private final String nameEn;
    private final Testament testament;
    private final Integer categoryOrder;
    private final String description;
    private final List<Integer> bookIds; // 책 ID 목록 (FK 관계 대신 ID만 보관)
    
    /**
     * 정적 팩토리 메서드 - 새로운 BibleCategory 생성 (ID 없음)
     */
    public static BibleCategory of(String name, String nameEn, Testament testament, 
                                 Integer categoryOrder, String description) {
        validateParameters(name, testament, categoryOrder);
        
        return BibleCategory.builder()
                .name(name)
                .nameEn(nameEn)
                .testament(testament)
                .categoryOrder(categoryOrder)
                .description(description)
                .bookIds(List.of()) // 빈 리스트로 초기화
                .build();
    }
    
    /**
     * 정적 팩토리 메서드 - 기존 BibleCategory 생성 (ID 있음, Infrastructure에서 사용)
     */
    public static BibleCategory of(Integer id, String name, String nameEn, Testament testament,
                                 Integer categoryOrder, String description, List<Integer> bookIds) {
        validateParameters(name, testament, categoryOrder);
        
        if (id == null) {
            throw new IllegalArgumentException("ID는 null일 수 없습니다.");
        }
        
        return BibleCategory.builder()
                .id(id)
                .name(name)
                .nameEn(nameEn)
                .testament(testament)
                .categoryOrder(categoryOrder)
                .description(description)
                .bookIds(bookIds != null ? List.copyOf(bookIds) : List.of())
                .build();
    }
    
    /**
     * Infrastructure에서 사용하는 팩토리 메서드 (Entity -> Domain 변환)
     */
    public static BibleCategory from(Integer id, String name, String nameEn, String testament,
                                   Integer categoryOrder, String description, List<Integer> bookIds) {
        Testament testamentEnum = "신약".equals(testament) ? Testament.신약 : Testament.구약;
        
        return of(id, name, nameEn, testamentEnum, categoryOrder, description, bookIds);
    }
    
    /**
     * 파라미터 유효성 검증
     */
    private static void validateParameters(String name, Testament testament, Integer categoryOrder) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("분류 이름은 null이거나 비어있을 수 없습니다.");
        }
        if (testament == null) {
            throw new IllegalArgumentException("신구약 구분은 null일 수 없습니다.");
        }
        if (categoryOrder == null || categoryOrder < 1) {
            throw new IllegalArgumentException("분류 순서는 1 이상이어야 합니다: " + categoryOrder);
        }
    }
    
    /**
     * 이 분류가 신약인지 확인
     */
    public boolean isNewTestament() {
        return testament == Testament.신약;
    }
    
    /**
     * 이 분류가 구약인지 확인
     */
    public boolean isOldTestament() {
        return testament == Testament.구약;
    }
    
    /**
     * 이 분류에 속한 책의 개수
     */
    public int getBookCount() {
        return bookIds != null ? bookIds.size() : 0;
    }
    
    /**
     * 특정 책이 이 분류에 속하는지 확인
     */
    public boolean containsBook(Integer bookId) {
        return bookIds != null && bookIds.contains(bookId);
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
        return String.format("%s (%s, %d권)", 
            name, 
            testament == Testament.신약 ? "신약" : "구약", 
            getBookCount());
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        BibleCategory that = (BibleCategory) o;
        
        // ID가 있으면 ID로 비교, 없으면 name과 testament로 비교
        if (id != null && that.id != null) {
            return Objects.equals(id, that.id);
        }
        
        return Objects.equals(name, that.name) && 
               Objects.equals(testament, that.testament);
    }
    
    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : Objects.hash(name, testament);
    }
    
    @Override
    public String toString() {
        return String.format("BibleCategory{id=%d, name='%s', testament=%s}", 
                           id, name, testament);
    }
}