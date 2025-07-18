package com.dong.bible.infrastructure.persistence.mapper;

import com.dong.bible.domain.category.BibleCategory;
import com.dong.bible.ENUM.Testament;
import com.dong.bible.infrastructure.persistence.entity.BibleCategoryEntity;
import com.dong.bible.infrastructure.persistence.entity.KrvBookEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * BibleCategory Domain과 Infrastructure Entity 간의 매핑을 담당하는 Mapper
 * Infrastructure 계층에서 Domain 변환 책임을 가짐 (DDD 원칙 준수)
 */
@Component
@Slf4j
public class BibleCategoryMapper {

    /**
     * Infrastructure Entity를 Domain 객체로 변환 (조회용)
     * @param entity Infrastructure Entity
     * @return Domain 객체 (Optional로 안전성 강화)
     */
    public Optional<BibleCategory> toDomain(BibleCategoryEntity entity) {
        if (entity == null) {
            log.warn("BibleCategoryEntity is null");
            return Optional.empty();
        }

        try {
            // 해당 카테고리의 책 ID 목록 추출
            List<Integer> bookIds = Optional.ofNullable(entity.getBooks())
                .orElse(List.of())
                .stream()
                .map(KrvBookEntity::getId)
                .collect(Collectors.toList());

            BibleCategory domain = BibleCategory.of(
                    entity.getId(),
                    entity.getName(),
                    entity.getNameEn(),
                    entity.getTestament(),
                    entity.getCategoryOrder(),
                    entity.getDescription(),
                    bookIds
            );

            return Optional.of(domain);

        } catch (Exception e) {
            log.error("Failed to convert BibleCategoryEntity to BibleCategory. Entity: {}", 
                     entity.getName(), e);
            return Optional.empty();  // RuntimeException 대신 Optional.empty() 반환
        }
    }

    /**
     * Domain 객체를 Infrastructure Entity로 변환 (저장용)
     * 현재는 읽기 전용 구현에 집중하여 저장 기능은 보류
     * TODO: BibleCategoryEntity 팩토리 메서드 구현 후 완성
     */
    public Optional<BibleCategoryEntity> toEntity(BibleCategory domain) {
        if (domain == null) {
            log.warn("BibleCategory domain is null");
            return Optional.empty();
        }

        try {
            // 향후 BibleCategoryEntity의 팩토리 메서드가 구현되면 여기서 변환
            // 예: BibleCategoryEntity.create(domain.getName(), domain.getTestament(), ...)
            
            log.info("BibleCategory save operation - Entity factory method implementation needed");
            // UnsupportedOperationException 대신 Optional.empty() 반환하여 더 안전하게 처리
            return Optional.empty();

        } catch (Exception e) {
            log.error("Failed to convert BibleCategory to BibleCategoryEntity. Domain: {}", 
                     domain.getName(), e);
            return Optional.empty();
        }
    }

    /**
     * List<BibleCategoryEntity> → List<BibleCategory> 변환
     */
    public List<BibleCategory> toDomainList(List<BibleCategoryEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return List.of();
        }

        return entities.stream()
                .map(this::toDomain)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * List<BibleCategory> → List<BibleCategoryEntity> 변환 (저장용)
     * 현재는 toEntity 구현 완료 후 활성화 예정
     */
    public List<BibleCategoryEntity> toEntityList(List<BibleCategory> domains) {
        if (domains == null || domains.isEmpty()) {
            return List.of();
        }

        return domains.stream()
                .map(this::toEntity)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * 안전한 단일 Domain 객체 변환 (null-safe)
     */
    public BibleCategory toDomainOrNull(BibleCategoryEntity entity) {
        return toDomain(entity).orElse(null);
    }

    /**
     * 디버깅용 헬퍼 메서드
     */
    public void logMappingInfo(BibleCategoryEntity entity, BibleCategory domain) {
        log.debug("=== BibleCategory Mapping Info ===");
        
        if (entity != null) {
            log.debug("  Entity: id={}, name={}, testament={}, order={}, bookCount={}",
                    entity.getId(),
                    entity.getName(),
                    entity.getTestament(),
                    entity.getCategoryOrder(),
                    Optional.ofNullable(entity.getBooks()).map(List::size).orElse(0));
        }
        
        if (domain != null) {
            log.debug("  Domain: id={}, name={}, testament={}, order={}, bookCount={}",
                    domain.getId(),
                    domain.getName(),
                    domain.getTestament(),
                    domain.getCategoryOrder(),
                    domain.getBookCount());
        }
        
        log.debug("=====================================");
    }
}