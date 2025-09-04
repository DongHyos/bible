package com.dong.bible.application.dto.command;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 구절 범위 조회 요청 Command DTO
 * 
 * Web Layer에서 Application Layer로 전달되는 구절 범위 조회 요청 정보
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VerseRangeQueryCommand {
    
    private final Integer bookId;
    private final Integer chapter;
    private final Integer fromVerse;
    private final Integer toVerse;
    
    /**
     * Command 생성
     */
    public static VerseRangeQueryCommand of(Integer bookId, Integer chapter, Integer fromVerse, Integer toVerse) {
        return new VerseRangeQueryCommand(bookId, chapter, fromVerse, toVerse);
    }
    
    /**
     * 유효성 검증
     */
    public boolean isValid() {
        return bookId != null && bookId > 0 
            && chapter != null && chapter > 0 
            && fromVerse != null && fromVerse > 0
            && toVerse != null && toVerse > 0
            && fromVerse <= toVerse;
    }
    
    /**
     * 범위 크기 계산
     */
    public int getRangeSize() {
        if (!isValid()) {
            return 0;
        }
        return toVerse - fromVerse + 1;
    }
    
    /**
     * 디버깅용 toString
     */
    @Override
    public String toString() {
        return String.format("VerseRangeQueryCommand{bookId=%d, chapter=%d, verses=%d-%d}", 
                           bookId, chapter, fromVerse, toVerse);
    }
}