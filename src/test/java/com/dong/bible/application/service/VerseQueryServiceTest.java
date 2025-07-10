package com.dong.bible.application.service;

import com.dong.bible.application.dto.ChapterQueryDto;
import com.dong.bible.application.dto.VerseQueryDto;
import com.dong.bible.application.dto.VerseRangeQueryDto;
import com.dong.bible.application.dto.VerseSearchDto;
import com.dong.bible.domain.book.Book;
import com.dong.bible.domain.book.BookName;
import com.dong.bible.domain.verse.BibleVerse;
import com.dong.bible.domain.verse.BibleVerseRepository;
import com.dong.bible.domain.verse.VerseContent;
import com.dong.bible.domain.verse.VerseReference;
import com.dong.bible.ENUM.Testament;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerseQueryServiceTest {

    @Mock
    private BibleVerseRepository bibleVerseRepository;

    @Mock
    private BookQueryService bookQueryService;

    @InjectMocks
    private VerseQueryService verseQueryService;

    private Book 요한복음;
    private Book 창세기;
    private BibleVerse 요한복음3장16절;
    private BibleVerse 요한복음3장17절;
    private BibleVerse 창세기1장1절;

    @BeforeEach
    void setUp() {
        // Book 도메인 객체들
        요한복음 = Book.of(43L, BookName.of("요한복음"), "요", Testament.신약, 43, 21, 6L);
        창세기 = Book.of(1L, BookName.of("창세기"), "창", Testament.구약, 1, 50, 1L);

        // BibleVerse 도메인 객체들
        VerseReference 요한복음3_16 = VerseReference.of("요한복음", 3, 16);
        VerseContent 내용3_16 = VerseContent.of("하나님이 세상을 이처럼 사랑하사");
        요한복음3장16절 = BibleVerse.of(요한복음3_16, 내용3_16);

        VerseReference 요한복음3_17 = VerseReference.of("요한복음", 3, 17);
        VerseContent 내용3_17 = VerseContent.of("하나님이 그 아들을 세상에 보내신 것은");
        요한복음3장17절 = BibleVerse.of(요한복음3_17, 내용3_17);

        VerseReference 창세기1_1 = VerseReference.of("창세기", 1, 1);
        VerseContent 창세기내용 = VerseContent.of("태초에 하나님이 천지를 창조하시니라");
        창세기1장1절 = BibleVerse.of(창세기1_1, 창세기내용);
    }

    @Test
    void 특정_장_모든_구절_조회_성공() {
        // Given
        String bookName = "요한복음";
        Integer chapter = 3;
        List<BibleVerse> verses = Arrays.asList(요한복음3장16절, 요한복음3장17절);

        when(bookQueryService.getBookByName(bookName)).thenReturn(Optional.of(요한복음));
        when(bibleVerseRepository.findByChapter(bookName, chapter)).thenReturn(verses);
        when(bookQueryService.getBookIdByName(bookName)).thenReturn(Optional.of(43));

        // When
        ChapterQueryDto result = verseQueryService.getChapter(bookName, chapter);

        // Then
        assertThat(result.getBookId()).isEqualTo(43);
        assertThat(result.getBookName()).isEqualTo("요한복음");
        assertThat(result.getChapter()).isEqualTo(3);
        assertThat(result.getVerses()).hasSize(2);

        verify(bookQueryService).getBookByName(bookName);
        verify(bibleVerseRepository).findByChapter(bookName, chapter);
        verify(bookQueryService).getBookIdByName(bookName);
    }

    @Test
    void 특정_장_조회_Book_없음_예외() {
        // Given
        String bookName = "존재하지않는책";
        Integer chapter = 1;

        when(bookQueryService.getBookByName(bookName)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> verseQueryService.getChapter(bookName, chapter))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Book not found: " + bookName);

        verify(bookQueryService).getBookByName(bookName);
        verify(bibleVerseRepository, never()).findByChapter(anyString(), any());
    }

    @Test
    void 특정_장_조회_잘못된_장번호_예외() {
        // Given
        String bookName = "요한복음";
        Integer invalidChapter = 25; // 요한복음은 21장까지

        when(bookQueryService.getBookByName(bookName)).thenReturn(Optional.of(요한복음));

        // When & Then
        assertThatThrownBy(() -> verseQueryService.getChapter(bookName, invalidChapter))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("요한복음는 21장까지만 있습니다. 요청된 장: 25");

        verify(bookQueryService).getBookByName(bookName);
        verify(bibleVerseRepository, never()).findByChapter(anyString(), any());
    }

    @Test
    void 특정_장_조회_구절_없음_예외() {
        // Given
        String bookName = "요한복음";
        Integer chapter = 3;

        when(bookQueryService.getBookByName(bookName)).thenReturn(Optional.of(요한복음));
        when(bibleVerseRepository.findByChapter(bookName, chapter)).thenReturn(List.of());

        // When & Then
        assertThatThrownBy(() -> verseQueryService.getChapter(bookName, chapter))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Chapter not found: 요한복음 3");

        verify(bookQueryService).getBookByName(bookName);
        verify(bibleVerseRepository).findByChapter(bookName, chapter);
    }

    @Test
    void 특정_구절_조회_성공() {
        // Given
        Integer bookId = 43;
        Integer chapter = 3;
        Integer verse = 16;
        String bookName = "요한복음";
        VerseReference expectedReference = VerseReference.of(bookName, chapter, verse);

        when(bookQueryService.getBookNameById(bookId)).thenReturn(bookName);
        when(bookQueryService.getBookByName(bookName)).thenReturn(Optional.of(요한복음));
        when(bibleVerseRepository.findByReference(expectedReference)).thenReturn(Optional.of(요한복음3장16절));

        // When
        VerseQueryDto result = verseQueryService.getVerse(bookId, chapter, verse);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getText()).isEqualTo("하나님이 세상을 이처럼 사랑하사");

        verify(bookQueryService).getBookNameById(bookId);
        verify(bookQueryService).getBookByName(bookName);
        verify(bibleVerseRepository).findByReference(expectedReference);
    }

    @Test
    void 특정_구절_조회_구절_없음_예외() {
        // Given
        Integer bookId = 43;
        String bookName = "요한복음";
        Integer chapter = 3;
        Integer verse = 999;
        VerseReference expectedReference = VerseReference.of(bookName, chapter, verse);

        when(bookQueryService.getBookNameById(bookId)).thenReturn(bookName);
        when(bookQueryService.getBookByName(bookName)).thenReturn(Optional.of(요한복음));
        when(bibleVerseRepository.findByReference(expectedReference)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> verseQueryService.getVerse(bookId, chapter, verse))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Verse not found: 요한복음 3:999");

        verify(bookQueryService).getBookNameById(bookId);
        verify(bookQueryService).getBookByName(bookName);
        verify(bibleVerseRepository).findByReference(expectedReference);
    }

    @Test
    void 구절_범위_조회_성공() {
        // Given
        Integer bookId = 43;
        String bookName = "요한복음";
        Integer chapter = 3;
        Integer startVerse = 16;
        Integer endVerse = 17;
        List<BibleVerse> verses = Arrays.asList(요한복음3장16절, 요한복음3장17절);

        when(bookQueryService.getBookNameById(bookId)).thenReturn(bookName);
        when(bookQueryService.getBookByName(bookName)).thenReturn(Optional.of(요한복음));
        when(bibleVerseRepository.findByChapterRange(bookName, chapter, startVerse, endVerse))
                .thenReturn(verses);
        when(bookQueryService.getBookIdByName(bookName)).thenReturn(Optional.of(43));

        // When
        VerseRangeQueryDto result = verseQueryService.getVerseRange(bookId, chapter, startVerse, endVerse);

        // Then
        assertThat(result.getBookId()).isEqualTo(43);
        assertThat(result.getChapter()).isEqualTo(3);
        assertThat(result.getStartVerse()).isEqualTo(16);
        assertThat(result.getEndVerse()).isEqualTo(17);
        assertThat(result.getVerses()).hasSize(2);

        verify(bookQueryService).getBookNameById(bookId);
        verify(bookQueryService).getBookByName(bookName);
        verify(bibleVerseRepository).findByChapterRange(bookName, chapter, startVerse, endVerse);
        verify(bookQueryService).getBookIdByName(bookName);
    }

    @Test
    void 구절_범위_조회_잘못된_범위_예외() {
        // Given
        Integer bookId = 43;
        String bookName = "요한복음";
        Integer chapter = 3;
        Integer startVerse = 20;
        Integer endVerse = 10; // start > end

        when(bookQueryService.getBookNameById(bookId)).thenReturn(bookName);

        // When & Then
        assertThatThrownBy(() -> verseQueryService.getVerseRange(bookId, chapter, startVerse, endVerse))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("시작 절이 끝 절보다 클 수 없습니다");

        // bookId를 bookName으로 변환하는 것까지는 호출되지만, 이후 검증에서 실패
        verify(bookQueryService).getBookNameById(bookId);
        verify(bibleVerseRepository, never()).findByChapterRange(anyString(), any(), any(), any());
    }

    @Test
    void 텍스트_검색_성공() {
        // Given
        String keyword = "하나님";
        List<BibleVerse> allVerses = Arrays.asList(요한복음3장16절, 요한복음3장17절, 창세기1장1절);

        when(bibleVerseRepository.findAll()).thenReturn(allVerses);

        // When
        VerseSearchDto result = verseQueryService.searchVerses(keyword);

        // Then
        assertThat(result.getKeyword()).isEqualTo("하나님");
        assertThat(result.getVerses()).hasSize(3); // 모든 구절에 "하나님" 포함

        verify(bibleVerseRepository).findAll();
    }

    @Test
    void 텍스트_검색_키워드_없음_예외() {
        // When & Then
        assertThatThrownBy(() -> verseQueryService.searchVerses(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Search keyword cannot be empty");

        assertThatThrownBy(() -> verseQueryService.searchVerses(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Search keyword cannot be empty");

        assertThatThrownBy(() -> verseQueryService.searchVerses("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Search keyword cannot be empty");

        verify(bibleVerseRepository, never()).findAll();
    }

    @Test
    void ID_기반_구절_조회_성공() {
        // Given
        Long verseId = 123L;
        BibleVerse mockVerse = BibleVerse.of(verseId,
                VerseReference.of("요한복음", 3, 16),
                VerseContent.of("하나님이 세상을 이처럼 사랑하사"));

        when(bibleVerseRepository.findById(verseId)).thenReturn(Optional.of(mockVerse));

        // When
        VerseQueryDto result = verseQueryService.getVerseById(verseId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getText()).isEqualTo("하나님이 세상을 이처럼 사랑하사");

        verify(bibleVerseRepository).findById(verseId);
    }

    @Test
    void ID_기반_구절_조회_구절_없음_예외() {
        // Given
        Long verseId = 999L;

        when(bibleVerseRepository.findById(verseId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> verseQueryService.getVerseById(verseId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Verse not found with id: 999");

        verify(bibleVerseRepository).findById(verseId);
    }

    @Test
    void 책의_모든_구절_조회_성공() {
        // Given
        Integer bookId = 43;
        String bookName = "요한복음";
        List<BibleVerse> verses = Arrays.asList(요한복음3장16절, 요한복음3장17절);

        when(bookQueryService.getBookNameById(bookId)).thenReturn(bookName);
        when(bookQueryService.getBookByName(bookName)).thenReturn(Optional.of(요한복음));
        when(bibleVerseRepository.findByBook(bookName)).thenReturn(verses);

        // When
        List<VerseQueryDto> result = verseQueryService.getBookVerses(bookId);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getText()).isEqualTo("하나님이 세상을 이처럼 사랑하사");
        assertThat(result.get(1).getText()).isEqualTo("하나님이 그 아들을 세상에 보내신 것은");

        verify(bookQueryService).getBookNameById(bookId);
        verify(bookQueryService).getBookByName(bookName);
        verify(bibleVerseRepository).findByBook(bookName);
    }

    @Test
    void 신구약_구절_조회_성공() {
        // Given
        boolean isNewTestament = true;
        List<BibleVerse> newTestamentVerses = Arrays.asList(요한복음3장16절, 요한복음3장17절);

        when(bibleVerseRepository.findByTestament(isNewTestament)).thenReturn(newTestamentVerses);

        // When
        List<VerseQueryDto> result = verseQueryService.getTestamentVerses(isNewTestament);

        // Then
        assertThat(result).hasSize(2);
        verify(bibleVerseRepository).findByTestament(isNewTestament);
    }

    @Test
    void bookId로_장_조회_성공() {
        // Given
        Integer bookId = 43;
        Integer chapter = 3;
        String bookName = "요한복음";
        List<BibleVerse> verses = Arrays.asList(요한복음3장16절, 요한복음3장17절);

        when(bookQueryService.getBookNameById(bookId)).thenReturn(bookName);
        when(bookQueryService.getBookByName(bookName)).thenReturn(Optional.of(요한복음));
        when(bibleVerseRepository.findByChapter(bookName, chapter)).thenReturn(verses);
        when(bookQueryService.getBookIdByName(bookName)).thenReturn(Optional.of(43));

        // When
        ChapterQueryDto result = verseQueryService.getChapterById(bookId, chapter);

        // Then
        assertThat(result.getBookId()).isEqualTo(43);
        assertThat(result.getBookName()).isEqualTo("요한복음");
        assertThat(result.getChapter()).isEqualTo(3);

        verify(bookQueryService).getBookNameById(bookId);
        verify(bookQueryService).getBookByName(bookName);
    }

    @Test
    void bookId로_장_조회_Book_없음_예외() {
        // Given
        Integer bookId = 999;
        Integer chapter = 1;

        when(bookQueryService.getBookNameById(bookId))
                .thenThrow(new IllegalArgumentException("Book not found with id: 999"));

        // When & Then
        assertThatThrownBy(() -> verseQueryService.getChapterById(bookId, chapter))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Book not found with id: 999");

        verify(bookQueryService).getBookNameById(bookId);
    }

    @Test
    void 입력값_검증_테스트() {
        // Given & When & Then
        assertThatThrownBy(() -> verseQueryService.getChapter(null, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Book name cannot be empty");

        assertThatThrownBy(() -> verseQueryService.getChapter("", 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Book name cannot be empty");

        assertThatThrownBy(() -> verseQueryService.getChapter("   ", 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Book name cannot be empty");

        assertThatThrownBy(() -> verseQueryService.getVerseById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Verse ID cannot be null");
    }
}
