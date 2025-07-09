package com.dong.bible.domain.verse;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class VerseContentTest {
    @Test
    void 정상적인_구절_내용_생성() {
        // Given & When
        VerseContent content = VerseContent.of("하나님이 세상을 이처럼 사랑하사");

        // Then
        assertThat(content.getText()).isEqualTo("하나님이 세상을 이처럼 사랑하사");
        assertThat(content.isEmpty()).isFalse();
        assertThat(content.isNotEmpty()).isTrue();
    }

    @Test
    void 빈_내용_확인() {
        // Given
        VerseContent normalContent = VerseContent.of("하나님이 세상을 사랑하사");

        // When & Then
        assertThat(normalContent.isEmpty()).isFalse();
        assertThat(normalContent.isNotEmpty()).isTrue();
    }

    @Test
    void 길이_관련_메서드() {
        // Given
        VerseContent shortContent = VerseContent.of("짧은 구절");
        VerseContent longContent = VerseContent.of("하나님이 세상을 이처럼 사랑하사 독생자를 주셨으니");

        // When & Then
        assertThat(shortContent.getLength()).isEqualTo(5); // 띄워쓰기 포함
        assertThat(longContent.getLength()).isEqualTo(27);

        assertThat(shortContent.isLongerThan(3)).isTrue();
        assertThat(shortContent.isLongerThan(5)).isFalse();

        assertThat(shortContent.isShorterThan(10)).isTrue();
        assertThat(longContent.isShorterThan(10)).isFalse();
    }

    @Test
    void 검색_가능_여부_확인() {
        // Given
        VerseContent valid = VerseContent.of("하나님이 세상을 사랑하사");
        VerseContent tooShort = VerseContent.of("짧");  // 1글자
        VerseContent barelyValid = VerseContent.of("사랑해");  // 3글자

        // When & Then
        assertThat(valid.isValidForSearch()).isTrue();
        assertThat(tooShort.isValidForSearch()).isFalse();
        assertThat(barelyValid.isValidForSearch()).isTrue();
    }

    @Test
    void 표시용_문자열_생성() {
        // Given
        VerseContent shortContent = VerseContent.of("하나님이 세상을 사랑하사");
        VerseContent longContent = VerseContent.of("하나님이 세상을 이처럼 사랑하사 독생자를 주셨으니 이는 그를 믿는 자마다 멸망하지 않고 영생을 얻게 하려 하심이라");

        // When
        String shortDisplay = shortContent.toDisplayString();
        String longDisplay = longContent.toDisplayString();
        String summary = longContent.getSummary(20);

        // Then
        assertThat(shortDisplay).isEqualTo("하나님이 세상을 사랑하사");
        assertThat(longDisplay).hasSize(53);  // 50글자 + "..." = 53
        assertThat(longDisplay).endsWith("...");

        assertThat(summary).hasSize(23);  // 20글자 + "..." = 23
        assertThat(summary).endsWith("...");
        assertThat(summary).startsWith("하나님이 세상을 이처럼 사랑하사");
    }

    @Test
    void 잘못된_입력값_예외_처리() {
        // Given & When & Then

        // 1. null 내용
        assertThatThrownBy(() -> VerseContent.of(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("구절 내용은 null일 수 없습니다");

        // 2. 빈 문자열
        assertThatThrownBy(() -> VerseContent.of(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("구절 내용은 비어있을 수 없습니다");

        // 3. 공백만 있는 문자열
        assertThatThrownBy(() -> VerseContent.of("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("구절 내용은 비어있을 수 없습니다");

        // 4. 너무 긴 문자열 (1000자 초과)
        String tooLongText = "가".repeat(1001);
        assertThatThrownBy(() -> VerseContent.of(tooLongText))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("구절 내용이 너무 깁니다");
    }
}