package com.dong.bible.domain.verse;

import lombok.Value;

import java.util.Set;

@Value
public class VerseReference { // Value Object
    String bookName;
    int chapter;
    int verse;

    // 신약 성경 책들
    private static final Set<String> NEW_TESTAMENT_BOOKS = Set.of(
            "마태복음", "마가복음", "누가복음", "요한복음",
            "사도행전", "로마서", "고린도전서", "고린도후서",
            "갈라디아서", "에베소서", "빌립보서", "골로새서",
            "데살로니가전서", "데살로니가후서", "디모데전서", "디모데후서",
            "디도서", "빌레몬서", "히브리서", "야고보서",
            "베드로전서", "베드로후서", "요한일서", "요한이서", "요한삼서",
            "유다서", "요한계시록"
    );

    // 정적 팩토리 메서드
    public static VerseReference of(String bookName, int chapter, int verse) {
        if (bookName == null || bookName.trim().isEmpty()) {
            throw new IllegalArgumentException("책 이름은 비어있을 수 없습니다");
        }
        if (chapter <= 0) {
            throw new IllegalArgumentException("장 번호는 1 이상이어야 합니다");
        }
        if (verse <= 0) {
            throw new IllegalArgumentException("절 번호는 1 이상이어야 합니다");
        }

        return new VerseReference(bookName.trim(), chapter, verse);
    }

    // 표시용 문자열
    public String toDisplayString() {
        return bookName + " " + chapter + ":" + verse;
    }

    // 유효성 검사
    public boolean isValid() {
        return bookName != null &&
                !bookName.trim().isEmpty() &&
                chapter > 0 &&
                verse > 0;
    }

    // 같은 장인지 확인
    public boolean isSameChapter(VerseReference other) {
        if (other == null) return false;
        return this.bookName.equals(other.bookName) &&
                this.chapter == other.chapter;
    }

    // 신약 성경인지 확인
    public boolean isNewTestament() {
        return NEW_TESTAMENT_BOOKS.contains(bookName);
    }

    // 구약 성경인지 확인 (보너스 메서드)
    public boolean isOldTestament() {
        return !isNewTestament() && isValid();
    }

    // 구절 비교 (같은 책 내에서 순서 비교)
    public int compareVerse(VerseReference other) {
        if (!this.bookName.equals(other.bookName)) {
            throw new IllegalArgumentException("같은 책이 아닙니다");
        }

        int chapterCompare = Integer.compare(this.chapter, other.chapter);
        if (chapterCompare != 0) {
            return chapterCompare;
        }

        return Integer.compare(this.verse, other.verse);
    }

    // 구절이 특정 범위에 포함되는지 확인
    public boolean isBetween(VerseReference start, VerseReference end) {
        if (!bookName.equals(start.bookName) || !bookName.equals(end.bookName)) {
            return false;
        }

        return compareVerse(start) >= 0 && compareVerse(end) <= 0;
    }
}
