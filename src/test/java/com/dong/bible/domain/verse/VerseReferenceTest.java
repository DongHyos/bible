package com.dong.bible.domain.verse;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class VerseReferenceTest {
    @Test
    void 정상적인_구절_참조_생성() {
        // Given & When
        VerseReference reference = VerseReference.of("요한복음", 3, 16);

        // Then
        assertThat(reference.getBookName()).isEqualTo("요한복음");
        assertThat(reference.getChapter()).isEqualTo(3);
        assertThat(reference.getVerse()).isEqualTo(16);
        assertThat(reference.isValid()).isTrue();
    }

    @Test
    void 표시_문자열_형식_확인() {
        // Given
        VerseReference reference = VerseReference.of("요한복음", 3, 16);

        // When
        String displayString = reference.toDisplayString();

        // Then
        assertThat(displayString).isEqualTo("요한복음 3:16");
    }

    @Test
    void 신약_성경_여부_확인() {
        // Given
        VerseReference 요한복음 = VerseReference.of("요한복음", 3, 16);
        VerseReference 로마서 = VerseReference.of("로마서", 1, 1);
        VerseReference 요한계시록 = VerseReference.of("요한계시록", 21, 4);

        // When & Then
        assertThat(요한복음.isNewTestament()).isTrue();
        assertThat(로마서.isNewTestament()).isTrue();
        assertThat(요한계시록.isNewTestament()).isTrue();
    }

    @Test
    void 구약_성경_여부_확인() {
        // Given
        VerseReference 창세기 = VerseReference.of("창세기", 1, 1);
        VerseReference 시편 = VerseReference.of("시편", 23, 1);
        VerseReference 말라기 = VerseReference.of("말라기", 4, 6);

        // When & Then
        assertThat(창세기.isNewTestament()).isFalse();
        assertThat(창세기.isOldTestament()).isTrue();

        assertThat(시편.isNewTestament()).isFalse();
        assertThat(시편.isOldTestament()).isTrue();

        assertThat(말라기.isNewTestament()).isFalse();
        assertThat(말라기.isOldTestament()).isTrue();
    }

    @Test
    void 잘못된_입력값_예외_처리() {
        // Given & When & Then

        // 1. null 책 이름
        assertThatThrownBy(() -> VerseReference.of(null, 3, 16))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("책 이름은 비어있을 수 없습니다");

        // 2. 빈 문자열 책 이름
        assertThatThrownBy(() -> VerseReference.of("", 3, 16))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("책 이름은 비어있을 수 없습니다");

        // 3. 공백만 있는 책 이름
        assertThatThrownBy(() -> VerseReference.of("   ", 3, 16))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("책 이름은 비어있을 수 없습니다");

        // 4. 0 이하의 장 번호
        assertThatThrownBy(() -> VerseReference.of("요한복음", 0, 16))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("장 번호는 1 이상이어야 합니다");

        assertThatThrownBy(() -> VerseReference.of("요한복음", -1, 16))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("장 번호는 1 이상이어야 합니다");

        // 5. 0 이하의 절 번호
        assertThatThrownBy(() -> VerseReference.of("요한복음", 3, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("절 번호는 1 이상이어야 합니다");

        assertThatThrownBy(() -> VerseReference.of("요한복음", 3, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("절 번호는 1 이상이어야 합니다");
    }

    @Test
    void 같은_장인지_확인() {
        // Given
        VerseReference 요한복음3장16절 = VerseReference.of("요한복음", 3, 16);
        VerseReference 요한복음3장17절 = VerseReference.of("요한복음", 3, 17);
        VerseReference 요한복음4장1절 = VerseReference.of("요한복음", 4, 1);
        VerseReference 마태복음3장16절 = VerseReference.of("마태복음", 3, 16);

        // When & Then
        assertThat(요한복음3장16절.isSameChapter(요한복음3장17절)).isTrue();
        assertThat(요한복음3장16절.isSameChapter(요한복음4장1절)).isFalse();
        assertThat(요한복음3장16절.isSameChapter(마태복음3장16절)).isFalse();
        assertThat(요한복음3장16절.isSameChapter(null)).isFalse();
    }

    @Test
    void 구절_비교() {
        // Given
        VerseReference 요한복음3장16절 = VerseReference.of("요한복음", 3, 16);
        VerseReference 요한복음3장17절 = VerseReference.of("요한복음", 3, 17);
        VerseReference 요한복음4장1절 = VerseReference.of("요한복음", 4, 1);

        // When & Then
        assertThat(요한복음3장16절.compareVerse(요한복음3장17절)).isLessThan(0);  // 16절이 17절보다 앞
        assertThat(요한복음3장17절.compareVerse(요한복음3장16절)).isGreaterThan(0);  // 17절이 16절보다 뒤
        assertThat(요한복음3장16절.compareVerse(요한복음4장1절)).isLessThan(0);  // 3장이 4장보다 앞
    }
}