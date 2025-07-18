package com.dong.bible.domain.sermon;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 설교 미디어 정보(유튜브URL, 썸네일URL, 재생시간)를 나타내는 Value Object
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Value
public class SermonMedia {
    
    String youtubeUrl;
    String thumbnailUrl;
    Integer durationMinutes;

    // 유튜브 URL 패턴
    private static final Pattern YOUTUBE_URL_PATTERN = Pattern.compile(
        "^https?://(www\\.)?(youtube\\.com/watch\\?v=|youtu\\.be/)[a-zA-Z0-9_-]+.*$"
    );
    
    /**
     * 정적 팩토리 메서드 - 모든 정보로 생성
     */
    public static SermonMedia of(String youtubeUrl, String thumbnailUrl, Integer durationMinutes) {
        String validatedYoutubeUrl = validateYoutubeUrl(youtubeUrl);
        String validatedThumbnailUrl = validateThumbnailUrl(thumbnailUrl);
        Integer validatedDuration = validateDuration(durationMinutes);
        
        return new SermonMedia(validatedYoutubeUrl, validatedThumbnailUrl, validatedDuration);
    }
    
    /**
     * 정적 팩토리 메서드 - 유튜브 URL만으로 생성
     */
    public static SermonMedia withYoutubeOnly(String youtubeUrl) {
        return of(youtubeUrl, null, null);
    }
    
    /**
     * 정적 팩토리 메서드 - 빈 미디어 정보 생성
     */
    public static SermonMedia empty() {
        return new SermonMedia(null, null, null);
    }
    
    /**
     * 유튜브 URL 유효성 검증
     */
    private static String validateYoutubeUrl(String youtubeUrl) {
        if (youtubeUrl == null || youtubeUrl.trim().isEmpty()) {
            return null;
        }
        
        String trimmed = youtubeUrl.trim();
        if (trimmed.length() > 500) {
            throw new IllegalArgumentException("유튜브 URL은 500자를 초과할 수 없습니다: " + trimmed.length() + "자");
        }
        
        if (!YOUTUBE_URL_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("올바른 유튜브 URL 형식이 아닙니다: " + trimmed);
        }
        
        return trimmed;
    }
    
    /**
     * 썸네일 URL 유효성 검증
     */
    private static String validateThumbnailUrl(String thumbnailUrl) {
        if (thumbnailUrl == null || thumbnailUrl.trim().isEmpty()) {
            return null;
        }
        
        String trimmed = thumbnailUrl.trim();
        if (trimmed.length() > 500) {
            throw new IllegalArgumentException("썸네일 URL은 500자를 초과할 수 없습니다: " + trimmed.length() + "자");
        }
        
        if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            throw new IllegalArgumentException("썸네일 URL은 http:// 또는 https://로 시작해야 합니다: " + trimmed);
        }
        
        return trimmed;
    }
    
    /**
     * 재생시간 유효성 검증
     */
    private static Integer validateDuration(Integer durationMinutes) {
        if (durationMinutes == null) {
            return null;
        }
        
        if (durationMinutes < 0) {
            throw new IllegalArgumentException("재생시간은 0분 이상이어야 합니다: " + durationMinutes);
        }
        
        if (durationMinutes > 600) { // 10시간 제한
            throw new IllegalArgumentException("재생시간은 600분(10시간)을 초과할 수 없습니다: " + durationMinutes);
        }
        
        return durationMinutes;
    }
    
    /**
     * 유튜브 영상이 있는지 확인
     */
    public boolean hasYoutubeVideo() {
        return youtubeUrl != null && !youtubeUrl.isEmpty();
    }
    
    /**
     * 썸네일이 있는지 확인
     */
    public boolean hasThumbnail() {
        return thumbnailUrl != null && !thumbnailUrl.isEmpty();
    }
    
    /**
     * 재생시간 정보가 있는지 확인
     */
    public boolean hasDuration() {
        return durationMinutes != null;
    }
    
    /**
     * 미디어 정보가 비어있는지 확인
     */
    public boolean isEmpty() {
        return !hasYoutubeVideo() && !hasThumbnail() && !hasDuration();
    }
    
    /**
     * 완전한 미디어 정보를 가지고 있는지 확인
     */
    public boolean isComplete() {
        return hasYoutubeVideo() && hasThumbnail() && hasDuration();
    }
    
    /**
     * 유튜브 비디오 ID 추출
     */
    public String getYoutubeVideoId() {
        if (!hasYoutubeVideo()) {
            return null;
        }
        
        // youtube.com/watch?v=VIDEO_ID 형태
        if (youtubeUrl.contains("youtube.com/watch?v=")) {
            String[] parts = youtubeUrl.split("v=");
            if (parts.length > 1) {
                String videoId = parts[1].split("&")[0]; // 다른 파라미터 제거
                return videoId;
            }
        }
        
        // youtu.be/VIDEO_ID 형태
        if (youtubeUrl.contains("youtu.be/")) {
            String[] parts = youtubeUrl.split("youtu.be/");
            if (parts.length > 1) {
                String videoId = parts[1].split("\\?")[0]; // 파라미터 제거
                return videoId;
            }
        }
        
        return null;
    }
    
    /**
     * 재생시간을 시간:분 형태로 포맷
     */
    public String getFormattedDuration() {
        if (!hasDuration()) {
            return "재생시간 정보 없음";
        }
        
        int hours = durationMinutes / 60;
        int minutes = durationMinutes % 60;
        
        if (hours > 0) {
            return String.format("%d시간 %d분", hours, minutes);
        } else {
            return String.format("%d분", minutes);
        }
    }
    
    /**
     * 썸네일 URL이 없을 때 유튜브 기본 썸네일 URL 생성
     */
    public String getThumbnailOrDefault() {
        if (hasThumbnail()) {
            return thumbnailUrl;
        }
        
        if (hasYoutubeVideo()) {
            String videoId = getYoutubeVideoId();
            if (videoId != null) {
                return "https://img.youtube.com/vi/" + videoId + "/maxresdefault.jpg";
            }
        }
        
        return null;
    }
    
    /**
     * 표시용 문자열 생성
     */
    public String toDisplayString() {
        StringBuilder sb = new StringBuilder();
        
        if (hasYoutubeVideo()) {
            sb.append("유튜브 영상");
            if (hasDuration()) {
                sb.append(" (").append(getFormattedDuration()).append(")");
            }
        } else {
            sb.append("미디어 정보 없음");
        }
        
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SermonMedia that = (SermonMedia) o;
        return Objects.equals(youtubeUrl, that.youtubeUrl) && 
               Objects.equals(thumbnailUrl, that.thumbnailUrl) && 
               Objects.equals(durationMinutes, that.durationMinutes);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(youtubeUrl, thumbnailUrl, durationMinutes);
    }
    
    @Override
    public String toString() {
        return toDisplayString();
    }
}