package com.dong.bible.application.service;

import com.dong.bible.application.dto.BookDto;
import com.dong.bible.application.dto.VerseQueryDto;
import com.dong.bible.domain.verse.BibleVerse;
import com.dong.bible.infrastructure.search.document.VerseSearchDocument;
import com.dong.bible.infrastructure.search.repository.VerseSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 성경 구절 인덱싱 Application Service
 * MySQL 데이터를 ElasticSearch로 인덱싱하는 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class VerseIndexingApplicationService {
    
    private final VerseApplicationService verseApplicationService;
    private final BookApplicationService bookApplicationService;
    private final VerseSearchRepository verseSearchRepository;
    
    /**
     * 전체 성경 구절 인덱싱
     * MySQL의 모든 구절 데이터를 ElasticSearch로 복사
     */
    @Transactional
    public void indexAllVerses() {
        log.info("전체 성경 구절 인덱싱 시작");
        
        try {
            // 1. 기존 인덱스 삭제
            verseSearchRepository.deleteAll();
            log.info("기존 ElasticSearch 인덱스 삭제 완료");
            
            // 2. 모든 책 조회
            List<BookDto> books = bookApplicationService.getAllBooks();
            log.info("총 {}개 책 조회 완료", books.size());
            
            int totalIndexed = 0;
            
            // 3. 각 책별로 구절 인덱싱
            for (BookDto book : books) {
                int bookIndexed = indexVersesByBook(book.getId().intValue(), book.getName());
                totalIndexed += bookIndexed;
                log.info("책 '{}' 인덱싱 완료: {}개 구절", book.getName(), bookIndexed);
            }
            
            log.info("전체 성경 구절 인덱싱 완료: 총 {}개 구절", totalIndexed);
            
        } catch (Exception e) {
            log.error("전체 인덱싱 중 오류 발생", e);
            throw new RuntimeException("인덱싱 작업 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * 특정 책의 모든 구절 인덱싱
     */
    @Transactional
    public int indexVersesByBook(Integer bookId, String bookName) {
        log.info("책별 인덱싱 시작: bookId={}, bookName={}", bookId, bookName);
        
        try {
            // 1. 해당 책의 모든 구절 조회
            List<VerseQueryDto> verses = verseApplicationService.getBookVerses(bookId);
            log.info("책 '{}'의 구절 {}개 조회 완료", bookName, verses.size());
            
            // 2. 각 구절을 ElasticSearch Document로 변환 및 저장
            int indexedCount = 0;
            for (VerseQueryDto verse : verses) {
                try {
                    indexSingleVerse(verse, bookId, bookName);
                    indexedCount++;
                } catch (Exception e) {
                    log.warn("구절 인덱싱 실패: bookId={}, chapter={}, verse={}, error={}", 
                            bookId, verse.getChapter(), verse.getVerse(), e.getMessage());
                }
            }
            
            log.info("책 '{}' 인덱싱 완료: {}/{}개 성공", bookName, indexedCount, verses.size());
            return indexedCount;
            
        } catch (Exception e) {
            log.error("책별 인덱싱 중 오류 발생: bookId={}, bookName={}", bookId, bookName, e);
            throw new RuntimeException("책별 인덱싱 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * 개별 구절 인덱싱
     */
    @Transactional
    public void indexSingleVerse(VerseQueryDto verse, Integer bookId, String bookName) {
        try {
            // 1. VerseDto를 BibleVerse Domain 객체로 변환 (임시)
            // TODO: 더 좋은 변환 방법 고려
            
            // 2. VerseSearchDocument 생성
            VerseSearchDocument document = VerseSearchDocument.builder()
                    .id(createId(bookId, verse.getChapter(), verse.getVerse()))
                    .bookId(bookId)
                    .bookName(bookName)
                    .chapter(verse.getChapter())
                    .verse(verse.getVerse())
                    .content(verse.getText())
                    .displayReference(bookName + " " + verse.getChapter() + ":" + verse.getVerse())
                    .build();
            
            // 3. ElasticSearch에 저장
            verseSearchRepository.save(document);
            
        } catch (Exception e) {
            log.error("개별 구절 인덱싱 실패: bookId={}, chapter={}, verse={}", 
                    bookId, verse.getChapter(), verse.getVerse(), e);
            throw e;
        }
    }
    
    /**
     * 인덱스 전체 삭제
     */
    @Transactional
    public void deleteAllIndex() {
        log.info("ElasticSearch 인덱스 전체 삭제 시작");
        
        try {
            verseSearchRepository.deleteAll();
            log.info("ElasticSearch 인덱스 전체 삭제 완료");
            
        } catch (Exception e) {
            log.error("인덱스 삭제 중 오류 발생", e);
            throw new RuntimeException("인덱스 삭제 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * 인덱스 상태 조회
     */
    public long getIndexedVerseCount() {
        try {
            long count = verseSearchRepository.count();
            log.info("현재 인덱싱된 구절 수: {}", count);
            return count;
            
        } catch (Exception e) {
            log.error("인덱스 상태 조회 중 오류 발생", e);
            return 0;
        }
    }
    
    /**
     * 복합 ID 생성 로직
     */
    private String createId(Integer bookId, Integer chapter, Integer verse) {
        return bookId + ":" + chapter + ":" + verse;
    }
}