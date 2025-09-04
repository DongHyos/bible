package com.dong.bible.application.dto.command;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 장 조회 요청 Command DTO
 * 
 * Web Layer에서 Application Layer로 전달되는 장 조회 요청 정보
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChapterQueryCommand {
    
    private final Integer bookId;
    private final Integer chapter;
    
    /**
     * Command 생성
     */
    public static ChapterQueryCommand of(Integer bookId, Integer chapter) {
        return new ChapterQueryCommand(bookId, chapter);
    }
    
    /**
     * 유효성 검증
     */
    public boolean isValid() {
        return bookId != null && bookId > 0 && chapter != null && chapter > 0;
    }
    
    /**
     * 디버깅용 toString
     */
    @Override
    public String toString() {
        return String.format("ChapterQueryCommand{bookId=%d, chapter=%d}", bookId, chapter);
    }
}