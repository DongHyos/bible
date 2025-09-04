package com.dong.bible.application.dto.query;

import com.dong.bible.domain.sermon.Sermon;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

/**
 * 설교 목록 조회용 Application DTO
 * Use Case: 설교 목록 API (/api/sermons, /api/sermons/search 등)
 * - content 제외하여 네트워크 비용 최적화
 */
@Getter
@Builder
public class SermonSummaryQuery {
    private final Long id;
    private final String title;
    private final String pastorName;
    private final String churchName;
    private final LocalDate sermonDate;
    private final String youtubeUrl;
    private final String thumbnailUrl;
    private final Integer durationMinutes;
    private final List<String> tags;
    private final Integer viewCount;
    private final Integer likeCount;
    
    public static SermonSummaryQuery from(Sermon domain) {
        return SermonSummaryQuery.builder()
                .id(domain.getId())
                .title(domain.getInfo().getTitle())
                .pastorName(domain.getPastor().getPastorName())
                .churchName(domain.getPastor().getChurchName())
                .sermonDate(domain.getInfo().getSermonDate())
                .youtubeUrl(domain.getMedia().getYoutubeUrl())
                .thumbnailUrl(domain.getMedia().getThumbnailUrl())
                .durationMinutes(domain.getMedia().getDurationMinutes())
                .tags(domain.getTags().getTagList())
                .viewCount(domain.getStats().getViewCount())
                .likeCount(domain.getStats().getLikeCount())
                .build();
    }
}
