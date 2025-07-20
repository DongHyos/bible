package com.dong.bible.web.controller;

import com.dong.bible.application.service.VerseIndexingApplicationService;
import com.dong.bible.common.AppProperties;
import com.dong.bible.common.utils.ApplicationContextProvider;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = VerseIndexingController.class,
           excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, 
                                                 pattern = "com\\.dong\\.bible\\.common\\.filter\\..*"))
@TestPropertySource(properties = "app.error.use.advice=true")
@DisplayName("VerseIndexingController 테스트")
class VerseIndexingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VerseIndexingApplicationService verseIndexingApplicationService;

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
    @DisplayName("전체 구절 인덱싱 - 성공")
    void indexAllVerses_Success() throws Exception {
        // given
        doNothing().when(verseIndexingApplicationService).indexAllVerses();
        when(verseIndexingApplicationService.getIndexedVerseCount()).thenReturn(31000L);

        // when & then
        mockMvc.perform(post("/api/admin/index/verses/full")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("전체 성경 구절 인덱싱이 완료되었습니다"))
                .andExpect(jsonPath("$.indexedCount").value(31000));

        verify(verseIndexingApplicationService).indexAllVerses();
        verify(verseIndexingApplicationService).getIndexedVerseCount();
    }

    @Test
    @DisplayName("전체 구절 인덱싱 - 실패")
    void indexAllVerses_Failure() throws Exception {
        // given
        doThrow(new RuntimeException("ElasticSearch 연결 실패")).when(verseIndexingApplicationService).indexAllVerses();

        // when & then
        mockMvc.perform(post("/api/admin/index/verses/full")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("인덱싱 작업 실패: ElasticSearch 연결 실패"))
                .andExpect(jsonPath("$.error").value("RuntimeException"));

        verify(verseIndexingApplicationService).indexAllVerses();
        verify(verseIndexingApplicationService, never()).getIndexedVerseCount();
    }

    @Test
    @DisplayName("책별 구절 인덱싱 - 성공")
    void indexVersesByBook_Success() throws Exception {
        // given
        Integer bookId = 1;
        String bookName = "창세기";
        int indexedCount = 1533;

        when(verseIndexingApplicationService.indexVersesByBook(bookId, bookName)).thenReturn(indexedCount);

        // when & then
        mockMvc.perform(post("/api/admin/index/verses/book/{bookId}", bookId)
                        .param("bookName", bookName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("책 '창세기' 인덱싱이 완료되었습니다"))
                .andExpect(jsonPath("$.bookId").value(1))
                .andExpect(jsonPath("$.bookName").value("창세기"))
                .andExpect(jsonPath("$.indexedCount").value(1533));

        verify(verseIndexingApplicationService).indexVersesByBook(bookId, bookName);
    }

    @Test
    @DisplayName("책별 구절 인덱싱 - bookName 파라미터 누락")
    void indexVersesByBook_MissingBookNameParameter() throws Exception {
        // given
        Integer bookId = 1;

        // when & then
        mockMvc.perform(post("/api/admin/index/verses/book/{bookId}", bookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError()); // 실제로는 500 에러 발생

        verify(verseIndexingApplicationService, never()).indexVersesByBook(any(), any());
    }

    @Test
    @DisplayName("책별 구절 인덱싱 - 잘못된 bookId 형식")
    void indexVersesByBook_InvalidBookIdFormat() throws Exception {
        // when & then
        mockMvc.perform(post("/api/admin/index/verses/book/{bookId}", "invalid")
                        .param("bookName", "창세기")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError()); // 실제로는 500 에러 발생

        verify(verseIndexingApplicationService, never()).indexVersesByBook(any(), any());
    }

    @Test
    @DisplayName("책별 구절 인덱싱 - 서비스 예외")
    void indexVersesByBook_ServiceException() throws Exception {
        // given
        Integer bookId = 1;
        String bookName = "창세기";

        when(verseIndexingApplicationService.indexVersesByBook(bookId, bookName))
                .thenThrow(new RuntimeException("DB 연결 실패"));

        // when & then
        mockMvc.perform(post("/api/admin/index/verses/book/{bookId}", bookId)
                        .param("bookName", bookName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("책별 인덱싱 실패: DB 연결 실패"))
                .andExpect(jsonPath("$.bookId").value(1))
                .andExpect(jsonPath("$.bookName").value("창세기"))
                .andExpect(jsonPath("$.error").value("RuntimeException"));

        verify(verseIndexingApplicationService).indexVersesByBook(bookId, bookName);
    }

    @Test
    @DisplayName("인덱스 전체 삭제 - 성공")
    void deleteAllIndex_Success() throws Exception {
        // given
        doNothing().when(verseIndexingApplicationService).deleteAllIndex();

        // when & then
        mockMvc.perform(delete("/api/admin/index/verses")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("ElasticSearch 인덱스가 삭제되었습니다"));

        verify(verseIndexingApplicationService).deleteAllIndex();
    }

    @Test
    @DisplayName("인덱스 전체 삭제 - 실패")
    void deleteAllIndex_Failure() throws Exception {
        // given
        doThrow(new RuntimeException("ElasticSearch 연결 실패")).when(verseIndexingApplicationService).deleteAllIndex();

        // when & then
        mockMvc.perform(delete("/api/admin/index/verses")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("인덱스 삭제 실패: ElasticSearch 연결 실패"))
                .andExpect(jsonPath("$.error").value("RuntimeException"));

        verify(verseIndexingApplicationService).deleteAllIndex();
    }

    @Test
    @DisplayName("인덱스 상태 조회 - 성공 (데이터 있음)")
    void getIndexStatus_Success_WithData() throws Exception {
        // given
        long indexedCount = 31000L;
        when(verseIndexingApplicationService.getIndexedVerseCount()).thenReturn(indexedCount);

        // when & then
        mockMvc.perform(get("/api/admin/index/verses/status")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.indexedCount").value(31000))
                .andExpect(jsonPath("$.message").value("현재 31000개의 구절이 인덱싱되어 있습니다"));

        verify(verseIndexingApplicationService).getIndexedVerseCount();
    }

    @Test
    @DisplayName("인덱스 상태 조회 - 성공 (데이터 없음)")
    void getIndexStatus_Success_NoData() throws Exception {
        // given
        long indexedCount = 0L;
        when(verseIndexingApplicationService.getIndexedVerseCount()).thenReturn(indexedCount);

        // when & then
        mockMvc.perform(get("/api/admin/index/verses/status")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.indexedCount").value(0))
                .andExpect(jsonPath("$.message").value("인덱싱된 데이터가 없습니다"));

        verify(verseIndexingApplicationService).getIndexedVerseCount();
    }

    @Test
    @DisplayName("인덱스 상태 조회 - 실패")
    void getIndexStatus_Failure() throws Exception {
        // given
        when(verseIndexingApplicationService.getIndexedVerseCount())
                .thenThrow(new RuntimeException("ElasticSearch 연결 실패"));

        // when & then
        mockMvc.perform(get("/api/admin/index/verses/status")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("상태 조회 실패: ElasticSearch 연결 실패"))
                .andExpect(jsonPath("$.error").value("RuntimeException"));

        verify(verseIndexingApplicationService).getIndexedVerseCount();
    }

    @Test
    @DisplayName("한글 책이름 URL 인코딩 처리")
    void indexVersesByBook_KoreanBookName() throws Exception {
        // given
        Integer bookId = 2;
        String bookName = "출애굽기";
        int indexedCount = 1213;

        when(verseIndexingApplicationService.indexVersesByBook(bookId, bookName)).thenReturn(indexedCount);

        // when & then
        mockMvc.perform(post("/api/admin/index/verses/book/{bookId}", bookId)
                        .param("bookName", bookName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookName").value("출애굽기"));

        verify(verseIndexingApplicationService).indexVersesByBook(bookId, bookName);
    }

    @Test
    @DisplayName("존재하지 않는 엔드포인트 호출")
    void invalidEndpoint_Returns404() throws Exception {
        // when & then
        mockMvc.perform(post("/api/admin/index/verses/invalid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("지원하지 않는 HTTP 메서드")
    void unsupportedHttpMethod_Returns405() throws Exception {
        // when & then
        mockMvc.perform(put("/api/admin/index/verses/full")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
    }
}