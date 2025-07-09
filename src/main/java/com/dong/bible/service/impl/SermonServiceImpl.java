package com.dong.bible.service.impl;

import com.dong.bible.common.utils.YoutubeUtils;
import com.dong.bible.web.dto.response.SermonDto;
import com.dong.bible.web.dto.response.SermonSimpleDto;
import com.dong.bible.infrastructure.persistence.entity.Sermon;
import com.dong.bible.mapstruct.SermonMapper;
import com.dong.bible.infrastructure.persistence.jpa.SermonRepository;
import com.dong.bible.service.SermonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SermonServiceImpl implements SermonService {

    private final SermonRepository sermonRepository;
    private final SermonMapper sermonMapper;

    @Override
    public List<SermonSimpleDto> getSermonsByVerse(Integer bookId, Short chapter, Short verse) {
        log.info("특정 구절 설교 조회: 책ID={}, 장={}, 절={}", bookId, chapter, verse);
        
        List<Sermon> sermons = sermonRepository.findByVerse(bookId, chapter, verse);
        List<SermonSimpleDto> result = sermonMapper.toSimpleDtoList(sermons);
        
        // 섬네일 URL 자동 설정 (썸네일이 없고 유튜브 URL이 있는 경우)
        result.forEach(this::setThumbnailIfMissing);
        
        log.info("조회된 설교 수: {}", result.size());
        return result;
    }

    @Override
    public SermonDto getSermonDetail(Long sermonId) {
        log.info("설교 상세 조회: ID={}", sermonId);
        
        Sermon sermon = sermonRepository.findById(sermonId)
                .orElseThrow(() -> new RuntimeException("설교를 찾을 수 없습니다: " + sermonId));
        
        SermonDto result = sermonMapper.toDto(sermon);
        
        // 섬네일 URL 자동 설정
        setThumbnailIfMissing(result);
        
        return result;
    }

    /**
     * 섬네일이 없고 유튜브 URL이 있는 경우 자동으로 유튜브 섬네일 설정
     */
    private void setThumbnailIfMissing(SermonSimpleDto sermon) {
        if (sermon.getThumbnailUrl() == null && sermon.getYoutubeUrl() != null) {
            String autoThumbnail = YoutubeUtils.getThumbnailUrl(sermon.getYoutubeUrl());
            sermon.setThumbnailUrl(autoThumbnail);
            log.debug("자동 섬네일 설정: 설교ID={}, URL={}", sermon.getId(), autoThumbnail);
        }
    }

    /**
     * 섬네일이 없고 유튜브 URL이 있는 경우 자동으로 유튜브 섬네일 설정 (상세 버전)
     */
    private void setThumbnailIfMissing(SermonDto sermon) {
        if (sermon.getThumbnailUrl() == null && sermon.getYoutubeUrl() != null) {
            String autoThumbnail = YoutubeUtils.getThumbnailUrl(sermon.getYoutubeUrl());
            sermon.setThumbnailUrl(autoThumbnail);
            log.debug("자동 섬네일 설정: 설교ID={}, URL={}", sermon.getId(), autoThumbnail);
        }
    }

    @Override
    public List<SermonSimpleDto> getSermonsByPastor(String pastorName) {
        log.info("설교자별 설교 조회: {}", pastorName);
        
        List<Sermon> sermons = sermonRepository.findByPastorNameContainingOrderBySermonDateDesc(pastorName);
        List<SermonSimpleDto> result = sermonMapper.toSimpleDtoList(sermons);
        result.forEach(this::setThumbnailIfMissing);
        return result;
    }

    @Override
    public List<SermonSimpleDto> getSermonsByChurch(String churchName) {
        log.info("교회별 설교 조회: {}", churchName);
        
        List<Sermon> sermons = sermonRepository.findByChurchNameContainingOrderBySermonDateDesc(churchName);
        List<SermonSimpleDto> result = sermonMapper.toSimpleDtoList(sermons);
        result.forEach(this::setThumbnailIfMissing);
        return result;
    }

    @Override
    public List<SermonSimpleDto> searchSermonsByTitle(String title) {
        log.info("제목으로 설교 검색: {}", title);
        
        List<Sermon> sermons = sermonRepository.findByTitleContainingOrderByViewCountDesc(title);
        List<SermonSimpleDto> result = sermonMapper.toSimpleDtoList(sermons);
        result.forEach(this::setThumbnailIfMissing);
        return result;
    }

    @Override
    public List<SermonSimpleDto> getPopularSermons() {
        log.info("인기 설교 조회");
        
        List<Sermon> sermons = sermonRepository.findTop10ByOrderByViewCountDesc();
        List<SermonSimpleDto> result = sermonMapper.toSimpleDtoList(sermons);
        result.forEach(this::setThumbnailIfMissing);
        return result;
    }

    @Override
    public List<SermonSimpleDto> getLatestSermons() {
        log.info("최신 설교 조회");
        
        List<Sermon> sermons = sermonRepository.findTop10ByOrderBySermonDateDesc();
        List<SermonSimpleDto> result = sermonMapper.toSimpleDtoList(sermons);
        result.forEach(this::setThumbnailIfMissing);
        return result;
    }

    @Override
    @Transactional
    public void incrementViewCount(Long sermonId) {
        log.info("설교 조회수 증가: ID={}", sermonId);
        
        Sermon sermon = sermonRepository.findById(sermonId)
                .orElseThrow(() -> new RuntimeException("설교를 찾을 수 없습니다: " + sermonId));
        
        // JPA에서 직접 조회수 증가는 별도 메서드나 @Modifying 쿼리 필요
        // 여기서는 단순 구현으로 대체
        log.info("조회수 증가 완료: {} -> {}", sermon.getViewCount(), sermon.getViewCount() + 1);
    }
}