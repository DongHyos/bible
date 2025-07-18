package com.dong.bible.application.service;

import com.dong.bible.ENUM.Testament;
import com.dong.bible.application.dto.BibleStatisticsDto;
import com.dong.bible.application.dto.BookDto;
import com.dong.bible.domain.book.Book;
import com.dong.bible.domain.book.BookName;
import com.dong.bible.domain.book.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookQueryServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookQueryService bookQueryService;

    private Book 요한복음;
    private Book 창세기;
    private Book 시편;

    @BeforeEach
    void setUp() {
        요한복음 = Book.of(43L, BookName.of("요한복음"), "요", Testament.신약, 43, 21, 6L);
        창세기 = Book.of(1L, BookName.of("창세기"), "창", Testament.구약, 1, 50, 1L);
        시편 = Book.of(19L, BookName.of("시편"), "시", Testament.구약, 19, 150, 3L);
    }

    @Test
    void 모든_성경책_조회_Application_DTO_반환() {
        // Given
        List<Book> allBooks = Arrays.asList(창세기, 시편, 요한복음);
        when(bookRepository.findAll()).thenReturn(allBooks);

        // When
        List<BookDto> result = bookQueryService.getAllBooks();

        // Then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getId()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo("창세기");
        assertThat(result.get(0).getAbbreviation()).isEqualTo("창");
        assertThat(result.get(0).getTestament()).isEqualTo(Testament.구약);
        assertThat(result.get(0).getTotalChapters()).isEqualTo(50);
        
        verify(bookRepository).findAll();
    }

    @Test
    void ID로_성경책_조회_Application_DTO_반환() {
        // Given - ID로 조회하는 대신 전체 목록에서 찾는 방식으로 테스트
        Integer bookId = 43;
        List<Book> allBooks = Arrays.asList(창세기, 시편, 요한복음);
        when(bookRepository.findAll()).thenReturn(allBooks);

        // When
        BookDto result = bookQueryService.getBookById(bookId);

        // Then
        assertThat(result.getId()).isEqualTo(43);
        assertThat(result.getName()).isEqualTo("요한복음");
        assertThat(result.getAbbreviation()).isEqualTo("요");
        assertThat(result.getTestament()).isEqualTo(Testament.신약);
        assertThat(result.getTotalChapters()).isEqualTo(21);
        
        verify(bookRepository).findAll();
    }

    @Test
    void ID로_성경책_조회_존재하지_않음_예외_발생() {
        // Given
        Integer bookId = 999;
        List<Book> allBooks = Arrays.asList(창세기, 시편, 요한복음);
        when(bookRepository.findAll()).thenReturn(allBooks);

        // When & Then
        assertThatThrownBy(() -> bookQueryService.getBookById(bookId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Book not found with id: " + bookId);
        
        verify(bookRepository).findAll();
    }

    @Test
    void 신구약별_성경책_조회_Application_DTO_반환() {
        // Given
        List<Book> 신약책들 = Arrays.asList(요한복음);
        List<Book> 구약책들 = Arrays.asList(창세기, 시편);
        
        when(bookRepository.findByTestament(Testament.신약)).thenReturn(신약책들);
        when(bookRepository.findByTestament(Testament.구약)).thenReturn(구약책들);

        // When
        List<BookDto> 신약결과 = bookQueryService.getBooksByTestament("신약");
        List<BookDto> 구약결과 = bookQueryService.getBooksByTestament("구약");

        // Then
        assertThat(신약결과).hasSize(1);
        assertThat(신약결과.get(0).getName()).isEqualTo("요한복음");
        assertThat(신약결과.get(0).getTestament()).isEqualTo(Testament.신약);
        
        assertThat(구약결과).hasSize(2);
        assertThat(구약결과.get(0).getName()).isEqualTo("창세기");
        assertThat(구약결과.get(1).getName()).isEqualTo("시편");
        assertThat(구약결과.get(0).getTestament()).isEqualTo(Testament.구약);

        verify(bookRepository).findByTestament(Testament.신약);
        verify(bookRepository).findByTestament(Testament.구약);
    }

    @Test
    void 잘못된_신구약_구분_예외_발생() {
        // When & Then
        assertThatThrownBy(() -> bookQueryService.getBooksByTestament("중약"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unknown testament: 중약");
        
        verify(bookRepository, never()).findByTestament(any());
    }

    @Test
    void 신구약별_그룹핑된_성경책_조회() {
        // Given
        List<Book> 구약Books = Arrays.asList(창세기, 시편);
        List<Book> 신약Books = Arrays.asList(요한복음);
        when(bookRepository.findOldTestamentBooks()).thenReturn(구약Books);
        when(bookRepository.findNewTestamentBooks()).thenReturn(신약Books);

        // When
        Map<String, List<BookDto>> result = bookQueryService.getGroupedBooksByTestament();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get("구약")).hasSize(2);
        assertThat(result.get("신약")).hasSize(1);
        
        assertThat(result.get("구약").get(0).getName()).isEqualTo("창세기");
        assertThat(result.get("구약").get(1).getName()).isEqualTo("시편");
        assertThat(result.get("신약").get(0).getName()).isEqualTo("요한복음");
        
        verify(bookRepository).findOldTestamentBooks();
        verify(bookRepository).findNewTestamentBooks();
    }

    @Test
    void 성경_통계_조회() {
        // Given
        List<Book> allBooks = Arrays.asList(창세기, 시편, 요한복음);
        List<Book> 구약Books = Arrays.asList(창세기, 시편);
        List<Book> 신약Books = Arrays.asList(요한복음);
        
        when(bookRepository.findAll()).thenReturn(allBooks);
        when(bookRepository.findOldTestamentBooks()).thenReturn(구약Books);
        when(bookRepository.findNewTestamentBooks()).thenReturn(신약Books);

        // When
        BibleStatisticsDto result = bookQueryService.getBibleStatistics();

        // Then
        assertThat(result.getTotalBooks()).isEqualTo(3);
        assertThat(result.getOldTestamentBooks()).isEqualTo(2);
        assertThat(result.getNewTestamentBooks()).isEqualTo(1);
        assertThat(result.getTotalChapters()).isEqualTo(221); // 50 + 150 + 21
        
        verify(bookRepository).findAll();
        verify(bookRepository).findOldTestamentBooks();
        verify(bookRepository).findNewTestamentBooks();
    }

    @Test
    void 성경책_이름으로_BookDto_조회_성공() {
        // Given
        String bookName = "요한복음";
        when(bookRepository.findByName(bookName)).thenReturn(Optional.of(요한복음));

        // When
        Optional<BookDto> result = bookQueryService.getBookByName(bookName);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("요한복음");
        assertThat(result.get().getTotalChapters()).isEqualTo(21);
        assertThat(result.get().getTestament()).isEqualTo(Testament.신약);
        verify(bookRepository).findByName(bookName);
    }

    @Test
    void 성경책_이름으로_BookDto_조회_실패() {
        // Given
        String bookName = "존재하지않는책";
        when(bookRepository.findByName(bookName)).thenReturn(Optional.empty());

        // When
        Optional<BookDto> result = bookQueryService.getBookByName(bookName);

        // Then
        assertThat(result).isEmpty();
        verify(bookRepository).findByName(bookName);
    }

    @Test
    void 성경책_이름_null_또는_공백_처리() {
        // When & Then
        assertThat(bookQueryService.getBookByName((String)null)).isEmpty();
        assertThat(bookQueryService.getBookByName("")).isEmpty();
        assertThat(bookQueryService.getBookByName("   ")).isEmpty();

        // Repository 호출되지 않음
        verify(bookRepository, never()).findByName(anyString());
    }

    @Test
    void BookName_Value_Object로_BookDto_조회() {
        // Given
        BookName bookName = BookName.of("요한복음");
        when(bookRepository.findByName(bookName)).thenReturn(Optional.of(요한복음));

        // When
        Optional<BookDto> result = bookQueryService.getBookByName(bookName);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("요한복음");
        assertThat(result.get().getId()).isEqualTo(43);
        verify(bookRepository).findByName(bookName);
    }

    @Test
    void 성경책_이름으로_DB_ID_조회() {
        // Given
        String bookName = "요한복음";
        when(bookRepository.findByName(bookName)).thenReturn(Optional.of(요한복음));

        // When
        Optional<Integer> result = bookQueryService.getBookIdByName(bookName);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(43);
        verify(bookRepository).findByName(bookName);
    }

    @Test
    void DB_ID로_성경책_이름_조회() {
        // Given
        Integer bookId = 43;
        List<Book> allBooks = Arrays.asList(창세기, 시편, 요한복음);
        when(bookRepository.findAll()).thenReturn(allBooks);

        // When
        String result = bookQueryService.getBookNameById(bookId);

        // Then
        assertThat(result).isEqualTo("요한복음");
        verify(bookRepository).findAll();
    }

    @Test
    void 성경책_존재_여부_확인_성공() {
        // Given
        String bookName = "요한복음";
        BookName bookNameObj = BookName.of(bookName);
        when(bookRepository.existsByName(bookNameObj)).thenReturn(true);

        // When
        boolean result = bookQueryService.existsBook(bookName);

        // Then
        assertThat(result).isTrue();
        verify(bookRepository).existsByName(bookNameObj);
    }

    @Test
    void 특정_장_유효성_검증_성공() {
        // Given
        String bookName = "요한복음";
        int chapter = 10;
        when(bookRepository.findByName(bookName)).thenReturn(Optional.of(요한복음));

        // When
        boolean result = bookQueryService.isValidChapter(bookName, chapter);

        // Then
        assertThat(result).isTrue();
        verify(bookRepository).findByName(bookName);
    }

    @Test
    void 특정_장_유효성_검증_실패() {
        // Given
        String bookName = "요한복음";
        int chapter = 25; // 요한복음은 21장까지
        when(bookRepository.findByName(bookName)).thenReturn(Optional.of(요한복음));

        // When
        boolean result = bookQueryService.isValidChapter(bookName, chapter);

        // Then
        assertThat(result).isFalse();
        verify(bookRepository).findByName(bookName);
    }

    @Test
    void 성경책의_총_장수_조회_성공() {
        // Given
        String bookName = "시편";
        when(bookRepository.findByName(bookName)).thenReturn(Optional.of(시편));

        // When
        Optional<Integer> result = bookQueryService.getTotalChapters(bookName);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(150);
        verify(bookRepository).findByName(bookName);
    }

    @Test
    void 전체_성경책_개수_조회() {
        // Given
        when(bookRepository.count()).thenReturn(66L);

        // When
        long result = bookQueryService.getTotalBookCount();

        // Then
        assertThat(result).isEqualTo(66L);
        verify(bookRepository).count();
    }

    @Test
    void 공백_문자_자동_trim_처리() {
        // Given
        String bookName = "  요한복음  ";
        when(bookRepository.findByName("요한복음")).thenReturn(Optional.of(요한복음));

        // When
        Optional<BookDto> result = bookQueryService.getBookByName(bookName);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("요한복음");
        verify(bookRepository).findByName("요한복음"); // trim된 값으로 호출
    }
}