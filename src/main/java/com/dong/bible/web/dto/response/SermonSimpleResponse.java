package com.dong.bible.web.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class SermonSimpleResponse {
    
    private Long id;
    private String title;
    private String pastorName;
    private String churchName;
    private LocalDate sermonDate;
    private String youtubeUrl;
    private String thumbnailUrl;
    private Integer durationMinutes;
    private List<String> tags;
    private Integer viewCount;
    private Integer likeCount;
}