package com.dong.bible.domain.verse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.assertj.core.api.Assertions.*;

@DisplayName("SearchScore 테스트")
class SearchScoreTest {
    
    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {
        
        @Test
        @DisplayName("유효한 점수로 생성 - 성공")
        void createWithValidScore_Success() {
            // given
            double score = 0.85;
            
            // when
            SearchScore searchScore = SearchScore.of(score);
            
            // then
            assertThat(searchScore).isNotNull();
            assertThat(searchScore.getValue()).isEqualTo(0.85);
        }
        
        @Test
        @DisplayName("0점으로 생성 - 성공")
        void createWithZeroScore_Success() {
            // when
            SearchScore searchScore = SearchScore.zero();
            
            // then
            assertThat(searchScore.getValue()).isEqualTo(0.0);
            assertThat(searchScore.isZero()).isTrue();
        }
        
        @Test
        @DisplayName("완벽한 점수로 생성 - 성공")
        void createWithPerfectScore_Success() {
            // when
            SearchScore searchScore = SearchScore.perfect();
            
            // then
            assertThat(searchScore.getValue()).isEqualTo(1.0);
        }
        
        @Test
        @DisplayName("음수 점수로 생성 - 실패")
        void createWithNegativeScore_ThrowsException() {
            // given
            double negativeScore = -0.5;
            
            // when & then
            assertThatThrownBy(() -> SearchScore.of(negativeScore))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("0.0 이상이어야 합니다");
        }
        
        @Test
        @DisplayName("NaN으로 생성 - 실패")
        void createWithNaN_ThrowsException() {
            // given
            double nanScore = Double.NaN;
            
            // when & then
            assertThatThrownBy(() -> SearchScore.of(nanScore))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("유한한 숫자여야 합니다");
        }
        
        @Test
        @DisplayName("무한대로 생성 - 실패")
        void createWithInfinity_ThrowsException() {
            // given
            double infinityScore = Double.POSITIVE_INFINITY;
            
            // when & then
            assertThatThrownBy(() -> SearchScore.of(infinityScore))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("유한한 숫자여야 합니다");
        }
    }
    
    @Nested
    @DisplayName("관련도 등급 테스트")
    class RelevanceLevelTest {
        
        @Test
        @DisplayName("높은 관련도 - 0.5 이상")
        void highRelevance() {
            // given
            SearchScore score1 = SearchScore.of(0.5);
            SearchScore score2 = SearchScore.of(0.9);
            SearchScore score3 = SearchScore.of(1.5);
            
            // then
            assertThat(score1.isHighRelevance()).isTrue();
            assertThat(score2.isHighRelevance()).isTrue();
            assertThat(score3.isHighRelevance()).isTrue();
            
            assertThat(score1.getRelevanceLevel()).isEqualTo(SearchScore.RelevanceLevel.HIGH);
            assertThat(score2.getRelevanceLevel()).isEqualTo(SearchScore.RelevanceLevel.HIGH);
            assertThat(score3.getRelevanceLevel()).isEqualTo(SearchScore.RelevanceLevel.HIGH);
        }
        
        @Test
        @DisplayName("중간 관련도 - 0.2 이상 0.5 미만")
        void mediumRelevance() {
            // given
            SearchScore score1 = SearchScore.of(0.2);
            SearchScore score2 = SearchScore.of(0.35);
            SearchScore score3 = SearchScore.of(0.49);
            
            // then
            assertThat(score1.isMediumRelevance()).isTrue();
            assertThat(score2.isMediumRelevance()).isTrue();
            assertThat(score3.isMediumRelevance()).isTrue();
            
            assertThat(score1.getRelevanceLevel()).isEqualTo(SearchScore.RelevanceLevel.MEDIUM);
            assertThat(score2.getRelevanceLevel()).isEqualTo(SearchScore.RelevanceLevel.MEDIUM);
            assertThat(score3.getRelevanceLevel()).isEqualTo(SearchScore.RelevanceLevel.MEDIUM);
        }
        
        @Test
        @DisplayName("낮은 관련도 - 0.2 미만")
        void lowRelevance() {
            // given
            SearchScore score1 = SearchScore.of(0.01);
            SearchScore score2 = SearchScore.of(0.1);
            SearchScore score3 = SearchScore.of(0.19);
            
            // then
            assertThat(score1.isLowRelevance()).isTrue();
            assertThat(score2.isLowRelevance()).isTrue();
            assertThat(score3.isLowRelevance()).isTrue();
            
            assertThat(score1.getRelevanceLevel()).isEqualTo(SearchScore.RelevanceLevel.LOW);
            assertThat(score2.getRelevanceLevel()).isEqualTo(SearchScore.RelevanceLevel.LOW);
            assertThat(score3.getRelevanceLevel()).isEqualTo(SearchScore.RelevanceLevel.LOW);
        }
        
        @Test
        @DisplayName("관련도 없음 - 0.0")
        void noRelevance() {
            // given
            SearchScore score = SearchScore.of(0.0);
            
            // then
            assertThat(score.isZero()).isTrue();
            assertThat(score.getRelevanceLevel()).isEqualTo(SearchScore.RelevanceLevel.NONE);
        }
    }
    
    @Nested
    @DisplayName("비교 테스트")
    class ComparisonTest {
        
        @Test
        @DisplayName("점수 비교")
        void compareScores() {
            // given
            SearchScore score1 = SearchScore.of(0.3);
            SearchScore score2 = SearchScore.of(0.7);
            SearchScore score3 = SearchScore.of(0.3);
            
            // then
            assertThat(score1.compareTo(score2)).isLessThan(0);
            assertThat(score2.compareTo(score1)).isGreaterThan(0);
            assertThat(score1.compareTo(score3)).isEqualTo(0);
        }
        
        @Test
        @DisplayName("더 높은 점수인지 확인")
        void isHigherThan() {
            // given
            SearchScore score1 = SearchScore.of(0.3);
            SearchScore score2 = SearchScore.of(0.7);
            
            // then
            assertThat(score2.isHigherThan(score1)).isTrue();
            assertThat(score1.isHigherThan(score2)).isFalse();
            assertThat(score1.isHigherThan(score1)).isFalse();
        }
    }
    
    @Nested
    @DisplayName("equals & hashCode 테스트")
    class EqualsAndHashCodeTest {
        
        @Test
        @DisplayName("동일한 점수는 같다")
        void sameScoreEquals() {
            // given
            SearchScore score1 = SearchScore.of(0.75);
            SearchScore score2 = SearchScore.of(0.75);
            
            // then
            assertThat(score1).isEqualTo(score2);
            assertThat(score1.hashCode()).isEqualTo(score2.hashCode());
        }
        
        @Test
        @DisplayName("다른 점수는 다르다")
        void differentScoreNotEquals() {
            // given
            SearchScore score1 = SearchScore.of(0.75);
            SearchScore score2 = SearchScore.of(0.76);
            
            // then
            assertThat(score1).isNotEqualTo(score2);
            assertThat(score1.hashCode()).isNotEqualTo(score2.hashCode());
        }
        
        @Test
        @DisplayName("null과 비교")
        void equalsWithNull() {
            // given
            SearchScore score = SearchScore.of(0.5);
            
            // then
            assertThat(score).isNotEqualTo(null);
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
            
            // when
            String result = score.toString();
            
            // then
            assertThat(result).isEqualTo("SearchScore(0.750)");
        }
    }
    
    @Nested
    @DisplayName("관련도 등급 열거형 테스트")
    class RelevanceLevelEnumTest {
        
        @Test
        @DisplayName("각 등급의 설명 확인")
        void levelDescriptions() {
            assertThat(SearchScore.RelevanceLevel.NONE.getDescription()).isEqualTo("관련도 없음");
            assertThat(SearchScore.RelevanceLevel.LOW.getDescription()).isEqualTo("낮은 관련도");
            assertThat(SearchScore.RelevanceLevel.MEDIUM.getDescription()).isEqualTo("중간 관련도");
            assertThat(SearchScore.RelevanceLevel.HIGH.getDescription()).isEqualTo("높은 관련도");
        }
    }
}