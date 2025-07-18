package com.dong.bible.domain.sermon;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 설교를 나타내는 Aggregate Root
 * 설교의 모든 정보와 비즈니스 로직을 관리합니다.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Sermon {
    
    private final Long id;                          // 식별자 (nullable for new entities)
    private final SermonInfo info;                  // 기본 정보 (제목, 날짜)
    private final PastorInfo pastor;                // 설교자 정보
    private final SermonMedia media;                // 미디어 정보
    private final SermonStats stats;                // 통계 정보
    private final SermonTags tags;                  // 태그 정보
    private final String content;                   // 설교 내용
    private final List<SermonVerse> verses;         // 관련 구절들
    
    /**
     * 정적 팩토리 메서드 - 새로운 설교 생성 (ID 없음)
     */
    public static Sermon of(SermonInfo info, PastorInfo pastor, String content) {
        validateRequiredFields(info, pastor);
        
        return new Sermon(
                null,                           // 새 설교는 ID 없음
                info,
                pastor,
                SermonMedia.empty(),            // 미디어 정보는 나중에 추가
                SermonStats.initial(),          // 초기 통계 (0, 0)
                SermonTags.empty(),             // 태그는 나중에 추가
                validateContent(content),
                new ArrayList<>()               // 빈 구절 목록
        );
    }
    
    /**
     * 정적 팩토리 메서드 - 전체 정보로 생성 (새 설교)
     */
    public static Sermon of(SermonInfo info, PastorInfo pastor, SermonMedia media, 
                           SermonTags tags, String content) {
        validateRequiredFields(info, pastor);
        
        return new Sermon(
                null,
                info,
                pastor,
                media != null ? media : SermonMedia.empty(),
                SermonStats.initial(),
                tags != null ? tags : SermonTags.empty(),
                validateContent(content),
                new ArrayList<>()
        );
    }
    
    /**
     * 정적 팩토리 메서드 - 기존 설교 생성 (ID 있음, Infrastructure에서 사용)
     */
    public static Sermon of(Long id, SermonInfo info, PastorInfo pastor, SermonMedia media,
                           SermonStats stats, SermonTags tags, String content, List<SermonVerse> verses) {
        validateRequiredFields(info, pastor);
        
        if (id == null) {
            throw new IllegalArgumentException("ID는 null일 수 없습니다");
        }
        
        return new Sermon(
                id,
                info,
                pastor,
                media != null ? media : SermonMedia.empty(),
                stats != null ? stats : SermonStats.initial(),
                tags != null ? tags : SermonTags.empty(),
                validateContent(content),
                verses != null ? Collections.unmodifiableList(new ArrayList<>(verses)) : Collections.emptyList()
        );
    }
    
    /**
     * Infrastructure에서 사용하는 팩토리 메서드 (Entity에서 Domain으로 변환)
     */
    public static Sermon from(Long id, String title, LocalDate sermonDate, String pastorName, 
                             String churchName, String youtubeUrl, String thumbnailUrl, 
                             String content, Integer durationMinutes, String tagsJson,
                             Integer viewCount, Integer likeCount) {
        
        SermonInfo sermonInfo = SermonInfo.of(title, sermonDate);
        PastorInfo pastorInfo = PastorInfo.of(pastorName, churchName);
        SermonMedia sermonMedia = SermonMedia.of(youtubeUrl, thumbnailUrl, durationMinutes);
        SermonStats sermonStats = SermonStats.of(viewCount, likeCount);
        SermonTags sermonTags = parseTagsFromJson(tagsJson);
        
        return of(id, sermonInfo, pastorInfo, sermonMedia, sermonStats, sermonTags, content, Collections.emptyList());
    }
    
    /**
     * 필수 필드 유효성 검증
     */
    private static void validateRequiredFields(SermonInfo info, PastorInfo pastor) {
        if (info == null) {
            throw new IllegalArgumentException("설교 정보는 null일 수 없습니다");
        }
        if (pastor == null) {
            throw new IllegalArgumentException("설교자 정보는 null일 수 없습니다");
        }
    }
    
    /**
     * 설교 내용 유효성 검증
     */
    private static String validateContent(String content) {
        if (content != null && content.length() > 100000) { // 10만자 제한
            throw new IllegalArgumentException("설교 내용은 10만자를 초과할 수 없습니다: " + content.length() + "자");
        }
        
        return content; // null 허용 (나중에 추가할 수 있음)
    }
    
    /**
     * JSON 문자열에서 태그 파싱 (Infrastructure 지원 메서드)
     */
    private static SermonTags parseTagsFromJson(String tagsJson) {
        if (tagsJson == null || tagsJson.trim().isEmpty()) {
            return SermonTags.empty();
        }
        
        try {
            // 간단한 JSON 배열 파싱 (["tag1", "tag2"] 형태)
            String cleaned = tagsJson.trim().replaceAll("^\\[|\\]$", "");
            if (cleaned.isEmpty()) {
                return SermonTags.empty();
            }
            
            List<String> tagList = new ArrayList<>();
            String[] parts = cleaned.split(",");
            for (String part : parts) {
                String tag = part.trim().replaceAll("^\"|\"$", ""); // 따옴표 제거
                if (!tag.isEmpty()) {
                    tagList.add(tag);
                }
            }
            
            return SermonTags.of(tagList);
        } catch (Exception e) {
            // 파싱 실패 시 빈 태그 반환
            return SermonTags.empty();
        }
    }
    
    // === 비즈니스 메서드들 ===
    
    /**
     * 새로운 설교인지 확인 (ID가 없는지)
     */
    public boolean isNew() {
        return id == null;
    }
    
    /**
     * 영속화된 설교인지 확인
     */
    public boolean isPersisted() {
        return id != null;
    }
    
    /**
     * 오늘 설교인지 확인
     */
    public boolean isToday() {
        return info.isToday();
    }
    
    /**
     * 최근 설교인지 확인 (N일 이내)
     */
    public boolean isRecent(int days) {
        return info.isRecent(days);
    }
    
    /**
     * 특정 설교자의 설교인지 확인
     */
    public boolean isPastorOf(String searchName) {
        return pastor.isPastorOf(searchName);
    }
    
    /**
     * 특정 교회의 설교인지 확인
     */
    public boolean isFromChurch(String searchChurch) {
        return pastor.isFromChurch(searchChurch);
    }
    
    /**
     * 제목으로 검색 가능한지 확인
     */
    public boolean matchesTitle(String searchTitle) {
        if (searchTitle == null || searchTitle.trim().isEmpty()) {
            return false;
        }
        
        return info.getTitle().toLowerCase().contains(searchTitle.toLowerCase());
    }
    
    /**
     * 태그로 검색 가능한지 확인
     */
    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }
    
    /**
     * 유튜브 영상이 있는지 확인
     */
    public boolean hasYoutubeVideo() {
        return media.hasYoutubeVideo();
    }
    
    /**
     * 인기 설교인지 확인
     */
    public boolean isPopular() {
        return stats.isPopular();
    }
    
    /**
     * 설교 내용이 있는지 확인
     */
    public boolean hasContent() {
        return content != null && !content.trim().isEmpty();
    }
    
    /**
     * 관련 구절이 있는지 확인
     */
    public boolean hasVerses() {
        return !verses.isEmpty();
    }
    
    // === 상태 변경 메서드들 (새 객체 반환) ===
    
    /**
     * 조회수 증가
     */
    public Sermon incrementViewCount() {
        return new Sermon(id, info, pastor, media, stats.incrementViewCount(), tags, content, verses);
    }
    
    /**
     * 좋아요 증가
     */
    public Sermon incrementLikeCount() {
        return new Sermon(id, info, pastor, media, stats.incrementLikeCount(), tags, content, verses);
    }
    
    /**
     * 좋아요 감소
     */
    public Sermon decrementLikeCount() {
        return new Sermon(id, info, pastor, media, stats.decrementLikeCount(), tags, content, verses);
    }
    
    /**
     * 미디어 정보 업데이트
     */
    public Sermon withMedia(SermonMedia newMedia) {
        if (newMedia == null) {
            throw new IllegalArgumentException("미디어 정보는 null일 수 없습니다");
        }
        
        return new Sermon(id, info, pastor, newMedia, stats, tags, content, verses);
    }
    
    /**
     * 태그 추가
     */
    public Sermon addTag(String tag) {
        SermonTags newTags = tags.addTag(tag);
        return new Sermon(id, info, pastor, media, stats, newTags, content, verses);
    }
    
    /**
     * 태그 제거
     */
    public Sermon removeTag(String tag) {
        SermonTags newTags = tags.removeTag(tag);
        return new Sermon(id, info, pastor, media, stats, newTags, content, verses);
    }
    
    /**
     * 설교 내용 업데이트
     */
    public Sermon withContent(String newContent) {
        String validatedContent = validateContent(newContent);
        return new Sermon(id, info, pastor, media, stats, tags, validatedContent, verses);
    }
    
    // === 표시 메서드들 ===
    
    /**
     * 표시용 문자열 생성
     */
    public String toDisplayString() {
        StringBuilder sb = new StringBuilder();
        sb.append(info.toDisplayString());
        sb.append(" - ").append(pastor.toDisplayString());
        
        if (stats.getViewCount() > 0) {
            sb.append(" (").append(stats.getSimpleDisplay()).append(")");
        }
        
        return sb.toString();
    }
    
    /**
     * 간단한 표시용 문자열 (제목과 설교자만)
     */
    public String getSimpleDisplay() {
        return info.getTitle() + " - " + pastor.getPastorOnly();
    }
    
    /**
     * 썸네일 URL 반환 (없으면 유튜브 기본 썸네일)
     */
    public String getThumbnailUrl() {
        return media.getThumbnailOrDefault();
    }
    
    /**
     * 구절 목록을 불변 리스트로 반환
     */
    public List<SermonVerse> getVerseList() {
        return Collections.unmodifiableList(verses);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sermon sermon = (Sermon) o;
        
        // ID가 있으면 ID로 비교, 없으면 제목+날짜+설교자로 비교
        if (id != null && sermon.id != null) {
            return Objects.equals(id, sermon.id);
        }
        
        return Objects.equals(info, sermon.info) && 
               Objects.equals(pastor, sermon.pastor);
    }
    
    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : Objects.hash(info, pastor);
    }
    
    @Override
    public String toString() {
        return toDisplayString();
    }
}