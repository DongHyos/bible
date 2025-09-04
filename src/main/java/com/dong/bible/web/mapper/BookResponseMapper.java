package com.dong.bible.web.mapper;

import com.dong.bible.application.dto.query.BookQuery;
import com.dong.bible.application.dto.query.BibleStatisticsQuery;
import com.dong.bible.ENUM.Testament;
import com.dong.bible.web.dto.response.BibleBookResponse;
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
     * BookQuery → BibleBookResponse 변환
     */
    public BibleBookResponse fromBookQuery(BookQuery bookDto) {
        if (bookDto == null) {
            return null;
        }
        
        return BibleBookResponse.builder()
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
     * List<BookQuery> → List<BibleBookResponse> 변환
     */
    public List<BibleBookResponse> fromBookQueryList(List<BookQuery> bookDtos) {
        if (bookDtos == null) {
            return Collections.emptyList();
        }
        
        return bookDtos.stream()
                .map(this::fromBookQuery)
                .filter(dto -> dto != null)
                .toList();
    }
    
    /**
     * Map<String, List<BookQuery>> → Map<String, List<BibleBookResponse>> 변환
     * 그룹핑된 성경책 데이터 변환
     */
    public Map<String, List<BibleBookResponse>> fromGroupedBooks(Map<String, List<BookQuery>> groupedBooks) {
        if (groupedBooks == null) {
            return Collections.emptyMap();
        }
        
        Map<String, List<BibleBookResponse>> result = new HashMap<>();
        
        for (Map.Entry<String, List<BookQuery>> entry : groupedBooks.entrySet()) {
            String testament = entry.getKey();
            List<BookQuery> books = entry.getValue();
            List<BibleBookResponse> convertedBooks = fromBookQueryList(books);
            result.put(testament, convertedBooks);
        }
        
        return result;
    }
    
    /**
     * BibleStatisticsQuery → Map<String, Integer> 변환
     * 통계 데이터를 기존 API 호환 형태로 변환
     */
    public Map<String, Integer> fromStatisticsDto(BibleStatisticsQuery statisticsDto) {
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
