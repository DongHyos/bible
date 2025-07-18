package com.dong.bible.application.dto;

import com.dong.bible.domain.sermon.Sermon;
import com.dong.bible.domain.sermon.SermonVerse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 설교 상세 조회용 Application DTO
 * Use Case: 설교 상세 API (/api/sermons/{id})
 * - 모든 정보 포함 (content, verses 등)
 */
@Getter
@Builder
public class SermonDetailDto {
    private final Long id;
    private final String title;
    private final String pastorName;
    private final String churchName;
    private final LocalDate sermonDate;
    private final String youtubeUrl;
    private final String thumbnailUrl;
    private final String content;  // 상세 조회에서만 필요
    private final Integer durationMinutes;
    private final List<String> tags;
    private final Integer viewCount;
    private final Integer likeCount;
    private final List<SermonVerseInfo> verses;  // 상세 조회에서만 필요
    
    @Getter
    @Builder
    public static class SermonVerseInfo {
        private final Integer bookId;
        private final String bookName;
        private final String bookAbbr;
        private final Short chapter;
        private final Short verseStart;
        private final Short verseEnd;
        private final boolean isMainText;
        
        public static SermonVerseInfo from(SermonVerse domain) {
            return SermonVerseInfo.builder()
                    .bookId(domain.getBookId())
                    .bookName(domain.getBookName())
                    .bookAbbr(domain.getBookAbbr())
                    .chapter(domain.getChapter())
                    .verseStart(domain.getVerseStart())
                    .verseEnd(domain.getVerseEnd())
                    .isMainText(domain.isMainText())
                    .build();
        }
    }
    
    public static SermonDetailDto from(Sermon domain) {
        return SermonDetailDto.builder()
                .id(domain.getId())
                .title(domain.getInfo().getTitle())
                .pastorName(domain.getPastor().getPastorName())
                .churchName(domain.getPastor().getChurchName())
                .sermonDate(domain.getInfo().getSermonDate())
                .youtubeUrl(domain.getMedia().getYoutubeUrl())
                .thumbnailUrl(domain.getMedia().getThumbnailUrl())
                .content(domain.getContent())
                .durationMinutes(domain.getMedia().getDurationMinutes())
                .tags(domain.getTags().getTagList())
                .viewCount(domain.getStats().getViewCount())
                .likeCount(domain.getStats().getLikeCount())
                .verses(domain.getVerseList().stream()
                        .map(SermonVerseInfo::from)
                        .collect(Collectors.toList()))
                .build();
    }
}
