package com.dong.bible.web.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class SermonDto {
    
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
    private List<SermonVerseDto> verses;
    
    // 검색 결과에서 메인 본문 여부 표시용
    private Boolean isMainText;
}