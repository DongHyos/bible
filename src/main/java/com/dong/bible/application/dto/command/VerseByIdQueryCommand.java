package com.dong.bible.application.dto.command;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ID 기반 구절 조회 요청 Command DTO
 * 
 * Web Layer에서 Application Layer로 전달되는 ID 기반 구절 조회 요청 정보
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VerseByIdQueryCommand {
    
    private final Long id;
    
    /**
     * Command 생성
     */
    public static VerseByIdQueryCommand of(Long id) {
        return new VerseByIdQueryCommand(id);
    }
    
    /**
     * 유효성 검증
     */
    public boolean isValid() {
        return id != null && id > 0;
    }
    
    /**
     * 디버깅용 toString
     */
    @Override
    public String toString() {
        return String.format("VerseByIdQueryCommand{id=%d}", id);
    }
}