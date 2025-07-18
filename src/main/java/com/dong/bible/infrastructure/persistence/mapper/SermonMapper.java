package com.dong.bible.infrastructure.persistence.mapper;

import com.dong.bible.domain.sermon.*;
import com.dong.bible.infrastructure.persistence.entity.SermonEntity;
import com.dong.bible.infrastructure.persistence.entity.SermonVerseEntity;
import com.dong.bible.infrastructure.persistence.entity.KrvBookEntity;
import com.dong.bible.infrastructure.persistence.jpa.KrvBookJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Collections;

/**
 * Sermon Domain ↔ Infrastructure Entity 매핑
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SermonMapper {
    
    private final KrvBookJpaRepository bookRepository;
    
    /**
     * Infrastructure Entity → Domain 변환
     */
    public com.dong.bible.domain.sermon.Sermon toDomain(SermonEntity entity) {
        if (entity == null) {
            return null;
        }
        
        log.debug("Converting sermon entity to domain: id={}, title={}", entity.getId(), entity.getTitle());
        
        try {
            // SermonVerse Entity List → Domain List 변환
            List<com.dong.bible.domain.sermon.SermonVerse> domainVerses = mapSermonVersesToDomain(entity.getSermonVerses());
            
            // Infrastructure의 from 팩토리 메서드 사용 (null 값들을 안전한 기본값으로 변환)
            return com.dong.bible.domain.sermon.Sermon.from(
                entity.getId(),
                entity.getTitle(),
                entity.getSermonDate(),
                entity.getPastorName(),
                entity.getChurchName(),
                entity.getYoutubeUrl(),  // null 허용 (SermonMedia.of에서 처리)
                entity.getThumbnailUrl(), // null 허용 (SermonMedia.of에서 처리)
                entity.getContent(),
                entity.getDurationMinutes(), // null 허용 (SermonMedia.of에서 처리)
                entity.getTags() != null ? entity.getTags() : "[]", // null → 빈 JSON 배열
                entity.getViewCount() != null ? entity.getViewCount() : 0,
                entity.getLikeCount() != null ? entity.getLikeCount() : 0
            );
            
        } catch (Exception e) {
            log.error("Failed to convert sermon entity to domain: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Invalid sermon entity data: " + e.getMessage(), e);
        }
    }
    
    /**
     * Domain → Infrastructure Entity 변환
     */
    public SermonEntity toEntity(com.dong.bible.domain.sermon.Sermon domain) {
        if (domain == null) {
            return null;
        }
        
        log.debug("Converting sermon domain to entity: {}", domain.toDisplayString());
        
        try {
            // Builder 패턴 사용
            SermonEntity.SermonEntityBuilder builder = SermonEntity.builder()
                    .id(domain.getId())
                    .title(domain.getInfo().getTitle())
                    .sermonDate(domain.getInfo().getSermonDate())
                    .pastorName(domain.getPastor().getPastorName())
                    .churchName(domain.getPastor().getChurchName())
                    .youtubeUrl(domain.getMedia().getYoutubeUrl())
                    .thumbnailUrl(domain.getMedia().getThumbnailUrl())
                    .content(domain.getContent())
                    .durationMinutes(domain.getMedia().getDurationMinutes())
                    .viewCount(domain.getStats().getViewCount())
                    .likeCount(domain.getStats().getLikeCount());
            
            // 태그를 JSON 문자열로 변환
            if (!domain.getTags().isEmpty()) {
                builder.tags(convertTagsToJson(domain.getTags()));
            }
            
            SermonEntity entity = builder.build();
            
            // SermonVerse 매핑 (연관관계 설정)
            List<SermonVerseEntity> verseEntities = mapSermonVersesToEntity(domain.getVerseList(), entity);
            entity.getSermonVerses().addAll(verseEntities);
            
            return entity;
            
        } catch (Exception e) {
            log.error("Failed to convert sermon domain to entity: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Invalid sermon domain data: " + e.getMessage(), e);
        }
    }
    
    /**
     * Domain List → Entity List 변환
     */
    public List<SermonEntity> toEntityList(List<com.dong.bible.domain.sermon.Sermon> domains) {
        if (domains == null) {
            return null;
        }
        
        return domains.stream()
                .map(this::toEntity)
                .toList();
    }
    
    /**
     * Entity List → Domain List 변환
     */
    public List<com.dong.bible.domain.sermon.Sermon> toDomainList(List<SermonEntity> entities) {
        if (entities == null) {
            return null;
        }
        
        return entities.stream()
                .map(this::toDomain)
                .toList();
    }
    
    // === SermonVerse 변환 메서드들 ===
    
    /**
     * SermonVerse Entity List → Domain List 변환
     */
    private List<com.dong.bible.domain.sermon.SermonVerse> mapSermonVersesToDomain(List<SermonVerseEntity> entityVerses) {
        if (entityVerses == null || entityVerses.isEmpty()) {
            return Collections.emptyList();
        }
        
        return entityVerses.stream()
                .map(this::mapSermonVerseToDomain)
                .toList();
    }
    
    /**
     * SermonVerse Entity → Domain 변환
     */
    private com.dong.bible.domain.sermon.SermonVerse mapSermonVerseToDomain(SermonVerseEntity entity) {
        if (entity == null) {
            return null;
        }
        
        String bookName = entity.getBook() != null ? entity.getBook().getName() : null;
        String bookAbbr = entity.getBook() != null ? entity.getBook().getAbbr() : null;
        
        return com.dong.bible.domain.sermon.SermonVerse.of(
                entity.getBook() != null ? entity.getBook().getId() : null,
                bookName,
                bookAbbr,
                entity.getChapter(),
                entity.getVerseStart(),
                entity.getVerseEnd(),
                entity.getIsMainText()
        );
    }
    
    /**
     * SermonVerse Domain List → Entity List 변환
     */
    private List<SermonVerseEntity> mapSermonVersesToEntity(List<com.dong.bible.domain.sermon.SermonVerse> domainVerses, SermonEntity sermonEntity) {
        if (domainVerses == null || domainVerses.isEmpty()) {
            return Collections.emptyList();
        }
        
        return domainVerses.stream()
                .map(domain -> mapSermonVerseToEntity(domain, sermonEntity))
                .toList();
    }
    
    /**
     * SermonVerse Domain → Entity 변환
     */
    private SermonVerseEntity mapSermonVerseToEntity(com.dong.bible.domain.sermon.SermonVerse domain, SermonEntity sermonEntity) {
        if (domain == null) {
            return null;
        }
        
        // Book Entity 조회
        KrvBookEntity book = findBookById(domain.getBookId());
        
        // SermonVerse Entity 생성 (Builder 패턴 사용)
        return SermonVerseEntity.builder()
                .sermon(sermonEntity)
                .book(book)
                .chapter(domain.getChapter())
                .verseStart(domain.getVerseStart())
                .verseEnd(domain.getVerseEnd())
                .isMainText(domain.isMainText())
                .build();
    }
    
    // === Private 헬퍼 메서드들 ===
    
    /**
     * SermonTags를 JSON 문자열로 변환
     */
    private String convertTagsToJson(SermonTags tags) {
        if (tags.isEmpty()) {
            return null;
        }
        
        // 간단한 JSON 배열 형태로 변환
        StringBuilder json = new StringBuilder("[");
        List<String> tagList = tags.getTagList();
        
        for (int i = 0; i < tagList.size(); i++) {
            json.append("\"").append(tagList.get(i)).append("\"");
            if (i < tagList.size() - 1) {
                json.append(",");
            }
        }
        
        json.append("]");
        return json.toString();
    }
    
    /**
     * 책 ID로 KrvBookEntity 조회
     */
    private KrvBookEntity findBookById(Integer bookId) {
        if (bookId == null) {
            throw new IllegalArgumentException("Book ID cannot be null");
        }
        
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookId));
    }
}