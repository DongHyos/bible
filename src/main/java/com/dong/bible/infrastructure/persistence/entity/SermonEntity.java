package com.dong.bible.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sermons")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SermonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "pastor_name", nullable = false, length = 100)
    private String pastorName;

    @Column(name = "church_name", nullable = false, length = 150)
    private String churchName;

    @Column(name = "sermon_date", nullable = false)
    private LocalDate sermonDate;

    @Column(name = "youtube_url", length = 500)
    private String youtubeUrl;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "tags", columnDefinition = "JSON")
    private String tags;

    @Column(name = "view_count")
    @Builder.Default
    private Integer viewCount = 0;

    @Column(name = "like_count")
    @Builder.Default
    private Integer likeCount = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 설교와 구절의 관계
    @OneToMany(mappedBy = "sermon", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<SermonVerseEntity> sermonVerses = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}