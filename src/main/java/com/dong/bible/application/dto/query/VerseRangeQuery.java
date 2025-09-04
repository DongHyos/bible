package com.dong.bible.application.dto.query;

import com.dong.bible.domain.verse.BibleVerse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 구절 범위 조회 결과 DTO
 *
 * Application Layer에서 사용하는 DTO로,
 * 특정 장의 구절 범위 조회 결과를 담음
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VerseRangeQuery {

    private final Integer bookId;
    private final String bookName;
    private final String bookAbbr;
    private final Integer chapter;
    private final Integer startVerse;
    private final Integer endVerse;
    private final Integer actualCount; // 실제 조회된 구절 수
    private final String rangeReference; // "창세기 1:1-5" 형태
    private final List<VerseQuery> verses;

    /**
     * BibleVerse 리스트와 범위 정보로부터 DTO 생성
     */
    public static VerseRangeQuery of(Integer bookId, Integer chapter, Integer startVerse, Integer endVerse, List<BibleVerse> bibleVerses) {
        if (startVerse == null || endVerse == null || chapter == null) {
            throw new IllegalArgumentException("Chapter and verse range cannot be null");
        }

        if (startVerse > endVerse) {
            throw new IllegalArgumentException("Start verse cannot be greater than end verse");
        }

        // 빈 결과도 허용 (해당 범위에 구절이 없을 수 있음)
        if (bibleVerses == null) {
            bibleVerses = List.of();
        }

        String bookName = null;
        String rangeReference = "";

        if (!bibleVerses.isEmpty()) {
            // 첫 번째 구절에서 책 정보 추출
            BibleVerse firstVerse = bibleVerses.get(0);
            bookName = firstVerse.getReference().getBookName();
            rangeReference = bookName + " " + chapter + ":" + startVerse + "-" + endVerse;
        } else {
            // 구절이 없어도 범위 정보는 표시
            rangeReference = "Unknown " + chapter + ":" + startVerse + "-" + endVerse;
        }

        // BibleVerse → VerseQuery 변환
        List<VerseQuery> verseDtos = bibleVerses.stream()
                .map(VerseQuery::of)
                .collect(Collectors.toList());

        return new VerseRangeQuery(
                bookId,
                bookName,
                bookName != null ? extractBookAbbr(bookName) : "",
                chapter,
                startVerse,
                endVerse,
                verseDtos.size(),
                rangeReference,
                verseDtos
        );
    }

    /**
     * 도메인에서 범위 정보를 추출하는 팩토리 메서드
     */
    public static VerseRangeQuery of(Integer startVerse, Integer endVerse, List<BibleVerse> bibleVerses) {
        if (bibleVerses == null || bibleVerses.isEmpty()) {
            throw new IllegalArgumentException("Cannot extract range info from empty verse list");
        }

        BibleVerse firstVerse = bibleVerses.get(0);
        Integer bookId = firstVerse.getId() != null ? firstVerse.getId().intValue() : null; // 임시
        Integer chapter = firstVerse.getReference().getChapter();

        return of(bookId, chapter, startVerse, endVerse, bibleVerses);
    }

    /**
     * 책 약어 추출 임시 로직
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
     * 범위가 유효한지 검증
     */
    public boolean isValidRange() {
        return startVerse != null && endVerse != null && startVerse <= endVerse;
    }

    /**
     * 요청한 범위와 실제 조회된 구절 수가 일치하는지 확인
     */
    public boolean isCompleteRange() {
        if (!isValidRange()) return false;
        int expectedCount = endVerse - startVerse + 1;
        return actualCount == expectedCount;
    }

    /**
     * 디버깅용 toString
     */
    @Override
    public String toString() {
        return String.format("VerseRangeQuery{range='%s', actualCount=%d/%d}",
                rangeReference, actualCount, (endVerse - startVerse + 1));
    }
}
