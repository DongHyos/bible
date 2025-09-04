package com.dong.bible.application.dto.command;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 구절 조회 요청 Command DTO
 * 
 * Web Layer에서 Application Layer로 전달되는 구절 조회 요청 정보
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VerseQueryCommand {
    
    private final Integer bookId;
    private final Integer chapter;
    private final Integer verse;
    
    /**
     * Command 생성
     */
    public static VerseQueryCommand of(Integer bookId, Integer chapter, Integer verse) {
        return new VerseQueryCommand(bookId, chapter, verse);
    }
    
    /**
     * 유효성 검증
     */
    public boolean isValid() {
        return bookId != null && bookId > 0 
            && chapter != null && chapter > 0 
            && verse != null && verse > 0;
    }
    
    /**
     * 디버깅용 toString
     */
    @Override
    public String toString() {
        return String.format("VerseQueryCommand{bookId=%d, chapter=%d, verse=%d}", 
                           bookId, chapter, verse);
    }
}