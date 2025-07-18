package com.dong.bible.domain.statistics;

import com.dong.bible.domain.book.Book;
import com.dong.bible.domain.book.BookName;
import com.dong.bible.ENUM.Testament;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * BibleStatisticsDomainService 단위 테스트
 * 
 * 도메인 서비스는 순수한 비즈니스 로직만 포함하므로:
 * - Mock 없이 실제 도메인 객체를 사용
 * - 입력과 출력에 집중한 테스트
 * - 비즈니스 로직의 정확성 검증
 */
class BibleStatisticsDomainServiceTest {
    
    private BibleStatisticsDomainService domainService;
    
    // 테스트용 도메인 객체들
    private Book 창세기;
    private Book 출애굽기;
    private Book 마태복음;
    private Book 요한복음;
    
    @BeforeEach
    void setUp() {
        domainService = new BibleStatisticsDomainService();
        
        // 테스트 데이터 생성
        창세기 = Book.of(1L, BookName.of("창세기"), "창", Testament.구약, 1, 50, 1L);
        출애굽기 = Book.of(2L, BookName.of("출애굽기"), "출", Testament.구약, 2, 40, 1L);
        마태복음 = Book.of(40L, BookName.of("마태복음"), "마", Testament.신약, 40, 28, 2L);
        요한복음 = Book.of(43L, BookName.of("요한복음"), "요", Testament.신약, 43, 21, 2L);
    }
    
    @Test
    void 정상적인_통계_계산() {
        // Given
        List<Book> allBooks = Arrays.asList(창세기, 출애굽기, 마태복음, 요한복음);
        List<Book> oldBooks = Arrays.asList(창세기, 출애굽기);
        List<Book> newBooks = Arrays.asList(마태복음, 요한복음);
        
        // When
        BibleStatistics result = domainService.calculateStatistics(allBooks, oldBooks, newBooks);
        
        // Then
        assertThat(result.getTotalBooks()).isEqualTo(4);
        assertThat(result.getOldTestamentBooks()).isEqualTo(2);
        assertThat(result.getNewTestamentBooks()).isEqualTo(2);
        assertThat(result.getTotalChapters()).isEqualTo(139); // 50 + 40 + 28 + 21
        assertThat(result.getOldTestamentRatio()).isEqualTo(0.5);
        assertThat(result.getNewTestamentRatio()).isEqualTo(0.5);
        assertThat(result.getAverageChaptersPerBook()).isEqualTo(34.75); // 139 / 4
    }
    
    @Test
    void 구약만_있는_경우() {
        // Given
        List<Book> allBooks = Arrays.asList(창세기, 출애굽기);
        List<Book> oldBooks = Arrays.asList(창세기, 출애굽기);
        List<Book> newBooks = Collections.emptyList();
        
        // When
        BibleStatistics result = domainService.calculateStatistics(allBooks, oldBooks, newBooks);
        
        // Then
        assertThat(result.getTotalBooks()).isEqualTo(2);
        assertThat(result.getOldTestamentBooks()).isEqualTo(2);
        assertThat(result.getNewTestamentBooks()).isEqualTo(0);
        assertThat(result.getTotalChapters()).isEqualTo(90); // 50 + 40
        assertThat(result.getOldTestamentRatio()).isEqualTo(1.0);
        assertThat(result.getNewTestamentRatio()).isEqualTo(0.0);
        assertThat(result.getAverageChaptersPerBook()).isEqualTo(45.0); // 90 / 2
    }
    
    @Test
    void 신약만_있는_경우() {
        // Given
        List<Book> allBooks = Arrays.asList(마태복음, 요한복음);
        List<Book> oldBooks = Collections.emptyList();
        List<Book> newBooks = Arrays.asList(마태복음, 요한복음);
        
        // When
        BibleStatistics result = domainService.calculateStatistics(allBooks, oldBooks, newBooks);
        
        // Then
        assertThat(result.getTotalBooks()).isEqualTo(2);
        assertThat(result.getOldTestamentBooks()).isEqualTo(0);
        assertThat(result.getNewTestamentBooks()).isEqualTo(2);
        assertThat(result.getTotalChapters()).isEqualTo(49); // 28 + 21
        assertThat(result.getOldTestamentRatio()).isEqualTo(0.0);
        assertThat(result.getNewTestamentRatio()).isEqualTo(1.0);
        assertThat(result.getAverageChaptersPerBook()).isEqualTo(24.5); // 49 / 2
    }
    
    @Test
    void 빈_목록_처리() {
        // Given
        List<Book> allBooks = Collections.emptyList();
        List<Book> oldBooks = Collections.emptyList();
        List<Book> newBooks = Collections.emptyList();
        
        // When
        BibleStatistics result = domainService.calculateStatistics(allBooks, oldBooks, newBooks);
        
        // Then
        assertThat(result.getTotalBooks()).isEqualTo(0);
        assertThat(result.getOldTestamentBooks()).isEqualTo(0);
        assertThat(result.getNewTestamentBooks()).isEqualTo(0);
        assertThat(result.getTotalChapters()).isEqualTo(0);
        assertThat(result.getOldTestamentRatio()).isEqualTo(0.0);
        assertThat(result.getNewTestamentRatio()).isEqualTo(0.0);
        assertThat(result.getAverageChaptersPerBook()).isEqualTo(0.0);
    }
    
    @Test
    void 단일_성경책_처리() {
        // Given
        List<Book> allBooks = Arrays.asList(창세기);
        List<Book> oldBooks = Arrays.asList(창세기);
        List<Book> newBooks = Collections.emptyList();
        
        // When
        BibleStatistics result = domainService.calculateStatistics(allBooks, oldBooks, newBooks);
        
        // Then
        assertThat(result.getTotalBooks()).isEqualTo(1);
        assertThat(result.getOldTestamentBooks()).isEqualTo(1);
        assertThat(result.getNewTestamentBooks()).isEqualTo(0);
        assertThat(result.getTotalChapters()).isEqualTo(50);
        assertThat(result.getOldTestamentRatio()).isEqualTo(1.0);
        assertThat(result.getNewTestamentRatio()).isEqualTo(0.0);
        assertThat(result.getAverageChaptersPerBook()).isEqualTo(50.0);
    }
}