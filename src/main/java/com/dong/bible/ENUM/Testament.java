package com.dong.bible.ENUM;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// Enum 정의
public enum Testament {
    구약("구약"),
    신약("신약");

    private final String value;

    Testament(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    // 문자열로부터 Enum 생성
    public static Testament fromString(String value) {
        if (value == null) {
            return null;
        }

        for (Testament testament : Testament.values()) {
            if (testament.getValue().equals(value)) {
                return testament;
            }
        }
        throw new IllegalArgumentException("Unknown testament: " + value);
    }

    // 안전한 변환 (예외 대신 null 반환)
    public static Testament fromStringSafe(String value) {
        if (value == null) {
            return null;
        }

        for (Testament testament : Testament.values()) {
            if (testament.getValue().equals(value)) {
                return testament;
            }
        }
        return null; // 찾지 못하면 null 반환
    }

    // 영어 이름으로도 찾을 수 있도록 (옵션)
    public static Testament fromEnglish(String englishName) {
        if (englishName == null) {
            return null;
        }

        switch (englishName.toLowerCase()) {
            case "old":
            case "old_testament":
            case "구약":
                return 구약;
            case "new":
            case "new_testament":
            case "신약":
                return 신약;
            default:
                throw new IllegalArgumentException("Unknown testament: " + englishName);
        }
    }

    // 모든 값을 List로 반환
    public static List<String> getAllValues() {
        return Arrays.stream(Testament.values())
                .map(Testament::getValue)
                .collect(Collectors.toList());
    }
}