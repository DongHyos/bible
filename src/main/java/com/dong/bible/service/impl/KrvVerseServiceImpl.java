package com.dong.bible.service.impl;

import com.dong.bible.web.dto.response.ChapterDto;
import com.dong.bible.web.dto.response.VerseDto;
import com.dong.bible.web.dto.response.VerseSearchResultDto;
import com.dong.bible.infrastructure.persistence.entity.KrvBook;
import com.dong.bible.infrastructure.persistence.entity.KrvVerse;
import com.dong.bible.mapstruct.KrvVerseMapper;
import com.dong.bible.infrastructure.persistence.jpa.KrvBookRepository;
import com.dong.bible.infrastructure.persistence.jpa.KrvVerseRepository;
import com.dong.bible.service.KrvVerseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KrvVerseServiceImpl implements KrvVerseService {
    
    private final KrvVerseRepository verseRepository;
    private final KrvBookRepository bookRepository;
    private final KrvVerseMapper verseMapper;

    // 특정 장의 모든 구절 조회
    @Override
    @Cacheable(value = "chapterVerses", key = "#bookId + '_' + #chapter")
    public ChapterDto getChapter(Integer bookId, Integer chapter) {
        KrvBook book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookId));
                
        List<KrvVerse> verses = verseRepository.findByBookIdAndChapterOrderByVerse(bookId, chapter);
        
        return verseMapper.toChapterDto(book, chapter, verses);
    }

    // 특정 구절 조회
    @Override
    public VerseDto getVerse(Integer bookId, Integer chapter, Integer verse) {
        KrvVerse verseEntity = verseRepository.findByBookIdAndChapterAndVerse(bookId, chapter, verse)
                .orElseThrow(() -> new IllegalArgumentException("Verse not found: " + bookId + ":" + chapter + ":" + verse));
                
        return verseMapper.toDto(verseEntity);
    }

    // 구절 범위 조회
    @Override
    public List<VerseDto> getVerseRange(Integer bookId, Integer chapter, Integer startVerse, Integer endVerse) {
        List<KrvVerse> verses = verseRepository.findByBookIdAndChapterAndVerseRange(bookId, chapter, startVerse, endVerse);
        return verseMapper.toDtoList(verses);
    }

    // 텍스트 검색
    @Override
    public List<VerseSearchResultDto> searchVerses(String keyword) {
        List<KrvVerse> verses = verseRepository.findByTextContaining(keyword);
        return verseMapper.toSearchResultDtoList(verses);
    }
}
