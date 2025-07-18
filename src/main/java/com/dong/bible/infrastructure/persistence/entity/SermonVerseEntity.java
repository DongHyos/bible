package com.dong.bible.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sermon_verses", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"sermon_id", "book_id", "chapter", "verse_start", "verse_end"}))
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SermonVerseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sermon_id", nullable = false)
    private SermonEntity sermon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private KrvBookEntity book;

    @Column(name = "chapter", nullable = false)
    private Short chapter;

    @Column(name = "verse_start", nullable = false)
    private Short verseStart;

    @Column(name = "verse_end", nullable = false)
    private Short verseEnd;

    @Column(name = "is_main_text", nullable = false, columnDefinition = "TINYINT(1)")
    @Builder.Default
    private Boolean isMainText = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}