package com.dong.bible.infrastructure.persistence.jpa;

import com.dong.bible.infrastructure.persistence.entity.SermonVerseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SermonVerseRepository extends JpaRepository<SermonVerseEntity, Long> {

    // 특정 설교의 모든 구절 조회 (명시적 쿼리 사용)
    @Query("SELECT sv FROM SermonVerseEntity sv " +
           "JOIN FETCH sv.book b " +
           "WHERE sv.sermon.id = :sermonId " +
           "ORDER BY sv.isMainText DESC, b.bookOrder ASC, sv.chapter ASC, sv.verseStart ASC")
    List<SermonVerseEntity> findBySermonIdWithBook(@Param("sermonId") Long sermonId);

    // 가장 많이 언급되는 구절 TOP 조회
    @Query("SELECT b.name, sv.chapter, sv.verseStart, sv.verseEnd, COUNT(sv) as count " +
           "FROM SermonVerseEntity sv " +
           "JOIN sv.book b " +
           "GROUP BY b.name, sv.chapter, sv.verseStart, sv.verseEnd " +
           "ORDER BY COUNT(sv) DESC")
    List<Object[]> findMostReferencedVerses();
}