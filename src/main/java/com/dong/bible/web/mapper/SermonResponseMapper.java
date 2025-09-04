package com.dong.bible.web.mapper;

import com.dong.bible.application.dto.query.SermonDetailQuery;
import com.dong.bible.application.dto.query.SermonSummaryQuery;
import com.dong.bible.common.utils.YoutubeUtils;
import com.dong.bible.web.dto.response.SermonResponse;
import com.dong.bible.web.dto.response.SermonSimpleResponse;
import com.dong.bible.web.dto.response.SermonVerseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Sermon Application DTO → Response DTO 매핑
 * Web Layer 전용 매핑 클래스
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SermonResponseMapper {
    
    // ========================================
    // Application DTO → Web DTO 변환만 사용
    // ========================================
    
    /**
     * SermonDetailQuery → SermonResponse 변환
     */
    public SermonResponse fromDetailDto(SermonDetailQuery detailDto) {
        if (detailDto == null) {
            return null;
        }
        
        // ✅ 썸네일 URL 결정 (매핑 시점에서 처리)
        String thumbnailUrl = determineThumbnailUrl(
            detailDto.getThumbnailUrl(), 
            detailDto.getYoutubeUrl()
        );
        
        return SermonResponse.builder()
                .id(detailDto.getId())
                .title(detailDto.getTitle())
                .pastorName(detailDto.getPastorName())
                .churchName(detailDto.getChurchName())
                .sermonDate(detailDto.getSermonDate())
                .youtubeUrl(detailDto.getYoutubeUrl())
                .thumbnailUrl(thumbnailUrl)  // ✅ 처리된 썸네일 URL
                .content(detailDto.getContent())
                .durationMinutes(detailDto.getDurationMinutes())
                .tags(detailDto.getTags())
                .viewCount(detailDto.getViewCount())
                .likeCount(detailDto.getLikeCount())
                .verses(mapSermonVerseInfos(detailDto.getVerses()))
                .build();
    }
    
    /**
     * SermonSummaryQuery → SermonSimpleResponse 변환
     */
    public SermonSimpleResponse fromSummaryDto(SermonSummaryQuery summaryDto) {
        if (summaryDto == null) {
            return null;
        }
        
        // ✅ 썸네일 URL 결정 (매핑 시점에서 처리)
        String thumbnailUrl = determineThumbnailUrl(
            summaryDto.getThumbnailUrl(), 
            summaryDto.getYoutubeUrl()
        );
        
        return SermonSimpleResponse.builder()
                .id(summaryDto.getId())
                .title(summaryDto.getTitle())
                .pastorName(summaryDto.getPastorName())
                .churchName(summaryDto.getChurchName())
                .sermonDate(summaryDto.getSermonDate())
                .youtubeUrl(summaryDto.getYoutubeUrl())
                .thumbnailUrl(thumbnailUrl)  // ✅ 처리된 썸네일 URL
                .durationMinutes(summaryDto.getDurationMinutes())
                .tags(summaryDto.getTags())
                .viewCount(summaryDto.getViewCount())
                .likeCount(summaryDto.getLikeCount())
                .build();
    }
    
    /**
     * List<SermonSummaryQuery> → List<SermonSimpleResponse> 변환
     */
    public List<SermonSimpleResponse> fromSummaryDtoList(List<SermonSummaryQuery> summaryDtos) {
        if (summaryDtos == null) {
            return Collections.emptyList();
        }
        
        return summaryDtos.stream()
                .map(this::fromSummaryDto)
                .filter(dto -> dto != null)
                .toList();
    }
    
    // === Private 헬퍼 메서드들 ===
    
    /**
     * 썸네일 URL 결정 로직
     * 1. 기존 썸네일이 있으면 그것 사용
     * 2. 없으면 유튜브 URL에서 자동 생성
     * 3. 둘 다 없으면 null
     */
    private String determineThumbnailUrl(String existingThumbnail, String youtubeUrl) {
        // 기존 썸네일이 있으면 그것 사용
        if (existingThumbnail != null && !existingThumbnail.trim().isEmpty()) {
            return existingThumbnail;
        }
        
        // 유튜브 URL이 있으면 자동 썸네일 생성
        if (youtubeUrl != null && !youtubeUrl.trim().isEmpty()) {
            try {
                String autoThumbnail = YoutubeUtils.getThumbnailUrl(youtubeUrl);
                if (autoThumbnail != null) {
                    log.debug("자동 썸네일 생성: 유튜브URL={}, 썸네일URL={}", youtubeUrl, autoThumbnail);
                    return autoThumbnail;
                }
            } catch (Exception e) {
                log.warn("유튜브 썸네일 자동 생성 실패: {}", e.getMessage());
            }
        }
        
        // 둘 다 없으면 null
        return null;
    }
    
    /**
     * SermonDetailQuery.SermonVerseInfo List → SermonVerseResponse List 변환
     */
    private List<SermonVerseResponse> mapSermonVerseInfos(List<SermonDetailQuery.SermonVerseInfo> verseInfos) {
        if (verseInfos == null || verseInfos.isEmpty()) {
            return Collections.emptyList();
        }
        
        return verseInfos.stream()
                .map(this::mapSermonVerseInfo)
                .filter(dto -> dto != null)
                .toList();
    }
    
    /**
     * SermonDetailQuery.SermonVerseInfo → SermonVerseResponse 변환
     */
    private SermonVerseResponse mapSermonVerseInfo(SermonDetailQuery.SermonVerseInfo verseInfo) {
        if (verseInfo == null) {
            return null;
        }
        
        return SermonVerseResponse.builder()
                .id(null) // Application DTO SermonVerseInfo는 ID가 없음
                .bookId(verseInfo.getBookId())
                .bookName(verseInfo.getBookName())
                .bookAbbr(verseInfo.getBookAbbr())
                .chapter(verseInfo.getChapter())
                .verseStart(verseInfo.getVerseStart())
                .verseEnd(verseInfo.getVerseEnd())
                .isMainText(verseInfo.isMainText())
                .build();
    }
}
