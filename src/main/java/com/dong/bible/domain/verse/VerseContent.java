package com.dong.bible.domain.verse;

import lombok.Value;

@Value
public class VerseContent { // Value Object
    String text;

    // 정적 팩토리 메서드
    public static VerseContent of(String text) {
        if (text == null) {
            throw new IllegalArgumentException("구절 내용은 null일 수 없습니다");
        }

        String trimmedText = text.trim();
        if (trimmedText.isEmpty()) {
            throw new IllegalArgumentException("구절 내용은 비어있을 수 없습니다");
        }

        if (trimmedText.length() > 1000) {
            throw new IllegalArgumentException("구절 내용이 너무 깁니다 (최대 1000자)");
        }

        return new VerseContent(trimmedText);
    }

    // 빈 내용인지 확인
    public boolean isEmpty() {
        return text == null || text.trim().isEmpty();
    }

    // 비어있지 않은지 확인
    public boolean isNotEmpty() {
        return !isEmpty();
    }

    // 문자 길이
    public int getLength() {
        return text != null ? text.length() : 0;
    }

    // 유효한 검색 대상인지 (너무 짧지 않은지)
    public boolean isValidForSearch() {
        return isNotEmpty() && getLength() >= 3;
    }

    // 특정 길이 이상인지 확인
    public boolean isLongerThan(int length) {
        return getLength() > length;
    }

    // 특정 길이 이하인지 확인
    public boolean isShorterThan(int length) {
        return getLength() < length;
    }

    // 표시용 문자열 (긴 텍스트 줄임)
    public String toDisplayString() {
        if (text.length() <= 50) {
            return text;
        }
        return text.substring(0, 50) + "...";
    }

    // 텍스트 요약 (첫 N글자)
    public String getSummary(int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...";
    }
}
