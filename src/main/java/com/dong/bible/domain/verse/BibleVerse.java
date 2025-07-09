package com.dong.bible.domain.verse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class BibleVerse { // entity
    Long id;                        // 식별자 (nullable)
    VerseReference reference;       // 책:장:절 정보
    VerseContent content;          // 구절 내용

    // 정적 팩토리 메서드 - 새로 생성 (id = null)
    public static BibleVerse of(VerseReference reference, VerseContent content) {
        if (reference == null) {
            throw new IllegalArgumentException("구절 참조는 null일 수 없습니다");
        }
        if (content == null) {
            throw new IllegalArgumentException("구절 내용은 null일 수 없습니다");
        }

        return new BibleVerse(null, reference, content);
    }

    // 정적 팩토리 메서드 - 기존 데이터에서 생성 (id 있음)
    public static BibleVerse of(Long id, VerseReference reference, VerseContent content) {
        if (id == null) {
            throw new IllegalArgumentException("ID는 null일 수 없습니다");
        }
        if (reference == null) {
            throw new IllegalArgumentException("구절 참조는 null일 수 없습니다");
        }
        if (content == null) {
            throw new IllegalArgumentException("구절 내용은 null일 수 없습니다");
        }

        return new BibleVerse(id, reference, content);
    }

    // KrvVerse Entity에서 변환 (나중에 Infrastructure에서 사용)
    public static BibleVerse from(Integer id, String bookName, Integer chapter, Integer verse, String text) {
        VerseReference ref = VerseReference.of(bookName, chapter, verse);
        VerseContent cont = VerseContent.of(text);
        return BibleVerse.of(id.longValue(), ref, cont);
    }

    // === 기존 Service 로직들을 Domain으로 이관 ===

    // 1. 특정 장에 속하는지 확인 (getChapter 로직)
    public boolean belongsToChapter(String bookName, Integer chapter) {
        if (bookName == null || chapter == null) {
            return false;
        }

        return reference.getBookName().equals(bookName) &&
                reference.getChapter() == chapter;
    }

    // 2. bookId 버전 (기존 코드 호환성)
    public boolean belongsToChapter(Integer bookId, Integer chapter) {
        // TODO: bookId -> bookName 변환 로직 필요
        // 지금은 임시로 bookId를 무시하고 chapter만 확인
        if (chapter == null) {
            return false;
        }
        return reference.getChapter() == chapter;
    }

    // 3. 정확한 구절인지 확인 (getVerse 로직)
    public boolean matchesExactly(String bookName, Integer chapter, Integer verse) {
        if (bookName == null || chapter == null || verse == null) {
            return false;
        }

        return reference.getBookName().equals(bookName) &&
                reference.getChapter() == chapter &&
                reference.getVerse() == verse;
    }

    // 4. bookId 버전 (기존 코드 호환성)
    public boolean matchesExactly(Integer bookId, Integer chapter, Integer verse) {
        // TODO: bookId -> bookName 변환 로직 필요
        if (chapter == null || verse == null) {
            return false;
        }
        return reference.getChapter() == chapter &&
                reference.getVerse() == verse;
    }

    // 5. 구절 범위에 포함되는지 확인 (getVerseRange 로직)
    public boolean isInVerseRange(Integer startVerse, Integer endVerse) {
        if (startVerse == null || endVerse == null) {
            return false;
        }
        if (startVerse > endVerse) {
            return false;
        }

        int currentVerse = reference.getVerse();
        return currentVerse >= startVerse && currentVerse <= endVerse;
    }

    // 6. 같은 장의 다른 구절과 순서 비교 (정렬용)
    public int compareVerseOrder(BibleVerse other) {
        if (other == null) {
            return 1;
        }

        // 같은 책, 같은 장이어야 비교 가능
        if (!reference.isSameChapter(other.reference)) {
            throw new IllegalArgumentException("같은 장의 구절끼리만 비교할 수 있습니다");
        }

        return Integer.compare(reference.getVerse(), other.reference.getVerse());
    }

    // === 기본적인 도메인 로직들 ===

    // 7. 신약인지 확인 (VerseReference에 위임)
    public boolean isNewTestament() {
        return reference.isNewTestament();
    }

    // 8. 구약인지 확인 (VerseReference에 위임)
    public boolean isOldTestament() {
        return reference.isOldTestament();
    }

    // 9. 검색 가능한지 확인 (VerseContent에 위임)
    public boolean isValidForSearch() {
        return content.isValidForSearch();
    }

    // 10. 표시용 문자열 생성
    public String toDisplayString() {
        return reference.toDisplayString() + " - " + content.toDisplayString();
    }

    // 11. 짧은 표시용 (구절 참조만)
    public String toReferenceString() {
        return reference.toDisplayString();
    }

    // 12. 같은 장인지 확인
    public boolean isSameChapter(BibleVerse other) {
        if (other == null) {
            return false;
        }
        return reference.isSameChapter(other.reference);
    }

    // 13. 새로 생성된 객체인지 (ID가 없는지)
    public boolean isNew() {
        return id == null;
    }

    // 14. 저장된 객체인지 (ID가 있는지)
    public boolean isPersisted() {
        return id != null;
    }

    // === equals/hashCode는 ID 기준 ===
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        BibleVerse that = (BibleVerse) obj;

        // ID가 둘 다 있으면 ID로 비교
        if (this.id != null && that.id != null) {
            return this.id.equals(that.id);
        }

        // ID가 없으면 reference로 비교 (같은 구절이면 같은 객체)
        return this.reference.equals(that.reference);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : reference.hashCode();
    }
}
