package com.dong.bible.web.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class DailyVerseSimpleDto {
    
    private Long id;
    private LocalDate verseDate;
    private Integer bookId; // 책 ID 추가
    private String bookName;
    private String bookAbbr;
    private Integer chapter;      // Short → Integer 변경
    private Integer verseStart;   // Short → Integer 변경
    private Integer verseEnd;     // Short → Integer 변경
    private String title;
    private Boolean isActive;
    
    // 구절 참조 형태로 표시 (예: "빌립보서 4:13")
    public String getVerseReference() {
        if (verseStart.equals(verseEnd)) {
            return String.format("%s %d:%d", bookName, chapter, verseStart);
        } else {
            return String.format("%s %d:%d-%d", bookName, chapter, verseStart, verseEnd);
        }
    }
}
