package com.dong.bible.infrastructure.persistence.mapper;

import com.dong.bible.domain.dailyverse.DailyDate;
import com.dong.bible.domain.dailyverse.DailyMessage;
import com.dong.bible.domain.dailyverse.DailyVerse;
import com.dong.bible.domain.dailyverse.VerseRange;
import com.dong.bible.infrastructure.persistence.entity.DailyVerseEntity;
import com.dong.bible.infrastructure.persistence.entity.KrvBookEntity;
import com.dong.bible.infrastructure.persistence.jpa.KrvBookJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * DailyVerse Domain ↔ Infrastructure Entity 매핑
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DailyVerseMapper {
    
    private final KrvBookJpaRepository bookRepository;
    
    /**
     * Infrastructure Entity → Domain 변환
     */
    public DailyVerse toDomain(DailyVerseEntity entity) {
        if (entity == null) {
            return null;
        }
        
        log.debug("Converting entity to domain: id={}, date={}", entity.getId(), entity.getVerseDate());
        
        try {
            // Value Objects 생성
            DailyDate date = DailyDate.of(entity.getVerseDate());
            VerseRange verseRange = VerseRange.of(
                entity.getVerseStart().intValue(), 
                entity.getVerseEnd().intValue()
            );
            DailyMessage message = DailyMessage.of(entity.getTitle(), entity.getDescription());
            
            // Book 이름 추출
            String bookName = entity.getBook() != null ? entity.getBook().getName() : null;
            
            // Domain Entity 생성
            return DailyVerse.from(
                entity.getId(),
                entity.getVerseDate(),
                bookName,
                entity.getChapter().intValue(),
                entity.getVerseStart().intValue(),
                entity.getVerseEnd().intValue(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getIsActive()
            );
            
        } catch (Exception e) {
            log.error("Failed to convert entity to domain: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Invalid entity data: " + e.getMessage(), e);
        }
    }
    
    /**
     * Domain → Infrastructure Entity 변환
     */
    public DailyVerseEntity toEntity(DailyVerse domain) {
        if (domain == null) {
            return null;
        }
        
        log.debug("Converting domain to entity: {}", domain.toDisplayString());
        
        try {
            // Book 조회
            KrvBookEntity book = findBookByName(domain.getBookName());
            
            // Entity 빌더 패턴이 없다면 리플렉션이나 별도 방법 필요
            // 임시로 새 Entity 생성 방법 제시
            return createEntity(domain, book);
            
        } catch (Exception e) {
            log.error("Failed to convert domain to entity: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Invalid domain data: " + e.getMessage(), e);
        }
    }
    
    /**
     * Domain List → Entity List 변환
     */
    public List<DailyVerseEntity> toEntityList(List<DailyVerse> domains) {
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
    public List<DailyVerse> toDomainList(List<DailyVerseEntity> entities) {
        if (entities == null) {
            return null;
        }
        
        return entities.stream()
                .map(this::toDomain)
                .toList();
    }
    
    // === Private 헬퍼 메서드들 ===
    
    /**
     * 책 이름으로 KrvBookEntity 조회
     */
    private KrvBookEntity findBookByName(String bookName) {
        if (bookName == null || bookName.trim().isEmpty()) {
            throw new IllegalArgumentException("Book name cannot be null or empty");
        }
        
        return bookRepository.findByName(bookName.trim())
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookName));
    }
    
    /**
     * Domain으로부터 Entity 생성 (Builder 패턴 사용)
     */
    private DailyVerseEntity createEntity(DailyVerse domain, KrvBookEntity book) {
        return DailyVerseEntity.builder()
                .id(domain.getId())
                .verseDate(domain.getDate().getDate())
                .book(book)
                .chapter(domain.getChapter().shortValue())
                .verseStart(domain.getVerseRange().getStartVerse().shortValue())
                .verseEnd(domain.getVerseRange().getEndVerse().shortValue())
                .title(domain.getMessage().getTitle())
                .description(domain.getMessage().getDescription())
                .isActive(domain.isActive())
                .build();
    }
}