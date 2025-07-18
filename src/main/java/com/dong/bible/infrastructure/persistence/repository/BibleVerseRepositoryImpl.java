package com.dong.bible.infrastructure.persistence.repository;

import com.dong.bible.ENUM.Testament;
import com.dong.bible.domain.verse.BibleVerse;
import com.dong.bible.domain.verse.BibleVerseRepository;
import com.dong.bible.domain.verse.VerseReference;
import com.dong.bible.infrastructure.persistence.entity.KrvBookEntity;
import com.dong.bible.infrastructure.persistence.entity.KrvVerseEntity;
import com.dong.bible.infrastructure.persistence.mapper.VerseMapper;
import com.dong.bible.infrastructure.persistence.jpa.KrvBookJpaRepository;
import com.dong.bible.infrastructure.persistence.jpa.KrvVerseJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class BibleVerseRepositoryImpl implements BibleVerseRepository {
    private final KrvVerseJpaRepository jpaVerseRepository;  // JPA Repository
    private final KrvBookJpaRepository jpaBookRepository;       // 책 정보 조회용
    private final VerseMapper verseMapper;                   // Domain ↔ Entity 변환

    // === 단일 구절 조회 ===

    @Override
    public Optional<BibleVerse> findByReference(VerseReference reference) {
        log.debug("Finding verse by reference: {}", reference.toDisplayString());

        // 1. 책 정보 조회
        Optional<KrvBookEntity> bookOpt = jpaBookRepository.findByName(reference.getBookName());
        if (bookOpt.isEmpty()) {
            log.warn("Book not found: {}", reference.getBookName());
            return Optional.empty();
        }

        // 2. 구절 조회
        Optional<KrvVerseEntity> verseOpt = jpaVerseRepository.findByBookIdAndChapterAndVerse(
                bookOpt.get().getId(),
                reference.getChapter(),
                reference.getVerse()
        );

        return verseOpt.map(verseMapper::toDomain);
    }

    @Override
    public Optional<BibleVerse> findById(Long id) {
        log.debug("Finding verse by id: {}", id);

        return jpaVerseRepository.findById(id.intValue())
                .map(verseMapper::toDomain);
    }

    // === 다중 구절 조회 ===

    @Override
    public List<BibleVerse> findByChapter(String bookName, Integer chapter) {
        log.debug("Finding verses by chapter: {} {}", bookName, chapter);

        // 1. 책 정보 조회
        Optional<KrvBookEntity> bookOpt = jpaBookRepository.findByName(bookName);
        if (bookOpt.isEmpty()) {
            log.warn("Book not found: {}", bookName);
            return List.of();
        }

        // 2. 해당 장의 모든 구절 조회 (구절 순서대로 정렬)
        List<KrvVerseEntity> verses = jpaVerseRepository.findByBookIdAndChapterOrderByVerse(
                bookOpt.get().getId(),
                chapter
        );

        return verseMapper.toDomainList(verses);
    }

    @Override
    public List<BibleVerse> findByChapterRange(String bookName, Integer chapter, Integer startVerse, Integer endVerse) {
        log.debug("Finding verses by chapter range: {} {}:{}-{}", bookName, chapter, startVerse, endVerse);

        // 1. 책 정보 조회
        Optional<KrvBookEntity> bookOpt = jpaBookRepository.findByName(bookName);
        if (bookOpt.isEmpty()) {
            log.warn("Book not found: {}", bookName);
            return List.of();
        }

        // 2. 구절 범위 조회 (기존 Repository 메서드 활용)
        List<KrvVerseEntity> verses = jpaVerseRepository.findByBookIdAndChapterAndVerseRange(
                bookOpt.get().getId(),
                chapter,
                startVerse,
                endVerse
        );

        return verseMapper.toDomainList(verses);
    }

    @Override
    public List<BibleVerse> findByBook(String bookName) {
        log.debug("Finding all verses by book: {}", bookName);

        // 1. 책 정보 조회
        Optional<KrvBookEntity> bookOpt = jpaBookRepository.findByName(bookName);
        if (bookOpt.isEmpty()) {
            log.warn("Book not found: {}", bookName);
            return List.of();
        }

        // 2. 해당 책의 모든 구절 조회 (장:절 순서대로 정렬)
        List<KrvVerseEntity> verses = jpaVerseRepository.findByBookIdOrderByChapterAscVerseAsc(
                bookOpt.get().getId()
        );

        return verseMapper.toDomainList(verses);
    }

    @Override
    public List<BibleVerse> findByTestament(boolean isNewTestament) {
        log.debug("Finding verses by testament: {}", isNewTestament ? "NEW" : "OLD");

        // Testament enum 활용
        Testament testament = isNewTestament ? Testament.신약 : Testament.구약;

        List<KrvVerseEntity> verses = jpaVerseRepository.findByBookTestamentOrderByBookBookOrderAscChapterAscVerseAsc(testament);

        return verseMapper.toDomainList(verses);
    }

    // === 저장/삭제 ===

    @Override
    public BibleVerse save(BibleVerse verse) {
        log.debug("Saving verse: {}", verse.toReferenceString());

        // 1. 책 정보 조회
        Optional<KrvBookEntity> bookOpt = jpaBookRepository.findByName(verse.getReference().getBookName());
        if (bookOpt.isEmpty()) {
            throw new IllegalArgumentException("Book not found: " + verse.getReference().getBookName());
        }

        // 2. Domain → Entity 변환 (book 전달)
        KrvVerseEntity entity = verseMapper.toEntity(verse, bookOpt.get());

        // 3. 저장
        KrvVerseEntity savedEntity = jpaVerseRepository.save(entity);

        // 4. Entity → Domain 변환하여 반환
        return verseMapper.toDomain(savedEntity);
    }

    @Override
    public List<BibleVerse> saveAll(List<BibleVerse> verses) {
        log.debug("Saving {} verses", verses.size());

        return verses.stream()
                .map(this::save)
                .toList();
    }

    @Override
    public void delete(BibleVerse verse) {
        if (verse.getId() == null) {
            throw new IllegalArgumentException("Cannot delete verse without ID");
        }

        log.debug("Deleting verse: {}", verse.toReferenceString());
        jpaVerseRepository.deleteById(verse.getId().intValue());
    }

    @Override
    public void deleteById(Long id) {
        log.debug("Deleting verse by id: {}", id);
        jpaVerseRepository.deleteById(id.intValue());
    }

    // === 존재 여부 확인 ===

    @Override
    public boolean existsByReference(VerseReference reference) {
        // findByReference를 재활용
        return findByReference(reference).isPresent();
    }

    @Override
    public boolean existsById(Long id) {
        return jpaVerseRepository.existsById(id.intValue());
    }

    // === 개수 조회 ===

    @Override
    public long countByChapter(String bookName, Integer chapter) {
        log.debug("Counting verses by chapter: {} {}", bookName, chapter);

        Optional<KrvBookEntity> bookOpt = jpaBookRepository.findByName(bookName);
        if (bookOpt.isEmpty()) {
            return 0;
        }

        return jpaVerseRepository.countByBookIdAndChapter(bookOpt.get().getId(), chapter);
    }

    @Override
    public long countByBook(String bookName) {
        log.debug("Counting verses by book: {}", bookName);

        Optional<KrvBookEntity> bookOpt = jpaBookRepository.findByName(bookName);
        if (bookOpt.isEmpty()) {
            return 0;
        }

        return jpaVerseRepository.countByBookId(bookOpt.get().getId());
    }

    @Override
    public long count() {
        return jpaVerseRepository.count();
    }

    // === 유틸리티 메서드 ===

    @Override
    public List<BibleVerse> findAll() {
        log.warn("Finding all verses - this might be a large dataset!");

        List<KrvVerseEntity> allVerses = jpaVerseRepository.findAll();
        return verseMapper.toDomainList(allVerses);
    }

    @Override
    public List<BibleVerse> findAllById(List<Long> ids) {
        log.debug("Finding verses by ids: {}", ids);

        List<Integer> integerIds = ids.stream()
                .map(Long::intValue)
                .toList();

        List<KrvVerseEntity> verses = jpaVerseRepository.findAllById(integerIds);
        return verseMapper.toDomainList(verses);
    }
}