package com.dong.bible.infrastructure.persistence.jpa;

import com.dong.bible.infrastructure.persistence.entity.KrvBookEntity;
import com.dong.bible.infrastructure.persistence.entity.KrvVerseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KrvVerseJpaRepository extends JpaRepository<KrvVerseEntity, Integer> {
    
    // 특정 책의 특정 장 모든 구절 조회
    List<KrvVerseEntity> findByBookAndChapterOrderByVerse(KrvBookEntity book, Integer chapter);
    
    // 책 ID로 특정 장 모든 구절 조회
    @Query("SELECT v FROM KrvVerseEntity v WHERE v.book.id = :bookId AND v.chapter = :chapter ORDER BY v.verse")
    List<KrvVerseEntity> findByBookIdAndChapterOrderByVerse(@Param("bookId") Integer bookId, @Param("chapter") Integer chapter);
    
    // 특정 구절 조회
    Optional<KrvVerseEntity> findByBookAndChapterAndVerse(KrvBookEntity book, Integer chapter, Integer verse);
    
    // 책 ID로 특정 구절 조회
    @Query("SELECT v FROM KrvVerseEntity v WHERE v.book.id = :bookId AND v.chapter = :chapter AND v.verse = :verse")
    Optional<KrvVerseEntity> findByBookIdAndChapterAndVerse(@Param("bookId") Integer bookId, @Param("chapter") Integer chapter, @Param("verse") Integer verse);
    
    // 텍스트 검색 (LIKE)
    @Query("SELECT v FROM KrvVerseEntity v JOIN v.book b WHERE v.text LIKE %:keyword% ORDER BY b.bookOrder, v.chapter, v.verse")
    List<KrvVerseEntity> findByTextContaining(@Param("keyword") String keyword);
    
    // 전문 검색 (FULLTEXT) - MySQL 전용
    @Query(value = "SELECT * FROM krv_verses v JOIN krv_books b ON v.book_id = b.id WHERE MATCH(v.text) AGAINST(:keyword IN NATURAL LANGUAGE MODE) ORDER BY b.book_order, v.chapter, v.verse", nativeQuery = true)
    List<KrvVerseEntity> findByTextFullTextSearch(@Param("keyword") String keyword);
    
    // 특정 책의 총 구절 수
    @Query("SELECT COUNT(v) FROM KrvVerseEntity v WHERE v.book.id = :bookId")
    Long countByBookId(@Param("bookId") Integer bookId);
    
    // 특정 장의 총 구절 수
    @Query("SELECT COUNT(v) FROM KrvVerseEntity v WHERE v.book.id = :bookId AND v.chapter = :chapter")
    Long countByBookIdAndChapter(@Param("bookId") Integer bookId, @Param("chapter") Integer chapter);
    
    // 구절 범위 조회 (예: 1장 1-5절)
    @Query("SELECT v FROM KrvVerseEntity v WHERE v.book.id = :bookId AND v.chapter = :chapter AND v.verse BETWEEN :startVerse AND :endVerse ORDER BY v.verse")
    List<KrvVerseEntity> findByBookIdAndChapterAndVerseRange(@Param("bookId") Integer bookId, @Param("chapter") Integer chapter, @Param("startVerse") Integer startVerse, @Param("endVerse") Integer endVerse);
    
    // 특정 책의 모든 장 목록 조회
    @Query("SELECT DISTINCT v.chapter FROM KrvVerseEntity v WHERE v.book.id = :bookId ORDER BY v.chapter")
    List<Integer> findChaptersByBookId(@Param("bookId") Integer bookId);
    
    // 특정 책의 특정 장에서 최대 구절 번호
    @Query("SELECT MAX(v.verse) FROM KrvVerseEntity v WHERE v.book.id = :bookId AND v.chapter = :chapter")
    Integer findMaxVerseByBookIdAndChapter(@Param("bookId") Integer bookId, @Param("chapter") Integer chapter);

    // 특정 책의 모든 구절 조회 (장:절 순서대로)
    @Query("SELECT v FROM KrvVerseEntity v WHERE v.book.id = :bookId ORDER BY v.chapter ASC, v.verse ASC")
    List<KrvVerseEntity> findByBookIdOrderByChapterAscVerseAsc(@Param("bookId") Integer bookId);

    // 신구약별 모든 구절 조회 (책 순서 → 장:절 순서대로)
    @Query("SELECT v FROM KrvVerseEntity v JOIN v.book b WHERE b.testament = :testament ORDER BY b.bookOrder ASC, v.chapter ASC, v.verse ASC")
    List<KrvVerseEntity> findByBookTestamentOrderByBookBookOrderAscChapterAscVerseAsc(@Param("testament") com.dong.bible.ENUM.Testament testament);
}