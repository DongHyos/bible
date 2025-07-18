package com.dong.bible.domain.verse;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 구절 검색을 담당하는 도메인 서비스
 * 
 * 도메인 서비스를 사용하는 이유:
 * - 구절 검색은 BibleVerse 단일 Entity에 속하지 않는 복잡한 비즈니스 로직
 * - 검색 알고리즘은 도메인 전문가와 논의가 필요한 핵심 비즈니스 로직
 * - 향후 다양한 검색 옵션 확장 가능성
 */
@Service
public class VerseSearchDomainService {
    
    /**
     * 키워드로 구절 검색
     * 
     * @param verses 검색 대상 구절 목록
     * @param keyword 검색 키워드
     * @return 검색된 구절 목록
     */
    public List<BibleVerse> searchByKeyword(List<BibleVerse> verses, String keyword) {
        if (verses == null || verses.isEmpty()) {
            return List.of();
        }
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        
        String trimmedKeyword = keyword.trim();
        
        return verses.stream()
            .filter(verse -> containsKeyword(verse, trimmedKeyword))
            .toList();
    }
    
    /**
     * 키워드로 구절 검색 (대소문자 구분 없음)
     * 
     * @param verses 검색 대상 구절 목록
     * @param keyword 검색 키워드
     * @return 검색된 구절 목록
     */
    public List<BibleVerse> searchByKeywordIgnoreCase(List<BibleVerse> verses, String keyword) {
        if (verses == null || verses.isEmpty()) {
            return List.of();
        }
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        
        String trimmedKeyword = keyword.trim().toLowerCase();
        
        return verses.stream()
            .filter(verse -> containsKeywordIgnoreCase(verse, trimmedKeyword))
            .toList();
    }
    
    /**
     * 완전 일치 검색
     * 
     * @param verses 검색 대상 구절 목록
     * @param keyword 검색 키워드
     * @return 검색된 구절 목록
     */
    public List<BibleVerse> searchByExactMatch(List<BibleVerse> verses, String keyword) {
        if (verses == null || verses.isEmpty()) {
            return List.of();
        }
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        
        String trimmedKeyword = keyword.trim();
        
        return verses.stream()
            .filter(verse -> isExactMatch(verse, trimmedKeyword))
            .toList();
    }
    
    /**
     * 여러 키워드로 AND 검색
     * 
     * @param verses 검색 대상 구절 목록
     * @param keywords 검색 키워드 목록
     * @return 모든 키워드를 포함하는 구절 목록
     */
    public List<BibleVerse> searchByMultipleKeywords(List<BibleVerse> verses, List<String> keywords) {
        if (verses == null || verses.isEmpty()) {
            return List.of();
        }
        
        if (keywords == null || keywords.isEmpty()) {
            return List.of();
        }
        
        List<String> trimmedKeywords = keywords.stream()
            .filter(keyword -> keyword != null && !keyword.trim().isEmpty())
            .map(String::trim)
            .toList();
        
        if (trimmedKeywords.isEmpty()) {
            return List.of();
        }
        
        return verses.stream()
            .filter(verse -> containsAllKeywords(verse, trimmedKeywords))
            .toList();
    }
    
    /**
     * 검색 결과 정렬 (관련성 순)
     * 
     * @param verses 검색된 구절 목록
     * @param keyword 검색 키워드
     * @return 관련성 순으로 정렬된 구절 목록
     */
    public List<BibleVerse> sortByRelevance(List<BibleVerse> verses, String keyword) {
        if (verses == null || verses.isEmpty()) {
            return List.of();
        }
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return verses;
        }
        
        String trimmedKeyword = keyword.trim();
        
        return verses.stream()
            .sorted((v1, v2) -> Integer.compare(
                calculateRelevanceScore(v2, trimmedKeyword),
                calculateRelevanceScore(v1, trimmedKeyword)
            ))
            .toList();
    }
    
    // ========================================
    // Private 헬퍼 메서드들 (비즈니스 로직)
    // ========================================
    
    /**
     * 구절이 키워드를 포함하는지 확인
     */
    private boolean containsKeyword(BibleVerse verse, String keyword) {
        if (verse == null || verse.getContent() == null) {
            return false;
        }
        
        String content = verse.getContent().getText();
        return content != null && content.contains(keyword);
    }
    
    /**
     * 구절이 키워드를 포함하는지 확인 (대소문자 구분 없음)
     */
    private boolean containsKeywordIgnoreCase(BibleVerse verse, String keyword) {
        if (verse == null || verse.getContent() == null) {
            return false;
        }
        
        String content = verse.getContent().getText();
        return content != null && content.toLowerCase().contains(keyword);
    }
    
    /**
     * 구절이 키워드와 완전 일치하는지 확인
     */
    private boolean isExactMatch(BibleVerse verse, String keyword) {
        if (verse == null || verse.getContent() == null) {
            return false;
        }
        
        String content = verse.getContent().getText();
        return content != null && content.equals(keyword);
    }
    
    /**
     * 구절이 모든 키워드를 포함하는지 확인
     */
    private boolean containsAllKeywords(BibleVerse verse, List<String> keywords) {
        if (verse == null || verse.getContent() == null) {
            return false;
        }
        
        String content = verse.getContent().getText();
        if (content == null) {
            return false;
        }
        
        return keywords.stream()
            .allMatch(content::contains);
    }
    
    /**
     * 관련성 점수 계산 (키워드 출현 횟수 기반)
     */
    private int calculateRelevanceScore(BibleVerse verse, String keyword) {
        if (verse == null || verse.getContent() == null) {
            return 0;
        }
        
        String content = verse.getContent().getText();
        if (content == null) {
            return 0;
        }
        
        int score = 0;
        int index = 0;
        
        while ((index = content.indexOf(keyword, index)) != -1) {
            score++;
            index += keyword.length();
        }
        
        return score;
    }
}