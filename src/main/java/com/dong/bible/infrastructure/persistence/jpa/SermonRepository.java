package com.dong.bible.infrastructure.persistence.jpa;

import com.dong.bible.infrastructure.persistence.entity.Sermon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SermonRepository extends JpaRepository<Sermon, Long> {

    // 특정 구절과 관련된 설교 조회
    @Query("SELECT s FROM Sermon s " +
           "WHERE s.id IN (" +
           "    SELECT DISTINCT sv.sermon.id FROM SermonVerse sv " +
           "    WHERE sv.book.id = :bookId " +
           "    AND sv.chapter = :chapter " +
           "    AND sv.verseStart <= :verse " +
           "    AND sv.verseEnd >= :verse" +
           ") " +
           "ORDER BY s.viewCount DESC, s.sermonDate DESC")
    List<Sermon> findByVerse(@Param("bookId") Integer bookId, 
                             @Param("chapter") Short chapter, 
                             @Param("verse") Short verse);

    // 설교자별 설교 조회
    List<Sermon> findByPastorNameContainingOrderBySermonDateDesc(String pastorName);

    // 교회별 설교 조회
    List<Sermon> findByChurchNameContainingOrderBySermonDateDesc(String churchName);

    // 제목으로 설교 검색
    List<Sermon> findByTitleContainingOrderByViewCountDesc(String title);

    // 인기 설교 조회 (조회수 기준)
    List<Sermon> findTop10ByOrderByViewCountDesc();

    // 최신 설교 조회
    List<Sermon> findTop10ByOrderBySermonDateDesc();
}