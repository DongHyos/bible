package com.dong.bible.application.service;

import com.dong.bible.application.dto.VerseSearchResultDto;
import com.dong.bible.infrastructure.search.document.VerseSearchDocument;
import com.dong.bible.infrastructure.search.repository.VerseSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 성경 구절 검색 Application Service
 * ElasticSearch를 활용한 검색 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VerseSearchApplicationService {
    
    private final VerseSearchRepository verseSearchRepository;
    
    /**
     * 구절 내용으로 검색
     */
    public List<VerseSearchResultDto> searchByContent(String keyword) {
        log.info("구절 내용 검색 시작: keyword={}", keyword);
        
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("검색 키워드는 비어있을 수 없습니다");
        }
        
        List<VerseSearchDocument> searchResults = verseSearchRepository.findByContentContaining(keyword.trim());
        log.info("검색 결과: {}개", searchResults.size());
        
        return searchResults.stream()
                .map(VerseSearchResultDto::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 책 이름으로 검색
     */
    public List<VerseSearchResultDto> searchByBookName(String bookName) {
        log.info("책 이름 검색 시작: bookName={}", bookName);
        
        if (bookName == null || bookName.trim().isEmpty()) {
            throw new IllegalArgumentException("책 이름은 비어있을 수 없습니다");
        }
        
        List<VerseSearchDocument> searchResults = verseSearchRepository.findByBookName(bookName.trim());
        log.info("검색 결과: {}개", searchResults.size());
        
        return searchResults.stream()
                .map(VerseSearchResultDto::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 특정 책의 특정 장에서 검색
     */
    public List<VerseSearchResultDto> searchByBookAndChapter(Integer bookId, Integer chapter) {
        log.info("책별 장별 검색 시작: bookId={}, chapter={}", bookId, chapter);
        
        if (bookId == null || bookId <= 0) {
            throw new IllegalArgumentException("올바른 책 ID를 입력해주세요");
        }
        if (chapter == null || chapter <= 0) {
            throw new IllegalArgumentException("올바른 장 번호를 입력해주세요");
        }
        
        List<VerseSearchDocument> searchResults = verseSearchRepository.findByBookIdAndChapter(bookId, chapter);
        log.info("검색 결과: {}개", searchResults.size());
        
        return searchResults.stream()
                .map(VerseSearchResultDto::from)
                .collect(Collectors.toList());
    }
}