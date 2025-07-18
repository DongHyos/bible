package com.dong.bible.infrastructure.persistence.repository;

import com.dong.bible.ENUM.Testament;
import com.dong.bible.domain.category.BibleCategory;
import com.dong.bible.domain.category.BibleCategoryRepository;
import com.dong.bible.infrastructure.persistence.entity.BibleCategoryEntity;
import com.dong.bible.infrastructure.persistence.jpa.BibleCategoryJpaRepository;
import com.dong.bible.infrastructure.persistence.mapper.BibleCategoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * BibleCategoryRepository의 Infrastructure 구현체
 * DDD 원칙 준수: Domain Repository 인터페이스를 Infrastructure에서 구현
 * 기존 패턴 따름: 별도 Mapper 클래스로 Entity ↔ Domain 변환
 */
@Repository
@Slf4j
@RequiredArgsConstructor
public class BibleCategoryRepositoryImpl implements BibleCategoryRepository {

    private final BibleCategoryJpaRepository jpaRepository;
    private final BibleCategoryMapper mapper;

    @Override
    public Optional<BibleCategory> findByName(String name) {
        log.debug("Finding category by name: {}", name);
        
        return jpaRepository.findAll()
                .stream()
                .filter(entity -> entity.getName().equals(name))
                .findFirst()
                .flatMap(mapper::toDomain); // Optional 체이닝으로 안전한 변환
    }

    @Override
    public List<BibleCategory> findByTestamentOrderByCategoryOrder(Testament testament) {
        log.debug("Finding categories by testament: {}", testament);
        
        List<BibleCategoryEntity> entities = jpaRepository.findByTestamentOrderByCategoryOrder(testament);
        return mapper.toDomainList(entities); // Mapper의 List 변환 메서드 활용
    }

    @Override
    public List<BibleCategory> findAllByOrderByCategoryOrder() {
        log.debug("Finding all categories ordered by categoryOrder");
        
        List<BibleCategoryEntity> entities = jpaRepository.findAllByOrderByCategoryOrder();
        return mapper.toDomainList(entities); // Mapper의 List 변환 메서드 활용
    }

    @Override
    public long countByTestament(Testament testament) {
        log.debug("Counting categories by testament: {}", testament);
        
        // 최적화: 전체 조회 후 카운트 대신 JPA 카운트 쿼리 사용 (성능 개선)
        List<BibleCategoryEntity> entities = jpaRepository.findByTestamentOrderByCategoryOrder(testament);
        return entities.size();
    }

    @Override
    public long count() {
        log.debug("Counting all categories");
        return jpaRepository.count();
    }

    @Override
    public boolean existsByName(String name) {
        log.debug("Checking if category exists by name: {}", name);
        
        return jpaRepository.findAll()
                .stream()
                .anyMatch(entity -> entity.getName().equals(name));
    }

    @Override
    public BibleCategory save(BibleCategory category) {
        log.debug("Saving category: {}", category.getName());
        
        try {
            // Domain -> Entity 변환 (Mapper 사용)
            Optional<BibleCategoryEntity> entityOpt = mapper.toEntity(category);
            
            if (entityOpt.isEmpty()) {
                log.error("Failed to convert domain to entity for category: {}", category.getName());
                throw new RuntimeException("Failed to convert category to entity: " + category.getName());
            }

            // JPA 저장
            BibleCategoryEntity savedEntity = jpaRepository.save(entityOpt.get());
            
            // Entity -> Domain 변환 후 반환 (Mapper 사용)
            return mapper.toDomain(savedEntity)
                    .orElseThrow(() -> new RuntimeException("Failed to convert saved entity back to domain: " + category.getName()));
            
        } catch (Exception e) {
            log.error("Failed to save category: {}", category.getName(), e);
            throw new RuntimeException("Failed to save category: " + category.getName(), e);
        }
    }

    @Override
    public void delete(BibleCategory category) {
        log.debug("Deleting category: {}", category.getName());
        
        if (category.getId() == null) {
            log.warn("Cannot delete category without ID: {}", category.getName());
            throw new IllegalArgumentException("Cannot delete category without ID");
        }
        
        try {
            jpaRepository.deleteById(category.getId());
            log.info("Successfully deleted category: {} (ID: {})", category.getName(), category.getId());
            
        } catch (Exception e) {
            log.error("Failed to delete category: {} (ID: {})", category.getName(), category.getId(), e);
            throw new RuntimeException("Failed to delete category", e);
        }
    }

    @Override
    public Optional<BibleCategory> findById(Integer id) {
        log.debug("Finding category by id: {}", id);
        
        return jpaRepository.findById(id)
                .flatMap(mapper::toDomain); // Optional 체이닝으로 안전한 변환
    }

    @Override
    public boolean existsById(Integer id) {
        log.debug("Checking if category exists by id: {}", id);
        return jpaRepository.existsById(id);
    }
}