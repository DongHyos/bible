package com.dong.bible.application.dto;

import com.dong.bible.domain.verse.BibleVerse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 장 조회 결과 DTO
 *
 * Application Layer에서 사용하는 DTO로,
 * 기존 ChapterDto와 호환성을 유지하면서 도메인 중심 설계
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChapterQueryDto {

    private final Integer bookId;
    private final String bookName;
    private final String bookAbbr;
    private final Integer chapter;
    private final Integer totalVerses;
    private final List<VerseQueryDto> verses;

    /**
     * BibleVerse 리스트로부터 ChapterQueryDto 생성
     */
    public static ChapterQueryDto of(Integer bookId, String bookName, Integer chapter, List<BibleVerse> bibleVerses) {
        if (bibleVerses == null || bibleVerses.isEmpty()) {
            throw new IllegalArgumentException("Verses cannot be empty for chapter");
        }

        // 첫 번째 구절에서 책 정보 추출 (모든 구절이 같은 책이므로)
        BibleVerse firstVerse = bibleVerses.get(0);
        String extractedBookName = firstVerse.getReference().getBookName();

        // bookName 파라미터와 일치성 검증
        if (bookName != null && !bookName.equals(extractedBookName)) {
            throw new IllegalArgumentException("Book name mismatch: expected=" + bookName + ", actual=" + extractedBookName);
        }

        // BibleVerse → VerseQueryDto 변환
        List<VerseQueryDto> verseDtos = bibleVerses.stream()
                .map(VerseQueryDto::of)
                .collect(Collectors.toList());

        return new ChapterQueryDto(
                bookId,
                extractedBookName,
                extractBookAbbr(extractedBookName), // 임시 로직
                chapter,
                verseDtos.size(),
                verseDtos
        );
    }

    /**
     * 간단한 팩토리 메서드 (bookId와 bookName을 도메인에서 추출)
     */
    public static ChapterQueryDto of(Integer chapter, List<BibleVerse> bibleVerses) {
        if (bibleVerses == null || bibleVerses.isEmpty()) {
            throw new IllegalArgumentException("Verses cannot be empty for chapter");
        }

        BibleVerse firstVerse = bibleVerses.get(0);
        Integer extractedBookId = firstVerse.getId() != null ? firstVerse.getId().intValue() : null; // 임시
        String bookName = firstVerse.getReference().getBookName();

        return of(extractedBookId, bookName, chapter, bibleVerses);
    }

    /**
     * 책 약어 추출 임시 로직 (VerseQueryDto와 동일)
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
        return String.format("ChapterQueryDto{book='%s %d', totalVerses=%d}",
                bookName, chapter, totalVerses);
    }
}
