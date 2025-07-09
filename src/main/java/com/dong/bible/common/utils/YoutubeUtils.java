package com.dong.bible.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class YoutubeUtils {

    // 유튜브 URL에서 비디오 ID 추출하는 정규식
    private static final Pattern YOUTUBE_URL_PATTERN = Pattern.compile(
            "(?:youtube\\.com/watch\\?v=|youtu\\.be/|youtube\\.com/embed/|youtube\\.com/v/)([a-zA-Z0-9_-]{11})"
    );

    /**
     * 유튜브 URL에서 비디오 ID 추출
     */
    public static String extractVideoId(String youtubeUrl) {
        if (youtubeUrl == null || youtubeUrl.trim().isEmpty()) {
            return null;
        }

        Matcher matcher = YOUTUBE_URL_PATTERN.matcher(youtubeUrl);
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        log.warn("유튜브 URL에서 비디오 ID를 추출할 수 없습니다: {}", youtubeUrl);
        return null;
    }

    /**
     * 유튜브 비디오 ID로 섬네일 URL 생성
     * @param videoId 유튜브 비디오 ID
     * @param quality 섬네일 품질 (default, mqdefault, hqdefault, sddefault, maxresdefault)
     */
    public static String getThumbnailUrl(String videoId, String quality) {
        if (videoId == null || videoId.trim().isEmpty()) {
            return null;
        }
        
        // 기본값은 고화질 섬네일
        String thumbnailQuality = (quality != null) ? quality : "hqdefault";
        return String.format("https://img.youtube.com/vi/%s/%s.jpg", videoId, thumbnailQuality);
    }

    /**
     * 유튜브 URL에서 직접 섬네일 URL 생성 (고화질)
     */
    public static String getThumbnailUrl(String youtubeUrl) {
        String videoId = extractVideoId(youtubeUrl);
        return getThumbnailUrl(videoId, "hqdefault");
    }

    /**
     * 여러 화질의 섬네일 URL 제공
     */
    public static class ThumbnailUrls {
        private final String videoId;

        public ThumbnailUrls(String videoId) {
            this.videoId = videoId;
        }

        public String getDefault() {
            return getThumbnailUrl(videoId, "default");
        }

        public String getMedium() {
            return getThumbnailUrl(videoId, "mqdefault");
        }

        public String getHigh() {
            return getThumbnailUrl(videoId, "hqdefault");
        }

        public String getStandard() {
            return getThumbnailUrl(videoId, "sddefault");
        }

        public String getMaxRes() {
            return getThumbnailUrl(videoId, "maxresdefault");
        }
    }

    /**
     * 유튜브 URL에서 모든 화질의 섬네일 URL 제공
     */
    public static ThumbnailUrls getAllThumbnails(String youtubeUrl) {
        String videoId = extractVideoId(youtubeUrl);
        return new ThumbnailUrls(videoId);
    }
}