package com.dong.bible.domain.sermon;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 설교 태그 목록을 나타내는 Value Object
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Value
public class SermonTags {
    
    List<String> tags;
    
    /**
     * 정적 팩토리 메서드 - 태그 목록으로 생성
     */
    public static SermonTags of(List<String> tags) {
        List<String> validatedTags = validateAndCleanTags(tags);
        return new SermonTags(validatedTags);
    }
    
    /**
     * 정적 팩토리 메서드 - 태그 배열로 생성
     */
    public static SermonTags of(String... tags) {
        if (tags == null || tags.length == 0) {
            return empty();
        }
        
        return of(Arrays.asList(tags));
    }
    
    /**
     * 정적 팩토리 메서드 - 빈 태그 목록 생성
     */
    public static SermonTags empty() {
        return new SermonTags(Collections.emptyList());
    }
    
    /**
     * 정적 팩토리 메서드 - 쉼표로 구분된 문자열에서 생성
     */
    public static SermonTags fromCommaSeparated(String tagsString) {
        if (tagsString == null || tagsString.trim().isEmpty()) {
            return empty();
        }
        
        List<String> tagList = Arrays.stream(tagsString.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .collect(Collectors.toList());
        
        return of(tagList);
    }
    
    /**
     * 태그 목록 유효성 검증 및 정리
     */
    private static List<String> validateAndCleanTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<String> cleanedTags = tags.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .map(SermonTags::validateSingleTag)
                .distinct() // 중복 제거
                .collect(Collectors.toList());
        
        if (cleanedTags.size() > 20) {
            throw new IllegalArgumentException("태그는 최대 20개까지만 허용됩니다: " + cleanedTags.size() + "개");
        }
        
        return Collections.unmodifiableList(cleanedTags);
    }
    
    /**
     * 단일 태그 유효성 검증
     */
    private static String validateSingleTag(String tag) {
        if (tag.length() > 30) {
            throw new IllegalArgumentException("태그는 30자를 초과할 수 없습니다: " + tag);
        }
        
        // 특수문자 체크 (기본적인 한글, 영어, 숫자만 허용)
        if (!tag.matches("^[가-힣a-zA-Z0-9]+$")) {
            throw new IllegalArgumentException("태그에는 한글, 영어, 숫자만 허용됩니다: " + tag);
        }
        
        return tag;
    }
    
    /**
     * 태그가 비어있는지 확인
     */
    public boolean isEmpty() {
        return tags.isEmpty();
    }
    
    /**
     * 태그 개수 반환
     */
    public int size() {
        return tags.size();
    }
    
    /**
     * 특정 태그를 포함하는지 확인
     */
    public boolean contains(String tag) {
        if (tag == null || tag.trim().isEmpty()) {
            return false;
        }
        
        return tags.contains(tag.trim());
    }
    
    /**
     * 여러 태그 중 하나라도 포함하는지 확인
     */
    public boolean containsAny(List<String> searchTags) {
        if (searchTags == null || searchTags.isEmpty()) {
            return false;
        }
        
        return searchTags.stream()
                .anyMatch(this::contains);
    }
    
    /**
     * 모든 태그를 포함하는지 확인
     */
    public boolean containsAll(List<String> searchTags) {
        if (searchTags == null || searchTags.isEmpty()) {
            return true;
        }
        
        return searchTags.stream()
                .allMatch(this::contains);
    }
    
    /**
     * 태그 추가 (새로운 SermonTags 반환)
     */
    public SermonTags addTag(String tag) {
        if (tag == null || tag.trim().isEmpty()) {
            return this;
        }
        
        String cleanTag = validateSingleTag(tag.trim());
        
        if (contains(cleanTag)) {
            return this; // 이미 존재하면 그대로 반환
        }
        
        List<String> newTags = new ArrayList<>(tags);
        newTags.add(cleanTag);
        
        return of(newTags);
    }
    
    /**
     * 태그 제거 (새로운 SermonTags 반환)
     */
    public SermonTags removeTag(String tag) {
        if (tag == null || tag.trim().isEmpty() || !contains(tag.trim())) {
            return this;
        }
        
        List<String> newTags = tags.stream()
                .filter(t -> !t.equals(tag.trim()))
                .collect(Collectors.toList());
        
        return of(newTags);
    }
    
    /**
     * 여러 태그 추가
     */
    public SermonTags addTags(List<String> newTags) {
        if (newTags == null || newTags.isEmpty()) {
            return this;
        }
        
        List<String> combinedTags = new ArrayList<>(tags);
        combinedTags.addAll(newTags);
        
        return of(combinedTags);
    }
    
    /**
     * 태그 목록을 불변 리스트로 반환
     */
    public List<String> getTagList() {
        return Collections.unmodifiableList(tags);
    }
    
    /**
     * 쉼표로 구분된 문자열로 변환
     */
    public String toCommaSeparatedString() {
        return String.join(", ", tags);
    }
    
    /**
     * 해시태그 형태 문자열로 변환
     */
    public String toHashtagString() {
        return tags.stream()
                .map(tag -> "#" + tag)
                .collect(Collectors.joining(" "));
    }
    
    /**
     * 특정 카테고리 태그만 필터링
     */
    public SermonTags filterByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return this;
        }
        
        List<String> filteredTags = tags.stream()
                .filter(tag -> tag.toLowerCase().contains(category.toLowerCase()))
                .collect(Collectors.toList());
        
        return of(filteredTags);
    }
    
    /**
     * 주요 태그들 반환 (최대 5개)
     */
    public List<String> getMainTags() {
        return tags.stream()
                .limit(5)
                .collect(Collectors.toList());
    }
    
    /**
     * 다른 SermonTags와 공통 태그 반환
     */
    public SermonTags getCommonTags(SermonTags other) {
        if (other == null || other.isEmpty()) {
            return empty();
        }
        
        List<String> commonTags = tags.stream()
                .filter(other::contains)
                .collect(Collectors.toList());
        
        return of(commonTags);
    }
    
    /**
     * 표시용 문자열 생성
     */
    public String toDisplayString() {
        if (isEmpty()) {
            return "태그 없음";
        }
        
        if (size() <= 3) {
            return toCommaSeparatedString();
        } else {
            List<String> displayTags = tags.subList(0, 3);
            return String.join(", ", displayTags) + " 외 " + (size() - 3) + "개";
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SermonTags that = (SermonTags) o;
        return Objects.equals(tags, that.tags);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(tags);
    }
    
    @Override
    public String toString() {
        return toDisplayString();
    }
}