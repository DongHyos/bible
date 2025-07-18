package com.dong.bible.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "krv_verses", indexes = {
        @Index(name = "idx_book_chapter", columnList = "book_id, chapter"),
        @Index(name = "idx_book_chapter_verse", columnList = "book_id, chapter, verse")
})
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class KrvVerseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", foreignKey = @ForeignKey(name = "FK_verse_book"))
    private KrvBookEntity book;

    @Column(name = "chapter")
    private Integer chapter;

    @Column(name = "verse")
    private Integer verse;

    @Column(name = "text", columnDefinition = "TEXT")
    private String text;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
