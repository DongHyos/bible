package com.dong.bible.domain.dailyverse;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 오늘의 말씀 메시지(제목 + 설명)를 나타내는 Value Object
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DailyMessage {
    
    private final String title;
    private final String description;
    
    /**
     * 정적 팩토리 메서드 - 제목과 설명으로 생성
     */
    public static DailyMessage of(String title, String description) {
        return new DailyMessage(
            validateAndClean(title, "제목"),
            validateAndClean(description, "설명")
        );
    }
    
    /**
     * 정적 팩토리 메서드 - 제목만으로 생성 (설명 없음)
     */
    public static DailyMessage withTitle(String title) {
        return of(title, null);
    }
    
    /**
     * 정적 팩토리 메서드 - 빈 메시지 생성
     */
    public static DailyMessage empty() {
        return new DailyMessage(null, null);
    }
    
    /**
     * 문자열 유효성 검증 및 정리
     */
    private static String validateAndClean(String text, String fieldName) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        
        String cleaned = text.trim();
        
        // 길이 제한
        if (fieldName.equals("제목") && cleaned.length() > 100) {
            throw new IllegalArgumentException("제목은 100자를 초과할 수 없습니다: " + cleaned.length() + "자");
        }
        
        if (fieldName.equals("설명") && cleaned.length() > 1000) {
            throw new IllegalArgumentException("설명은 1000자를 초과할 수 없습니다: " + cleaned.length() + "자");
        }
        
        return cleaned;
    }
    
    /**
     * 제목이 있는지 확인
     */
    public boolean hasTitle() {
        return title != null && !title.isEmpty();
    }
    
    /**
     * 설명이 있는지 확인
     */
    public boolean hasDescription() {
        return description != null && !description.isEmpty();
    }
    
    /**
     * 비어있는 메시지인지 확인
     */
    public boolean isEmpty() {
        return !hasTitle() && !hasDescription();
    }
    
    /**
     * 제목과 설명을 합친 표시용 문자열
     */
    public String toDisplayString() {
        if (isEmpty()) {
            return "";
        }
        
        if (hasTitle() && hasDescription()) {
            return title + " - " + description;
        } else if (hasTitle()) {
            return title;
        } else {
            return description;
        }
    }
    
    /**
     * 제목 반환 (없으면 빈 문자열)
     */
    public String getTitleOrEmpty() {
        return title != null ? title : "";
    }
    
    /**
     * 설명 반환 (없으면 빈 문자열)
     */
    public String getDescriptionOrEmpty() {
        return description != null ? description : "";
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DailyMessage that = (DailyMessage) o;
        return Objects.equals(title, that.title) && 
               Objects.equals(description, that.description);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(title, description);
    }
    
    @Override
    public String toString() {
        return toDisplayString();
    }
}