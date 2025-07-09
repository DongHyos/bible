package com.dong.bible.infrastructure.persistence.entity;

import com.dong.bible.ENUM.Testament;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "krv_books")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class KrvBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "abbr")
    private String abbr;

    @Enumerated(EnumType.STRING)
    @Column(name = "testament")
    private Testament testament;

    // 분류 추가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private BibleCategory category;

    @Column(name = "book_order")
    private Integer bookOrder;

    @Column(name = "chapters")
    private Integer chapters;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 구절들과의 관계 (옵션)
    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
    private List<KrvVerse> verses = new ArrayList<>();
}
