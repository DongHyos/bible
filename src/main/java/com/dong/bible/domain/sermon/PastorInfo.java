package com.dong.bible.domain.sermon;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

import java.util.Objects;

/**
 * 설교자 정보(설교자명, 교회명)를 나타내는 Value Object
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Value
public class PastorInfo {
    
    String pastorName;
    String churchName;
    
    /**
     * 정적 팩토리 메서드 - 설교자명과 교회명으로 생성
     */
    public static PastorInfo of(String pastorName, String churchName) {
        validatePastorName(pastorName);
        validateChurchName(churchName);
        
        return new PastorInfo(pastorName.trim(), churchName.trim());
    }
    
    /**
     * 설교자명 유효성 검증
     */
    private static void validatePastorName(String pastorName) {
        if (pastorName == null || pastorName.trim().isEmpty()) {
            throw new IllegalArgumentException("설교자명은 비어있을 수 없습니다");
        }
        
        String trimmed = pastorName.trim();
        if (trimmed.length() > 100) {
            throw new IllegalArgumentException("설교자명은 100자를 초과할 수 없습니다: " + trimmed.length() + "자");
        }
        
        // 특수문자 체크 (기본적인 한글, 영어, 숫자, 공백, 일부 특수문자만 허용)
        if (!trimmed.matches("^[가-힣a-zA-Z0-9\\s.·]+$")) {
            throw new IllegalArgumentException("설교자명에 허용되지 않는 문자가 포함되어 있습니다: " + trimmed);
        }
    }
    
    /**
     * 교회명 유효성 검증
     */
    private static void validateChurchName(String churchName) {
        if (churchName == null || churchName.trim().isEmpty()) {
            throw new IllegalArgumentException("교회명은 비어있을 수 없습니다");
        }
        
        String trimmed = churchName.trim();
        if (trimmed.length() > 150) {
            throw new IllegalArgumentException("교회명은 150자를 초과할 수 없습니다: " + trimmed.length() + "자");
        }
    }
    
    /**
     * 특정 설교자의 설교인지 확인
     */
    public boolean isPastorOf(String searchName) {
        if (searchName == null || searchName.trim().isEmpty()) {
            return false;
        }
        
        return pastorName.contains(searchName.trim());
    }
    
    /**
     * 특정 교회의 설교인지 확인
     */
    public boolean isFromChurch(String searchChurch) {
        if (searchChurch == null || searchChurch.trim().isEmpty()) {
            return false;
        }
        
        return churchName.contains(searchChurch.trim());
    }
    
    /**
     * 같은 설교자인지 확인
     */
    public boolean isSamePastor(PastorInfo other) {
        if (other == null) {
            return false;
        }
        
        return pastorName.equals(other.pastorName);
    }
    
    /**
     * 같은 교회인지 확인
     */
    public boolean isSameChurch(PastorInfo other) {
        if (other == null) {
            return false;
        }
        
        return churchName.equals(other.churchName);
    }
    
    /**
     * 표시용 문자열 생성 ("홍길동 목사 (사랑교회)")
     */
    public String toDisplayString() {
        return pastorName + " (" + churchName + ")";
    }
    
    /**
     * 짧은 표시용 문자열 (설교자명만)
     */
    public String getPastorOnly() {
        return pastorName;
    }
    
    /**
     * 교회명만 반환
     */
    public String getChurchOnly() {
        return churchName;
    }
    
    /**
     * 설교자명에서 직책 제거 ("홍길동 목사" → "홍길동")
     */
    public String getPastorNameWithoutTitle() {
        String name = pastorName;
        
        // 일반적인 직책들 제거
        String[] titles = {"목사", "전도사", "강도사", "선교사", "사모", "장로", "권사", "집사"};
        
        for (String title : titles) {
            if (name.endsWith(" " + title)) {
                name = name.substring(0, name.length() - title.length() - 1);
                break;
            } else if (name.endsWith(title)) {
                name = name.substring(0, name.length() - title.length());
                break;
            }
        }
        
        return name.trim();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PastorInfo that = (PastorInfo) o;
        return Objects.equals(pastorName, that.pastorName) && 
               Objects.equals(churchName, that.churchName);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(pastorName, churchName);
    }
    
    @Override
    public String toString() {
        return toDisplayString();
    }
}