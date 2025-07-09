package com.dong.bible.web.mapper;

import com.dong.bible.application.dto.ChapterQueryDto;
import com.dong.bible.application.dto.VerseQueryDto;
import com.dong.bible.application.dto.VerseRangeQueryDto;
import com.dong.bible.application.dto.VerseSearchDto;
import com.dong.bible.web.dto.response.ChapterDto;
import com.dong.bible.web.dto.response.VerseDto;
import com.dong.bible.web.dto.response.VerseRangeResponseDto;
import com.dong.bible.web.dto.response.VerseSearchResultDto;
import com.dong.bible.web.dto.response.VerseSimpleDto;
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
     * VerseQueryDto → VerseDto 매핑 (Builder 패턴)
     */
    public VerseDto toVerseDto(VerseQueryDto queryDto) {
        if (queryDto == null) {
            log.warn("VerseQueryDto is null");
            return null;
        }

        return VerseDto.builder()
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
     * ChapterQueryDto → ChapterDto 매핑 (Builder 패턴)
     */
    public ChapterDto toChapterDto(ChapterQueryDto queryDto) {
        if (queryDto == null) {
            log.warn("ChapterQueryDto is null");
            return null;
        }

        // VerseQueryDto → VerseSimpleDto 변환
        List<VerseSimpleDto> simpleVerses = queryDto.getVerses().stream()
                .map(this::toVerseSimpleDto)
                .collect(Collectors.toList());

        log.debug("Mapping ChapterQueryDto: book={}, chapter={}, verses={}", 
                 queryDto.getBookName(), queryDto.getChapter(), simpleVerses.size());

        return ChapterDto.builder()
                .bookId(queryDto.getBookId())
                .bookName(queryDto.getBookName())
                .bookAbbr(queryDto.getBookAbbr())
                .chapter(queryDto.getChapter())
                .totalVerses(queryDto.getTotalVerses())
                .verses(simpleVerses)
                .build();
    }

    /**
     * VerseRangeQueryDto → VerseRangeResponseDto 매핑 (Builder 패턴)
     */
    public VerseRangeResponseDto toVerseRangeDto(VerseRangeQueryDto queryDto) {
        if (queryDto == null) {
            log.warn("VerseRangeQueryDto is null");
            return null;
        }

        // VerseQueryDto → VerseDto 변환
        List<VerseDto> verses = queryDto.getVerses().stream()
                .map(this::toVerseDto)
                .collect(Collectors.toList());

        log.debug("Mapping VerseRangeQueryDto: range={}-{}, verses={}", 
                 queryDto.getStartVerse(), queryDto.getEndVerse(), verses.size());

        return VerseRangeResponseDto.builder()
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
     * VerseSearchDto → List<VerseSearchResultDto> 매핑 (Builder 패턴)
     */
    public List<VerseSearchResultDto> toSearchResultDtoList(VerseSearchDto searchDto) {
        if (searchDto == null) {
            log.warn("VerseSearchDto is null");
            return List.of();
        }

        if (searchDto.getVerses() == null) {
            log.warn("VerseSearchDto.verses is null");
            return List.of();
        }

        List<VerseSearchResultDto> results = searchDto.getVerses().stream()
                .map(this::toSearchResultDto)
                .collect(Collectors.toList());

        log.debug("Mapping VerseSearchDto: keyword='{}', results={}", 
                 searchDto.getKeyword(), results.size());

        return results;
    }

    /**
     * VerseQueryDto → VerseSimpleDto 매핑 (Builder 패턴)
     * Chapter 응답용 간소화된 구절 정보
     */
    private VerseSimpleDto toVerseSimpleDto(VerseQueryDto queryDto) {
        if (queryDto == null) {
            return null;
        }

        return VerseSimpleDto.builder()
                .id(queryDto.getId() != null ? queryDto.getId().intValue() : null)
                .verse(queryDto.getVerse())
                .text(queryDto.getText())
                .build();
    }

    /**
     * VerseQueryDto → VerseSearchResultDto 매핑 (Builder 패턴)
     * 검색 결과용 구절 정보
     */
    private VerseSearchResultDto toSearchResultDto(VerseQueryDto queryDto) {
        if (queryDto == null) {
            return null;
        }

        return VerseSearchResultDto.builder()
                .id(queryDto.getId() != null ? queryDto.getId().intValue() : null)
                .bookName(queryDto.getBookName())
                .chapter(queryDto.getChapter())
                .verse(queryDto.getVerse())
                .text(queryDto.getText())
                .reference(queryDto.getReference())
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
