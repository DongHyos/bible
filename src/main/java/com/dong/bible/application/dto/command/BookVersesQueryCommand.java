package com.dong.bible.application.dto.command;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 책의 모든 구절 조회 요청 Command DTO
 * 
 * Web Layer에서 Application Layer로 전달되는 책별 구절 조회 요청 정보
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BookVersesQueryCommand {
    
    private final Integer bookId;
    
    /**
     * Command 생성
     */
    public static BookVersesQueryCommand of(Integer bookId) {
        return new BookVersesQueryCommand(bookId);
    }
    
    /**
     * 유효성 검증
     */
    public boolean isValid() {
        return bookId != null && bookId > 0;
    }
    
    /**
     * 디버깅용 toString
     */
    @Override
    public String toString() {
        return String.format("BookVersesQueryCommand{bookId=%d}", bookId);
    }
}