package com.dong.bible.web.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class SermonSimpleDto {
    
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