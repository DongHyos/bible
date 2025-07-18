package com.dong.bible.domain.category;

import com.dong.bible.ENUM.Testament;

import java.util.List;
import java.util.Optional;

/**
 * 성경 분류 도메인을 위한 Repository Interface (순수 DDD)
 * Infrastructure 세부사항을 완전 배제하고 도메인 언어만 사용
 */
public interface BibleCategoryRepository {
    
    /**
     * ID로 분류 조회
     */
    Optional<BibleCategory> findById(Integer id);
    
    /**
     * 분류 이름으로 조회 (도메인 언어)
     */
    Optional<BibleCategory> findByName(String name);
    
    /**
     * 신구약별 분류 목록 조회 (순서대로)
     */
    List<BibleCategory> findByTestamentOrderByCategoryOrder(Testament testament);
    
    /**
     * 모든 분류 조회 (순서대로)
     */
    List<BibleCategory> findAllByOrderByCategoryOrder();
    
    /**
     * 특정 신구약의 분류 개수
     */
    long countByTestament(Testament testament);
    
    /**
     * 전체 분류 개수
     */
    long count();
    
    /**
     * 분류 존재 여부 확인 (이름으로)
     */
    boolean existsByName(String name);
    
    /**
     * 분류 존재 여부 확인 (ID로)
     */
    boolean existsById(Integer id);
    
    /**
     * 분류 저장 (생성/수정)
     */
    BibleCategory save(BibleCategory category);
    
    /**
     * 분류 삭제 (도메인 객체로)
     */
    void delete(BibleCategory category);
}