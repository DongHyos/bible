package com.dong.bible.web.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DailyVerseSimpleDto {
    
    private Long id;
    private LocalDate verseDate;
    private Integer bookId; // 책 ID 추가
    private String bookName;
    private String bookAbbr;
    private Short chapter;
    private Short verseStart;
    private Short verseEnd;
    private String title;
    
    // 구절 참조 형태로 표시 (예: "빌립보서 4:13")
    public String getVerseReference() {
        if (verseStart.equals(verseEnd)) {
            return String.format("%s %d:%d", bookName, chapter.intValue(), verseStart.intValue());
        } else {
            return String.format("%s %d:%d-%d", bookName, chapter.intValue(), verseStart.intValue(), verseEnd.intValue());
        }
    }
}