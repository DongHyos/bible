package com.dong.bible.domain.sermon;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

import java.util.Objects;

/**
 * 설교와 관련된 성경 구절 정보를 나타내는 Value Object
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Value
public class SermonVerse {
    
    Integer bookId;
    String bookName;
    String bookAbbr;
    Short chapter;
    Short verseStart;
    Short verseEnd;
    Boolean isMainText;
    
    /**
     * 정적 팩토리 메서드 - 전체 정보로 생성
     */
    public static SermonVerse of(Integer bookId, String bookName, String bookAbbr,
                                Short chapter, Short verseStart, Short verseEnd, Boolean isMainText) {
        validateBookInfo(bookId, bookName);
        validateChapter(chapter);
        validateVerseRange(verseStart, verseEnd);
        
        return new SermonVerse(
                bookId,
                bookName.trim(),
                bookAbbr != null ? bookAbbr.trim() : null,
                chapter,
                verseStart,
                verseEnd,
                isMainText != null ? isMainText : false
        );
    }
    
    /**
     * 정적 팩토리 메서드 - 단일 절로 생성
     */
    public static SermonVerse single(Integer bookId, String bookName, String bookAbbr,
                                    Short chapter, Short verse, Boolean isMainText) {
        return of(bookId, bookName, bookAbbr, chapter, verse, verse, isMainText);
    }
    
    /**
     * 정적 팩토리 메서드 - 메인 본문으로 생성
     */
    public static SermonVerse mainText(Integer bookId, String bookName, String bookAbbr,
                                      Short chapter, Short verseStart, Short verseEnd) {
        return of(bookId, bookName, bookAbbr, chapter, verseStart, verseEnd, true);
    }
    
    /**
     * 책 정보 유효성 검증
     */
    private static void validateBookInfo(Integer bookId, String bookName) {
        if (bookId == null || bookId <= 0) {
            throw new IllegalArgumentException("책 ID는 1 이상이어야 합니다: " + bookId);
        }
        
        if (bookName == null || bookName.trim().isEmpty()) {
            throw new IllegalArgumentException("책 이름은 비어있을 수 없습니다");
        }
        
        if (bookName.trim().length() > 50) {
            throw new IllegalArgumentException("책 이름은 50자를 초과할 수 없습니다: " + bookName.trim().length() + "자");
        }
    }
    
    /**
     * 장 번호 유효성 검증
     */
    private static void validateChapter(Short chapter) {
        if (chapter == null || chapter <= 0) {
            throw new IllegalArgumentException("장 번호는 1 이상이어야 합니다: " + chapter);
        }
        
        if (chapter > 150) { // 시편이 150장으로 가장 많음
            throw new IllegalArgumentException("장 번호가 범위를 벗어났습니다: " + chapter);
        }
    }
    
    /**
     * 구절 범위 유효성 검증
     */
    private static void validateVerseRange(Short verseStart, Short verseEnd) {
        if (verseStart == null || verseStart <= 0) {
            throw new IllegalArgumentException("시작 절 번호는 1 이상이어야 합니다: " + verseStart);
        }
        
        if (verseEnd == null || verseEnd <= 0) {
            throw new IllegalArgumentException("끝 절 번호는 1 이상이어야 합니다: " + verseEnd);
        }
        
        if (verseStart > verseEnd) {
            throw new IllegalArgumentException("시작 절이 끝 절보다 클 수 없습니다: " + verseStart + " > " + verseEnd);
        }
        
        if (verseEnd > 176) { // 시편 119편이 176절로 가장 많음
            throw new IllegalArgumentException("절 번호가 범위를 벗어났습니다: " + verseEnd);
        }
    }
    
    /**
     * 단일 절인지 확인
     */
    public boolean isSingleVerse() {
        return verseStart.equals(verseEnd);
    }
    
    /**
     * 메인 본문인지 확인
     */
    public boolean isMainText() {
        return isMainText != null && isMainText;
    }
    
    /**
     * 참조 본문인지 확인 (메인 본문이 아닌)
     */
    public boolean isReferenceText() {
        return !isMainText();
    }
    
    /**
     * 구절 개수 반환
     */
    public int getVerseCount() {
        return verseEnd - verseStart + 1;
    }
    
    /**
     * 특정 절이 이 범위에 포함되는지 확인
     */
    public boolean containsVerse(Short verse) {
        if (verse == null) {
            return false;
        }
        
        return verse >= verseStart && verse <= verseEnd;
    }
    
    /**
     * 같은 책인지 확인
     */
    public boolean isSameBook(SermonVerse other) {
        if (other == null) {
            return false;
        }
        
        return bookId.equals(other.bookId);
    }
    
    /**
     * 같은 장인지 확인
     */
    public boolean isSameChapter(SermonVerse other) {
        if (other == null) {
            return false;
        }
        
        return isSameBook(other) && chapter.equals(other.chapter);
    }
    
    /**
     * 구절 범위가 겹치는지 확인
     */
    public boolean overlaps(SermonVerse other) {
        if (!isSameChapter(other)) {
            return false;
        }
        
        return !(verseEnd < other.verseStart || verseStart > other.verseEnd);
    }
    
    /**
     * 구절 참조 문자열 생성 ("요한복음 3:16" 또는 "요한복음 3:16-17")
     */
    public String getVerseReference() {
        String reference = bookName + " " + chapter + ":";
        
        if (isSingleVerse()) {
            reference += verseStart;
        } else {
            reference += verseStart + "-" + verseEnd;
        }
        
        return reference;
    }
    
    /**
     * 축약형 구절 참조 ("요 3:16")
     */
    public String getShortReference() {
        String shortBookName = bookAbbr != null ? bookAbbr : 
                              (bookName.length() > 2 ? bookName.substring(0, 2) : bookName);
        
        String reference = shortBookName + " " + chapter + ":";
        
        if (isSingleVerse()) {
            reference += verseStart;
        } else {
            reference += verseStart + "-" + verseEnd;
        }
        
        return reference;
    }
    
    /**
     * 표시용 문자열 생성
     */
    public String toDisplayString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getVerseReference());
        
        if (isMainText()) {
            sb.append(" (본문)");
        }
        
        return sb.toString();
    }
    
    /**
     * 다른 구절과 순서 비교 (같은 책 내에서)
     */
    public int compareOrder(SermonVerse other) {
        if (other == null) {
            return 1;
        }
        
        // 책 ID로 먼저 비교
        int bookCompare = Integer.compare(this.bookId, other.bookId);
        if (bookCompare != 0) {
            return bookCompare;
        }
        
        // 장으로 비교
        int chapterCompare = Short.compare(this.chapter, other.chapter);
        if (chapterCompare != 0) {
            return chapterCompare;
        }
        
        // 시작 절로 비교
        return Short.compare(this.verseStart, other.verseStart);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SermonVerse that = (SermonVerse) o;
        return Objects.equals(bookId, that.bookId) &&
               Objects.equals(chapter, that.chapter) &&
               Objects.equals(verseStart, that.verseStart) &&
               Objects.equals(verseEnd, that.verseEnd) &&
               Objects.equals(isMainText, that.isMainText);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(bookId, chapter, verseStart, verseEnd, isMainText);
    }
    
    @Override
    public String toString() {
        return toDisplayString();
    }
}