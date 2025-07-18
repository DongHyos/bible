package com.dong.bible.infrastructure.persistence.repository;

import com.dong.bible.ENUM.Testament;
import com.dong.bible.domain.verse.BibleVerse;
import com.dong.bible.domain.verse.BibleVerseRepository;
import com.dong.bible.domain.verse.VerseContent;
import com.dong.bible.domain.verse.VerseReference;
import com.dong.bible.infrastructure.persistence.entity.KrvBookEntity;
import com.dong.bible.infrastructure.persistence.entity.KrvVerseEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")  // application-test.properties 사용
@ComponentScan(basePackages = "com.dong.bible.infrastructure.persistence")  // Mapper 스캔
class BibleVerseRepositoryImplTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BibleVerseRepository bibleVerseRepository;  // 우리가 만든 구현체

    private KrvBookEntity testBook;
    private KrvVerseEntity testVerse1;
    private KrvVerseEntity testVerse2;

    @BeforeEach
    void setUp() {
        // 테스트용 책 생성 (Builder 패턴 사용)
        testBook = KrvBookEntity.builder()
                .name("요한복음")
                .abbr("요")
                .testament(Testament.신약)
                .bookOrder(43)
                .chapters(21)
                .build();

        entityManager.persistAndFlush(testBook);

        // 테스트용 구절들 생성 (Builder 패턴 사용)
        testVerse1 = KrvVerseEntity.builder()
                .book(testBook)
                .chapter(3)
                .verse(16)
                .text("하나님이 세상을 이처럼 사랑하사")
                .build();

        testVerse2 = KrvVerseEntity.builder()
                .book(testBook)
                .chapter(3)
                .verse(17)
                .text("하나님이 그 아들을 세상에 보내신 것은")
                .build();

        entityManager.persistAndFlush(testVerse1);
        entityManager.persistAndFlush(testVerse2);
        entityManager.clear();
    }

    // === 단일 구절 조회 테스트 ===

    @Test
    void findByReference_구절_참조로_조회_성공() {
        // Given
        VerseReference reference = VerseReference.of("요한복음", 3, 16);

        // When
        Optional<BibleVerse> result = bibleVerseRepository.findByReference(reference);

        // Then
        assertThat(result).isPresent();

        BibleVerse verse = result.get();
        assertThat(verse.getReference().getBookName()).isEqualTo("요한복음");
        assertThat(verse.getReference().getChapter()).isEqualTo(3);
        assertThat(verse.getReference().getVerse()).isEqualTo(16);
        assertThat(verse.getContent().getText()).isEqualTo("하나님이 세상을 이처럼 사랑하사");
    }

    @Test
    void findByReference_존재하지_않는_구절_조회() {
        // Given
        VerseReference reference = VerseReference.of("요한복음", 99, 99);

        // When
        Optional<BibleVerse> result = bibleVerseRepository.findByReference(reference);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findByReference_존재하지_않는_책_조회() {
        // Given
        VerseReference reference = VerseReference.of("존재하지않는책", 1, 1);

        // When
        Optional<BibleVerse> result = bibleVerseRepository.findByReference(reference);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findById_ID로_구절_조회_성공() {
        // Given
        Long verseId = testVerse1.getId().longValue();

        // When
        Optional<BibleVerse> result = bibleVerseRepository.findById(verseId);

        // Then
        assertThat(result).isPresent();

        BibleVerse verse = result.get();
        assertThat(verse.getId()).isEqualTo(verseId);
        assertThat(verse.getContent().getText()).isEqualTo("하나님이 세상을 이처럼 사랑하사");
    }

    // === 다중 구절 조회 테스트 ===

    @Test
    void findByChapter_특정_장의_모든_구절_조회() {
        // Given
        String bookName = "요한복음";
        Integer chapter = 3;

        // When
        List<BibleVerse> result = bibleVerseRepository.findByChapter(bookName, chapter);

        // Then
        assertThat(result).hasSize(2);

        // 구절 순서대로 정렬되어 있는지 확인
        assertThat(result.get(0).getReference().getVerse()).isEqualTo(16);
        assertThat(result.get(1).getReference().getVerse()).isEqualTo(17);

        // 내용 확인
        assertThat(result.get(0).getContent().getText()).isEqualTo("하나님이 세상을 이처럼 사랑하사");
        assertThat(result.get(1).getContent().getText()).isEqualTo("하나님이 그 아들을 세상에 보내신 것은");
    }

    @Test
    void findByChapter_존재하지_않는_장_조회() {
        // Given
        String bookName = "요한복음";
        Integer chapter = 999;

        // When
        List<BibleVerse> result = bibleVerseRepository.findByChapter(bookName, chapter);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findByChapterRange_구절_범위_조회() {
        // Given
        String bookName = "요한복음";
        Integer chapter = 3;
        Integer startVerse = 16;
        Integer endVerse = 17;

        // When
        List<BibleVerse> result = bibleVerseRepository.findByChapterRange(bookName, chapter, startVerse, endVerse);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getReference().getVerse()).isEqualTo(16);
        assertThat(result.get(1).getReference().getVerse()).isEqualTo(17);
    }

    @Test
    void findByChapterRange_부분_범위_조회() {
        // Given
        String bookName = "요한복음";
        Integer chapter = 3;
        Integer startVerse = 16;
        Integer endVerse = 16;  // 16절만

        // When
        List<BibleVerse> result = bibleVerseRepository.findByChapterRange(bookName, chapter, startVerse, endVerse);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getReference().getVerse()).isEqualTo(16);
    }

    // === 저장 테스트 ===

    @Test
    void save_새로운_구절_저장() {
        // Given
        VerseReference newReference = VerseReference.of("요한복음", 3, 18);
        VerseContent newContent = VerseContent.of("그를 믿는 자는 심판을 받지 아니하는 것이요");
        BibleVerse newVerse = BibleVerse.of(newReference, newContent);

        // When
        BibleVerse savedVerse = bibleVerseRepository.save(newVerse);

        // Then
        assertThat(savedVerse.getId()).isNotNull();  // ID가 생성되었는지
        assertThat(savedVerse.getReference().getVerse()).isEqualTo(18);
        assertThat(savedVerse.getContent().getText()).isEqualTo("그를 믿는 자는 심판을 받지 아니하는 것이요");

        // DB에서 실제로 조회되는지 확인
        Optional<BibleVerse> found = bibleVerseRepository.findByReference(newReference);
        assertThat(found).isPresent();
        assertThat(found.get().getContent().getText()).isEqualTo("그를 믿는 자는 심판을 받지 아니하는 것이요");
    }

    // === 삭제 테스트 ===

    @Test
    void delete_구절_삭제() {
        // Given
        Long verseId = testVerse1.getId().longValue();
        BibleVerse verseToDelete = bibleVerseRepository.findById(verseId).orElseThrow();

        // When
        bibleVerseRepository.delete(verseToDelete);

        // Then
        Optional<BibleVerse> deleted = bibleVerseRepository.findById(verseId);
        assertThat(deleted).isEmpty();
    }

    @Test
    void deleteById_ID로_구절_삭제() {
        // Given
        Long verseId = testVerse2.getId().longValue();

        // When
        bibleVerseRepository.deleteById(verseId);

        // Then
        Optional<BibleVerse> deleted = bibleVerseRepository.findById(verseId);
        assertThat(deleted).isEmpty();
    }

    // === 존재 여부 확인 테스트 ===

    @Test
    void existsByReference_구절_존재_확인() {
        // Given
        VerseReference existingReference = VerseReference.of("요한복음", 3, 16);
        VerseReference nonExistingReference = VerseReference.of("요한복음", 99, 99);

        // When & Then
        assertThat(bibleVerseRepository.existsByReference(existingReference)).isTrue();
        assertThat(bibleVerseRepository.existsByReference(nonExistingReference)).isFalse();
    }

    @Test
    void existsById_ID로_구절_존재_확인() {
        // Given
        Long existingId = testVerse1.getId().longValue();
        Long nonExistingId = 99999L;

        // When & Then
        assertThat(bibleVerseRepository.existsById(existingId)).isTrue();
        assertThat(bibleVerseRepository.existsById(nonExistingId)).isFalse();
    }

    // === 개수 조회 테스트 ===

    @Test
    void countByChapter_장별_구절_개수() {
        // Given
        String bookName = "요한복음";
        Integer chapter = 3;

        // When
        long count = bibleVerseRepository.countByChapter(bookName, chapter);

        // Then
        assertThat(count).isEqualTo(2);  // setUp에서 2개 생성
    }

    @Test
    void countByBook_책별_구절_개수() {
        // Given
        String bookName = "요한복음";

        // When
        long count = bibleVerseRepository.countByBook(bookName);

        // Then
        assertThat(count).isEqualTo(2);  // setUp에서 2개 생성
    }
}