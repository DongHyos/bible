package com.dong.bible.domain.book;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 성경책 이름을 나타내는 Value Object
 * 66권 성경책의 유효성을 검증하고 도메인 비즈니스 로직을 포함합니다.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BookName {
    
    private final String name;
    
    // 66권 성경책 목록 (한글명)
    private static final List<String> OLD_TESTAMENT_BOOKS = Arrays.asList(
        "창세기", "출애굽기", "레위기", "민수기", "신명기",
        "여호수아", "사사기", "룻기", "사무엘상", "사무엘하",
        "열왕기상", "열왕기하", "역대상", "역대하", "에스라",
        "느헤미야", "에스더", "욥기", "시편", "잠언",
        "전도서", "아가", "이사야", "예레미야", "예레미야애가",
        "에스겔", "다니엘", "호세아", "요엘", "아모스",
        "오바댜", "요나", "미가", "나훔", "하박국",
        "스바냐", "학개", "스가랴", "말라기"
    );
    
    private static final List<String> NEW_TESTAMENT_BOOKS = Arrays.asList(
        "마태복음", "마가복음", "누가복음", "요한복음", "사도행전",
        "로마서", "고린도전서", "고린도후서", "갈라디아서", "에베소서",
        "빌립보서", "골로새서", "데살로니가전서", "데살로니가후서", "디모데전서",
        "디모데후서", "디도서", "빌레몬서", "히브리서", "야고보서",
        "베드로전서", "베드로후서", "요한일서", "요한이서", "요한삼서",
        "유다서", "요한계시록"
    );
    
    /**
     * 정적 팩토리 메서드 - 유효성 검증 후 생성
     * @param name 성경책 이름
     * @return BookName 인스턴스
     * @throws IllegalArgumentException 유효하지 않은 성경책 이름인 경우
     */
    public static BookName of(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("성경책 이름은 필수입니다.");
        }
        
        String trimmedName = name.trim();
        if (!isValidBookName(trimmedName)) {
            throw new IllegalArgumentException("유효하지 않은 성경책 이름입니다: " + trimmedName);
        }
        
        return new BookName(trimmedName);
    }
    
    /**
     * 안전한 정적 팩토리 메서드 - 예외 대신 null 반환
     * @param name 성경책 이름
     * @return 유효한 경우 BookName, 유효하지 않은 경우 null
     */
    public static BookName ofSafe(String name) {
        try {
            return of(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * 성경책 이름 유효성 검증
     * @param name 검증할 이름
     * @return 유효한 성경책 이름인지 여부
     */
    private static boolean isValidBookName(String name) {
        return OLD_TESTAMENT_BOOKS.contains(name) || NEW_TESTAMENT_BOOKS.contains(name);
    }
    
    /**
     * 신약 성경책인지 확인
     * @return 신약 성경책이면 true, 구약 성경책이면 false
     */
    public boolean isNewTestament() {
        return NEW_TESTAMENT_BOOKS.contains(this.name);
    }
    
    /**
     * 구약 성경책인지 확인
     * @return 구약 성경책이면 true, 신약 성경책이면 false
     */
    public boolean isOldTestament() {
        return OLD_TESTAMENT_BOOKS.contains(this.name);
    }
    
    /**
     * 성경책의 순서 번호 반환 (1-66)
     * @return 성경책 순서 번호
     */
    public int getBookOrder() {
        int oldTestamentIndex = OLD_TESTAMENT_BOOKS.indexOf(this.name);
        if (oldTestamentIndex != -1) {
            return oldTestamentIndex + 1; // 1부터 시작
        }
        
        int newTestamentIndex = NEW_TESTAMENT_BOOKS.indexOf(this.name);
        if (newTestamentIndex != -1) {
            return OLD_TESTAMENT_BOOKS.size() + newTestamentIndex + 1; // 구약 다음부터
        }
        
        throw new IllegalStateException("유효하지 않은 성경책입니다: " + this.name);
    }
    
    /**
     * 성경책의 축약형 이름 생성
     * @return 축약형 이름
     */
    public String getAbbreviation() {
        // 기본적으로 첫 번째와 두 번째 글자 사용
        if (name.length() >= 2) {
            return name.substring(0, 2);
        }
        return name;
    }
    
    /**
     * 모든 구약 성경책 목록 반환
     * @return 구약 성경책 리스트 (불변)
     */
    public static List<String> getOldTestamentBooks() {
        return List.copyOf(OLD_TESTAMENT_BOOKS);
    }
    
    /**
     * 모든 신약 성경책 목록 반환
     * @return 신약 성경책 리스트 (불변)
     */
    public static List<String> getNewTestamentBooks() {
        return List.copyOf(NEW_TESTAMENT_BOOKS);
    }
    
    /**
     * 모든 성경책 목록 반환 (구약 + 신약)
     * @return 전체 성경책 리스트 (불변)
     */
    public static List<String> getAllBooks() {
        List<String> allBooks = new java.util.ArrayList<>();
        allBooks.addAll(OLD_TESTAMENT_BOOKS);
        allBooks.addAll(NEW_TESTAMENT_BOOKS);
        return List.copyOf(allBooks);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookName bookName = (BookName) o;
        return Objects.equals(name, bookName.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
    
    @Override
    public String toString() {
        return name;
    }
}
