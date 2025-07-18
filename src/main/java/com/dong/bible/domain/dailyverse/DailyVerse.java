package com.dong.bible.domain.dailyverse;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Objects;

/**
 * 오늘의 말씀을 나타내는 Entity (Aggregate Root)
 * 날짜별로 유일한 성경 구절과 메시지를 관리합니다.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DailyVerse {
    
    private final Long id;                  // 식별자 (nullable for new entities)
    private final DailyDate date;           // 날짜 (유일성 보장)
    private final String bookName;          // 성경책 이름
    private final Integer chapter;          // 장
    private final VerseRange verseRange;    // 절 범위
    private final DailyMessage message;     // 제목 + 설명
    private final boolean isActive;         // 활성화 여부
    
    /**
     * 정적 팩토리 메서드 - 새로운 DailyVerse 생성 (ID 없음)
     */
    public static DailyVerse of(DailyDate date, String bookName, Integer chapter, 
                               VerseRange verseRange, DailyMessage message) {
        validateParameters(date, bookName, chapter, verseRange, message);
        
        return new DailyVerse(null, date, bookName, chapter, verseRange, message, true);
    }
    
    /**
     * 정적 팩토리 메서드 - 기존 DailyVerse 생성 (ID 있음, Infrastructure에서 사용)
     */
    public static DailyVerse of(Long id, DailyDate date, String bookName, Integer chapter,
                               VerseRange verseRange, DailyMessage message, boolean isActive) {
        validateParameters(date, bookName, chapter, verseRange, message);
        
        if (id == null) {
            throw new IllegalArgumentException("ID는 null일 수 없습니다");
        }
        
        return new DailyVerse(id, date, bookName, chapter, verseRange, message, isActive);
    }
    
    /**
     * Infrastructure에서 사용하는 팩토리 메서드 (DB 조회 결과를 Domain으로 변환)
     */
    public static DailyVerse from(Long id, LocalDate date, String bookName, Integer chapter,
                                 Integer verseStart, Integer verseEnd, String title, 
                                 String description, Boolean isActive) {
        DailyDate dailyDate = DailyDate.of(date);
        VerseRange range = VerseRange.of(verseStart, verseEnd);
        DailyMessage dailyMessage = DailyMessage.of(title, description);
        
        return of(id, dailyDate, bookName, chapter, range, dailyMessage, 
                 isActive != null ? isActive : true);
    }
    
    /**
     * 파라미터 유효성 검증
     */
    private static void validateParameters(DailyDate date, String bookName, Integer chapter, 
                                         VerseRange verseRange, DailyMessage message) {
        if (date == null) {
            throw new IllegalArgumentException("날짜는 null일 수 없습니다");
        }
        
        if (bookName == null || bookName.trim().isEmpty()) {
            throw new IllegalArgumentException("성경책 이름은 null이거나 빈 값일 수 없습니다");
        }
        
        if (chapter == null || chapter <= 0) {
            throw new IllegalArgumentException("장 번호는 1 이상이어야 합니다: " + chapter);
        }
        
        if (verseRange == null) {
            throw new IllegalArgumentException("구절 범위는 null일 수 없습니다");
        }
        
        if (message == null) {
            throw new IllegalArgumentException("메시지는 null일 수 없습니다");
        }
    }
    
    // === 비즈니스 메서드들 ===
    
    /**
     * 오늘의 말씀인지 확인
     */
    public boolean isToday() {
        return date.isToday();
    }
    
    /**
     * 특정 월에 속하는지 확인
     */
    public boolean isInMonth(int year, int month) {
        return date.isInMonth(year, month);
    }
    
    /**
     * 특정 날짜 범위에 속하는지 확인
     */
    public boolean isInDateRange(LocalDate startDate, LocalDate endDate) {
        return date.isBetween(startDate, endDate);
    }
    
    /**
     * 단일 절인지 확인
     */
    public boolean isSingleVerse() {
        return verseRange.isSingleVerse();
    }
    
    /**
     * 활성화되어 있는지 확인
     */
    public boolean isActiveVerse() {
        return isActive;
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
    
    // === 표시 메서드들 ===
    
    /**
     * 성경 참조 문자열 생성 ("요한복음 3:16" 또는 "요한복음 3:16-17")
     */
    public String getDisplayReference() {
        return bookName + " " + chapter + ":" + verseRange.toDisplayString();
    }
    
    /**
     * 축약형 참조 문자열 ("요 3:16")
     */
    public String getShortReference() {
        // 성경책 이름 축약 로직 (간단히 첫 글자만)
        String shortBookName = bookName.length() > 2 ? bookName.substring(0, 2) : bookName;
        return shortBookName + " " + chapter + ":" + verseRange.toDisplayString();
    }
    
    /**
     * 표시용 문자열 생성
     */
    public String toDisplayString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(date.toString()).append("] ");
        sb.append(getDisplayReference());
        
        if (message.hasTitle()) {
            sb.append(" - ").append(message.getTitleOrEmpty());
        }
        
        return sb.toString();
    }
    
    /**
     * 비활성화된 DailyVerse 생성 (소프트 삭제)
     */
    public DailyVerse deactivate() {
        return new DailyVerse(id, date, bookName, chapter, verseRange, message, false);
    }
    
    /**
     * 활성화된 DailyVerse 생성
     */
    public DailyVerse activate() {
        return new DailyVerse(id, date, bookName, chapter, verseRange, message, true);
    }
    
    /**
     * 메시지 변경
     */
    public DailyVerse withMessage(DailyMessage newMessage) {
        if (newMessage == null) {
            throw new IllegalArgumentException("메시지는 null일 수 없습니다");
        }
        return new DailyVerse(id, date, bookName, chapter, verseRange, newMessage, isActive);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        DailyVerse that = (DailyVerse) o;
        
        // ID가 있으면 ID로 비교, 없으면 날짜로 비교 (날짜는 유일해야 함)
        if (id != null && that.id != null) {
            return Objects.equals(id, that.id);
        }
        
        return Objects.equals(date, that.date);
    }
    
    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : Objects.hash(date);
    }
    
    @Override
    public String toString() {
        return toDisplayString();
    }
}