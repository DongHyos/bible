package com.dong.bible.domain.verse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.assertj.core.api.Assertions.*;

@DisplayName("HighlightedContent 테스트")
class HighlightedContentTest {
    
    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {
        
        @Test
        @DisplayName("하이라이팅된 내용으로 생성 - 성공")
        void createWithHighlightedText_Success() {
            // given
            String originalText = "태초에 하나님이 천지를 창조하시니라";
            String highlightedText = "태초에 <mark>하나님</mark>이 천지를 창조하시니라";
            
            // when
            HighlightedContent content = HighlightedContent.of(originalText, highlightedText);
            
            // then
            assertThat(content).isNotNull();
            assertThat(content.getOriginalText()).isEqualTo(originalText);
            assertThat(content.getHighlightedText()).isEqualTo(highlightedText);
            assertThat(content.getHighlightCount()).isEqualTo(1);
            assertThat(content.hasHighlight()).isTrue();
        }
        
        @Test
        @DisplayName("하이라이팅 없는 내용으로 생성 - 성공")
        void createWithoutHighlight_Success() {
            // given
            String text = "태초에 하나님이 천지를 창조하시니라";
            
            // when
            HighlightedContent content = HighlightedContent.withoutHighlight(text);
            
            // then
            assertThat(content.getOriginalText()).isEqualTo(text);
            assertThat(content.getHighlightedText()).isEqualTo(text);
            assertThat(content.getHighlightCount()).isEqualTo(0);
            assertThat(content.hasHighlight()).isFalse();
        }
        
        @Test
        @DisplayName("ElasticSearch 하이라이트 결과로부터 생성 - 성공")
        void createFromElasticSearchHighlights_Success() {
            // given
            String originalText = "하나님이 세상을 이처럼 사랑하사 독생자를 주셨으니";
            String[] highlights = {
                "<mark>하나님</mark>이 세상을 이처럼",
                "<mark>사랑</mark>하사 독생자를"
            };
            
            // when
            HighlightedContent content = HighlightedContent.fromElasticSearchHighlights(originalText, highlights);
            
            // then
            assertThat(content.getOriginalText()).isEqualTo(originalText);
            assertThat(content.getHighlightedText()).isEqualTo("<mark>하나님</mark>이 세상을 이처럼 ... <mark>사랑</mark>하사 독생자를");
            assertThat(content.getHighlightCount()).isEqualTo(2);
            assertThat(content.hasHighlight()).isTrue();
        }
        
        @Test
        @DisplayName("null 원본 텍스트로 생성 - 실패")
        void createWithNullOriginalText_ThrowsException() {
            // given
            String highlightedText = "테스트";
            
            // when & then
            assertThatThrownBy(() -> HighlightedContent.of(null, highlightedText))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("원본 텍스트는 null일 수 없습니다");
        }
        
        @Test
        @DisplayName("null 하이라이팅 텍스트로 생성 - 실패")
        void createWithNullHighlightedText_ThrowsException() {
            // given
            String originalText = "테스트";
            
            // when & then
            assertThatThrownBy(() -> HighlightedContent.of(originalText, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("하이라이팅된 텍스트는 null일 수 없습니다");
        }
        
        @Test
        @DisplayName("빈 원본 텍스트로 생성 - 실패")
        void createWithEmptyOriginalText_ThrowsException() {
            // given
            String originalText = "  ";
            String highlightedText = "테스트";
            
            // when & then
            assertThatThrownBy(() -> HighlightedContent.of(originalText, highlightedText))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("원본 텍스트는 비어있을 수 없습니다");
        }
    }
    
    @Nested
    @DisplayName("하이라이팅 정보 테스트")
    class HighlightInfoTest {
        
        @Test
        @DisplayName("다중 하이라이팅 카운트")
        void multipleHighlightCount() {
            // given
            String originalText = "하나님은 사랑이시라 하나님이 우리를 사랑하시니라";
            String highlightedText = "<mark>하나님</mark>은 <mark>사랑</mark>이시라 <mark>하나님</mark>이 우리를 <mark>사랑</mark>하시니라";
            
            // when
            HighlightedContent content = HighlightedContent.of(originalText, highlightedText);
            
            // then
            assertThat(content.getHighlightCount()).isEqualTo(4);
            assertThat(content.hasHighlight()).isTrue();
            assertThat(content.hasMultipleHighlights()).isTrue();
        }
        
        @Test
        @DisplayName("하이라이팅된 키워드 추출")
        void extractHighlightedKeywords() {
            // given
            String originalText = "하나님은 사랑이시라 하나님이 우리를 사랑하시니라";
            String highlightedText = "<mark>하나님</mark>은 <mark>사랑</mark>이시라 <mark>하나님</mark>이 우리를 <mark>사랑</mark>하시니라";
            
            // when
            HighlightedContent content = HighlightedContent.of(originalText, highlightedText);
            String[] keywords = content.extractHighlightedKeywords();
            
            // then
            assertThat(keywords).containsExactlyInAnyOrder("하나님", "사랑");
        }
        
        @Test
        @DisplayName("하이라이팅 없는 경우 키워드 추출")
        void extractKeywordsWithoutHighlight() {
            // given
            HighlightedContent content = HighlightedContent.withoutHighlight("일반 텍스트");
            
            // when
            String[] keywords = content.extractHighlightedKeywords();
            
            // then
            assertThat(keywords).isEmpty();
        }
        
        @Test
        @DisplayName("HTML 태그 제거한 순수 텍스트")
        void getPlainText() {
            // given
            String originalText = "하나님은 사랑이시라";
            String highlightedText = "<mark>하나님</mark>은 <mark>사랑</mark>이시라";
            
            // when
            HighlightedContent content = HighlightedContent.of(originalText, highlightedText);
            String plainText = content.getPlainText();
            
            // then
            assertThat(plainText).isEqualTo("하나님은 사랑이시라");
        }
    }
    
    @Nested
    @DisplayName("하이라이팅 품질 테스트")
    class HighlightQualityTest {
        
        @Test
        @DisplayName("하이라이팅 품질 점수 계산")
        void calculateHighlightQuality() {
            // given
            String originalText = "하나님은 사랑이시라"; // 10자
            String highlightedText = "<mark>하나님</mark>은 <mark>사랑</mark>이시라"; // 2개 하이라이트
            
            // when
            HighlightedContent content = HighlightedContent.of(originalText, highlightedText);
            double quality = content.getHighlightQuality();
            
            // then
            assertThat(quality).isGreaterThan(0.0);
            assertThat(quality).isLessThanOrEqualTo(1.0);
        }
        
        @Test
        @DisplayName("하이라이팅 없는 경우 품질 점수 0")
        void qualityWithoutHighlight() {
            // given
            HighlightedContent content = HighlightedContent.withoutHighlight("일반 텍스트");
            
            // when
            double quality = content.getHighlightQuality();
            
            // then
            assertThat(quality).isEqualTo(0.0);
        }
    }
    
    @Nested
    @DisplayName("ElasticSearch 통합 테스트")
    class ElasticSearchIntegrationTest {
        
        @Test
        @DisplayName("빈 하이라이트 배열 처리")
        void handleEmptyHighlights() {
            // given
            String originalText = "테스트 텍스트";
            String[] emptyHighlights = {};
            
            // when
            HighlightedContent content = HighlightedContent.fromElasticSearchHighlights(originalText, emptyHighlights);
            
            // then
            assertThat(content.getOriginalText()).isEqualTo(originalText);
            assertThat(content.getHighlightedText()).isEqualTo(originalText);
            assertThat(content.hasHighlight()).isFalse();
        }
        
        @Test
        @DisplayName("null 하이라이트 배열 처리")
        void handleNullHighlights() {
            // given
            String originalText = "테스트 텍스트";
            
            // when
            HighlightedContent content = HighlightedContent.fromElasticSearchHighlights(originalText, null);
            
            // then
            assertThat(content.getOriginalText()).isEqualTo(originalText);
            assertThat(content.getHighlightedText()).isEqualTo(originalText);
            assertThat(content.hasHighlight()).isFalse();
        }
    }
    
    @Nested
    @DisplayName("equals & hashCode 테스트")
    class EqualsAndHashCodeTest {
        
        @Test
        @DisplayName("동일한 내용은 같다")
        void sameContentEquals() {
            // given
            String originalText = "테스트";
            String highlightedText = "<mark>테스트</mark>";
            
            HighlightedContent content1 = HighlightedContent.of(originalText, highlightedText);
            HighlightedContent content2 = HighlightedContent.of(originalText, highlightedText);
            
            // then
            assertThat(content1).isEqualTo(content2);
            assertThat(content1.hashCode()).isEqualTo(content2.hashCode());
        }
        
        @Test
        @DisplayName("다른 내용은 다르다")
        void differentContentNotEquals() {
            // given
            HighlightedContent content1 = HighlightedContent.of("텍스트1", "<mark>텍스트1</mark>");
            HighlightedContent content2 = HighlightedContent.of("텍스트2", "<mark>텍스트2</mark>");
            
            // then
            assertThat(content1).isNotEqualTo(content2);
        }
    }
    
    @Nested
    @DisplayName("toString 테스트")
    class ToStringTest {
        
        @Test
        @DisplayName("문자열 표현")
        void toStringFormat() {
            // given
            String originalText = "테스트 텍스트입니다";
            String highlightedText = "<mark>테스트</mark> 텍스트입니다";
            HighlightedContent content = HighlightedContent.of(originalText, highlightedText);
            
            // when
            String result = content.toString();
            
            // then
            assertThat(result).contains("HighlightedContent");
            assertThat(result).contains("highlights=1");
            assertThat(result).contains("length=" + originalText.length());
        }
    }
}