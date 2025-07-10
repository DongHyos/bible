package com.dong.bible.domain.book;

import com.dong.bible.ENUM.Testament;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookTest {

    @Test
    void 정상적인_Book_생성_ID_없음() {
        // Given
        BookName bookName = BookName.of("요한복음");
        String abbreviation = "요";
        Testament testament = Testament.신약;
        int bookOrder = 43;
        int totalChapters = 21;
        Long categoryId = 6L;

        // When
        Book book = Book.of(bookName, abbreviation, testament, bookOrder, totalChapters, categoryId);

        // Then
        assertThat(book.getId()).isNull();
        assertThat(book.getBookName()).isEqualTo(bookName);
        assertThat(book.getAbbreviation()).isEqualTo("요");
        assertThat(book.getTestament()).isEqualTo(Testament.신약);
        assertThat(book.getBookOrder()).isEqualTo(43);
        assertThat(book.getTotalChapters()).isEqualTo(21);
        assertThat(book.getCategoryId()).isEqualTo(6L);
        assertThat(book.isNew()).isTrue();
        assertThat(book.isPersisted()).isFalse();
    }

    @Test
    void 정상적인_Book_생성_ID_있음() {
        // Given
        Long id = 123L;
        BookName bookName = BookName.of("창세기");
        String abbreviation = "창";
        Testament testament = Testament.구약;
        int bookOrder = 1;
        int totalChapters = 50;
        Long categoryId = 1L;

        // When
        Book book = Book.of(id, bookName, abbreviation, testament, bookOrder, totalChapters, categoryId);

        // Then
        assertThat(book.getId()).isEqualTo(123L);
        assertThat(book.getBookName()).isEqualTo(bookName);
        assertThat(book.getAbbreviation()).isEqualTo("창");
        assertThat(book.getTestament()).isEqualTo(Testament.구약);
        assertThat(book.getBookOrder()).isEqualTo(1);
        assertThat(book.getTotalChapters()).isEqualTo(50);
        assertThat(book.getCategoryId()).isEqualTo(1L);
        assertThat(book.isNew()).isFalse();
        assertThat(book.isPersisted()).isTrue();
    }

    @Test
    void Infrastructure에서_변환() {
        // Given
        Integer id = 43;
        String name = "요한복음";
        String abbr = "요";
        String testament = "신약";
        Integer bookOrder = 43;
        Integer chapters = 21;
        Integer categoryId = 6;

        // When
        Book book = Book.from(id, name, abbr, testament, bookOrder, chapters, categoryId);

        // Then
        assertThat(book.getId()).isEqualTo(43L);
        assertThat(book.getBookName().getName()).isEqualTo("요한복음");
        assertThat(book.getAbbreviation()).isEqualTo("요");
        assertThat(book.getTestament()).isEqualTo(Testament.신약);
        assertThat(book.getBookOrder()).isEqualTo(43);
        assertThat(book.getTotalChapters()).isEqualTo(21);
        assertThat(book.getCategoryId()).isEqualTo(6L);
    }

    @Test
    void Infrastructure에서_변환_구약() {
        // Given
        Integer id = 1;
        String name = "창세기";
        String abbr = "창";
        String testament = "구약";
        Integer bookOrder = 1;
        Integer chapters = 50;
        Integer categoryId = 1;

        // When
        Book book = Book.from(id, name, abbr, testament, bookOrder, chapters, categoryId);

        // Then
        assertThat(book.getTestament()).isEqualTo(Testament.구약);
        assertThat(book.isOldTestament()).isTrue();
        assertThat(book.isNewTestament()).isFalse();
    }

    @Test
    void 장_유효성_검증() {
        // Given
        Book book = createSampleBook("시편", 150); // 시편은 150장

        // When & Then
        assertThat(book.hasChapter(1)).isTrue();
        assertThat(book.hasChapter(75)).isTrue();
        assertThat(book.hasChapter(150)).isTrue();
        assertThat(book.hasChapter(0)).isFalse();
        assertThat(book.hasChapter(-1)).isFalse();
        assertThat(book.hasChapter(151)).isFalse();
    }

    @Test
    void 장_번호_검증_예외() {
        // Given
        Book book = createSampleBook("룻기", 4); // 룻기는 4장

        // When & Then
        // 정상적인 장
        book.validateChapter(1);
        book.validateChapter(4);

        // 잘못된 장
        assertThatThrownBy(() -> book.validateChapter(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("룻기는 4장까지만 있습니다. 요청된 장: 0");

        assertThatThrownBy(() -> book.validateChapter(5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("룻기는 4장까지만 있습니다. 요청된 장: 5");
    }

    @Test
    void 신구약_확인() {
        // Given
        Book 신약책 = createSampleBook("요한복음", 21, Testament.신약);
        Book 구약책 = createSampleBook("창세기", 50, Testament.구약);

        // When & Then
        assertThat(신약책.isNewTestament()).isTrue();
        assertThat(신약책.isOldTestament()).isFalse();

        assertThat(구약책.isNewTestament()).isFalse();
        assertThat(구약책.isOldTestament()).isTrue();
    }

    @Test
    void 표시용_문자열_생성() {
        // Given
        Book 신약책 = createSampleBook("요한복음", 21, Testament.신약);
        Book 구약책 = createSampleBook("창세기", 50, Testament.구약);

        // When
        String 신약표시 = 신약책.toDisplayString();
        String 구약표시 = 구약책.toDisplayString();

        // Then
        assertThat(신약표시).isEqualTo("요한복음 (신약, 21장)");
        assertThat(구약표시).isEqualTo("창세기 (구약, 50장)");
    }

    @Test
    void 축약형_표시_문자열() {
        // Given
        Book book = Book.of(
                BookName.of("요한복음"), "요", Testament.신약, 
                43, 21, 6L
        );

        // When
        String shortDisplay = book.toShortDisplayString();

        // Then
        assertThat(shortDisplay).isEqualTo("요 (21장)");
    }

    @ParameterizedTest
    @ValueSource(strings = {"창세기", "출애굽기", "시편", "요한복음", "로마서", "요한계시록"})
    void 다양한_성경책_생성(String bookName) {
        // Given
        BookName name = BookName.of(bookName);
        Testament testament = name.isNewTestament() ? Testament.신약 : Testament.구약;

        // When
        Book book = Book.of(name, name.getAbbreviation(), testament, 
                           name.getBookOrder(), 10, 1L);

        // Then
        assertThat(book.getBookName().getName()).isEqualTo(bookName);
        assertThat(book.getTestament()).isEqualTo(testament);
        assertThat(book.getBookOrder()).isEqualTo(name.getBookOrder());
    }

    @Test
    void equals_hashCode_테스트_ID_있음() {
        // Given
        BookName bookName = BookName.of("요한복음");
        Book book1 = Book.of(123L, bookName, "요", Testament.신약, 43, 21, 6L);
        Book book2 = Book.of(123L, bookName, "요", Testament.신약, 43, 21, 6L);
        Book book3 = Book.of(456L, bookName, "요", Testament.신약, 43, 21, 6L);

        // When & Then
        assertThat(book1).isEqualTo(book2);                // 같은 ID
        assertThat(book1).isNotEqualTo(book3);             // 다른 ID
        assertThat(book1.hashCode()).isEqualTo(book2.hashCode());
    }

    @Test
    void equals_hashCode_테스트_ID_없음() {
        // Given
        BookName bookName1 = BookName.of("요한복음");
        BookName bookName2 = BookName.of("마태복음");
        Book book1 = Book.of(bookName1, "요", Testament.신약, 43, 21, 6L);
        Book book2 = Book.of(bookName1, "요", Testament.신약, 43, 21, 6L);  // 같은 bookName
        Book book3 = Book.of(bookName2, "마", Testament.신약, 40, 28, 6L);  // 다른 bookName

        // When & Then
        assertThat(book1).isEqualTo(book2);               // 같은 bookName
        assertThat(book1).isNotEqualTo(book3);            // 다른 bookName
        assertThat(book1.hashCode()).isEqualTo(book2.hashCode());
    }

    @Test
    void toString_테스트() {
        // Given
        Book book = createSampleBook("요한복음", 21);

        // When
        String result = book.toString();

        // Then
        assertThat(result).isEqualTo("요한복음");
    }

    @Test
    void 잘못된_입력값_예외_처리() {
        // Given
        BookName validBookName = BookName.of("요한복음");

        // When & Then
        // ID 없는 버전
        assertThatThrownBy(() -> Book.of(null, "요", Testament.신약, 43, 21, 6L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("성경책 이름은 null일 수 없습니다");

        assertThatThrownBy(() -> Book.of(validBookName, "요", Testament.신약, 0, 21, 6L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("성경책 순서는 1-66 사이여야 합니다");

        assertThatThrownBy(() -> Book.of(validBookName, "요", Testament.신약, 67, 21, 6L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("성경책 순서는 1-66 사이여야 합니다");

        assertThatThrownBy(() -> Book.of(validBookName, "요", Testament.신약, 43, 0, 6L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("총 장수는 1 이상이어야 합니다");

        // ID 있는 버전
        assertThatThrownBy(() -> Book.of(null, validBookName, "요", Testament.신약, 43, 21, 6L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID는 null일 수 없습니다");
    }

    @Test
    void categoryId_null_처리() {
        // Given
        BookName bookName = BookName.of("요한복음");

        // When
        Book book = Book.of(bookName, "요", Testament.신약, 43, 21, null);

        // Then
        assertThat(book.getCategoryId()).isNull();
    }

    // 테스트 유틸리티 메서드들
    private Book createSampleBook(String name, int chapters) {
        BookName bookName = BookName.of(name);
        Testament testament = bookName.isNewTestament() ? Testament.신약 : Testament.구약;
        return Book.of(bookName, bookName.getAbbreviation(), testament, 
                      bookName.getBookOrder(), chapters, 1L);
    }

    private Book createSampleBook(String name, int chapters, Testament testament) {
        BookName bookName = BookName.of(name);
        return Book.of(bookName, bookName.getAbbreviation(), testament, 
                      bookName.getBookOrder(), chapters, 1L);
    }
}
