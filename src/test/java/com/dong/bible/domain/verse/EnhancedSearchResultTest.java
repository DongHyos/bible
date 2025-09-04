package com.dong.bible.domain.verse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("EnhancedSearchResult 테스트")
class EnhancedSearchResultTest {
    
    private BibleVerse mockVerse;
    private VerseReference mockReference;
    private VerseContent mockContent;
    
    @BeforeEach
    void setUp() {
        // Mock 객체 설정
        mockReference = mock(VerseReference.class);
        when(mockReference.getBookName()).thenReturn("창세기");
        when(mockReference.getChapter()).thenReturn(1);
        when(mockReference.getVerse()).thenReturn(1);
        when(mockReference.toString()).thenReturn("창세기 1:1");
        // compareTo 대신 compareVerse 사용
        
        mockContent = mock(VerseContent.class);
        when(mockContent.getText()).thenReturn("태초에 하나님이 천지를 창조하시니라");
        
        mockVerse = mock(BibleVerse.class);
        when(mockVerse.getReference()).thenReturn(mockReference);
        when(mockVerse.getContent()).thenReturn(mockContent);
    }
    
    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {
        
        @Test
        @DisplayName("전체 정보로 생성 - 성공")
        void createWithFullInfo_Success() {
            // given
            SearchScore score = SearchScore.of(0.85);
            HighlightedContent highlightedContent = HighlightedContent.of(
                "태초에 하나님이 천지를 창조하시니라",
                "태초에 <mark>하나님</mark>이 천지를 창조하시니라"
            );
            String keyword = "하나님";
            
            // when
            EnhancedSearchResult result = EnhancedSearchResult.of(
                mockVerse, score, highlightedContent, keyword
            );
            
            // then
            assertThat(result).isNotNull();
            assertThat(result.getVerse()).isEqualTo(mockVerse);
            assertThat(result.getScore()).isEqualTo(score);
            assertThat(result.getHighlightedContent()).isEqualTo(highlightedContent);
            assertThat(result.getSearchKeyword()).isEqualTo(keyword);
        }
        
        @Test
        @DisplayName("기본 검색 결과로부터 생성 - 성공")
        void createFromBasicResult_Success() {
            // when
            EnhancedSearchResult result = EnhancedSearchResult.fromBasicResult(mockVerse);
            
            // then
            assertThat(result.getVerse()).isEqualTo(mockVerse);
            assertThat(result.getScore().getValue()).isEqualTo(0.0);
            assertThat(result.getHighlightedContent().hasHighlight()).isFalse();
            assertThat(result.getSearchKeyword()).isEmpty();
        }
        
        @Test
        @DisplayName("ElasticSearch 결과로부터 생성 - 성공")
        void createFromElasticSearchResult_Success() {
            // given
            double elasticScore = 0.95;
            String originalText = "태초에 하나님이 천지를 창조하시니라";
            String[] highlightFragments = {"태초에 <mark>하나님</mark>이 천지를"};
            String keyword = "하나님";
            
            // when
            EnhancedSearchResult result = EnhancedSearchResult.fromElasticSearchResult(
                mockVerse, elasticScore, originalText, highlightFragments, keyword
            );
            
            // then
            assertThat(result.getVerse()).isEqualTo(mockVerse);
            assertThat(result.getScore().getValue()).isEqualTo(0.95);
            assertThat(result.getHighlightedContent().hasHighlight()).isTrue();
            assertThat(result.getSearchKeyword()).isEqualTo(keyword);
        }
        
        @Test
        @DisplayName("null verse로 생성 - 실패")
        void createWithNullVerse_ThrowsException() {
            // given
            SearchScore score = SearchScore.of(0.5);
            HighlightedContent content = HighlightedContent.withoutHighlight("텍스트");
            
            // when & then
            assertThatThrownBy(() -> EnhancedSearchResult.of(null, score, content, "keyword"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("구절은 null일 수 없습니다");
        }
        
        @Test
        @DisplayName("null score로 생성 - 실패")
        void createWithNullScore_ThrowsException() {
            // given
            HighlightedContent content = HighlightedContent.withoutHighlight("텍스트");
            
            // when & then
            assertThatThrownBy(() -> EnhancedSearchResult.of(mockVerse, null, content, "keyword"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("검색 점수는 null일 수 없습니다");
        }
        
        @Test
        @DisplayName("null highlighted content로 생성 - 실패")
        void createWithNullHighlightedContent_ThrowsException() {
            // given
            SearchScore score = SearchScore.of(0.5);
            
            // when & then
            assertThatThrownBy(() -> EnhancedSearchResult.of(mockVerse, score, null, "keyword"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("하이라이팅된 내용은 null일 수 없습니다");
        }
    }
    
    @Nested
    @DisplayName("품질 판단 테스트")
    class QualityTest {
        
        @Test
        @DisplayName("고품질 검색 결과 판단")
        void isHighQualityResult() {
            // given
            SearchScore highScore = SearchScore.of(0.8);
            HighlightedContent highlighted = HighlightedContent.of(
                "텍스트", "<mark>텍스트</mark>"
            );
            
            EnhancedSearchResult result = EnhancedSearchResult.of(
                mockVerse, highScore, highlighted, "텍스트"
            );
            
            // then
            assertThat(result.isHighQualityResult()).isTrue();
        }
        
        @Test
        @DisplayName("완벽한 매칭 판단")
        void isPerfectMatch() {
            // given
            SearchScore perfectScore = SearchScore.of(0.95);
            HighlightedContent multiHighlight = HighlightedContent.of(
                "하나님은 사랑이시라", 
                "<mark>하나님</mark>은 <mark>사랑</mark>이시라"
            );
            
            EnhancedSearchResult result = EnhancedSearchResult.of(
                mockVerse, perfectScore, multiHighlight, "하나님"
            );
            
            // then
            assertThat(result.isPerfectMatch()).isTrue();
        }
        
        @Test
        @DisplayName("부분 매칭 판단")
        void isPartialMatch() {
            // given
            SearchScore mediumScore = SearchScore.of(0.3);
            HighlightedContent highlighted = HighlightedContent.withoutHighlight("텍스트");
            
            EnhancedSearchResult result = EnhancedSearchResult.of(
                mockVerse, mediumScore, highlighted, "텍스트"
            );
            
            // then
            assertThat(result.isPartialMatch()).isTrue();
        }
    }
    
    @Nested
    @DisplayName("검색 결과 비교 테스트")
    class ComparisonTest {
        
        @Test
        @DisplayName("관련도순 비교 - 점수 기준")
        void compareByRelevance_ByScore() {
            // given
            EnhancedSearchResult result1 = EnhancedSearchResult.of(
                mockVerse, 
                SearchScore.of(0.5), 
                HighlightedContent.withoutHighlight("텍스트"), 
                "키워드"
            );
            
            EnhancedSearchResult result2 = EnhancedSearchResult.of(
                mockVerse, 
                SearchScore.of(0.8), 
                HighlightedContent.withoutHighlight("텍스트"), 
                "키워드"
            );
            
            // when
            int comparison = result1.compareByRelevance(result2);
            
            // then
            assertThat(comparison).isGreaterThan(0); // result2가 더 높은 점수
        }
        
        @Test
        @DisplayName("관련도순 비교 - 하이라이팅 개수 기준")
        void compareByRelevance_ByHighlightCount() {
            // given
            SearchScore sameScore = SearchScore.of(0.5);
            
            EnhancedSearchResult result1 = EnhancedSearchResult.of(
                mockVerse, 
                sameScore, 
                HighlightedContent.of("텍스트", "<mark>텍스트</mark>"), 
                "키워드"
            );
            
            EnhancedSearchResult result2 = EnhancedSearchResult.of(
                mockVerse, 
                sameScore, 
                HighlightedContent.of("텍스트 텍스트", "<mark>텍스트</mark> <mark>텍스트</mark>"), 
                "키워드"
            );
            
            // when
            int comparison = result1.compareByRelevance(result2);
            
            // then
            assertThat(comparison).isGreaterThan(0); // result2가 더 많은 하이라이팅
        }
    }
    
    @Nested
    @DisplayName("요약 정보 테스트")
    class SummaryTest {
        
        @Test
        @DisplayName("검색 결과 요약 생성")
        void getSummary() {
            // given
            SearchScore score = SearchScore.of(0.75);
            HighlightedContent highlighted = HighlightedContent.of(
                "텍스트", "<mark>텍스트</mark>"
            );
            String keyword = "텍스트";
            
            EnhancedSearchResult result = EnhancedSearchResult.of(
                mockVerse, score, highlighted, keyword
            );
            
            // when
            EnhancedSearchResult.SearchResultSummary summary = result.getSummary();
            
            // then
            assertThat(summary.getReference()).isEqualTo("창세기 1:1");
            assertThat(summary.getBookName()).isEqualTo("창세기");
            assertThat(summary.getChapter()).isEqualTo(1);
            assertThat(summary.getVerse()).isEqualTo(1);
            assertThat(summary.getScore()).isEqualTo(0.75);
            assertThat(summary.getRelevanceLevel()).isEqualTo(SearchScore.RelevanceLevel.HIGH);
            assertThat(summary.getHighlightCount()).isEqualTo(1);
            assertThat(summary.isHasHighlight()).isTrue();
            assertThat(summary.getSearchKeyword()).isEqualTo(keyword);
        }
    }
    
    @Nested
    @DisplayName("equals & hashCode 테스트")
    class EqualsAndHashCodeTest {
        
        @Test
        @DisplayName("동일한 검색 결과는 같다")
        void sameResultEquals() {
            // given
            SearchScore score = SearchScore.of(0.5);
            HighlightedContent content = HighlightedContent.withoutHighlight("텍스트");
            String keyword = "키워드";
            
            EnhancedSearchResult result1 = EnhancedSearchResult.of(mockVerse, score, content, keyword);
            EnhancedSearchResult result2 = EnhancedSearchResult.of(mockVerse, score, content, keyword);
            
            // then
            assertThat(result1).isEqualTo(result2);
            assertThat(result1.hashCode()).isEqualTo(result2.hashCode());
        }
        
        @Test
        @DisplayName("다른 검색 결과는 다르다")
        void differentResultNotEquals() {
            // given
            EnhancedSearchResult result1 = EnhancedSearchResult.of(
                mockVerse, 
                SearchScore.of(0.5), 
                HighlightedContent.withoutHighlight("텍스트1"), 
                "키워드1"
            );
            
            EnhancedSearchResult result2 = EnhancedSearchResult.of(
                mockVerse, 
                SearchScore.of(0.6), 
                HighlightedContent.withoutHighlight("텍스트2"), 
                "키워드2"
            );
            
            // then
            assertThat(result1).isNotEqualTo(result2);
        }
    }
    
    @Nested
    @DisplayName("toString 테스트")
    class ToStringTest {
        
        @Test
        @DisplayName("문자열 표현")
        void toStringFormat() {
            // given
            SearchScore score = SearchScore.of(0.75);
            HighlightedContent highlighted = HighlightedContent.of(
                "텍스트", "<mark>텍스트</mark>"
            );
            
            EnhancedSearchResult result = EnhancedSearchResult.of(
                mockVerse, score, highlighted, "키워드"
            );
            
            // when
            String resultString = result.toString();
            
            // then
            assertThat(resultString).contains("EnhancedSearchResult");
            assertThat(resultString).contains("창세기 1:1");
            assertThat(resultString).contains("score=0.75");
            assertThat(resultString).contains("highlights=1");
        }
    }
}