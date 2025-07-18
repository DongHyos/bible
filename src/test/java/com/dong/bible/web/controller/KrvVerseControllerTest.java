package com.dong.bible.web.controller;

import com.dong.bible.application.service.BookApplicationService;
import com.dong.bible.application.service.VerseApplicationService;
import com.dong.bible.application.dto.ChapterQueryDto;
import com.dong.bible.application.dto.VerseQueryDto;
import com.dong.bible.application.dto.VerseRangeQueryDto;
import com.dong.bible.application.dto.VerseSearchDto;
import com.dong.bible.common.AppProperties;
import com.dong.bible.common.error.BizException;
import com.dong.bible.common.response.ResponseCode;
import com.dong.bible.common.utils.ApplicationContextProvider;
import com.dong.bible.domain.verse.BibleVerse;
import com.dong.bible.domain.verse.VerseContent;
import com.dong.bible.domain.verse.VerseReference;
import com.dong.bible.web.dto.response.ChapterDto;
import com.dong.bible.web.dto.response.VerseDto;
import com.dong.bible.web.dto.response.VerseSearchResultDto;
import com.dong.bible.web.dto.response.VerseSimpleDto;
import com.dong.bible.web.mapper.VerseResponseMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.context.TestPropertySource;

@WebMvcTest(value = KrvVerseController.class, 
            excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, 
                                                  pattern = "com\\.dong\\.bible\\.common\\.filter\\..*"))
@TestPropertySource(properties = "app.error.use.advice=true")
class KrvVerseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VerseApplicationService verseQueryService;

    @MockitoBean
    private BookApplicationService bookQueryService;

    @MockitoBean
    private VerseResponseMapper verseResponseMapper;

    private MockedStatic<ApplicationContextProvider> mockedStatic;

    @BeforeEach
    void setUp() {
        // 기존 static mock이 있다면 정리
        if (mockedStatic != null) {
            mockedStatic.close();
        }
        
        // 새로운 static mock 생성
        mockedStatic = Mockito.mockStatic(ApplicationContextProvider.class);
        ApplicationContext mockApplicationContext = Mockito.mock(ApplicationContext.class);
        mockedStatic.when(ApplicationContextProvider::getApplicationContext).thenReturn(mockApplicationContext);
        
        // AppProperties Mock 객체 생성 및 설정
        AppProperties mockAppProperties = Mockito.mock(AppProperties.class);
        AppProperties.Option mockOption = Mockito.mock(AppProperties.Option.class);
        
        // AppProperties의 getOption() 메서드 Mock 설정
        when(mockAppProperties.getOption()).thenReturn(mockOption);
        
        // Option의 메서드들 Mock 설정
        when(mockOption.exception()).thenReturn(true);
        when(mockOption.meta()).thenReturn(true);
        when(mockOption.trace()).thenReturn(true);
        when(mockOption.uuid()).thenReturn(true);
        when(mockOption.errors()).thenReturn(true);
        when(mockOption.report()).thenReturn(true);
        when(mockApplicationContext.getBean(AppProperties.class)).thenReturn(mockAppProperties);
        
        // 기타 필요한 Bean들에 대한 기본 설정
        when(mockApplicationContext.getBean(any(Class.class))).thenAnswer(invocation -> {
            Class<?> beanType = invocation.getArgument(0);
            if (beanType == AppProperties.class) {
                return mockAppProperties;
            }
            return Mockito.mock(beanType);
        });
    }

    @AfterEach
    void tearDown() {
        if (mockedStatic != null) {
            mockedStatic.close();
        }
    }

    @Test
    void 특정_장_모든_구절_조회_성공() throws Exception {
        // Given
        Integer bookId = 43;
        Integer chapter = 3;
        
        ChapterQueryDto chapterQuery = createMockChapterQueryDto(bookId, "요한복음", chapter);
        ChapterDto chapterDto = createMockChapterDto(bookId, "요한복음", chapter);
        
        when(verseQueryService.getChapterById(bookId, chapter)).thenReturn(chapterQuery);
        when(verseResponseMapper.toChapterDto(chapterQuery)).thenReturn(chapterDto);

        // When & Then
        mockMvc.perform(get("/api/bible/books/{bookId}/chapters/{chapter}", bookId, chapter))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.code").value("S000"))
                .andExpect(jsonPath("$.payload.bookId").value(43))
                .andExpect(jsonPath("$.payload.bookName").value("요한복음"))
                .andExpect(jsonPath("$.payload.chapter").value(3))
                .andExpect(jsonPath("$.payload.verses").isArray())
                .andExpect(jsonPath("$.payload.verses.length()").value(2));
    }

    @Test
    void 특정_구절_조회_성공() throws Exception {
        // Given
        Integer bookId = 43;
        Integer chapter = 3;
        Integer verse = 16;
        String bookName = "요한복음";
        
        VerseQueryDto verseQuery = createMockVerseQueryDto(bookId, bookName, chapter, verse);
        VerseDto verseDto = createMockVerseDto(bookId, bookName, chapter, verse);
        
        // Controller에서 getVerse() 호출하므로 이 메서드를 Mock해야 함
        when(verseQueryService.getVerse(bookId, chapter, verse)).thenReturn(verseQuery);
        when(verseResponseMapper.toVerseDto(verseQuery)).thenReturn(verseDto);

        // When & Then
        mockMvc.perform(get("/api/bible/books/{bookId}/chapters/{chapter}/verses/{verse}", 
                        bookId, chapter, verse))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.code").value("S000"))
                .andExpect(jsonPath("$.payload.bookId").value(43))
                .andExpect(jsonPath("$.payload.bookName").value("요한복음"))
                .andExpect(jsonPath("$.payload.chapter").value(3))
                .andExpect(jsonPath("$.payload.verse").value(16))
                .andExpect(jsonPath("$.payload.text").value("하나님이 세상을 이처럼 사랑하사"));
    }

    @Test
    void 특정_구절_조회_Book_없음_실패() throws Exception {
        // Given
        Integer bookId = 999;
        Integer chapter = 3;
        Integer verse = 16;
        
        // IllegalArgumentException이 던져지도록 Mock 설정 (BizException에서 IllegalArgumentException으로 변경됨)
        when(verseQueryService.getVerse(bookId, chapter, verse))
                .thenThrow(new BizException(ResponseCode.REQ_BAD_REQUEST, "Book not found with id: " + bookId));

        // When & Then
        mockMvc.perform(get("/api/bible/books/{bookId}/chapters/{chapter}/verses/{verse}", 
                        bookId, chapter, verse))
                .andDo(result -> {
                    System.out.println("Response Status: " + result.getResponse().getStatus());
                    System.out.println("Response Content: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("R000"))
                .andExpect(jsonPath("$.message").value("Book not found with id: 999"));
    }

    @Test
    void 구절_범위_조회_성공() throws Exception {
        // Given
        Integer bookId = 43;
        Integer chapter = 3;
        Integer fromVerse = 16;
        Integer toVerse = 17;
        String bookName = "요한복음";
        
        VerseRangeQueryDto rangeQuery = createMockVerseRangeQueryDto(bookId, bookName, chapter, fromVerse, toVerse);
        List<VerseDto> verseDtos = createMockVerseDtoList();
        
        // Controller에서 getVerseRange() 호출하므로 이 메서드를 Mock해야 함
        when(verseQueryService.getVerseRange(bookId, chapter, fromVerse, toVerse)).thenReturn(rangeQuery);
        when(verseResponseMapper.toVerseDto(any(VerseQueryDto.class))).thenReturn(verseDtos.get(0), verseDtos.get(1));

        // When & Then
        mockMvc.perform(get("/api/bible/verses")
                        .param("bookId", bookId.toString())
                        .param("chapter", chapter.toString())
                        .param("fromVerse", fromVerse.toString())
                        .param("toVerse", toVerse.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.code").value("S000"))
                .andExpect(jsonPath("$.payload").isArray())
                .andExpect(jsonPath("$.payload.length()").value(2));
    }

    @Test
    void 구절_범위_조회_잘못된_범위_실패() throws Exception {
        // Given
        Integer bookId = 43;
        Integer chapter = 3;
        Integer fromVerse = 20; // start > end
        Integer toVerse = 10;

        // IllegalArgumentException이 던져지도록 Mock 설정 (BizException에서 IllegalArgumentException으로 변경됨)
        when(verseQueryService.getVerseRange(bookId, chapter, fromVerse, toVerse))
                .thenThrow(new BizException(ResponseCode.REQ_BAD_REQUEST, "시작 절이 끝 절보다 클 수 없습니다"));

        // When & Then
        mockMvc.perform(get("/api/bible/verses")
                        .param("bookId", bookId.toString())
                        .param("chapter", chapter.toString())
                        .param("fromVerse", fromVerse.toString())
                        .param("toVerse", toVerse.toString()))
                .andDo(result -> {
                    System.out.println("Response Status: " + result.getResponse().getStatus());
                    System.out.println("Response Content: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("R000"))
                .andExpect(jsonPath("$.message").value("시작 절이 끝 절보다 클 수 없습니다"));
    }

    @Test
    void 구절_검색_성공() throws Exception {
        // Given
        String keyword = "사랑";
        VerseSearchDto searchResult = createMockVerseSearchDto(keyword);
        List<VerseSearchResultDto> searchResultDtos = createMockVerseSearchResultDtoList();
        
        when(verseQueryService.searchVerses(keyword)).thenReturn(searchResult);
        when(verseResponseMapper.toSearchResultDtoList(searchResult)).thenReturn(searchResultDtos);

        String requestBody = """
                {
                    "keyword": "사랑"
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/bible/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.code").value("S000"))
                .andExpect(jsonPath("$.payload").isArray())
                .andExpect(jsonPath("$.payload.length()").value(2))
                .andExpect(jsonPath("$.payload[0].text").value("하나님이 세상을 이처럼 사랑하사"));
    }

    @Test
    void 특정_책의_모든_구절_조회_성공() throws Exception {
        // Given
        Integer bookId = 43;
        String bookName = "요한복음";
        List<VerseQueryDto> verseQueries = createMockVerseQueryDtoList();
        List<VerseDto> verseDtos = createMockVerseDtoList();
        
        // Controller에서 getBookVerses() 호출하므로 이 메서드를 Mock해야 함
        when(verseQueryService.getBookVerses(bookId)).thenReturn(verseQueries);
        when(verseResponseMapper.toVerseDto(any(VerseQueryDto.class))).thenReturn(verseDtos.get(0), verseDtos.get(1));

        // When & Then
        mockMvc.perform(get("/api/bible/books/{bookId}/verses", bookId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.code").value("S000"))
                .andExpect(jsonPath("$.payload").isArray())
                .andExpect(jsonPath("$.payload.length()").value(2));
    }

    // ===============================
    // Mock 데이터 생성 헬퍼 메서드들
    // ===============================

    private ChapterQueryDto createMockChapterQueryDto(Integer bookId, String bookName, Integer chapter) {
        List<BibleVerse> verses = Arrays.asList(
            BibleVerse.of(VerseReference.of(bookName, chapter, 16), VerseContent.of("하나님이 세상을 이처럼 사랑하사")),
            BibleVerse.of(VerseReference.of(bookName, chapter, 17), VerseContent.of("하나님이 그 아들을 세상에 보내신 것은"))
        );
        return ChapterQueryDto.of(bookId, bookName, chapter, verses);
    }

    private ChapterDto createMockChapterDto(Integer bookId, String bookName, Integer chapter) {
        return ChapterDto.builder()
                .bookId(bookId)
                .bookName(bookName)
                .bookAbbr("요")
                .chapter(chapter)
                .totalVerses(2)
                .verses(Arrays.asList(
                    VerseSimpleDto.builder().id(1).verse(16).text("하나님이 세상을 이처럼 사랑하사").build(),
                    VerseSimpleDto.builder().id(2).verse(17).text("하나님이 그 아들을 세상에 보내신 것은").build()
                ))
                .build();
    }

    private VerseQueryDto createMockVerseQueryDto(Integer bookId, String bookName, Integer chapter, Integer verse) {
        return VerseQueryDto.of(
            BibleVerse.of(123L, 
                VerseReference.of(bookName, chapter, verse), 
                VerseContent.of("하나님이 세상을 이처럼 사랑하사"))
        );
    }

    private VerseDto createMockVerseDto(Integer bookId, String bookName, Integer chapter, Integer verse) {
        return VerseDto.builder()
                .id(123)
                .bookId(bookId)
                .bookName(bookName)
                .bookAbbr("요")
                .chapter(chapter)
                .verse(verse)
                .text("하나님이 세상을 이처럼 사랑하사")
                .build();
    }

    private VerseRangeQueryDto createMockVerseRangeQueryDto(Integer bookId, String bookName, Integer chapter, Integer fromVerse, Integer toVerse) {
        List<BibleVerse> verses = Arrays.asList(
            BibleVerse.of(VerseReference.of(bookName, chapter, fromVerse), VerseContent.of("하나님이 세상을 이처럼 사랑하사")),
            BibleVerse.of(VerseReference.of(bookName, chapter, toVerse), VerseContent.of("하나님이 그 아들을 세상에 보내신 것은"))
        );
        return VerseRangeQueryDto.of(bookId, chapter, fromVerse, toVerse, verses);
    }

    private VerseSearchDto createMockVerseSearchDto(String keyword) {
        List<BibleVerse> verses = Arrays.asList(
            BibleVerse.of(VerseReference.of("요한복음", 3, 16), VerseContent.of("하나님이 세상을 이처럼 사랑하사")),
            BibleVerse.of(VerseReference.of("요한일서", 4, 8), VerseContent.of("사랑하지 아니하는 자는 하나님을 알지 못하나니"))
        );
        return VerseSearchDto.of(keyword, verses);
    }

    private List<VerseQueryDto> createMockVerseQueryDtoList() {
        return Arrays.asList(
            createMockVerseQueryDto(43, "요한복음", 3, 16),
            createMockVerseQueryDto(43, "요한복음", 3, 17)
        );
    }

    private List<VerseDto> createMockVerseDtoList() {
        return Arrays.asList(
            createMockVerseDto(43, "요한복음", 3, 16),
            createMockVerseDto(43, "요한복음", 3, 17)
        );
    }

    private List<VerseSearchResultDto> createMockVerseSearchResultDtoList() {
        return Arrays.asList(
            VerseSearchResultDto.builder()
                .id(123)
                .bookName("요한복음")
                .chapter(3)
                .verse(16)
                .text("하나님이 세상을 이처럼 사랑하사")
                .reference("요한복음 3:16")
                .build(),
            VerseSearchResultDto.builder()
                .id(456)
                .bookName("요한일서")
                .chapter(4)
                .verse(8)
                .text("사랑하지 아니하는 자는 하나님을 알지 못하나니")
                .reference("요한일서 4:8")
                .build()
        );
    }
}
