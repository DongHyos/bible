package com.dong.bible.web.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 구절 범위 조회 응답 DTO
 *
 * Web Layer에서 사용하는 HTTP 응답 전용 DTO
 * GET /verses?bookId=1&chapter=1&fromVerse=1&toVerse=5 응답용
 */
@Data
@Builder
public class VerseRangeResponseDto {
    private Integer bookId;
    private String bookName;
    private String bookAbbr;
    private Integer chapter;
    private Integer startVerse;
    private Integer endVerse;
    private Integer totalCount;        // 실제 조회된 구절 수
    private String rangeReference;     // "창세기 1:1-5" 형태
    private List<VerseDto> verses;     // 구절 목록 (기존 VerseDto 재사용)

    /**
     * 요청한 범위가 완전히 조회되었는지 확인
     */
    public boolean isCompleteRange() {
        if (startVerse == null || endVerse == null || totalCount == null) {
            return false;
        }
        int expectedCount = endVerse - startVerse + 1;
        return totalCount == expectedCount;
    }

    /**
     * 범위가 유효한지 검증
     */
    public boolean isValidRange() {
        return startVerse != null && endVerse != null && startVerse <= endVerse;
    }
}
