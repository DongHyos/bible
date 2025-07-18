package com.dong.bible.domain.verse;

import lombok.Value;

/**
 * 완전한 구절 참조 범위를 나타내는 Value Object
 * 예: "요한복음 3:16-18"
 */
@Value
public class VerseReferenceRange {
    String bookName;
    int chapter;
    int startVerse;
    int endVerse;

    private VerseReferenceRange(String bookName, int chapter, int startVerse, int endVerse) {
        this.bookName = bookName;
        this.chapter = chapter;
        this.startVerse = startVerse;
        this.endVerse = endVerse;
    }

    /**
     * 구절 참조 범위 생성 팩토리 메서드
     */
    public static VerseReferenceRange of(String bookName, int chapter, int startVerse, int endVerse) {
        validateInputs(bookName, chapter, startVerse, endVerse);
        validateRange(startVerse, endVerse);
        
        return new VerseReferenceRange(bookName.trim(), chapter, startVerse, endVerse);
    }

    /**
     * 입력값 기본 검증
     */
    private static void validateInputs(String bookName, int chapter, int startVerse, int endVerse) {
        if (bookName == null || bookName.trim().isEmpty()) {
            throw new IllegalArgumentException("책 이름은 비어있을 수 없습니다");
        }
        if (chapter <= 0) {
            throw new IllegalArgumentException("장 번호는 1 이상이어야 합니다");
        }
        if (startVerse <= 0) {
            throw new IllegalArgumentException("시작 절 번호는 1 이상이어야 합니다");
        }
        if (endVerse <= 0) {
            throw new IllegalArgumentException("끝 절 번호는 1 이상이어야 합니다");
        }
    }

    /**
     * 범위 유효성 검증 (핵심 비즈니스 로직)
     */
    private static void validateRange(int startVerse, int endVerse) {
        if (startVerse > endVerse) {
            throw new IllegalArgumentException(
                String.format("시작 절(%d)이 끝 절(%d)보다 클 수 없습니다", startVerse, endVerse)
            );
        }
    }

    /**
     * 단일 구절인지 확인
     */
    public boolean isSingleVerse() {
        return startVerse == endVerse;
    }

    /**
     * 범위 크기 (포함되는 구절 수)
     */
    public int getRangeSize() {
        return endVerse - startVerse + 1;
    }

    /**
     * 특정 구절이 이 범위에 포함되는지 확인
     */
    public boolean contains(int verse) {
        return verse >= startVerse && verse <= endVerse;
    }

    /**
     * VerseReference가 이 범위에 포함되는지 확인
     */
    public boolean contains(VerseReference verseRef) {
        return bookName.equals(verseRef.getBookName()) &&
               chapter == verseRef.getChapter() &&
               contains(verseRef.getVerse());
    }

    /**
     * 표시용 문자열 생성
     */
    public String toDisplayString() {
        if (isSingleVerse()) {
            return bookName + " " + chapter + ":" + startVerse;
        }
        return bookName + " " + chapter + ":" + startVerse + "-" + endVerse;
    }

    /**
     * 축약 표시 문자열 (책 이름 제외)
     */
    public String toShortDisplayString() {
        if (isSingleVerse()) {
            return chapter + ":" + startVerse;
        }
        return chapter + ":" + startVerse + "-" + endVerse;
    }
}