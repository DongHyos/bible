package com.dong.bible.infrastructure.persistence.entity;

import com.dong.bible.ENUM.Testament;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bible_categories")
@Getter
public class BibleCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "name_en")
    private String nameEn;

    @Enumerated(EnumType.STRING)
    @Column(name = "testament")
    private Testament testament;

    @Column(name = "category_order")
    private Integer categoryOrder;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 해당 분류의 책들
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<KrvBook> books = new ArrayList<>();
}
