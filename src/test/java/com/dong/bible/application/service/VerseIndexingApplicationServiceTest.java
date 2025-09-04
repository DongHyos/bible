package com.dong.bible.application.service;

import com.dong.bible.application.dto.query.BookQuery;
import com.dong.bible.application.dto.query.VerseQuery;
import com.dong.bible.infrastructure.search.repository.VerseSearchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VerseIndexingApplicationService 테스트")
class VerseIndexingApplicationServiceTest {

    @Mock
    private VerseApplicationService verseApplicationService;

    @Mock
    private BookApplicationService bookApplicationService;

    @Mock
    private VerseSearchRepository verseSearchRepository;

    @InjectMocks
    private VerseIndexingApplicationService verseIndexingApplicationService;

    private BookQuery sampleBook1;
    private BookQuery sampleBook2;
    private VerseQuery sampleVerse1;
    private VerseQuery sampleVerse2;

    @BeforeEach
    void setUp() {
        sampleBook1 = BookQuery.builder()
                .id(1L)
                .name("창세기")
                .abbreviation("창")
                .testament(null)
                .bookOrder(1)
                .totalChapters(50)
                .categoryId(1L)
                .build();

        sampleBook2 = BookQuery.builder()
                .id(2L)
                .name("출애굽기")
                .abbreviation("출")
                .testament(null)
                .bookOrder(2)
                .totalChapters(40)
                .categoryId(1L)
                .build();

        // VerseQuery는 AllArgsConstructor(access = AccessLevel.PRIVATE)이므로 직접 생성 불가
        // 실제 테스트에서는 Mock 처리
    }

    @Test
    @DisplayName("전체 구절 인덱싱 - 성공")
    void indexAllVerses_Success() {
        // given
        List<BookQuery> books = Arrays.asList(sampleBook1, sampleBook2);
        VerseQuery mockVerse1 = mock(VerseQuery.class);
        VerseQuery mockVerse2 = mock(VerseQuery.class);
        VerseQuery mockVerse3 = mock(VerseQuery.class);
        
        when(mockVerse1.getChapter()).thenReturn(1);
        when(mockVerse1.getVerse()).thenReturn(1);
        when(mockVerse1.getText()).thenReturn("태초에 하나님이 천지를 창조하시니라");
        
        when(mockVerse2.getChapter()).thenReturn(1);
        when(mockVerse2.getVerse()).thenReturn(2);
        when(mockVerse2.getText()).thenReturn("땅이 혼돈하고 공허하며 흑암이 깊음 위에 있고");
        
        when(mockVerse3.getChapter()).thenReturn(1);
        when(mockVerse3.getVerse()).thenReturn(1);
        when(mockVerse3.getText()).thenReturn("하나님이 애굽에서 나온 이스라엘을 위하여");
        
        List<VerseQuery> book1Verses = Arrays.asList(mockVerse1, mockVerse2);
        List<VerseQuery> book2Verses = Arrays.asList(mockVerse3);

        when(bookApplicationService.getAllBooks()).thenReturn(books);
        when(verseApplicationService.getBookVerses(1)).thenReturn(book1Verses);
        when(verseApplicationService.getBookVerses(2)).thenReturn(book2Verses);
        when(verseSearchRepository.save(any())).thenReturn(null);

        // when
        assertThatNoException().isThrownBy(() -> verseIndexingApplicationService.indexAllVerses());

        // then
        verify(verseSearchRepository).deleteAll();
        verify(bookApplicationService).getAllBooks();
        verify(verseApplicationService).getBookVerses(1);
        verify(verseApplicationService).getBookVerses(2);
        verify(verseSearchRepository, times(3)).save(any()); // 총 3개 구절
    }

    @Test
    @DisplayName("전체 구절 인덱싱 - 책 조회 실패")
    void indexAllVerses_BookServiceException() {
        // given
        when(bookApplicationService.getAllBooks()).thenThrow(new RuntimeException("DB 연결 실패"));

        // when & then
        assertThatThrownBy(() -> verseIndexingApplicationService.indexAllVerses())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("인덱싱 작업 실패: DB 연결 실패");

        verify(verseSearchRepository).deleteAll();
        verify(bookApplicationService).getAllBooks();
        verify(verseApplicationService, never()).getBookVerses(any());
    }

    @Test
    @DisplayName("책별 구절 인덱싱 - 성공")
    void indexVersesByBook_Success() {
        // given
        Integer bookId = 1;
        String bookName = "창세기";
        
        VerseQuery mockVerse1 = mock(VerseQuery.class);
        VerseQuery mockVerse2 = mock(VerseQuery.class);
        
        when(mockVerse1.getChapter()).thenReturn(1);
        when(mockVerse1.getVerse()).thenReturn(1);
        when(mockVerse1.getText()).thenReturn("태초에 하나님이 천지를 창조하시니라");
        
        when(mockVerse2.getChapter()).thenReturn(1);
        when(mockVerse2.getVerse()).thenReturn(2);
        when(mockVerse2.getText()).thenReturn("땅이 혼돈하고 공허하며 흑암이 깊음 위에 있고");
        
        List<VerseQuery> verses = Arrays.asList(mockVerse1, mockVerse2);

        when(verseApplicationService.getBookVerses(bookId)).thenReturn(verses);
        when(verseSearchRepository.save(any())).thenReturn(null);

        // when
        int result = verseIndexingApplicationService.indexVersesByBook(bookId, bookName);

        // then
        assertThat(result).isEqualTo(2);
        verify(verseApplicationService).getBookVerses(bookId);
        verify(verseSearchRepository, times(2)).save(any());
    }

    @Test
    @DisplayName("책별 구절 인덱싱 - 구절이 없는 경우")
    void indexVersesByBook_NoVerses() {
        // given
        Integer bookId = 1;
        String bookName = "창세기";
        List<VerseQuery> emptyVerses = Collections.emptyList();

        when(verseApplicationService.getBookVerses(bookId)).thenReturn(emptyVerses);

        // when
        int result = verseIndexingApplicationService.indexVersesByBook(bookId, bookName);

        // then
        assertThat(result).isEqualTo(0);
        verify(verseApplicationService).getBookVerses(bookId);
        verify(verseSearchRepository, never()).save(any());
    }

    @Test
    @DisplayName("책별 구절 인덱싱 - 일부 구절 인덱싱 실패")
    void indexVersesByBook_PartialFailure() {
        // given
        Integer bookId = 1;
        String bookName = "창세기";
        
        VerseQuery mockVerse1 = mock(VerseQuery.class);
        VerseQuery mockVerse2 = mock(VerseQuery.class);
        
        when(mockVerse1.getChapter()).thenReturn(1);
        when(mockVerse1.getVerse()).thenReturn(1);
        when(mockVerse1.getText()).thenReturn("태초에 하나님이 천지를 창조하시니라");
        
        when(mockVerse2.getChapter()).thenReturn(1);
        when(mockVerse2.getVerse()).thenReturn(2);
        when(mockVerse2.getText()).thenReturn("땅이 혼돈하고 공허하며 흑암이 깊음 위에 있고");
        
        List<VerseQuery> verses = Arrays.asList(mockVerse1, mockVerse2);

        when(verseApplicationService.getBookVerses(bookId)).thenReturn(verses);
        when(verseSearchRepository.save(any()))
                .thenReturn(null) // 첫 번째 성공
                .thenThrow(new RuntimeException("ElasticSearch 오류")); // 두 번째 실패

        // when
        int result = verseIndexingApplicationService.indexVersesByBook(bookId, bookName);

        // then
        assertThat(result).isEqualTo(1); // 1개만 성공
        verify(verseApplicationService).getBookVerses(bookId);
        verify(verseSearchRepository, times(2)).save(any());
    }

    @Test
    @DisplayName("책별 구절 인덱싱 - 구절 조회 실패")
    void indexVersesByBook_VerseServiceException() {
        // given
        Integer bookId = 1;
        String bookName = "창세기";

        when(verseApplicationService.getBookVerses(bookId)).thenThrow(new RuntimeException("DB 연결 실패"));

        // when & then
        assertThatThrownBy(() -> verseIndexingApplicationService.indexVersesByBook(bookId, bookName))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("책별 인덱싱 실패: DB 연결 실패");

        verify(verseApplicationService).getBookVerses(bookId);
        verify(verseSearchRepository, never()).save(any());
    }

    @Test
    @DisplayName("개별 구절 인덱싱 - 성공")
    void indexSingleVerse_Success() {
        // given
        VerseQuery mockVerse = mock(VerseQuery.class);
        when(mockVerse.getChapter()).thenReturn(1);
        when(mockVerse.getVerse()).thenReturn(1);
        when(mockVerse.getText()).thenReturn("태초에 하나님이 천지를 창조하시니라");
        
        Integer bookId = 1;
        String bookName = "창세기";

        when(verseSearchRepository.save(any())).thenReturn(null);

        // when
        assertThatNoException().isThrownBy(() -> 
            verseIndexingApplicationService.indexSingleVerse(mockVerse, bookId, bookName));

        // then
        verify(verseSearchRepository).save(any());
    }

    @Test
    @DisplayName("개별 구절 인덱싱 - ElasticSearch 저장 실패")
    void indexSingleVerse_SaveFailure() {
        // given
        VerseQuery mockVerse = mock(VerseQuery.class);
        when(mockVerse.getChapter()).thenReturn(1);
        when(mockVerse.getVerse()).thenReturn(1);
        when(mockVerse.getText()).thenReturn("태초에 하나님이 천지를 창조하시니라");
        
        Integer bookId = 1;
        String bookName = "창세기";

        when(verseSearchRepository.save(any())).thenThrow(new RuntimeException("ElasticSearch 연결 실패"));

        // when & then
        assertThatThrownBy(() -> 
            verseIndexingApplicationService.indexSingleVerse(mockVerse, bookId, bookName))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("ElasticSearch 연결 실패");

        verify(verseSearchRepository).save(any());
    }

    @Test
    @DisplayName("인덱스 전체 삭제 - 성공")
    void deleteAllIndex_Success() {
        // given
        doNothing().when(verseSearchRepository).deleteAll();

        // when
        assertThatNoException().isThrownBy(() -> verseIndexingApplicationService.deleteAllIndex());

        // then
        verify(verseSearchRepository).deleteAll();
    }

    @Test
    @DisplayName("인덱스 전체 삭제 - 실패")
    void deleteAllIndex_Failure() {
        // given
        doThrow(new RuntimeException("ElasticSearch 연결 실패")).when(verseSearchRepository).deleteAll();

        // when & then
        assertThatThrownBy(() -> verseIndexingApplicationService.deleteAllIndex())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("인덱스 삭제 실패: ElasticSearch 연결 실패");

        verify(verseSearchRepository).deleteAll();
    }

    @Test
    @DisplayName("인덱스 상태 조회 - 성공")
    void getIndexedVerseCount_Success() {
        // given
        long expectedCount = 1000L;
        when(verseSearchRepository.count()).thenReturn(expectedCount);

        // when
        long result = verseIndexingApplicationService.getIndexedVerseCount();

        // then
        assertThat(result).isEqualTo(expectedCount);
        verify(verseSearchRepository).count();
    }

    @Test
    @DisplayName("인덱스 상태 조회 - 실패시 0 반환")
    void getIndexedVerseCount_Failure_ReturnsZero() {
        // given
        when(verseSearchRepository.count()).thenThrow(new RuntimeException("ElasticSearch 연결 실패"));

        // when
        long result = verseIndexingApplicationService.getIndexedVerseCount();

        // then
        assertThat(result).isEqualTo(0);
        verify(verseSearchRepository).count();
    }
}