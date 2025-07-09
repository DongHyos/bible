package com.dong.bible.domain.book;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookNameTest {

    @Test
    void 정상적인_구약_성경책_생성() {
        // Given
        String bookName = "창세기";

        // When
        BookName book = BookName.of(bookName);

        // Then
        assertThat(book.getName()).isEqualTo("창세기");
        assertThat(book.isOldTestament()).isTrue();
        assertThat(book.isNewTestament()).isFalse();
    }

    @Test
    void 정상적인_신약_성경책_생성() {
        // Given
        String bookName = "요한복음";

        // When
        BookName book = BookName.of(bookName);

        // Then
        assertThat(book.getName()).isEqualTo("요한복음");
        assertThat(book.isNewTestament()).isTrue();
        assertThat(book.isOldTestament()).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "창세기", "출애굽기", "레위기", "민수기", "신명기",
        "요한복음", "마태복음", "로마서", "갈라디아서", "에베소서"
    })
    void 대표_성경책들_유효성_검증(String bookName) {
        // When
        BookName book = BookName.of(bookName);

        // Then
        assertThat(book.getName()).isEqualTo(bookName);
        assertThat(book.getBookOrder()).isBetween(1, 66);
    }

    @Test
    void 성경책_순서_번호_확인() {
        // Given & When & Then
        assertThat(BookName.of("창세기").getBookOrder()).isEqualTo(1);
        assertThat(BookName.of("출애굽기").getBookOrder()).isEqualTo(2);
        assertThat(BookName.of("말라기").getBookOrder()).isEqualTo(39);  // 구약 마지막

        assertThat(BookName.of("마태복음").getBookOrder()).isEqualTo(40); // 신약 첫번째
        assertThat(BookName.of("요한복음").getBookOrder()).isEqualTo(43);
        assertThat(BookName.of("요한계시록").getBookOrder()).isEqualTo(66); // 신약 마지막
    }

    @Test
    void 축약형_이름_생성() {
        // Given & When & Then
        assertThat(BookName.of("창세기").getAbbreviation()).isEqualTo("창세");
        assertThat(BookName.of("요한복음").getAbbreviation()).isEqualTo("요한");
        assertThat(BookName.of("마태복음").getAbbreviation()).isEqualTo("마태");
        assertThat(BookName.of("시편").getAbbreviation()).isEqualTo("시편");
        assertThat(BookName.of("욥기").getAbbreviation()).isEqualTo("욥기");
    }

    @Test
    void 안전한_생성_메서드_유효한_경우() {
        // Given
        String validBookName = "요한복음";

        // When
        BookName book = BookName.ofSafe(validBookName);

        // Then
        assertThat(book).isNotNull();
        assertThat(book.getName()).isEqualTo("요한복음");
    }

    @Test
    void 안전한_생성_메서드_유효하지_않은_경우() {
        // Given
        String invalidBookName = "유효하지않은책";

        // When
        BookName book = BookName.ofSafe(invalidBookName);

        // Then
        assertThat(book).isNull();
    }

    @Test
    void 전체_성경책_목록_확인() {
        // When
        List<String> oldTestamentBooks = BookName.getOldTestamentBooks();
        List<String> newTestamentBooks = BookName.getNewTestamentBooks();
        List<String> allBooks = BookName.getAllBooks();

        // Then
        assertThat(oldTestamentBooks).hasSize(39);
        assertThat(newTestamentBooks).hasSize(27);
        assertThat(allBooks).hasSize(66);

        // 첫 번째와 마지막 책 확인
        assertThat(oldTestamentBooks.get(0)).isEqualTo("창세기");
        assertThat(oldTestamentBooks.get(38)).isEqualTo("말라기");
        assertThat(newTestamentBooks.get(0)).isEqualTo("마태복음");
        assertThat(newTestamentBooks.get(26)).isEqualTo("요한계시록");
    }

    @Test
    void 잘못된_입력값_예외_처리() {
        // When & Then
        assertThatThrownBy(() -> BookName.of(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("성경책 이름은 필수입니다");

        assertThatThrownBy(() -> BookName.of(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("성경책 이름은 필수입니다");

        assertThatThrownBy(() -> BookName.of("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("성경책 이름은 필수입니다");

        assertThatThrownBy(() -> BookName.of("유효하지않은책"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 성경책 이름입니다");
    }

    @Test
    void 공백_문자_처리() {
        // Given
        String bookNameWithSpaces = "  요한복음  ";

        // When
        BookName book = BookName.of(bookNameWithSpaces);

        // Then
        assertThat(book.getName()).isEqualTo("요한복음"); // 공백 제거됨
        assertThat(book.isNewTestament()).isTrue();
    }

    @Test
    void equals_hashCode_테스트() {
        // Given
        BookName book1 = BookName.of("요한복음");
        BookName book2 = BookName.of("요한복음");
        BookName book3 = BookName.of("마태복음");

        // When & Then
        assertThat(book1).isEqualTo(book2);
        assertThat(book1).isNotEqualTo(book3);
        assertThat(book1.hashCode()).isEqualTo(book2.hashCode());

        // 자기 자신과 비교
        assertThat(book1).isEqualTo(book1);

        // null과 비교
        assertThat(book1).isNotEqualTo(null);
    }

    @Test
    void toString_테스트() {
        // Given
        BookName book = BookName.of("요한복음");

        // When
        String result = book.toString();

        // Then
        assertThat(result).isEqualTo("요한복음");
    }

    @Test
    void 성경책_분류_경계값_테스트() {
        // Given
        BookName 구약마지막 = BookName.of("말라기");
        BookName 신약첫번째 = BookName.of("마태복음");

        // When & Then
        assertThat(구약마지막.isOldTestament()).isTrue();
        assertThat(구약마지막.isNewTestament()).isFalse();
        assertThat(구약마지막.getBookOrder()).isEqualTo(39);

        assertThat(신약첫번째.isNewTestament()).isTrue();
        assertThat(신약첫번째.isOldTestament()).isFalse();
        assertThat(신약첫번째.getBookOrder()).isEqualTo(40);
    }
}
