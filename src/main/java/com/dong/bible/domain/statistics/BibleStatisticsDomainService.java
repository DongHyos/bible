package com.dong.bible.domain.statistics;

import com.dong.bible.domain.book.Book;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 성경 통계 계산을 담당하는 도메인 서비스
 * 
 * 도메인 서비스는 다음과 같은 경우에 사용됩니다:
 * - 여러 도메인 객체가 협력하는 비즈니스 로직
 * - 단일 Entity에 속하지 않는 복잡한 계산
 * - 도메인 전문가와 논의가 필요한 핵심 비즈니스 로직
 */
@Service
public class BibleStatisticsDomainService {
    
    /**
     * 성경 통계 계산
     * 
     * @param allBooks 전체 성경책 목록
     * @param oldTestamentBooks 구약 성경책 목록
     * @param newTestamentBooks 신약 성경책 목록
     * @return 계산된 성경 통계
     */
    public BibleStatistics calculateStatistics(List<Book> allBooks, 
                                              List<Book> oldTestamentBooks, 
                                              List<Book> newTestamentBooks) {
        
        // 비즈니스 로직 1: 총 장수 계산
        int totalChapters = calculateTotalChapters(allBooks);
        
        // 비즈니스 로직 2: 구약/신약 비율 계산
        double oldTestamentRatio = calculateOldTestamentRatio(oldTestamentBooks, allBooks);
        double newTestamentRatio = calculateNewTestamentRatio(newTestamentBooks, allBooks);
        
        // 비즈니스 로직 3: 평균 장수 계산
        double averageChaptersPerBook = calculateAverageChaptersPerBook(allBooks);
        
        return BibleStatistics.of(
            allBooks.size(),
            oldTestamentBooks.size(),
            newTestamentBooks.size(),
            totalChapters,
            oldTestamentRatio,
            newTestamentRatio,
            averageChaptersPerBook
        );
    }
    
    /**
     * 총 장수 계산 (비즈니스 로직)
     */
    private int calculateTotalChapters(List<Book> books) {
        return books.stream()
            .mapToInt(Book::getTotalChapters)
            .sum();
    }
    
    /**
     * 구약 비율 계산 (비즈니스 로직)
     */
    private double calculateOldTestamentRatio(List<Book> oldBooks, List<Book> allBooks) {
        if (allBooks.isEmpty()) {
            return 0.0;
        }
        return (double) oldBooks.size() / allBooks.size();
    }
    
    /**
     * 신약 비율 계산 (비즈니스 로직)
     */
    private double calculateNewTestamentRatio(List<Book> newBooks, List<Book> allBooks) {
        if (allBooks.isEmpty()) {
            return 0.0;
        }
        return (double) newBooks.size() / allBooks.size();
    }
    
    /**
     * 평균 장수 계산 (비즈니스 로직)
     */
    private double calculateAverageChaptersPerBook(List<Book> books) {
        if (books.isEmpty()) {
            return 0.0;
        }
        return (double) calculateTotalChapters(books) / books.size();
    }
}