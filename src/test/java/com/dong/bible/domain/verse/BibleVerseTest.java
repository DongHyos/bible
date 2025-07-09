package com.dong.bible.domain.verse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class BibleVerseTest {
    @Test
    void 정상적인_구절_생성_ID_없음() {
        // Given
        VerseReference reference = VerseReference.of("요한복음", 3, 16);
        VerseContent content = VerseContent.of("하나님이 세상을 이처럼 사랑하사");

        // When
        BibleVerse verse = BibleVerse.of(reference, content);

        // Then
        assertThat(verse.getId()).isNull();
        assertThat(verse.getReference()).isEqualTo(reference);
        assertThat(verse.getContent()).isEqualTo(content);
        assertThat(verse.isNew()).isTrue();
        assertThat(verse.isPersisted()).isFalse();
    }

    @Test
    void 정상적인_구절_생성_ID_있음() {
        // Given
        Long id = 123L;
        VerseReference reference = VerseReference.of("요한복음", 3, 16);
        VerseContent content = VerseContent.of("하나님이 세상을 이처럼 사랑하사");

        // When
        BibleVerse verse = BibleVerse.of(id, reference, content);

        // Then
        assertThat(verse.getId()).isEqualTo(123L);
        assertThat(verse.getReference()).isEqualTo(reference);
        assertThat(verse.getContent()).isEqualTo(content);
        assertThat(verse.isNew()).isFalse();
        assertThat(verse.isPersisted()).isTrue();
    }

    @Test
    void KrvVerse에서_변환() {
        // Given
        Integer id = 123;
        String bookName = "요한복음";
        Integer chapter = 3;
        Integer verse = 16;
        String text = "하나님이 세상을 이처럼 사랑하사";

        // When
        BibleVerse bibleVerse = BibleVerse.from(id, bookName, chapter, verse, text);

        // Then
        assertThat(bibleVerse.getId()).isEqualTo(123L);
        assertThat(bibleVerse.getReference().getBookName()).isEqualTo("요한복음");
        assertThat(bibleVerse.getReference().getChapter()).isEqualTo(3);
        assertThat(bibleVerse.getReference().getVerse()).isEqualTo(16);
        assertThat(bibleVerse.getContent().getText()).isEqualTo("하나님이 세상을 이처럼 사랑하사");
    }

    @Test
    void 특정_장에_속하는지_확인_bookName() {
        // Given
        VerseReference reference = VerseReference.of("요한복음", 3, 16);
        VerseContent content = VerseContent.of("하나님이 세상을 이처럼 사랑하사");
        BibleVerse verse = BibleVerse.of(reference, content);

        // When & Then
        assertThat(verse.belongsToChapter("요한복음", 3)).isTrue();
        assertThat(verse.belongsToChapter("요한복음", 4)).isFalse();
        assertThat(verse.belongsToChapter("마태복음", 3)).isFalse();
        assertThat(verse.belongsToChapter((String)null, 3)).isFalse();
        assertThat(verse.belongsToChapter("요한복음", null)).isFalse();
    }

    @Test
    void 특정_장에_속하는지_확인_bookId() {
        // Given
        VerseReference reference = VerseReference.of("요한복음", 3, 16);
        VerseContent content = VerseContent.of("하나님이 세상을 이처럼 사랑하사");
        BibleVerse verse = BibleVerse.of(reference, content);

        // When & Then (bookId는 임시로 무시하고 chapter만 확인)
        assertThat(verse.belongsToChapter(1, 3)).isTrue();
        assertThat(verse.belongsToChapter(1, 4)).isFalse();
        assertThat(verse.belongsToChapter(2, 3)).isTrue();  // bookId 무시됨
        assertThat(verse.belongsToChapter(1, null)).isFalse();
    }

    @Test
    void 정확한_구절인지_확인_bookName() {
        // Given
        VerseReference reference = VerseReference.of("요한복음", 3, 16);
        VerseContent content = VerseContent.of("하나님이 세상을 이처럼 사랑하사");
        BibleVerse verse = BibleVerse.of(reference, content);

        // When & Then
        assertThat(verse.matchesExactly("요한복음", 3, 16)).isTrue();
        assertThat(verse.matchesExactly("요한복음", 3, 17)).isFalse();
        assertThat(verse.matchesExactly("요한복음", 4, 16)).isFalse();
        assertThat(verse.matchesExactly("마태복음", 3, 16)).isFalse();
        assertThat(verse.matchesExactly((String)null, 3, 16)).isFalse();
        assertThat(verse.matchesExactly("요한복음", null, 16)).isFalse();
        assertThat(verse.matchesExactly("요한복음", 3, null)).isFalse();
    }

    @Test
    void 정확한_구절인지_확인_bookId() {
        // Given
        VerseReference reference = VerseReference.of("요한복음", 3, 16);
        VerseContent content = VerseContent.of("하나님이 세상을 이처럼 사랑하사");
        BibleVerse verse = BibleVerse.of(reference, content);

        // When & Then (bookId는 임시로 무시)
        assertThat(verse.matchesExactly(1, 3, 16)).isTrue();
        assertThat(verse.matchesExactly(1, 3, 17)).isFalse();
        assertThat(verse.matchesExactly(1, 4, 16)).isFalse();
        assertThat(verse.matchesExactly(2, 3, 16)).isTrue();  // bookId 무시됨
        assertThat(verse.matchesExactly(1, null, 16)).isFalse();
        assertThat(verse.matchesExactly(1, 3, null)).isFalse();
    }

    @Test
    void 구절_범위에_포함되는지_확인() {
        // Given
        VerseReference reference = VerseReference.of("요한복음", 3, 16);
        VerseContent content = VerseContent.of("하나님이 세상을 이처럼 사랑하사");
        BibleVerse verse = BibleVerse.of(reference, content);

        // When & Then
        assertThat(verse.isInVerseRange(1, 20)).isTrue();     // 16은 1~20 범위에 포함
        assertThat(verse.isInVerseRange(16, 16)).isTrue();    // 정확히 16
        assertThat(verse.isInVerseRange(10, 15)).isFalse();   // 16은 10~15 범위에 미포함
        assertThat(verse.isInVerseRange(17, 20)).isFalse();   // 16은 17~20 범위에 미포함
        assertThat(verse.isInVerseRange(null, 20)).isFalse(); // null 처리
        assertThat(verse.isInVerseRange(1, null)).isFalse();  // null 처리
        assertThat(verse.isInVerseRange(20, 10)).isFalse();   // 잘못된 범위
    }

    @Test
    void 같은_장_구절들_순서_비교() {
        // Given
        VerseReference ref1 = VerseReference.of("요한복음", 3, 16);
        VerseReference ref2 = VerseReference.of("요한복음", 3, 17);
        VerseReference ref3 = VerseReference.of("요한복음", 3, 15);

        VerseContent content = VerseContent.of("내용");
        BibleVerse verse16 = BibleVerse.of(ref1, content);
        BibleVerse verse17 = BibleVerse.of(ref2, content);
        BibleVerse verse15 = BibleVerse.of(ref3, content);

        // When & Then
        assertThat(verse16.compareVerseOrder(verse17)).isLessThan(0);    // 16 < 17
        assertThat(verse17.compareVerseOrder(verse16)).isGreaterThan(0); // 17 > 16
        assertThat(verse16.compareVerseOrder(verse16)).isEqualTo(0);     // 16 == 16
        assertThat(verse15.compareVerseOrder(verse16)).isLessThan(0);    // 15 < 16
    }

    @Test
    void 다른_장_구절_비교시_예외() {
        // Given
        VerseReference ref1 = VerseReference.of("요한복음", 3, 16);
        VerseReference ref2 = VerseReference.of("요한복음", 4, 1);

        VerseContent content = VerseContent.of("내용");
        BibleVerse verse1 = BibleVerse.of(ref1, content);
        BibleVerse verse2 = BibleVerse.of(ref2, content);

        // When & Then
        assertThatThrownBy(() -> verse1.compareVerseOrder(verse2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("같은 장의 구절끼리만 비교할 수 있습니다");
    }

    @Test
    void 신구약_확인() {
        // Given
        VerseReference 신약 = VerseReference.of("요한복음", 3, 16);
        VerseReference 구약 = VerseReference.of("창세기", 1, 1);

        VerseContent content = VerseContent.of("내용");
        BibleVerse 신약구절 = BibleVerse.of(신약, content);
        BibleVerse 구약구절 = BibleVerse.of(구약, content);

        // When & Then
        assertThat(신약구절.isNewTestament()).isTrue();
        assertThat(신약구절.isOldTestament()).isFalse();

        assertThat(구약구절.isNewTestament()).isFalse();
        assertThat(구약구절.isOldTestament()).isTrue();
    }

    @Test
    void 검색_가능_여부_확인() {
        // Given
        VerseContent 긴내용 = VerseContent.of("하나님이 세상을 이처럼 사랑하사");
        VerseContent 짧은내용 = VerseContent.of("짧");

        VerseReference reference = VerseReference.of("요한복음", 3, 16);
        BibleVerse 긴구절 = BibleVerse.of(reference, 긴내용);
        BibleVerse 짧은구절 = BibleVerse.of(reference, 짧은내용);

        // When & Then
        assertThat(긴구절.isValidForSearch()).isTrue();
        assertThat(짧은구절.isValidForSearch()).isFalse();
    }

    @Test
    void 표시용_문자열_생성() {
        // Given
        VerseReference reference = VerseReference.of("요한복음", 3, 16);
        VerseContent content = VerseContent.of("하나님이 세상을 이처럼 사랑하사");
        BibleVerse verse = BibleVerse.of(reference, content);

        // When
        String displayString = verse.toDisplayString();
        String referenceString = verse.toReferenceString();

        // Then
        assertThat(displayString).isEqualTo("요한복음 3:16 - 하나님이 세상을 이처럼 사랑하사");
        assertThat(referenceString).isEqualTo("요한복음 3:16");
    }

    @Test
    void 같은_장인지_확인() {
        // Given
        VerseReference ref1 = VerseReference.of("요한복음", 3, 16);
        VerseReference ref2 = VerseReference.of("요한복음", 3, 17);
        VerseReference ref3 = VerseReference.of("요한복음", 4, 1);

        VerseContent content = VerseContent.of("내용");
        BibleVerse verse1 = BibleVerse.of(ref1, content);
        BibleVerse verse2 = BibleVerse.of(ref2, content);
        BibleVerse verse3 = BibleVerse.of(ref3, content);

        // When & Then
        assertThat(verse1.isSameChapter(verse2)).isTrue();   // 같은 장
        assertThat(verse1.isSameChapter(verse3)).isFalse();  // 다른 장
        assertThat(verse1.isSameChapter(null)).isFalse();    // null 처리
    }

    @Test
    void equals_hashCode_테스트_ID_있음() {
        // Given
        VerseReference reference = VerseReference.of("요한복음", 3, 16);
        VerseContent content = VerseContent.of("하나님이 세상을 이처럼 사랑하사");

        BibleVerse verse1 = BibleVerse.of(123L, reference, content);
        BibleVerse verse2 = BibleVerse.of(123L, reference, content);
        BibleVerse verse3 = BibleVerse.of(456L, reference, content);

        // When & Then
        assertThat(verse1).isEqualTo(verse2);                // 같은 ID
        assertThat(verse1).isNotEqualTo(verse3);             // 다른 ID
        assertThat(verse1.hashCode()).isEqualTo(verse2.hashCode());
    }

    @Test
    void equals_hashCode_테스트_ID_없음() {
        // Given
        VerseReference ref1 = VerseReference.of("요한복음", 3, 16);
        VerseReference ref2 = VerseReference.of("요한복음", 3, 17);
        VerseContent content = VerseContent.of("내용");

        BibleVerse verse1 = BibleVerse.of(ref1, content);
        BibleVerse verse2 = BibleVerse.of(ref1, content);  // 같은 reference
        BibleVerse verse3 = BibleVerse.of(ref2, content);  // 다른 reference

        // When & Then
        assertThat(verse1).isEqualTo(verse2);               // 같은 reference
        assertThat(verse1).isNotEqualTo(verse3);            // 다른 reference
        assertThat(verse1.hashCode()).isEqualTo(verse2.hashCode());
    }

    @Test
    void 잘못된_입력값_예외_처리() {
        // Given
        VerseReference reference = VerseReference.of("요한복음", 3, 16);
        VerseContent content = VerseContent.of("하나님이 세상을 이처럼 사랑하사");

        // When & Then
        // ID 없는 버전
        assertThatThrownBy(() -> BibleVerse.of(null, content))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("구절 참조는 null일 수 없습니다");

        assertThatThrownBy(() -> BibleVerse.of(reference, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("구절 내용은 null일 수 없습니다");

        // ID 있는 버전
        assertThatThrownBy(() -> BibleVerse.of(null, reference, content))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID는 null일 수 없습니다");

        assertThatThrownBy(() -> BibleVerse.of(123L, null, content))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("구절 참조는 null일 수 없습니다");

        assertThatThrownBy(() -> BibleVerse.of(123L, reference, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("구절 내용은 null일 수 없습니다");
    }
}