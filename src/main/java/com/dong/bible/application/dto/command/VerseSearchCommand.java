package com.dong.bible.application.dto.command;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 구절 검색 요청 Command DTO
 * 
 * Web Layer에서 Application Layer로 전달되는 검색 요청 정보
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VerseSearchCommand {
    
    private final String keyword;
    private final Integer bookId; // 선택: 특정 책에서만 검색
    private final String testament; // 선택: 구약/신약에서만 검색
    private final Integer page;
    private final Integer size;
    private final String sortBy; // bookOrder, relevance
    private final String sortDirection; // asc, desc
    
    /**
     * 기본 검색 Command 생성 (키워드만)
     */
    public static VerseSearchCommand of(String keyword) {
        return new VerseSearchCommand(
            keyword,
            null, // bookId
            null, // testament  
            0,    // page
            20,   // size
            "bookOrder", // sortBy
            "asc" // sortDirection
        );
    }
    
    /**
     * 상세 검색 Command 생성
     */
    public static VerseSearchCommand of(String keyword, Integer bookId, String testament, 
                                       Integer page, Integer size, String sortBy, String sortDirection) {
        return new VerseSearchCommand(keyword, bookId, testament, page, size, sortBy, sortDirection);
    }
    
    /**
     * Web Request DTO로부터 Command 생성
     */
    public static VerseSearchCommand fromWebRequest(String keyword, Integer bookId, String testament,
                                                   Integer page, Integer size, String sortBy, String sortDirection) {
        return new VerseSearchCommand(
            keyword,
            bookId,
            testament,
            page != null ? page : 0,
            size != null ? size : 20,
            sortBy != null ? sortBy : "bookOrder",
            sortDirection != null ? sortDirection : "asc"
        );
    }
    
    /**
     * 키워드 유효성 검증
     */
    public boolean hasValidKeyword() {
        return keyword != null && !keyword.trim().isEmpty();
    }
    
    /**
     * 책 필터링 여부 확인
     */
    public boolean hasBookFilter() {
        return bookId != null;
    }
    
    /**
     * 신구약 필터링 여부 확인  
     */
    public boolean hasTestamentFilter() {
        return testament != null && !testament.trim().isEmpty();
    }
}