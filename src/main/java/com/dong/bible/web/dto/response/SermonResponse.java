package com.dong.bible.web.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class SermonResponse {
    
    private Long id;
    private String title;
    private String pastorName;
    private String churchName;
    private LocalDate sermonDate;
    private String youtubeUrl;
    private String thumbnailUrl;
    private String content;
    private Integer durationMinutes;
    private List<String> tags;
    private Integer viewCount;
    private Integer likeCount;
    private List<SermonVerseResponse> verses;
    
    // 검색 결과에서 메인 본문 여부 표시용
    private Boolean isMainText;
}