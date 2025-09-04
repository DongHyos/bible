package com.dong.bible.web.controller;

import com.dong.bible.application.dto.query.VerseSearchResultQuery;
import com.dong.bible.application.dto.query.EnhancedVerseSearchResultQuery;
import com.dong.bible.application.service.VerseSearchApplicationService;
import com.dong.bible.common.AppProperties;
import com.dong.bible.common.utils.ApplicationContextProvider;
import com.dong.bible.web.dto.response.VerseSearchResponse;
import com.dong.bible.web.dto.response.EnhancedVerseSearchResponse;
import com.dong.bible.web.mapper.VerseSearchResponseMapper;
import com.dong.bible.web.mapper.EnhancedVerseSearchResponseMapper;
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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;
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

    @MockitoBean
    private EnhancedVerseSearchResponseMapper enhancedVerseSearchResponseMapper;

    private VerseSearchResultQuery sampleResultDto;
    private VerseSearchResponse sampleResponse;
    private EnhancedVerseSearchResultQuery enhancedResultDto;
    private EnhancedVerseSearchResponse enhancedResponse;
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
        sampleResultDto = VerseSearchResultQuery.builder()
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

        // Enhanced 검색 결과 DTO
        enhancedResultDto = EnhancedVerseSearchResultQuery.builder()
                .id("1:1:1")
                .bookId(1)
                .bookName("창세기")
                .chapter(1)
                .verse(1)
                .content("태초에 하나님이 천지를 창조하시니라")
                .displayReference("창세기 1:1")
                .highlightedContent("태초에 <mark>하나님</mark>이 천지를 창조하시니라")
                .score(0.85)
                .relevanceLevel("HIGH")
                .highlightCount(1)
                .hasHighlight(true)
                .searchKeyword("하나님")
                .isHighQuality(true)
                .isPerfectMatch(false)
                .isPartialMatch(true)
                .highlightQuality(0.8)
                .build();

        // Enhanced 검색 응답 DTO
        enhancedResponse = EnhancedVerseSearchResponse.builder()
                .id("1:1:1")
                .bookId(1)
                .bookName("창세기")
                .chapter(1)
                .verse(1)
                .content("태초에 하나님이 천지를 창조하시니라")
                .displayReference("창세기 1:1")
                .highlightedContent("태초에 <mark>하나님</mark>이 천지를 창조하시니라")
                .score(0.85)
                .relevanceLevel("HIGH")
                .highlightCount(1)
                .hasHighlight(true)
                .searchKeyword("하나님")
                .isHighQuality(true)
                .isPerfectMatch(false)
                .isPartialMatch(true)
                .highlightQuality(0.8)
                .processingTimeMs(50L)
                .searchType("content")
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
        List<VerseSearchResultQuery> resultDtos = Arrays.asList(sampleResultDto);
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
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.code").value("S000"))
                .andExpect(jsonPath("$.payload").isArray())
                .andExpect(jsonPath("$.payload[0].id").value("1:1:1"))
                .andExpect(jsonPath("$.payload[0].bookName").value("창세기"))
                .andExpect(jsonPath("$.payload[0].content").value("태초에 하나님이 천지를 창조하시니라"))
                .andExpect(jsonPath("$.payload[0].displayReference").value("창세기 1:1"));
    }

    @Test
    @DisplayName("구절 내용으로 검색 - 검색결과 없음")
    void searchByContent_NoResults() throws Exception {
        // given
        String keyword = "존재하지않는키워드";
        List<VerseSearchResultQuery> emptyResults = Collections.emptyList();
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
                .andExpect(jsonPath("$.status").value(204))
                .andExpect(jsonPath("$.code").value("S003"))
                .andExpect(jsonPath("$.message").value("조회된 결과가 없습니다."));
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
        List<VerseSearchResultQuery> resultDtos = Arrays.asList(sampleResultDto);
        List<VerseSearchResponse> responses = Arrays.asList(sampleResponse);

        when(verseSearchApplicationService.searchByBookName(bookName)).thenReturn(resultDtos);
        when(verseSearchResponseMapper.toResponseList(resultDtos)).thenReturn(responses);

        // when & then
        mockMvc.perform(get("/api/verses/search/book/{bookName}", bookName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.payload").isArray())
                .andExpect(jsonPath("$.payload[0].bookName").value("창세기"));
    }

    @Test
    @DisplayName("책 이름으로 검색 - 한글 책이름 URL 인코딩")
    void searchByBookName_KoreanBookName() throws Exception {
        // given
        String bookName = "출애굽기";
        List<VerseSearchResultQuery> resultDtos = Arrays.asList(sampleResultDto);
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
        List<VerseSearchResultQuery> resultDtos = Arrays.asList(sampleResultDto);
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
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.payload").isArray())
                .andExpect(jsonPath("$.payload[0].bookId").value(1))
                .andExpect(jsonPath("$.payload[0].chapter").value(1));
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
        List<VerseSearchResultQuery> resultDtos = Arrays.asList(sampleResultDto);

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

    // ========== 페이징 테스트 ==========

    @Test
    @DisplayName("구절 내용으로 검색 (페이징) - 성공")
    void searchByContentWithPaging_Success() throws Exception {
        // given
        String keyword = "하나님";
        Pageable pageable = PageRequest.of(0, 10);
        List<VerseSearchResultQuery> resultDtos = Arrays.asList(sampleResultDto);
        Page<VerseSearchResultQuery> page = new PageImpl<>(resultDtos, pageable, 1);
        List<VerseSearchResponse> responses = Arrays.asList(sampleResponse);

        when(verseSearchApplicationService.searchByContentWithPaging(eq(keyword), any(Pageable.class))).thenReturn(page);
        when(verseSearchResponseMapper.toResponseList(resultDtos)).thenReturn(responses);

        // when & then
        mockMvc.perform(get("/api/verses/search/paged")
                        .param("keyword", keyword)
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.code").value("S000"))
                .andExpect(jsonPath("$.payload").isArray())
                .andExpect(jsonPath("$.payload[0].id").value("1:1:1"))
                .andExpect(jsonPath("$.payload[0].bookName").value("창세기"))
                .andExpect(jsonPath("$.payload[0].content").value("태초에 하나님이 천지를 창조하시니라"))
                .andExpect(jsonPath("$.paging.totalElements").value(1))
                .andExpect(jsonPath("$.paging.totalPages").value(1))
                .andExpect(jsonPath("$.paging.number").value(0))
                .andExpect(jsonPath("$.paging.numberOfElements").value(1))
                .andExpect(jsonPath("$.paging.first").value(true))
                .andExpect(jsonPath("$.paging.last").value(true));
    }

    @Test
    @DisplayName("구절 내용으로 검색 (페이징) - 기본 페이징 파라미터")
    void searchByContentWithPaging_DefaultPaging() throws Exception {
        // given
        String keyword = "하나님";
        Pageable pageable = PageRequest.of(0, 20); // 기본값
        List<VerseSearchResultQuery> resultDtos = Arrays.asList(sampleResultDto);
        Page<VerseSearchResultQuery> page = new PageImpl<>(resultDtos, pageable, 1);
        List<VerseSearchResponse> responses = Arrays.asList(sampleResponse);

        when(verseSearchApplicationService.searchByContentWithPaging(eq(keyword), any(Pageable.class))).thenReturn(page);
        when(verseSearchResponseMapper.toResponseList(resultDtos)).thenReturn(responses);

        // when & then
        mockMvc.perform(get("/api/verses/search/paged")
                        .param("keyword", keyword)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.number").value(0));
    }

    @Test
    @DisplayName("구절 내용으로 검색 (페이징) - 두 번째 페이지")
    void searchByContentWithPaging_SecondPage() throws Exception {
        // given
        String keyword = "하나님";
        Pageable pageable = PageRequest.of(1, 5);
        List<VerseSearchResultQuery> resultDtos = Arrays.asList(sampleResultDto);
        Page<VerseSearchResultQuery> page = new PageImpl<>(resultDtos, pageable, 10); // 총 10개 항목
        List<VerseSearchResponse> responses = Arrays.asList(sampleResponse);

        when(verseSearchApplicationService.searchByContentWithPaging(eq(keyword), any(Pageable.class))).thenReturn(page);
        when(verseSearchResponseMapper.toResponseList(resultDtos)).thenReturn(responses);

        // when & then
        mockMvc.perform(get("/api/verses/search/paged")
                        .param("keyword", keyword)
                        .param("page", "1")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paging.number").value(1))
                .andExpect(jsonPath("$.paging.totalElements").value(10))
                .andExpect(jsonPath("$.paging.totalPages").value(2))
                .andExpect(jsonPath("$.paging.first").value(false))
                .andExpect(jsonPath("$.paging.last").value(true));
    }

    @Test
    @DisplayName("구절 내용으로 검색 (페이징) - 빈 결과")
    void searchByContentWithPaging_EmptyResult() throws Exception {
        // given
        String keyword = "존재하지않는키워드";
        Pageable pageable = PageRequest.of(0, 10);
        Page<VerseSearchResultQuery> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        List<VerseSearchResponse> emptyResponses = Collections.emptyList();

        when(verseSearchApplicationService.searchByContentWithPaging(eq(keyword), any(Pageable.class))).thenReturn(emptyPage);
        when(verseSearchResponseMapper.toResponseList(Collections.emptyList())).thenReturn(emptyResponses);

        // when & then
        mockMvc.perform(get("/api/verses/search/paged")
                        .param("keyword", keyword)
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload").isArray())
                .andExpect(jsonPath("$.payload").isEmpty())
                .andExpect(jsonPath("$.paging.totalElements").value(0))
                .andExpect(jsonPath("$.paging.empty").value(true));
    }

    @Test
    @DisplayName("구절 내용으로 검색 (페이징) - keyword 파라미터 누락")
    void searchByContentWithPaging_MissingKeywordParameter() throws Exception {
        // when & then
        mockMvc.perform(get("/api/verses/search/paged")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError()); // 파라미터 누락시 500 에러
    }

    // ========== Enhanced 검색 API 테스트 ==========

    @Test
    @DisplayName("Enhanced 구절 내용 검색 API - 성공")
    void searchByContentEnhanced_Success() throws Exception {
        // given
        String keyword = "하나님";
        String sortBy = "score";
        boolean useSynonyms = false;
        
        List<EnhancedVerseSearchResultQuery> resultDtos = Arrays.asList(enhancedResultDto);
        List<EnhancedVerseSearchResultQuery> sortedResults = Arrays.asList(enhancedResultDto);
        List<EnhancedVerseSearchResponse> responses = Arrays.asList(enhancedResponse);
        
        EnhancedVerseSearchResponse.SearchOptions searchOptions = 
                EnhancedVerseSearchResponse.SearchOptions.builder()
                        .useSynonyms(false)
                        .sortBy("score")
                        .includeScore(true)
                        .fragmentSize(150)
                        .maxFragments(3)
                        .build();

        when(verseSearchApplicationService.searchByContentEnhanced(keyword)).thenReturn(resultDtos);
        when(verseSearchApplicationService.sortEnhancedResults(resultDtos, sortBy)).thenReturn(sortedResults);
        when(enhancedVerseSearchResponseMapper.createSearchOptions(useSynonyms, sortBy, true, 150, 3))
                .thenReturn(searchOptions);
        when(enhancedVerseSearchResponseMapper.toResponseList(eq(sortedResults), eq("content"), any(Long.class), eq(searchOptions)))
                .thenReturn(responses);

        // when & then
        mockMvc.perform(get("/api/verses/search/enhanced")
                        .param("keyword", keyword)
                        .param("sortBy", sortBy)
                        .param("useSynonyms", String.valueOf(useSynonyms))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.payload", hasSize(1)))
                .andExpect(jsonPath("$.payload[0].id").value("1:1:1"))
                .andExpect(jsonPath("$.payload[0].bookName").value("창세기"))
                .andExpect(jsonPath("$.payload[0].score").value(0.85))
                .andExpect(jsonPath("$.payload[0].highlightedContent").value("태초에 <mark>하나님</mark>이 천지를 창조하시니라"))
                .andExpect(jsonPath("$.payload[0].relevanceLevel").value("HIGH"))
                .andExpect(jsonPath("$.payload[0].hasHighlight").value(true))
                .andExpect(jsonPath("$.payload[0].isHighQuality").value(true))
                .andExpect(jsonPath("$.payload[0].searchType").value("content"))
                .andExpect(jsonPath("$.payload[0].processingTimeMs").value(50));

        verify(verseSearchApplicationService).searchByContentEnhanced(keyword);
        verify(verseSearchApplicationService).sortEnhancedResults(resultDtos, sortBy);
        verify(enhancedVerseSearchResponseMapper).createSearchOptions(useSynonyms, sortBy, true, 150, 3);
        verify(enhancedVerseSearchResponseMapper).toResponseList(eq(sortedResults), eq("content"), any(Long.class), eq(searchOptions));
    }

    @Test
    @DisplayName("Enhanced 구절 내용 검색 (페이징) API - 성공")
    void searchByContentEnhancedWithPaging_Success() throws Exception {
        // given
        String keyword = "하나님";
        String sortBy = "score";
        boolean useSynonyms = false;
        
        List<EnhancedVerseSearchResultQuery> resultDtos = Arrays.asList(enhancedResultDto);
        Page<EnhancedVerseSearchResultQuery> pageResult = new PageImpl<>(resultDtos, PageRequest.of(0, 20), 1);
        List<EnhancedVerseSearchResponse> responses = Arrays.asList(enhancedResponse);
        
        EnhancedVerseSearchResponse.SearchOptions searchOptions = 
                EnhancedVerseSearchResponse.SearchOptions.builder()
                        .useSynonyms(false)
                        .sortBy("score")
                        .includeScore(true)
                        .fragmentSize(150)
                        .maxFragments(3)
                        .build();

        when(verseSearchApplicationService.searchByContentEnhancedWithPaging(eq(keyword), any(Pageable.class)))
                .thenReturn(pageResult);
        when(enhancedVerseSearchResponseMapper.createSearchOptions(useSynonyms, sortBy, true, 150, 3))
                .thenReturn(searchOptions);
        when(enhancedVerseSearchResponseMapper.toResponseList(eq(resultDtos), eq("content"), any(Long.class), eq(searchOptions)))
                .thenReturn(responses);

        // when & then
        mockMvc.perform(get("/api/verses/search/enhanced/paged")
                        .param("keyword", keyword)
                        .param("sortBy", sortBy)
                        .param("useSynonyms", String.valueOf(useSynonyms))
                        .param("page", "0")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload", hasSize(1)))
                .andExpect(jsonPath("$.payload[0].highlightedContent").value("태초에 <mark>하나님</mark>이 천지를 창조하시니라"))
                .andExpect(jsonPath("$.payload[0].isHighQuality").value(true))
                .andExpect(jsonPath("$.paging.totalElements").value(1));

        verify(verseSearchApplicationService).searchByContentEnhancedWithPaging(eq(keyword), any(Pageable.class));
        verify(enhancedVerseSearchResponseMapper).createSearchOptions(useSynonyms, sortBy, true, 150, 3);
        verify(enhancedVerseSearchResponseMapper).toResponseList(eq(resultDtos), eq("content"), any(Long.class), eq(searchOptions));
    }

    @Test
    @DisplayName("Enhanced 책 이름 검색 API - 성공")
    void searchByBookNameEnhanced_Success() throws Exception {
        // given
        String bookName = "창세기";
        String sortBy = "score";
        
        List<EnhancedVerseSearchResultQuery> resultDtos = Arrays.asList(enhancedResultDto);
        List<EnhancedVerseSearchResultQuery> sortedResults = Arrays.asList(enhancedResultDto);
        List<EnhancedVerseSearchResponse> responses = Arrays.asList(enhancedResponse);
        
        EnhancedVerseSearchResponse.SearchOptions defaultOptions = 
                EnhancedVerseSearchResponse.SearchOptions.builder()
                        .useSynonyms(false)
                        .sortBy("score")
                        .includeScore(true)
                        .fragmentSize(150)
                        .maxFragments(3)
                        .build();

        when(verseSearchApplicationService.searchByBookNameEnhanced(bookName)).thenReturn(resultDtos);
        when(verseSearchApplicationService.sortEnhancedResults(resultDtos, sortBy)).thenReturn(sortedResults);
        when(enhancedVerseSearchResponseMapper.createDefaultSearchOptions()).thenReturn(defaultOptions);
        when(enhancedVerseSearchResponseMapper.toResponseList(eq(sortedResults), eq("bookName"), any(Long.class), eq(defaultOptions)))
                .thenReturn(responses);

        // when & then
        mockMvc.perform(get("/api/verses/search/enhanced/book/{bookName}", bookName)
                        .param("sortBy", sortBy)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload", hasSize(1)))
                .andExpect(jsonPath("$.payload[0].bookName").value("창세기"));

        verify(verseSearchApplicationService).searchByBookNameEnhanced(bookName);
        verify(verseSearchApplicationService).sortEnhancedResults(resultDtos, sortBy);
        verify(enhancedVerseSearchResponseMapper).createDefaultSearchOptions();
    }

    @Test
    @DisplayName("Enhanced 복합 조건 검색 API - 성공")
    void searchByMultipleConditionsEnhanced_Success() throws Exception {
        // given
        String keyword = "하나님";
        String bookName = "창세기";
        Integer chapter = 1;
        String sortBy = "score";
        boolean useSynonyms = false;
        
        List<EnhancedVerseSearchResultQuery> resultDtos = Arrays.asList(enhancedResultDto);
        Page<EnhancedVerseSearchResultQuery> pageResult = new PageImpl<>(resultDtos, PageRequest.of(0, 20), 1);
        List<EnhancedVerseSearchResponse> responses = Arrays.asList(enhancedResponse);
        
        EnhancedVerseSearchResponse.SearchOptions searchOptions = 
                EnhancedVerseSearchResponse.SearchOptions.builder()
                        .useSynonyms(false)
                        .sortBy("score")
                        .includeScore(true)
                        .fragmentSize(150)
                        .maxFragments(3)
                        .build();

        when(verseSearchApplicationService.searchByMultipleConditionsEnhanced(eq(keyword), eq(bookName), eq(chapter), any(Pageable.class)))
                .thenReturn(pageResult);
        when(enhancedVerseSearchResponseMapper.createSearchOptions(useSynonyms, sortBy, true, 150, 3))
                .thenReturn(searchOptions);
        when(enhancedVerseSearchResponseMapper.toResponseList(eq(resultDtos), eq("advanced"), any(Long.class), eq(searchOptions)))
                .thenReturn(responses);

        // when & then
        mockMvc.perform(get("/api/verses/search/enhanced/advanced")
                        .param("keyword", keyword)
                        .param("bookName", bookName)
                        .param("chapter", String.valueOf(chapter))
                        .param("sortBy", sortBy)
                        .param("useSynonyms", String.valueOf(useSynonyms))
                        .param("page", "0")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload", hasSize(1)))
                .andExpect(jsonPath("$.payload[0].bookName").value("창세기"))
                .andExpect(jsonPath("$.payload[0].chapter").value(1));

        verify(verseSearchApplicationService).searchByMultipleConditionsEnhanced(eq(keyword), eq(bookName), eq(chapter), any(Pageable.class));
        verify(enhancedVerseSearchResponseMapper).createSearchOptions(useSynonyms, sortBy, true, 150, 3);
    }

    @Test
    @DisplayName("Enhanced 동의어 검색 API - 성공")
    void searchWithSynonyms_Success() throws Exception {
        // given
        String keyword = "하나님";
        String sortBy = "score";
        
        List<EnhancedVerseSearchResultQuery> resultDtos = Arrays.asList(enhancedResultDto);
        Page<EnhancedVerseSearchResultQuery> pageResult = new PageImpl<>(resultDtos, PageRequest.of(0, 20), 1);
        List<EnhancedVerseSearchResponse> responses = Arrays.asList(enhancedResponse);
        
        EnhancedVerseSearchResponse.SearchOptions searchOptions = 
                EnhancedVerseSearchResponse.SearchOptions.builder()
                        .useSynonyms(true) // 동의어 사용
                        .sortBy("score")
                        .includeScore(true)
                        .fragmentSize(150)
                        .maxFragments(3)
                        .build();

        when(verseSearchApplicationService.searchByContentEnhancedWithPaging(eq(keyword), any(Pageable.class)))
                .thenReturn(pageResult);
        when(enhancedVerseSearchResponseMapper.createSearchOptions(true, sortBy, true, 150, 3))
                .thenReturn(searchOptions);
        when(enhancedVerseSearchResponseMapper.toResponseList(eq(resultDtos), eq("synonyms"), any(Long.class), eq(searchOptions)))
                .thenReturn(responses);

        // when & then
        mockMvc.perform(get("/api/verses/search/enhanced/synonyms")
                        .param("keyword", keyword)
                        .param("sortBy", sortBy)
                        .param("page", "0")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload", hasSize(1)))
                .andExpect(jsonPath("$.paging.totalElements").value(1));

        verify(verseSearchApplicationService).searchByContentEnhancedWithPaging(eq(keyword), any(Pageable.class));
        verify(enhancedVerseSearchResponseMapper).createSearchOptions(true, sortBy, true, 150, 3);
    }

    @Test
    @DisplayName("Enhanced 검색 API - 빈 키워드 처리")
    void searchByContentEnhanced_EmptyKeyword() throws Exception {
        // given
        String emptyKeyword = "";
        when(verseSearchApplicationService.searchByContentEnhanced(emptyKeyword))
                .thenThrow(new IllegalArgumentException("검색 키워드는 비어있을 수 없습니다"));

        // when & then
        mockMvc.perform(get("/api/verses/search/enhanced")
                        .param("keyword", emptyKeyword)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError());

        verify(verseSearchApplicationService).searchByContentEnhanced(emptyKeyword);
    }

    @Test
    @DisplayName("Enhanced 검색 API - Application Service 예외 처리")
    void searchByContentEnhanced_ServiceException() throws Exception {
        // given
        String keyword = "하나님";
        when(verseSearchApplicationService.searchByContentEnhanced(keyword))
                .thenThrow(new RuntimeException("ElasticSearch 연결 실패"));

        // when & then
        mockMvc.perform(get("/api/verses/search/enhanced")
                        .param("keyword", keyword)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError());

        verify(verseSearchApplicationService).searchByContentEnhanced(keyword);
        verify(enhancedVerseSearchResponseMapper, never()).toResponseList(any(), anyString(), any(Long.class), any());
    }

    @Test
    @DisplayName("Enhanced 복합 검색 API - 선택적 파라미터 처리")
    void searchByMultipleConditionsEnhanced_OptionalParameters() throws Exception {
        // given - keyword만 제공
        String keyword = "하나님";
        
        List<EnhancedVerseSearchResultQuery> resultDtos = Arrays.asList(enhancedResultDto);
        Page<EnhancedVerseSearchResultQuery> pageResult = new PageImpl<>(resultDtos, PageRequest.of(0, 20), 1);
        List<EnhancedVerseSearchResponse> responses = Arrays.asList(enhancedResponse);
        
        EnhancedVerseSearchResponse.SearchOptions searchOptions = 
                EnhancedVerseSearchResponse.SearchOptions.builder()
                        .useSynonyms(false)
                        .sortBy("score")
                        .includeScore(true)
                        .fragmentSize(150)
                        .maxFragments(3)
                        .build();

        when(verseSearchApplicationService.searchByMultipleConditionsEnhanced(eq(keyword), eq(null), eq(null), any(Pageable.class)))
                .thenReturn(pageResult);
        when(enhancedVerseSearchResponseMapper.createSearchOptions(false, "score", true, 150, 3))
                .thenReturn(searchOptions);
        when(enhancedVerseSearchResponseMapper.toResponseList(eq(resultDtos), eq("advanced"), any(Long.class), eq(searchOptions)))
                .thenReturn(responses);

        // when & then
        mockMvc.perform(get("/api/verses/search/enhanced/advanced")
                        .param("keyword", keyword)
                        // bookName과 chapter는 제공하지 않음
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload", hasSize(1)));

        verify(verseSearchApplicationService).searchByMultipleConditionsEnhanced(eq(keyword), eq(null), eq(null), any(Pageable.class));
    }

    @Test
    @DisplayName("Enhanced 검색 API - 기본값 파라미터 테스트")
    void searchByContentEnhanced_DefaultParameters() throws Exception {
        // given
        String keyword = "하나님";
        // sortBy와 useSynonyms 파라미터를 제공하지 않음 (기본값 사용)
        
        List<EnhancedVerseSearchResultQuery> resultDtos = Arrays.asList(enhancedResultDto);
        List<EnhancedVerseSearchResultQuery> sortedResults = Arrays.asList(enhancedResultDto);
        List<EnhancedVerseSearchResponse> responses = Arrays.asList(enhancedResponse);
        
        EnhancedVerseSearchResponse.SearchOptions searchOptions = 
                EnhancedVerseSearchResponse.SearchOptions.builder()
                        .useSynonyms(false) // 기본값
                        .sortBy("score") // 기본값
                        .includeScore(true)
                        .fragmentSize(150)
                        .maxFragments(3)
                        .build();

        when(verseSearchApplicationService.searchByContentEnhanced(keyword)).thenReturn(resultDtos);
        when(verseSearchApplicationService.sortEnhancedResults(resultDtos, "score")).thenReturn(sortedResults);
        when(enhancedVerseSearchResponseMapper.createSearchOptions(false, "score", true, 150, 3))
                .thenReturn(searchOptions);
        when(enhancedVerseSearchResponseMapper.toResponseList(eq(sortedResults), eq("content"), any(Long.class), eq(searchOptions)))
                .thenReturn(responses);

        // when & then
        mockMvc.perform(get("/api/verses/search/enhanced")
                        .param("keyword", keyword)
                        // 다른 파라미터는 제공하지 않아서 기본값 사용
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // 기본값으로 호출되었는지 확인
        verify(verseSearchApplicationService).sortEnhancedResults(resultDtos, "score");
        verify(enhancedVerseSearchResponseMapper).createSearchOptions(false, "score", true, 150, 3);
    }

    @Test
    @DisplayName("Enhanced 검색 API - 처리 시간 측정 검증")
    void searchByContentEnhanced_ProcessingTimeTracking() throws Exception {
        // given
        String keyword = "하나님";
        List<EnhancedVerseSearchResultQuery> resultDtos = Arrays.asList(enhancedResultDto);
        List<EnhancedVerseSearchResultQuery> sortedResults = Arrays.asList(enhancedResultDto);
        List<EnhancedVerseSearchResponse> responses = Arrays.asList(enhancedResponse);
        
        EnhancedVerseSearchResponse.SearchOptions searchOptions = 
                EnhancedVerseSearchResponse.SearchOptions.builder()
                        .useSynonyms(false)
                        .sortBy("score")
                        .includeScore(true)
                        .fragmentSize(150)
                        .maxFragments(3)
                        .build();

        when(verseSearchApplicationService.searchByContentEnhanced(keyword)).thenReturn(resultDtos);
        when(verseSearchApplicationService.sortEnhancedResults(resultDtos, "score")).thenReturn(sortedResults);
        when(enhancedVerseSearchResponseMapper.createSearchOptions(false, "score", true, 150, 3))
                .thenReturn(searchOptions);
        when(enhancedVerseSearchResponseMapper.toResponseList(eq(sortedResults), eq("content"), any(Long.class), eq(searchOptions)))
                .thenReturn(responses);

        // when & then
        mockMvc.perform(get("/api/verses/search/enhanced")
                        .param("keyword", keyword)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload[0].processingTimeMs").value(50L));

        // 처리 시간이 mapper에 전달되었는지 확인
        verify(enhancedVerseSearchResponseMapper).toResponseList(eq(sortedResults), eq("content"), any(Long.class), eq(searchOptions));
    }
}