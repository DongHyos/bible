package com.dong.bible.application.dto.query;

import com.dong.bible.domain.verse.BibleVerse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 구절 검색 결과 DTO
 *
 * Application Layer에서 사용하는 DTO로,
 * 키워드 기반 구절 검색 결과를 담음
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VerseSearchQuery {

    private final String keyword;
    private final Integer totalCount;
    private final List<VerseQuery> verses;
    private final Map<String, Integer> bookCountMap; // 책별 검색 결과 수

    /**
     * 검색 키워드와 BibleVerse 리스트로부터 DTO 생성
     */
    public static VerseSearchQuery of(String keyword, List<BibleVerse> bibleVerses) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Search keyword cannot be empty");
        }

        if (bibleVerses == null) {
            bibleVerses = List.of();
        }

        // BibleVerse → VerseQuery 변환
        List<VerseQuery> verseDtos = bibleVerses.stream()
                .map(VerseQuery::of)
                .collect(Collectors.toList());

        // 책별 검색 결과 수 계산
        Map<String, Integer> bookCountMap = verseDtos.stream()
                .collect(Collectors.groupingBy(
                        VerseQuery::getBookName,
                        Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)
                ));

        return new VerseSearchQuery(
                keyword.trim(),
                verseDtos.size(),
                verseDtos,
                bookCountMap
        );
    }

    /**
     * 검색 결과가 있는지 확인
     */
    public boolean hasResults() {
        return totalCount > 0;
    }

    /**
     * 특정 책의 검색 결과 수 조회
     */
    public Integer getCountForBook(String bookName) {
        return bookCountMap.getOrDefault(bookName, 0);
    }

    /**
     * 검색된 책 목록 조회
     */
    public List<String> getSearchedBooks() {
        return List.copyOf(bookCountMap.keySet());
    }

    /**
     * 디버깅용 toString
     */
    @Override
    public String toString() {
        return String.format("VerseSearchQuery{keyword='%s', totalCount=%d, books=%d}",
                keyword, totalCount, bookCountMap.size());
    }
}