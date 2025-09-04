package com.dong.bible.web.mapper;

import com.dong.bible.application.dto.query.ChapterQuery;
import com.dong.bible.application.dto.query.VerseQuery;
import com.dong.bible.application.dto.query.VerseRangeQuery;
import com.dong.bible.application.dto.query.VerseSearchQuery;
import com.dong.bible.web.dto.response.ChapterResponse;
import com.dong.bible.web.dto.response.VerseResponse;
import com.dong.bible.web.dto.response.VerseRangeResponse;
import com.dong.bible.web.dto.response.VerseSearchResponse;
import com.dong.bible.web.dto.response.VerseSimpleResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 구절(Verse) 도메인 관련 Application DTO → Web Response DTO 매핑
 * 
 * DDD 학습 원칙 적용:
 * - Builder 패턴 사용 (불변 객체)
 * - 도메인별 매핑 응집
 * - 명시적 객체 생성
 */
@Component
@Slf4j
public class VerseResponseMapper {

    /**
     * VerseQuery → VerseResponse 매핑 (Builder 패턴)
     */
    public VerseResponse toVerseResponse(VerseQuery queryDto) {
        if (queryDto == null) {
            log.warn("VerseQuery is null");
            return null;
        }

        return VerseResponse.builder()
                .id(queryDto.getId() != null ? queryDto.getId().intValue() : null)
                .bookId(queryDto.getBookId())
                .bookName(queryDto.getBookName())
                .bookAbbr(queryDto.getBookAbbr())
                .chapter(queryDto.getChapter())
                .verse(queryDto.getVerse())
                .text(queryDto.getText())
                .createdAt(null) // 현재 Application DTO에 없음
                .build();
    }

    /**
     * ChapterQuery → ChapterResponse 매핑 (Builder 패턴)
     */
    public ChapterResponse toChapterResponse(ChapterQuery queryDto) {
        if (queryDto == null) {
            log.warn("ChapterQuery is null");
            return null;
        }

        // VerseQuery → VerseSimpleResponse 변환
        List<VerseSimpleResponse> simpleVerses = queryDto.getVerses().stream()
                .map(this::toVerseSimpleResponse)
                .collect(Collectors.toList());

        log.debug("Mapping ChapterQuery: book={}, chapter={}, verses={}", 
                 queryDto.getBookName(), queryDto.getChapter(), simpleVerses.size());

        return ChapterResponse.builder()
                .bookId(queryDto.getBookId())
                .bookName(queryDto.getBookName())
                .bookAbbr(queryDto.getBookAbbr())
                .chapter(queryDto.getChapter())
                .totalVerses(queryDto.getTotalVerses())
                .verses(simpleVerses)
                .build();
    }

    /**
     * VerseRangeQuery → VerseRangeResponse 매핑 (Builder 패턴)
     */
    public VerseRangeResponse toVerseRangeResponse(VerseRangeQuery queryDto) {
        if (queryDto == null) {
            log.warn("VerseRangeQuery is null");
            return null;
        }

        // VerseQuery → VerseResponse 변환
        List<VerseResponse> verses = queryDto.getVerses().stream()
                .map(this::toVerseResponse)
                .collect(Collectors.toList());

        log.debug("Mapping VerseRangeQuery: range={}-{}, verses={}", 
                 queryDto.getStartVerse(), queryDto.getEndVerse(), verses.size());

        return VerseRangeResponse.builder()
                .bookId(queryDto.getBookId())
                .bookName(queryDto.getBookName())
                .bookAbbr(queryDto.getBookAbbr())
                .chapter(queryDto.getChapter())
                .startVerse(queryDto.getStartVerse())
                .endVerse(queryDto.getEndVerse())
                .totalCount(queryDto.getActualCount())
                .rangeReference(queryDto.getRangeReference())
                .verses(verses)
                .build();
    }

    /**
     * VerseSearchQuery → List<VerseSearchResponse> 매핑 (Builder 패턴)
     */
    public List<VerseSearchResponse> toSearchResultDtoList(VerseSearchQuery searchDto) {
        if (searchDto == null) {
            log.warn("VerseSearchQuery is null");
            return List.of();
        }

        if (searchDto.getVerses() == null) {
            log.warn("VerseSearchQuery.verses is null");
            return List.of();
        }

        List<VerseSearchResponse> results = searchDto.getVerses().stream()
                .map(this::toSearchResultDto)
                .collect(Collectors.toList());

        log.debug("Mapping VerseSearchQuery: keyword='{}', results={}", 
                 searchDto.getKeyword(), results.size());

        return results;
    }

    /**
     * VerseQuery → VerseSimpleResponse 매핑 (Builder 패턴)
     * Chapter 응답용 간소화된 구절 정보
     */
    private VerseSimpleResponse toVerseSimpleResponse(VerseQuery queryDto) {
        if (queryDto == null) {
            return null;
        }

        return VerseSimpleResponse.builder()
                .id(queryDto.getId() != null ? queryDto.getId().intValue() : null)
                .verse(queryDto.getVerse())
                .text(queryDto.getText())
                .build();
    }

    /**
     * VerseQuery → VerseSearchResponse 매핑 (Builder 패턴)
     * 검색 결과용 구절 정보
     */
    private VerseSearchResponse toSearchResultDto(VerseQuery queryDto) {
        if (queryDto == null) {
            return null;
        }

        return VerseSearchResponse.builder()
                .id(queryDto.getId() != null ? queryDto.getId().toString() : null)
                .bookName(queryDto.getBookName())
                .chapter(queryDto.getChapter())
                .verse(queryDto.getVerse())
                .content(queryDto.getText())
                .displayReference(queryDto.getReference())
                .build();
    }

    /**
     * 매핑 통계 로깅 (디버깅용)
     */
    public void logMappingStats(String operation, int inputCount, int outputCount) {
        log.info("Mapping stats - Operation: {}, Input: {}, Output: {}", 
                operation, inputCount, outputCount);
    }
}
