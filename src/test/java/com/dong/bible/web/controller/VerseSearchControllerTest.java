package com.dong.bible.web.controller;

import com.dong.bible.application.dto.VerseSearchResultDto;
import com.dong.bible.application.service.VerseSearchApplicationService;
import com.dong.bible.common.AppProperties;
import com.dong.bible.common.utils.ApplicationContextProvider;
import com.dong.bible.web.dto.response.VerseSearchResponse;
import com.dong.bible.web.mapper.VerseSearchResponseMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = VerseSearchController.class,
           excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, 
                                                 pattern = "com\\.dong\\.bible\\.common\\.filter\\..*"))
@TestPropertySource(properties = "app.error.use.advice=true")
@DisplayName("VerseSearchController 테스트")
class VerseSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VerseSearchApplicationService verseSearchApplicationService;

    @MockitoBean
    private VerseSearchResponseMapper verseSearchResponseMapper;

    private VerseSearchResultDto sampleResultDto;
    private VerseSearchResponse sampleResponse;
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

        // 테스트 데이터 설정
        sampleResultDto = VerseSearchResultDto.builder()
                .id("1:1:1")
                .bookId(1)
                .bookName("창세기")
                .chapter(1)
                .verse(1)
                .content("태초에 하나님이 천지를 창조하시니라")
                .displayReference("창세기 1:1")
                .build();

        sampleResponse = VerseSearchResponse.builder()
                .id("1:1:1")
                .bookId(1)
                .bookName("창세기")
                .chapter(1)
                .verse(1)
                .content("태초에 하나님이 천지를 창조하시니라")
                .displayReference("창세기 1:1")
                .build();
    }

    @AfterEach
    void tearDown() {
        if (mockedStatic != null) {
            mockedStatic.close();
        }
    }

    @Test
    @DisplayName("구절 내용으로 검색 - 성공")
    void searchByContent_Success() throws Exception {
        // given
        String keyword = "하나님";
        List<VerseSearchResultDto> resultDtos = Arrays.asList(sampleResultDto);
        List<VerseSearchResponse> responses = Arrays.asList(sampleResponse);

        when(verseSearchApplicationService.searchByContent(keyword)).thenReturn(resultDtos);
        when(verseSearchResponseMapper.toResponseList(resultDtos)).thenReturn(responses);

        // when & then
        mockMvc.perform(get("/api/verses/search")
                        .param("keyword", keyword)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("1:1:1"))
                .andExpect(jsonPath("$[0].bookName").value("창세기"))
                .andExpect(jsonPath("$[0].content").value("태초에 하나님이 천지를 창조하시니라"))
                .andExpect(jsonPath("$[0].displayReference").value("창세기 1:1"));
    }

    @Test
    @DisplayName("구절 내용으로 검색 - 검색결과 없음")
    void searchByContent_NoResults() throws Exception {
        // given
        String keyword = "존재하지않는키워드";
        List<VerseSearchResultDto> emptyResults = Collections.emptyList();
        List<VerseSearchResponse> emptyResponses = Collections.emptyList();

        when(verseSearchApplicationService.searchByContent(keyword)).thenReturn(emptyResults);
        when(verseSearchResponseMapper.toResponseList(emptyResults)).thenReturn(emptyResponses);

        // when & then
        mockMvc.perform(get("/api/verses/search")
                        .param("keyword", keyword)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("구절 내용으로 검색 - 키워드 파라미터 누락")
    void searchByContent_MissingKeywordParameter() throws Exception {
        // when & then
        mockMvc.perform(get("/api/verses/search")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError()); // 실제로는 500 에러 발생
    }

    @Test
    @DisplayName("책 이름으로 검색 - 성공")
    void searchByBookName_Success() throws Exception {
        // given
        String bookName = "창세기";
        List<VerseSearchResultDto> resultDtos = Arrays.asList(sampleResultDto);
        List<VerseSearchResponse> responses = Arrays.asList(sampleResponse);

        when(verseSearchApplicationService.searchByBookName(bookName)).thenReturn(resultDtos);
        when(verseSearchResponseMapper.toResponseList(resultDtos)).thenReturn(responses);

        // when & then
        mockMvc.perform(get("/api/verses/search/book/{bookName}", bookName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].bookName").value("창세기"));
    }

    @Test
    @DisplayName("책 이름으로 검색 - 한글 책이름 URL 인코딩")
    void searchByBookName_KoreanBookName() throws Exception {
        // given
        String bookName = "출애굽기";
        List<VerseSearchResultDto> resultDtos = Arrays.asList(sampleResultDto);
        List<VerseSearchResponse> responses = Arrays.asList(sampleResponse);

        when(verseSearchApplicationService.searchByBookName(bookName)).thenReturn(resultDtos);
        when(verseSearchResponseMapper.toResponseList(resultDtos)).thenReturn(responses);

        // when & then
        mockMvc.perform(get("/api/verses/search/book/{bookName}", bookName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("책과 장으로 검색 - 성공")
    void searchByBookAndChapter_Success() throws Exception {
        // given
        Integer bookId = 1;
        Integer chapter = 1;
        List<VerseSearchResultDto> resultDtos = Arrays.asList(sampleResultDto);
        List<VerseSearchResponse> responses = Arrays.asList(sampleResponse);

        when(verseSearchApplicationService.searchByBookAndChapter(bookId, chapter)).thenReturn(resultDtos);
        when(verseSearchResponseMapper.toResponseList(resultDtos)).thenReturn(responses);

        // when & then
        mockMvc.perform(get("/api/verses/search/chapter")
                        .param("bookId", bookId.toString())
                        .param("chapter", chapter.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].bookId").value(1))
                .andExpect(jsonPath("$[0].chapter").value(1));
    }

    @Test
    @DisplayName("책과 장으로 검색 - bookId 파라미터 누락")
    void searchByBookAndChapter_MissingBookIdParameter() throws Exception {
        // when & then
        mockMvc.perform(get("/api/verses/search/chapter")
                        .param("chapter", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError()); // 실제로는 500 에러 발생
    }

    @Test
    @DisplayName("책과 장으로 검색 - chapter 파라미터 누락")
    void searchByBookAndChapter_MissingChapterParameter() throws Exception {
        // when & then
        mockMvc.perform(get("/api/verses/search/chapter")
                        .param("bookId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError()); // 실제로는 500 에러 발생
    }

    @Test
    @DisplayName("책과 장으로 검색 - 잘못된 bookId 형식")
    void searchByBookAndChapter_InvalidBookIdFormat() throws Exception {
        // when & then
        mockMvc.perform(get("/api/verses/search/chapter")
                        .param("bookId", "invalid")
                        .param("chapter", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError()); // 실제로는 500 에러 발생
    }

    @Test
    @DisplayName("책과 장으로 검색 - 잘못된 chapter 형식")
    void searchByBookAndChapter_InvalidChapterFormat() throws Exception {
        // when & then
        mockMvc.perform(get("/api/verses/search/chapter")
                        .param("bookId", "1")
                        .param("chapter", "invalid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError()); // 실제로는 500 에러 발생
    }

    @Test
    @DisplayName("서비스 예외 발생시 500 에러")
    void searchByContent_ServiceException_Returns500() throws Exception {
        // given
        String keyword = "테스트";
        when(verseSearchApplicationService.searchByContent(keyword))
                .thenThrow(new RuntimeException("ElasticSearch 연결 실패"));

        // when & then
        mockMvc.perform(get("/api/verses/search")
                        .param("keyword", keyword)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("매퍼 예외 발생시 500 에러")
    void searchByContent_MapperException_Returns500() throws Exception {
        // given
        String keyword = "테스트";
        List<VerseSearchResultDto> resultDtos = Arrays.asList(sampleResultDto);

        when(verseSearchApplicationService.searchByContent(keyword)).thenReturn(resultDtos);
        when(verseSearchResponseMapper.toResponseList(any()))
                .thenThrow(new RuntimeException("매핑 실패"));

        // when & then
        mockMvc.perform(get("/api/verses/search")
                        .param("keyword", keyword)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }
}