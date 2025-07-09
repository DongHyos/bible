package com.dong.bible.domain.book;

import com.dong.bible.ENUM.Testament;

import java.util.List;
import java.util.Optional;

/**
 * 성경책 도메인을 위한 Repository Interface (순수 DDD)
 * Infrastructure 세부사항(ID, 순서 등)을 완전 배제하고 도메인 언어만 사용
 */
public interface BookRepository {
    
    /**
     * 성경책 이름으로 조회 (도메인 중심)
     */
    Optional<Book> findByName(BookName bookName);
    
    /**
     * 성경책 이름으로 조회 (문자열)
     */
    Optional<Book> findByName(String name);
    
    /**
     * 신구약별 성경책 목록 조회
     */
    List<Book> findByTestament(Testament testament);
    
    /**
     * 모든 성경책 조회 (도메인 순서대로)
     */
    List<Book> findAll();
    
    /**
     * 구약 성경책 목록 조회
     */
    List<Book> findOldTestamentBooks();
    
    /**
     * 신약 성경책 목록 조회
     */
    List<Book> findNewTestamentBooks();
    
    /**
     * 성경책 존재 여부 확인
     */
    boolean existsByName(BookName bookName);
    
    /**
     * 전체 성경책 개수
     */
    long count();
}
