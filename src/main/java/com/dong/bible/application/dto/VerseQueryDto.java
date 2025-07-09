package com.dong.bible.application.dto;

import com.dong.bible.domain.verse.BibleVerse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 구절 조회 결과 DTO
 *
 * Application Layer에서 사용하는 DTO로,
 * 기존 VerseDto와 호환성을 유지하면서 도메인 중심 설계
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VerseQueryDto {

    private final Long id;
    private final Integer bookId; // 하위 호환성을 위해 유지 (추후 Book 도메인에서 관리)
    private final String bookName;
    private final String bookAbbr; // 책 약어 (추후 Book 도메인에서 관리)
    private final Integer chapter;
    private final Integer verse;
    private final String text;
    private final String reference; // "창세기 1:1" 형태

    /**
     * BibleVerse 도메인 객체로부터 DTO 생성
     */
    public static VerseQueryDto of(BibleVerse bibleVerse) {
        if (bibleVerse == null) {
            throw new IllegalArgumentException("BibleVerse cannot be null");
        }

        if (bibleVerse.getReference() == null) {
            throw new IllegalArgumentException("BibleVerse reference cannot be null");
        }

        if (bibleVerse.getContent() == null) {
            throw new IllegalArgumentException("BibleVerse content cannot be null");
        }

        return new VerseQueryDto(
                bibleVerse.getId(),
                extractBookId(bibleVerse), // 임시 로직
                bibleVerse.getReference().getBookName(),
                extractBookAbbr(bibleVerse.getReference().getBookName()), // 임시 로직
                bibleVerse.getReference().getChapter(),
                bibleVerse.getReference().getVerse(),
                bibleVerse.getContent().getText(),
                bibleVerse.toReferenceString()
        );
    }

    /**
     * bookId 추출 임시 로직 (추후 Book 도메인에서 관리)
     */
    private static Integer extractBookId(BibleVerse bibleVerse) {
        // 현재는 ID를 bookId로 사용 (임시)
        // 추후 Book 도메인 추가시 proper bookId 매핑 필요
        return bibleVerse.getId() != null ? bibleVerse.getId().intValue() : null;
    }

    /**
     * 책 약어 추출 임시 로직 (추후 Book 도메인에서 관리)
     */
    private static String extractBookAbbr(String bookName) {
        if (bookName == null) return "";

        return switch (bookName) {
            case "창세기" -> "창";
            case "출애굽기" -> "출";
            case "레위기" -> "레";
            case "민수기" -> "민";
            case "신명기" -> "신";
            case "마태복음" -> "마";
            case "마가복음" -> "막";
            case "누가복음" -> "눅";
            case "요한복음" -> "요";
            case "사도행전" -> "행";
            case "로마서" -> "롬";
            case "고린도전서" -> "고전";
            case "고린도후서" -> "고후";
            case "요한계시록" -> "계";
            default -> bookName.substring(0, Math.min(2, bookName.length()));
        };
    }

    /**
     * 디버깅용 toString
     */
    @Override
    public String toString() {
        return String.format("VerseQueryDto{reference='%s', text='%s'}",
                reference, text.substring(0, Math.min(20, text.length())) + "...");
    }
}