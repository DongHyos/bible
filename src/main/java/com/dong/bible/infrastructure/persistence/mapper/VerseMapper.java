package com.dong.bible.infrastructure.persistence.mapper;

import com.dong.bible.domain.verse.BibleVerse;
import com.dong.bible.domain.verse.VerseContent;
import com.dong.bible.domain.verse.VerseReference;
import com.dong.bible.infrastructure.persistence.entity.KrvBookEntity;
import com.dong.bible.infrastructure.persistence.entity.KrvVerseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class VerseMapper {

    /**
     * KrvVerseEntity Entity → BibleVerse Domain 변환
     */
    public BibleVerse toDomain(KrvVerseEntity entity) {
        if (entity == null) {
            log.warn("KrvVerseEntity entity is null");
            return null;
        }

        if (entity.getBook() == null) {
            log.warn("KrvVerseEntity.book is null for verse id: {}", entity.getId());
            return null;
        }

        try {
            // VerseReference 생성
            VerseReference reference = VerseReference.of(
                    entity.getBook().getName(),
                    entity.getChapter(),
                    entity.getVerse()
            );

            // VerseContent 생성
            VerseContent content = VerseContent.of(entity.getText());

            // BibleVerse 생성 (ID 타입 변환: Integer → Long)
            Long domainId = entity.getId() != null ? entity.getId().longValue() : null;

            return BibleVerse.of(domainId, reference, content);

        } catch (Exception e) {
            log.error("Failed to convert KrvVerseEntity to BibleVerse. Entity: {}", entity, e);
            throw new RuntimeException("Entity to Domain conversion failed", e);
        }
    }

    /**
     * BibleVerse Domain → KrvVerseEntity Entity 변환
     *
     * @param domain BibleVerse 도메인 객체
     * @param book 연관된 KrvBookEntity 엔티티 (별도로 조회해서 전달)
     */
    public KrvVerseEntity toEntity(BibleVerse domain, KrvBookEntity book) {
        if (domain == null) {
            log.warn("BibleVerse domain is null");
            return null;
        }

        if (book == null) {
            log.warn("KrvBookEntity is null for verse: {}", domain.toReferenceString());
            throw new IllegalArgumentException("Book cannot be null when converting to entity");
        }

        // Validation
        if (domain.getReference() == null) {
            throw new IllegalArgumentException("BibleVerse reference cannot be null");
        }

        if (domain.getContent() == null) {
            throw new IllegalArgumentException("BibleVerse content cannot be null");
        }

        try {
            // ID 타입 변환: Long → Integer
            Integer entityId = domain.getId() != null ? domain.getId().intValue() : null;

            return KrvVerseEntity.builder()
                    .id(entityId)
                    .book(book)
                    .chapter(domain.getReference().getChapter())
                    .verse(domain.getReference().getVerse())
                    .text(domain.getContent().getText())
                    // createdAt은 JPA @PrePersist에서 자동 설정
                    .build();

        } catch (Exception e) {
            log.error("Failed to convert BibleVerse to KrvVerseEntity. Domain: {}", domain.toReferenceString(), e);
            throw new RuntimeException("Domain to Entity conversion failed", e);
        }
    }

    /**
     * List<KrvVerseEntity> → List<BibleVerse> 변환
     */
    public List<BibleVerse> toDomainList(List<KrvVerseEntity> entities) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(this::toDomain)
                .filter(domain -> domain != null)  // null 제거
                .collect(Collectors.toList());
    }

    /**
     * List<BibleVerse> → List<KrvVerseEntity> 변환
     *
     * @param domains BibleVerse 도메인 객체들
     * @param book 모든 구절이 속한 공통 책 (같은 책의 구절들일 때 사용)
     */
    public List<KrvVerseEntity> toEntityList(List<BibleVerse> domains, KrvBookEntity book) {
        if (domains == null) {
            return List.of();
        }

        return domains.stream()
                .map(domain -> this.toEntity(domain, book))
                .filter(entity -> entity != null)  // null 제거
                .collect(Collectors.toList());
    }

    /**
     * 디버깅용 헬퍼 메서드
     */
    public void logMappingInfo(KrvVerseEntity entity, BibleVerse domain) {
        log.debug("Mapping Info:");
        log.debug("  Entity: id={}, book={}, chapter={}, verse={}, text='{}'",
                entity.getId(),
                entity.getBook() != null ? entity.getBook().getName() : "null",
                entity.getChapter(),
                entity.getVerse(),
                entity.getText());
        log.debug("  Domain: id={}, reference={}, content='{}'",
                domain.getId(),
                domain.toReferenceString(),
                domain.getContent().getText());
    }
}
