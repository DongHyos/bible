package com.dong.bible.infrastructure.persistence.jpa;

import com.dong.bible.ENUM.Testament;
import com.dong.bible.infrastructure.persistence.entity.BibleCategory;
import com.dong.bible.infrastructure.persistence.entity.KrvBook;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KrvBookRepository extends JpaRepository<KrvBook, Integer> {

    // 구약/신약별 조회
    List<KrvBook> findByTestamentOrderByBookOrder(Testament testament);

    // 전체 성경 목록 (순서대로)
    List<KrvBook> findAllByOrderByBookOrder();

    // 성경명으로 조회
    Optional<KrvBook> findByName(String name);
    Optional<KrvBook> findByAbbr(String abbr);

    // 구약/신약별 개수
    @Query("SELECT b.testament, COUNT(b) FROM KrvBook b GROUP BY b.testament")
    List<Object[]> countByTestament();

    // 분류별 책 조회
    List<KrvBook> findByCategoryOrderByBookOrder(BibleCategory category);

    // 분류 ID로 책 조회
    @Query("SELECT b FROM KrvBook b WHERE b.category.id = :categoryId ORDER BY b.bookOrder")
    List<KrvBook> findByCategoryIdOrderByBookOrder(@Param("categoryId") Integer categoryId);

    // 분류 포함 전체 조회
    @Query("SELECT b FROM KrvBook b JOIN FETCH b.category ORDER BY b.bookOrder")
    List<KrvBook> findAllWithCategory();
}
