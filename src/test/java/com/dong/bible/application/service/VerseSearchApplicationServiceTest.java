package com.dong.bible.application.service;

import com.dong.bible.application.dto.VerseSearchResultDto;
import com.dong.bible.infrastructure.search.document.VerseSearchDocument;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VerseSearchApplicationService 테스트")
class VerseSearchApplicationServiceTest {

    @Mock
    private VerseSearchRepository verseSearchRepository;

    @InjectMocks
    private VerseSearchApplicationService verseSearchApplicationService;

    private VerseSearchDocument sampleDocument1;
    private VerseSearchDocument sampleDocument2;

    @BeforeEach
    void setUp() {
        sampleDocument1 = VerseSearchDocument.builder()
                .id("1:1:1")
                .bookId(1)
                .bookName("창세기")
                .chapter(1)
                .verse(1)
                .content("태초에 하나님이 천지를 창조하시니라")
                .displayReference("창세기 1:1")
                .build();

        sampleDocument2 = VerseSearchDocument.builder()
                .id("1:1:2")
                .bookId(1)
                .bookName("창세기")
                .chapter(1)
                .verse(2)
                .content("땅이 혼돈하고 공허하며 흑암이 깊음 위에 있고")
                .displayReference("창세기 1:2")
                .build();
    }

    @Test
    @DisplayName("키워드로 구절 검색 - 성공")
    void searchByContent_Success() {
        // given
        String keyword = "하나님";
        List<VerseSearchDocument> mockDocuments = Arrays.asList(sampleDocument1);
        when(verseSearchRepository.findByContentContaining(keyword)).thenReturn(mockDocuments);

        // when
        List<VerseSearchResultDto> results = verseSearchApplicationService.searchByContent(keyword);

        // then
        assertThat(results).hasSize(1);
        VerseSearchResultDto result = results.get(0);
        assertThat(result.getId()).isEqualTo("1:1:1");
        assertThat(result.getBookName()).isEqualTo("창세기");
        assertThat(result.getContent()).isEqualTo("태초에 하나님이 천지를 창조하시니라");
        assertThat(result.getDisplayReference()).isEqualTo("창세기 1:1");

        verify(verseSearchRepository).findByContentContaining(keyword);
    }

    @Test
    @DisplayName("키워드로 구절 검색 - 검색결과 없음")
    void searchByContent_NoResults() {
        // given
        String keyword = "존재하지않는키워드";
        when(verseSearchRepository.findByContentContaining(keyword)).thenReturn(Collections.emptyList());

        // when
        List<VerseSearchResultDto> results = verseSearchApplicationService.searchByContent(keyword);

        // then
        assertThat(results).isEmpty();
        verify(verseSearchRepository).findByContentContaining(keyword);
    }

    @Test
    @DisplayName("키워드로 구절 검색 - 빈 키워드 예외")
    void searchByContent_EmptyKeyword_ThrowsException() {
        // given
        String emptyKeyword = "";

        // when & then
        assertThatThrownBy(() -> verseSearchApplicationService.searchByContent(emptyKeyword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("검색 키워드는 비어있을 수 없습니다");

        verify(verseSearchRepository, never()).findByContentContaining(anyString());
    }

    @Test
    @DisplayName("키워드로 구절 검색 - null 키워드 예외")
    void searchByContent_NullKeyword_ThrowsException() {
        // given
        String nullKeyword = null;

        // when & then
        assertThatThrownBy(() -> verseSearchApplicationService.searchByContent(nullKeyword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("검색 키워드는 비어있을 수 없습니다");

        verify(verseSearchRepository, never()).findByContentContaining(anyString());
    }

    @Test
    @DisplayName("책 이름으로 검색 - 성공")
    void searchByBookName_Success() {
        // given
        String bookName = "창세기";
        List<VerseSearchDocument> mockDocuments = Arrays.asList(sampleDocument1, sampleDocument2);
        when(verseSearchRepository.findByBookName(bookName)).thenReturn(mockDocuments);

        // when
        List<VerseSearchResultDto> results = verseSearchApplicationService.searchByBookName(bookName);

        // then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getBookName()).isEqualTo("창세기");
        assertThat(results.get(1).getBookName()).isEqualTo("창세기");

        verify(verseSearchRepository).findByBookName(bookName);
    }

    @Test
    @DisplayName("책 이름으로 검색 - 빈 책이름 예외")
    void searchByBookName_EmptyBookName_ThrowsException() {
        // given
        String emptyBookName = "";

        // when & then
        assertThatThrownBy(() -> verseSearchApplicationService.searchByBookName(emptyBookName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("책 이름은 비어있을 수 없습니다");

        verify(verseSearchRepository, never()).findByBookName(anyString());
    }

    @Test
    @DisplayName("책과 장으로 검색 - 성공")
    void searchByBookAndChapter_Success() {
        // given
        Integer bookId = 1;
        Integer chapter = 1;
        List<VerseSearchDocument> mockDocuments = Arrays.asList(sampleDocument1, sampleDocument2);
        when(verseSearchRepository.findByBookIdAndChapter(bookId, chapter)).thenReturn(mockDocuments);

        // when
        List<VerseSearchResultDto> results = verseSearchApplicationService.searchByBookAndChapter(bookId, chapter);

        // then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getBookId()).isEqualTo(1);
        assertThat(results.get(0).getChapter()).isEqualTo(1);
        assertThat(results.get(1).getBookId()).isEqualTo(1);
        assertThat(results.get(1).getChapter()).isEqualTo(1);

        verify(verseSearchRepository).findByBookIdAndChapter(bookId, chapter);
    }

    @Test
    @DisplayName("책과 장으로 검색 - null bookId 예외")
    void searchByBookAndChapter_NullBookId_ThrowsException() {
        // given
        Integer nullBookId = null;
        Integer chapter = 1;

        // when & then
        assertThatThrownBy(() -> verseSearchApplicationService.searchByBookAndChapter(nullBookId, chapter))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("올바른 책 ID를 입력해주세요");

        verify(verseSearchRepository, never()).findByBookIdAndChapter(any(), any());
    }

    @Test
    @DisplayName("책과 장으로 검색 - null chapter 예외")
    void searchByBookAndChapter_NullChapter_ThrowsException() {
        // given
        Integer bookId = 1;
        Integer nullChapter = null;

        // when & then
        assertThatThrownBy(() -> verseSearchApplicationService.searchByBookAndChapter(bookId, nullChapter))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("올바른 장 번호를 입력해주세요");

        verify(verseSearchRepository, never()).findByBookIdAndChapter(any(), any());
    }

    @Test
    @DisplayName("책과 장으로 검색 - 음수 bookId 예외")
    void searchByBookAndChapter_NegativeBookId_ThrowsException() {
        // given
        Integer negativeBookId = -1;
        Integer chapter = 1;

        // when & then
        assertThatThrownBy(() -> verseSearchApplicationService.searchByBookAndChapter(negativeBookId, chapter))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("올바른 책 ID를 입력해주세요");

        verify(verseSearchRepository, never()).findByBookIdAndChapter(any(), any());
    }

    @Test
    @DisplayName("책과 장으로 검색 - 음수 chapter 예외")
    void searchByBookAndChapter_NegativeChapter_ThrowsException() {
        // given
        Integer bookId = 1;
        Integer negativeChapter = -1;

        // when & then
        assertThatThrownBy(() -> verseSearchApplicationService.searchByBookAndChapter(bookId, negativeChapter))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("올바른 장 번호를 입력해주세요");

        verify(verseSearchRepository, never()).findByBookIdAndChapter(any(), any());
    }

    @Test
    @DisplayName("Repository 예외 처리")
    void searchByContent_RepositoryException_ThrowsRuntimeException() {
        // given
        String keyword = "테스트";
        when(verseSearchRepository.findByContentContaining(keyword))
                .thenThrow(new RuntimeException("ElasticSearch 연결 실패"));

        // when & then
        assertThatThrownBy(() -> verseSearchApplicationService.searchByContent(keyword))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("ElasticSearch 연결 실패");

        verify(verseSearchRepository).findByContentContaining(keyword);
    }
}