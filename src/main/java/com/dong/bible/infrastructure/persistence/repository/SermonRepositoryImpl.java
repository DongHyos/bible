package com.dong.bible.infrastructure.persistence.repository;

import com.dong.bible.domain.sermon.Sermon;
import com.dong.bible.domain.sermon.SermonRepository;
import com.dong.bible.infrastructure.persistence.entity.SermonEntity;
import com.dong.bible.infrastructure.persistence.jpa.SermonJpaRepository;
import com.dong.bible.infrastructure.persistence.mapper.SermonMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Sermon Repository의 JPA 구현체
 * Domain Repository 인터페이스를 Infrastructure에서 구현
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class SermonRepositoryImpl implements SermonRepository {
    
    private final SermonJpaRepository jpaRepository;
    private final SermonMapper mapper;
    
    // === 조회 메서드들 ===
    
    @Override
    public Optional<Sermon> getById(Long id) {
        log.debug("Getting sermon by id: {}", id);
        
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public List<Sermon> findByVerse(Integer bookId, Short chapter, Short verse) {
        log.debug("Finding sermons by verse: bookId={}, chapter={}, verse={}", bookId, chapter, verse);
        
        List<SermonEntity> entities = jpaRepository.findByVerse(bookId, chapter, verse);
        return mapper.toDomainList(entities);
    }
    
    @Override
    public List<Sermon> findByPastorNameContaining(String pastorName) {
        log.debug("Finding sermons by pastor name containing: {}", pastorName);
        
        List<SermonEntity> entities = jpaRepository.findByPastorNameContainingOrderBySermonDateDesc(pastorName);
        return mapper.toDomainList(entities);
    }
    
    @Override
    public List<Sermon> findByChurchNameContaining(String churchName) {
        log.debug("Finding sermons by church name containing: {}", churchName);
        
        List<SermonEntity> entities = jpaRepository.findByChurchNameContainingOrderBySermonDateDesc(churchName);
        return mapper.toDomainList(entities);
    }
    
    @Override
    public List<Sermon> findByTitleContaining(String title) {
        log.debug("Finding sermons by title containing: {}", title);
        
        List<SermonEntity> entities = jpaRepository.findByTitleContainingOrderByViewCountDesc(title);
        return mapper.toDomainList(entities);
    }
    
    @Override
    public List<Sermon> findTopByViewCount(int limit) {
        log.debug("Finding top {} sermons by view count", limit);
        
        // JPA Repository에서는 Top10만 지원하므로 limit 무시하고 10개 반환
        List<SermonEntity> entities = jpaRepository.findTop10ByOrderByViewCountDesc();
        return mapper.toDomainList(entities);
    }
    
    @Override
    public List<Sermon> findLatestSermons(int limit) {
        log.debug("Finding latest {} sermons", limit);
        
        // JPA Repository에서는 Top10만 지원하므로 limit 무시하고 10개 반환  
        List<SermonEntity> entities = jpaRepository.findTop10ByOrderBySermonDateDesc();
        return mapper.toDomainList(entities);
    }
    
    @Override
    public List<Sermon> findByDateRange(LocalDate startDate, LocalDate endDate) {
        log.debug("Finding sermons by date range: {} ~ {}", startDate, endDate);
        
        // TODO: JPA Repository에 메서드 추가 필요
        throw new UnsupportedOperationException("findByDateRange not implemented yet");
    }
    
    @Override
    public List<Sermon> findByYear(int year) {
        log.debug("Finding sermons by year: {}", year);
        
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        return findByDateRange(startDate, endDate);
    }
    
    @Override
    public List<Sermon> findByYearAndMonth(int year, int month) {
        log.debug("Finding sermons by year and month: {}/{}", year, month);
        
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        return findByDateRange(startDate, endDate);
    }
    
    @Override
    public List<Sermon> findByTag(String tag) {
        log.debug("Finding sermons by tag: {}", tag);
        
        // TODO: JPA Repository에 태그 검색 메서드 추가 필요
        throw new UnsupportedOperationException("findByTag not implemented yet");
    }
    
    @Override
    public List<Sermon> findByTagsContaining(List<String> tags) {
        log.debug("Finding sermons by tags containing: {}", tags);
        
        // TODO: JPA Repository에 태그 검색 메서드 추가 필요
        throw new UnsupportedOperationException("findByTagsContaining not implemented yet");
    }
    
    @Override
    public List<Sermon> findByViewCountGreaterThan(int viewCount) {
        log.debug("Finding sermons with view count greater than: {}", viewCount);
        
        // TODO: JPA Repository에 메서드 추가 필요
        throw new UnsupportedOperationException("findByViewCountGreaterThan not implemented yet");
    }
    
    @Override
    public List<Sermon> findWithYoutubeVideo() {
        log.debug("Finding sermons with youtube video");
        
        // TODO: JPA Repository에 메서드 추가 필요
        throw new UnsupportedOperationException("findWithYoutubeVideo not implemented yet");
    }
    
    @Override
    public List<Sermon> findLatestByPastor(String pastorName, int limit) {
        log.debug("Finding latest {} sermons by pastor: {}", limit, pastorName);
        
        // TODO: JPA Repository에 메서드 추가 필요
        throw new UnsupportedOperationException("findLatestByPastor not implemented yet");
    }
    
    @Override
    public List<Sermon> findLatestByChurch(String churchName, int limit) {
        log.debug("Finding latest {} sermons by church: {}", limit, churchName);
        
        // TODO: JPA Repository에 메서드 추가 필요
        throw new UnsupportedOperationException("findLatestByChurch not implemented yet");
    }
    
    // === 저장 및 삭제 메서드들 ===
    
    @Override
    public Sermon store(Sermon sermon) {
        log.debug("Storing sermon: {}", sermon.toDisplayString());
        
        SermonEntity entity = mapper.toEntity(sermon);
        SermonEntity savedEntity = jpaRepository.save(entity);
        
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    public void remove(Sermon sermon) {
        log.debug("Removing sermon: {}", sermon.toDisplayString());
        
        if (sermon.getId() == null) {
            throw new IllegalArgumentException("Cannot remove sermon without ID");
        }
        
        jpaRepository.deleteById(sermon.getId());
    }
    
    @Override
    public void removeById(Long id) {
        log.debug("Removing sermon by id: {}", id);
        
        jpaRepository.deleteById(id);
    }
    
    // === 존재 여부 확인 ===
    
    @Override
    public boolean exists(Long id) {
        log.debug("Checking if sermon exists: {}", id);
        
        return jpaRepository.existsById(id);
    }
    
    @Override
    public boolean existsSermon(String title, LocalDate sermonDate) {
        log.debug("Checking if sermon exists: title={}, date={}", title, sermonDate);
        
        // TODO: JPA Repository에 메서드 추가 필요
        throw new UnsupportedOperationException("existsSermon not implemented yet");
    }
    
    // === 통계 메서드들 ===
    
    @Override
    public long getTotalCount() {
        log.debug("Getting total sermon count");
        
        return jpaRepository.count();
    }
    
    @Override
    public long countByPastor(String pastorName) {
        log.debug("Counting sermons by pastor: {}", pastorName);
        
        // TODO: JPA Repository에 메서드 추가 필요
        throw new UnsupportedOperationException("countByPastor not implemented yet");
    }
    
    @Override
    public long countByChurch(String churchName) {
        log.debug("Counting sermons by church: {}", churchName);
        
        // TODO: JPA Repository에 메서드 추가 필요
        throw new UnsupportedOperationException("countByChurch not implemented yet");
    }
    
    @Override
    public long countByYear(int year) {
        log.debug("Counting sermons by year: {}", year);
        
        // TODO: JPA Repository에 메서드 추가 필요
        throw new UnsupportedOperationException("countByYear not implemented yet");
    }
    
    @Override
    public long countWithYoutubeVideo() {
        log.debug("Counting sermons with youtube video");
        
        // TODO: JPA Repository에 메서드 추가 필요
        throw new UnsupportedOperationException("countWithYoutubeVideo not implemented yet");
    }
    
    // === 비즈니스 특화 메서드들 ===
    
    @Override
    public void incrementViewCount(Long sermonId) {
        log.debug("Incrementing view count for sermon: {}", sermonId);
        
        // TODO: JPA Repository에 @Modifying 쿼리 추가 필요
        throw new UnsupportedOperationException("incrementViewCount not implemented yet");
    }
    
    @Override
    public void incrementLikeCount(Long sermonId) {
        log.debug("Incrementing like count for sermon: {}", sermonId);
        
        // TODO: JPA Repository에 @Modifying 쿼리 추가 필요
        throw new UnsupportedOperationException("incrementLikeCount not implemented yet");
    }
    
    @Override
    public void decrementLikeCount(Long sermonId) {
        log.debug("Decrementing like count for sermon: {}", sermonId);
        
        // TODO: JPA Repository에 @Modifying 쿼리 추가 필요
        throw new UnsupportedOperationException("decrementLikeCount not implemented yet");
    }
    
    @Override
    public List<Sermon> findTrendingSermons(int days, int limit) {
        log.debug("Finding trending sermons for {} days, limit: {}", days, limit);
        
        // TODO: 복잡한 쿼리 필요 - 최근 N일간 조회수 증가율 계산
        throw new UnsupportedOperationException("findTrendingSermons not implemented yet");
    }
    
    @Override
    public List<Sermon> findRecommendedSermons(int limit) {
        log.debug("Finding recommended sermons, limit: {}", limit);
        
        // TODO: 좋아요 비율 기준 쿼리 필요
        throw new UnsupportedOperationException("findRecommendedSermons not implemented yet");
    }
    
    @Override
    public List<Sermon> findRelatedSermons(Sermon sermon, int limit) {
        log.debug("Finding related sermons for: {}, limit: {}", sermon.toDisplayString(), limit);
        
        // TODO: 같은 구절 또는 태그 기준 쿼리 필요
        throw new UnsupportedOperationException("findRelatedSermons not implemented yet");
    }
    
    @Override
    public List<Sermon> findRepresentativeSermons(String pastorName, int limit) {
        log.debug("Finding representative sermons for pastor: {}, limit: {}", pastorName, limit);
        
        // TODO: 설교자별 조회수 상위 N개 쿼리 필요
        throw new UnsupportedOperationException("findRepresentativeSermons not implemented yet");
    }
}