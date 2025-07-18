package com.dong.bible.web.mapper;

import com.dong.bible.application.dto.BookDto;
import com.dong.bible.application.dto.BibleStatisticsDto;
import com.dong.bible.ENUM.Testament;
import com.dong.bible.web.dto.response.BibleBookDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Book Application DTO → Response DTO 매핑
 * Web Layer 전용 매핑 클래스
 */
@Component
@Slf4j
public class BookResponseMapper {
    
    // ========================================
    // Application DTO → Web DTO 변환
    // ========================================
    
    /**
     * BookDto → BibleBookDto 변환
     */
    public BibleBookDto fromBookDto(BookDto bookDto) {
        if (bookDto == null) {
            return null;
        }
        
        return BibleBookDto.builder()
                .id(bookDto.getId() != null ? bookDto.getId().intValue() : null)
                .name(bookDto.getName())
                .abbr(bookDto.getAbbreviation())
                .testament(bookDto.getTestament() == Testament.신약 ? "신약" : "구약")  // enum → String
                .bookOrder(bookDto.getBookOrder())
                .chapters(bookDto.getTotalChapters())
                .categoryId(bookDto.getCategoryId() != null ? bookDto.getCategoryId().intValue() : null)
                // TODO: category, totalVerses, createdAt 등은 필요시 추가 구현
                .build();
    }
    
    /**
     * List<BookDto> → List<BibleBookDto> 변환
     */
    public List<BibleBookDto> fromBookDtoList(List<BookDto> bookDtos) {
        if (bookDtos == null) {
            return Collections.emptyList();
        }
        
        return bookDtos.stream()
                .map(this::fromBookDto)
                .filter(dto -> dto != null)
                .toList();
    }
    
    /**
     * Map<String, List<BookDto>> → Map<String, List<BibleBookDto>> 변환
     * 그룹핑된 성경책 데이터 변환
     */
    public Map<String, List<BibleBookDto>> fromGroupedBooks(Map<String, List<BookDto>> groupedBooks) {
        if (groupedBooks == null) {
            return Collections.emptyMap();
        }
        
        Map<String, List<BibleBookDto>> result = new HashMap<>();
        
        for (Map.Entry<String, List<BookDto>> entry : groupedBooks.entrySet()) {
            String testament = entry.getKey();
            List<BookDto> books = entry.getValue();
            List<BibleBookDto> convertedBooks = fromBookDtoList(books);
            result.put(testament, convertedBooks);
        }
        
        return result;
    }
    
    /**
     * BibleStatisticsDto → Map<String, Integer> 변환
     * 통계 데이터를 기존 API 호환 형태로 변환
     */
    public Map<String, Integer> fromStatisticsDto(BibleStatisticsDto statisticsDto) {
        if (statisticsDto == null) {
            return Collections.emptyMap();
        }
        
        Map<String, Integer> statistics = new HashMap<>();
        statistics.put("totalBooks", statisticsDto.getTotalBooks());
        statistics.put("oldTestamentBooks", statisticsDto.getOldTestamentBooks());
        statistics.put("newTestamentBooks", statisticsDto.getNewTestamentBooks());
        statistics.put("totalChapters", statisticsDto.getTotalChapters());
        
        return statistics;
    }
}
