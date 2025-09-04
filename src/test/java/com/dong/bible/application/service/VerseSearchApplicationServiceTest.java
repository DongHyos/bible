package com.dong.bible.application.service;

import com.dong.bible.application.dto.query.VerseSearchResultQuery;
import com.dong.bible.application.dto.query.EnhancedVerseSearchResultQuery;
import com.dong.bible.infrastructure.search.document.VerseSearchDocument;
import com.dong.bible.infrastructure.search.repository.VerseSearchRepository;
import com.dong.bible.infrastructure.search.dto.EnhancedSearchHit;
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
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
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
        List<VerseSearchResultQuery> results = verseSearchApplicationService.searchByContent(keyword);

        // then
        assertThat(results).hasSize(1);
        VerseSearchResultQuery result = results.get(0);
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
        List<VerseSearchResultQuery> results = verseSearchApplicationService.searchByContent(keyword);

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
        List<VerseSearchResultQuery> results = verseSearchApplicationService.searchByBookName(bookName);

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
        List<VerseSearchResultQuery> results = verseSearchApplicationService.searchByBookAndChapter(bookId, chapter);

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

    // ========== 페이징 테스트 ==========

    @Test
    @DisplayName("키워드로 구절 검색 (페이징) - 성공")
    void searchByContentWithPaging_Success() {
        // given
        String keyword = "하나님";
        Pageable pageable = PageRequest.of(0, 10);
        List<VerseSearchDocument> mockDocuments = Arrays.asList(sampleDocument1);
        Page<VerseSearchDocument> mockPage = new PageImpl<>(mockDocuments, pageable, 1);
        
        when(verseSearchRepository.findByContentContaining(keyword, pageable)).thenReturn(mockPage);

        // when
        Page<VerseSearchResultQuery> results = verseSearchApplicationService.searchByContentWithPaging(keyword, pageable);

        // then
        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getTotalElements()).isEqualTo(1);
        assertThat(results.getTotalPages()).isEqualTo(1);
        assertThat(results.getNumber()).isEqualTo(0);
        assertThat(results.getSize()).isEqualTo(10);
        
        VerseSearchResultQuery result = results.getContent().get(0);
        assertThat(result.getId()).isEqualTo("1:1:1");
        assertThat(result.getBookName()).isEqualTo("창세기");
        assertThat(result.getContent()).isEqualTo("태초에 하나님이 천지를 창조하시니라");

        verify(verseSearchRepository).findByContentContaining(keyword, pageable);
    }

    @Test
    @DisplayName("키워드로 구절 검색 (페이징) - 빈 키워드 예외")
    void searchByContentWithPaging_EmptyKeyword_ThrowsException() {
        // given
        String emptyKeyword = "";
        Pageable pageable = PageRequest.of(0, 10);

        // when & then
        assertThatThrownBy(() -> verseSearchApplicationService.searchByContentWithPaging(emptyKeyword, pageable))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("검색 키워드는 비어있을 수 없습니다");

        verify(verseSearchRepository, never()).findByContentContaining(anyString(), any(Pageable.class));
    }

    @Test
    @DisplayName("책 이름으로 검색 (페이징) - 성공")
    void searchByBookNameWithPaging_Success() {
        // given
        String bookName = "창세기";
        Pageable pageable = PageRequest.of(0, 5);
        List<VerseSearchDocument> mockDocuments = Arrays.asList(sampleDocument1, sampleDocument2);
        Page<VerseSearchDocument> mockPage = new PageImpl<>(mockDocuments, pageable, 2);
        
        when(verseSearchRepository.findByBookName(bookName, pageable)).thenReturn(mockPage);

        // when
        Page<VerseSearchResultQuery> results = verseSearchApplicationService.searchByBookNameWithPaging(bookName, pageable);

        // then
        assertThat(results.getContent()).hasSize(2);
        assertThat(results.getTotalElements()).isEqualTo(2);
        assertThat(results.getNumber()).isEqualTo(0);
        assertThat(results.getSize()).isEqualTo(5);
        
        assertThat(results.getContent().get(0).getBookName()).isEqualTo("창세기");
        assertThat(results.getContent().get(1).getBookName()).isEqualTo("창세기");

        verify(verseSearchRepository).findByBookName(bookName, pageable);
    }

    @Test
    @DisplayName("책과 장으로 검색 (페이징) - 성공")
    void searchByBookAndChapterWithPaging_Success() {
        // given
        Integer bookId = 1;
        Integer chapter = 1;
        Pageable pageable = PageRequest.of(0, 20);
        List<VerseSearchDocument> mockDocuments = Arrays.asList(sampleDocument1, sampleDocument2);
        Page<VerseSearchDocument> mockPage = new PageImpl<>(mockDocuments, pageable, 2);
        
        when(verseSearchRepository.findByBookIdAndChapter(bookId, chapter, pageable)).thenReturn(mockPage);

        // when
        Page<VerseSearchResultQuery> results = verseSearchApplicationService.searchByBookAndChapterWithPaging(bookId, chapter, pageable);

        // then
        assertThat(results.getContent()).hasSize(2);
        assertThat(results.getTotalElements()).isEqualTo(2);
        assertThat(results.getNumber()).isEqualTo(0);
        assertThat(results.getSize()).isEqualTo(20);
        
        assertThat(results.getContent().get(0).getBookId()).isEqualTo(1);
        assertThat(results.getContent().get(0).getChapter()).isEqualTo(1);

        verify(verseSearchRepository).findByBookIdAndChapter(bookId, chapter, pageable);
    }

    @Test
    @DisplayName("책과 장으로 검색 (페이징) - null bookId 예외")
    void searchByBookAndChapterWithPaging_NullBookId_ThrowsException() {
        // given
        Integer nullBookId = null;
        Integer chapter = 1;
        Pageable pageable = PageRequest.of(0, 10);

        // when & then
        assertThatThrownBy(() -> verseSearchApplicationService.searchByBookAndChapterWithPaging(nullBookId, chapter, pageable))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("올바른 책 ID를 입력해주세요");

        verify(verseSearchRepository, never()).findByBookIdAndChapter(any(), any(), any(Pageable.class));
    }

    // ========== Enhanced 검색 테스트 ==========

    private EnhancedSearchHit sampleEnhancedHit1;
    private EnhancedSearchHit sampleEnhancedHit2;

    @Test
    @DisplayName("Enhanced 검색을 위한 Mock 객체 초기화")
    void setupEnhancedMockObjects() {
        VerseSearchDocument document1 = VerseSearchDocument.builder()
                .id("1:1:1")
                .bookId(1)
                .bookName("창세기")
                .chapter(1)
                .verse(1)
                .content("태초에 하나님이 천지를 창조하시니라")
                .displayReference("창세기 1:1")
                .build();

        Map<String, String[]> highlights1 = Map.of(
                "content", new String[]{"태초에 <mark>하나님</mark>이 천지를 창조하시니라"}
        );

        sampleEnhancedHit1 = EnhancedSearchHit.builder()
                .document(document1)
                .score(0.85f)
                .highlightFields(highlights1)
                .build();

        VerseSearchDocument document2 = VerseSearchDocument.builder()
                .id("43:3:16")
                .bookId(43)
                .bookName("요한복음")
                .chapter(3)
                .verse(16)
                .content("하나님이 세상을 이처럼 사랑하사 독생자를 주셨으니")
                .displayReference("요한복음 3:16")
                .build();

        Map<String, String[]> highlights2 = Map.of(
                "content", new String[]{"<mark>하나님</mark>이 세상을 이처럼 <mark>사랑</mark>하사"}
        );

        sampleEnhancedHit2 = EnhancedSearchHit.builder()
                .document(document2)
                .score(0.92f)
                .highlightFields(highlights2)
                .build();
    }

    @Test
    @DisplayName("Enhanced 구절 내용 검색 - 성공")
    void searchByContentEnhanced_Success() {
        // given
        setupEnhancedMockObjects();
        String keyword = "하나님";
        List<EnhancedSearchHit> mockHits = Arrays.asList(sampleEnhancedHit1, sampleEnhancedHit2);
        when(verseSearchRepository.findByContentWithHighlight(keyword)).thenReturn(mockHits);

        // when
        List<EnhancedVerseSearchResultQuery> results = verseSearchApplicationService.searchByContentEnhanced(keyword);

        // then
        assertThat(results).hasSize(2);
        
        EnhancedVerseSearchResultQuery result1 = results.get(0);
        assertThat(result1.getId()).isEqualTo("1:1:1");
        assertThat(result1.getBookName()).isEqualTo("창세기");
        assertThat(result1.getContent()).isEqualTo("태초에 하나님이 천지를 창조하시니라");
        assertThat(result1.getScore()).isEqualTo(0.85f);
        assertThat(result1.getHighlightedContent()).contains("<mark>하나님</mark>");
        assertThat(result1.getHasHighlight()).isTrue();
        assertThat(result1.getSearchKeyword()).isEqualTo(keyword);

        EnhancedVerseSearchResultQuery result2 = results.get(1);
        assertThat(result2.getBookName()).isEqualTo("요한복음");
        assertThat(result2.getScore()).isEqualTo(0.92f);
        assertThat(result2.getHighlightCount()).isEqualTo(2);

        verify(verseSearchRepository).findByContentWithHighlight(keyword);
    }

    @Test
    @DisplayName("Enhanced 구절 내용 검색 - 빈 키워드 예외")
    void searchByContentEnhanced_EmptyKeyword_ThrowsException() {
        // given
        String emptyKeyword = "";

        // when & then
        assertThatThrownBy(() -> verseSearchApplicationService.searchByContentEnhanced(emptyKeyword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("검색 키워드는 비어있을 수 없습니다");

        verify(verseSearchRepository, never()).findByContentWithHighlight(anyString());
    }

    @Test
    @DisplayName("Enhanced 구절 내용 검색 - null 키워드 예외")
    void searchByContentEnhanced_NullKeyword_ThrowsException() {
        // given
        String nullKeyword = null;

        // when & then
        assertThatThrownBy(() -> verseSearchApplicationService.searchByContentEnhanced(nullKeyword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("검색 키워드는 비어있을 수 없습니다");

        verify(verseSearchRepository, never()).findByContentWithHighlight(anyString());
    }

    @Test
    @DisplayName("Enhanced 구절 내용 검색 - 검색결과 없음")
    void searchByContentEnhanced_NoResults() {
        // given
        String keyword = "존재하지않는키워드";
        when(verseSearchRepository.findByContentWithHighlight(keyword)).thenReturn(Collections.emptyList());

        // when
        List<EnhancedVerseSearchResultQuery> results = verseSearchApplicationService.searchByContentEnhanced(keyword);

        // then
        assertThat(results).isEmpty();
        verify(verseSearchRepository).findByContentWithHighlight(keyword);
    }

    @Test
    @DisplayName("Enhanced 구절 내용 검색 (페이징) - 성공")
    void searchByContentEnhancedWithPaging_Success() {
        // given
        setupEnhancedMockObjects();
        String keyword = "하나님";
        Pageable pageable = PageRequest.of(0, 10);
        List<EnhancedSearchHit> mockHits = Arrays.asList(sampleEnhancedHit1);
        Page<EnhancedSearchHit> mockPage = new PageImpl<>(mockHits, pageable, 1);
        
        when(verseSearchRepository.findByContentWithHighlight(keyword, pageable)).thenReturn(mockPage);

        // when
        Page<EnhancedVerseSearchResultQuery> results = verseSearchApplicationService.searchByContentEnhancedWithPaging(keyword, pageable);

        // then
        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getTotalElements()).isEqualTo(1);
        assertThat(results.getTotalPages()).isEqualTo(1);
        assertThat(results.getNumber()).isEqualTo(0);
        assertThat(results.getSize()).isEqualTo(10);
        
        EnhancedVerseSearchResultQuery result = results.getContent().get(0);
        assertThat(result.getId()).isEqualTo("1:1:1");
        assertThat(result.getBookName()).isEqualTo("창세기");
        assertThat(result.getScore()).isEqualTo(0.85f);
        assertThat(result.getHasHighlight()).isTrue();

        verify(verseSearchRepository).findByContentWithHighlight(keyword, pageable);
    }

    @Test
    @DisplayName("Enhanced 구절 내용 검색 (페이징) - 빈 키워드 예외")
    void searchByContentEnhancedWithPaging_EmptyKeyword_ThrowsException() {
        // given
        String emptyKeyword = "";
        Pageable pageable = PageRequest.of(0, 10);

        // when & then
        assertThatThrownBy(() -> verseSearchApplicationService.searchByContentEnhancedWithPaging(emptyKeyword, pageable))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("검색 키워드는 비어있을 수 없습니다");

        verify(verseSearchRepository, never()).findByContentWithHighlight(anyString(), any(Pageable.class));
    }

    @Test
    @DisplayName("Enhanced 검색 결과 품질 검증")
    void searchByContentEnhanced_QualityValidation() {
        // given
        setupEnhancedMockObjects();
        String keyword = "하나님";
        
        // 고품질 결과 (높은 점수 + 하이라이팅)
        VerseSearchDocument highQualityDocument = VerseSearchDocument.builder()
                .id("test:1:1")
                .bookId(1)
                .bookName("테스트")
                .chapter(1)
                .verse(1)
                .content("하나님은 사랑이시라")
                .displayReference("테스트 1:1")
                .build();

        Map<String, String[]> highQualityHighlights = Map.of(
                "content", new String[]{"<mark>하나님</mark>은 <mark>사랑</mark>이시라"}
        );

        EnhancedSearchHit highQualityHit = EnhancedSearchHit.builder()
                .document(highQualityDocument)
                .score(0.95f)
                .highlightFields(highQualityHighlights)
                .build();
        
        List<EnhancedSearchHit> mockHits = Arrays.asList(highQualityHit);
        when(verseSearchRepository.findByContentWithHighlight(keyword)).thenReturn(mockHits);

        // when
        List<EnhancedVerseSearchResultQuery> results = verseSearchApplicationService.searchByContentEnhanced(keyword);

        // then
        assertThat(results).hasSize(1);
        EnhancedVerseSearchResultQuery result = results.get(0);
        
        // 품질 지표 검증
        assertThat(result.getIsHighQuality()).isTrue();
        assertThat(result.getIsPerfectMatch()).isTrue(); // 점수 0.95 + 하이라이팅 2개
        assertThat(result.getIsPartialMatch()).isTrue();
        assertThat(result.getRelevanceLevel()).isEqualTo("HIGH");
        assertThat(result.getHighlightQuality()).isGreaterThan(0.0);

        verify(verseSearchRepository).findByContentWithHighlight(keyword);
    }

    @Test
    @DisplayName("Enhanced Repository 예외 처리")
    void searchByContentEnhanced_RepositoryException_ThrowsRuntimeException() {
        // given
        String keyword = "테스트";
        when(verseSearchRepository.findByContentWithHighlight(keyword))
                .thenThrow(new RuntimeException("ElasticSearch 연결 실패"));

        // when & then
        assertThatThrownBy(() -> verseSearchApplicationService.searchByContentEnhanced(keyword))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("ElasticSearch 연결 실패");

        verify(verseSearchRepository).findByContentWithHighlight(keyword);
    }

    @Test
    @DisplayName("Enhanced 검색 결과 변환 로직 검증")
    void searchByContentEnhanced_ConversionLogic() {
        // given
        setupEnhancedMockObjects();
        String keyword = "하나님";
        
        // 다양한 점수와 하이라이팅을 가진 결과들
        VerseSearchDocument lowScoreDocument = VerseSearchDocument.builder()
                .id("low:1:1")
                .bookId(1)
                .bookName("테스트")
                .chapter(1)
                .verse(1)
                .content("하나님께서 말씀하시니라")
                .displayReference("테스트 1:1")
                .build();

        Map<String, String[]> lowScoreHighlights = Map.of(
                "content", new String[]{"<mark>하나님</mark>께서 말씀하시니라"}
        );

        EnhancedSearchHit lowScoreHit = EnhancedSearchHit.builder()
                .document(lowScoreDocument)
                .score(0.15f)
                .highlightFields(lowScoreHighlights)
                .build();
        
        List<EnhancedSearchHit> mockHits = Arrays.asList(lowScoreHit);
        when(verseSearchRepository.findByContentWithHighlight(keyword)).thenReturn(mockHits);

        // when
        List<EnhancedVerseSearchResultQuery> results = verseSearchApplicationService.searchByContentEnhanced(keyword);

        // then
        assertThat(results).hasSize(1);
        EnhancedVerseSearchResultQuery result = results.get(0);
        
        // 낮은 점수 결과 검증
        assertThat(result.getRelevanceLevel()).isEqualTo("LOW");
        assertThat(result.getIsHighQuality()).isFalse(); // 낮은 점수
        assertThat(result.getIsPerfectMatch()).isFalse();
        assertThat(result.getIsPartialMatch()).isFalse(); // 낮은 점수
        
        // 기본 필드 검증
        assertThat(result.getBookName()).isEqualTo("테스트");
        assertThat(result.getChapter()).isEqualTo(1);
        assertThat(result.getVerse()).isEqualTo(1);
        assertThat(result.getDisplayReference()).isEqualTo("테스트 1:1");
    }
}