package com.dong.bible.domain.statistics;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * BibleStatistics 도메인 객체 테스트
 * 
 * Value Object의 특성 검증:
 * - 불변성
 * - 입력 검증
 * - 비즈니스 로직
 */
class BibleStatisticsTest {
    
    @Test
    void 정상적인_통계_객체_생성() {
        // Given & When
        BibleStatistics statistics = BibleStatistics.of(
            66, 39, 27, 1189, 
            0.59, 0.41, 18.0
        );
        
        // Then
        assertThat(statistics.getTotalBooks()).isEqualTo(66);
        assertThat(statistics.getOldTestamentBooks()).isEqualTo(39);
        assertThat(statistics.getNewTestamentBooks()).isEqualTo(27);
        assertThat(statistics.getTotalChapters()).isEqualTo(1189);
        assertThat(statistics.getOldTestamentRatio()).isEqualTo(0.59);
        assertThat(statistics.getNewTestamentRatio()).isEqualTo(0.41);
        assertThat(statistics.getAverageChaptersPerBook()).isEqualTo(18.0);
    }
    
    @Test
    void 구약이_더_많은_경우() {
        // Given
        BibleStatistics statistics = BibleStatistics.of(
            66, 39, 27, 1189,
            0.59, 0.41, 18.0
        );
        
        // When & Then
        assertThat(statistics.isOldTestamentDominant()).isTrue();
    }
    
    @Test
    void 신약이_더_많은_경우() {
        // Given
        BibleStatistics statistics = BibleStatistics.of(
            50, 20, 30, 1000,
            0.4, 0.6, 20.0
        );
        
        // When & Then
        assertThat(statistics.isOldTestamentDominant()).isFalse();
    }
    
    @Test
    void 높은_장_밀도_확인() {
        // Given
        BibleStatistics statistics = BibleStatistics.of(
            10, 5, 5, 250,
            0.5, 0.5, 25.0
        );
        
        // When & Then
        assertThat(statistics.hasHighChapterDensity()).isTrue();
    }
    
    @Test
    void 낮은_장_밀도_확인() {
        // Given
        BibleStatistics statistics = BibleStatistics.of(
            10, 5, 5, 150,
            0.5, 0.5, 15.0
        );
        
        // When & Then
        assertThat(statistics.hasHighChapterDensity()).isFalse();
    }
    
    @Test
    void 표시_문자열_생성() {
        // Given
        BibleStatistics statistics = BibleStatistics.of(
            66, 39, 27, 1189,
            0.59, 0.41, 18.0
        );
        
        // When
        String displayString = statistics.toDisplayString();
        
        // Then
        assertThat(displayString).isEqualTo("총 66권 (구약 39권, 신약 27권), 총 1189장, 평균 18.0장/권");
    }
    
    @Test
    void 음수_총책수_입력시_예외() {
        // When & Then
        assertThatThrownBy(() -> BibleStatistics.of(
            -1, 0, 0, 0,
            0.0, 0.0, 0.0
        )).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("총 성경책 수는 0 이상이어야 합니다");
    }
    
    @Test
    void 음수_구약책수_입력시_예외() {
        // When & Then
        assertThatThrownBy(() -> BibleStatistics.of(
            10, -1, 11, 100,
            0.0, 1.0, 10.0
        )).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("구약 성경책 수는 0 이상이어야 합니다");
    }
    
    @Test
    void 음수_신약책수_입력시_예외() {
        // When & Then
        assertThatThrownBy(() -> BibleStatistics.of(
            10, 11, -1, 100,
            1.0, 0.0, 10.0
        )).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("신약 성경책 수는 0 이상이어야 합니다");
    }
    
    @Test
    void 음수_총장수_입력시_예외() {
        // When & Then
        assertThatThrownBy(() -> BibleStatistics.of(
            10, 5, 5, -1,
            0.5, 0.5, 0.0
        )).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("총 장수는 0 이상이어야 합니다");
    }
    
    @Test
    void 구약_신약_합계_불일치시_예외() {
        // When & Then
        assertThatThrownBy(() -> BibleStatistics.of(
            10, 5, 4, 100,  // 5 + 4 = 9 ≠ 10
            0.5, 0.4, 10.0
        )).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("총 성경책 수가 구약 + 신약 수와 일치하지 않습니다");
    }
}