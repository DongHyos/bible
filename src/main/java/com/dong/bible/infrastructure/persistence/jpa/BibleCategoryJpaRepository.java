package com.dong.bible.infrastructure.persistence.jpa;

import com.dong.bible.ENUM.Testament;
import com.dong.bible.infrastructure.persistence.entity.BibleCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BibleCategoryJpaRepository extends JpaRepository<BibleCategoryEntity, Integer> {

    // 구약/신약별 분류 조회
    List<BibleCategoryEntity> findByTestamentOrderByCategoryOrder(Testament testament);

    // 전체 분류 조회
    List<BibleCategoryEntity> findAllByOrderByCategoryOrder();

    // 분류별 책 수 포함 조회
    @Query("SELECT c, COUNT(b) as bookCount FROM BibleCategoryEntity c LEFT JOIN c.books b GROUP BY c ORDER BY c.categoryOrder")
    List<Object[]> findAllWithBookCount();
}
