package com.dong.bible.web.controller;

import com.dong.bible.application.service.IndexManagementApplicationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = IndexManagementController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX,
                pattern = "com\\.dong\\.bible\\.common\\.filter\\..*"))
@TestPropertySource(properties = "app.error.use.advice=true")
@DisplayName("IndexManagementController 테스트")
class IndexManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IndexManagementApplicationService indexManagementApplicationService;

    @Test
    @DisplayName("동의어 설정이 포함된 인덱스 생성 - 성공")
    void createIndexWithSynonyms_Success() throws Exception {
        // given
        String indexName = "test_index";
        List<String> synonyms = Arrays.asList(
            "하나님,하느님,여호와",
            "예수,그리스도,메시아"
        );
        
        IndexManagementController.CreateIndexRequest request = new IndexManagementController.CreateIndexRequest();
        request.setSynonyms(synonyms);

        doNothing().when(indexManagementApplicationService).createIndexWithSynonyms(indexName, synonyms);

        // when & then
        mockMvc.perform(post("/api/admin/elasticsearch/index/{indexName}/create", indexName)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("인덱스가 성공적으로 생성되었습니다"))
                .andExpect(jsonPath("$.indexName").value(indexName))
                .andExpect(jsonPath("$.synonymCount").value(2));

        verify(indexManagementApplicationService).createIndexWithSynonyms(indexName, synonyms);
    }

    @Test
    @DisplayName("동의어 설정이 포함된 인덱스 생성 - 실패")
    void createIndexWithSynonyms_Failure() throws Exception {
        // given
        String indexName = "test_index";
        List<String> synonyms = Arrays.asList("하나님,하느님,여호와");
        
        IndexManagementController.CreateIndexRequest request = new IndexManagementController.CreateIndexRequest();
        request.setSynonyms(synonyms);

        doThrow(new RuntimeException("ElasticSearch 연결 실패"))
            .when(indexManagementApplicationService).createIndexWithSynonyms(indexName, synonyms);

        // when & then
        mockMvc.perform(post("/api/admin/elasticsearch/index/{indexName}/create", indexName)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("인덱스 생성 실패: ElasticSearch 연결 실패"))
                .andExpect(jsonPath("$.indexName").value(indexName))
                .andExpect(jsonPath("$.error").value("RuntimeException"));

        verify(indexManagementApplicationService).createIndexWithSynonyms(indexName, synonyms);
    }

    @Test
    @DisplayName("동의어 null로 인덱스 생성")
    void createIndexWithSynonyms_NullSynonyms() throws Exception {
        // given
        String indexName = "test_index";
        
        IndexManagementController.CreateIndexRequest request = new IndexManagementController.CreateIndexRequest();
        request.setSynonyms(null);

        doNothing().when(indexManagementApplicationService).createIndexWithSynonyms(eq(indexName), any());

        // when & then
        mockMvc.perform(post("/api/admin/elasticsearch/index/{indexName}/create", indexName)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.synonymCount").value(0));

        verify(indexManagementApplicationService).createIndexWithSynonyms(eq(indexName), eq(null));
    }

    @Test
    @DisplayName("리인덱싱 - 성공")
    void reindexData_Success() throws Exception {
        // given
        String sourceIndex = "old_index";
        String targetIndex = "new_index";
        
        IndexManagementController.ReindexRequest request = new IndexManagementController.ReindexRequest();
        request.setSourceIndex(sourceIndex);
        request.setTargetIndex(targetIndex);

        doNothing().when(indexManagementApplicationService).reindexData(sourceIndex, targetIndex);

        // when & then
        mockMvc.perform(post("/api/admin/elasticsearch/reindex")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("리인덱싱이 성공적으로 완료되었습니다"))
                .andExpect(jsonPath("$.sourceIndex").value(sourceIndex))
                .andExpect(jsonPath("$.targetIndex").value(targetIndex));

        verify(indexManagementApplicationService).reindexData(sourceIndex, targetIndex);
    }

    @Test
    @DisplayName("리인덱싱 - 실패")
    void reindexData_Failure() throws Exception {
        // given
        String sourceIndex = "old_index";
        String targetIndex = "new_index";
        
        IndexManagementController.ReindexRequest request = new IndexManagementController.ReindexRequest();
        request.setSourceIndex(sourceIndex);
        request.setTargetIndex(targetIndex);

        doThrow(new RuntimeException("소스 인덱스가 존재하지 않습니다"))
            .when(indexManagementApplicationService).reindexData(sourceIndex, targetIndex);

        // when & then
        mockMvc.perform(post("/api/admin/elasticsearch/reindex")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("리인덱싱 실패: 소스 인덱스가 존재하지 않습니다"))
                .andExpect(jsonPath("$.sourceIndex").value(sourceIndex))
                .andExpect(jsonPath("$.targetIndex").value(targetIndex))
                .andExpect(jsonPath("$.error").value("RuntimeException"));

        verify(indexManagementApplicationService).reindexData(sourceIndex, targetIndex);
    }

    @Test
    @DisplayName("인덱스 상태 조회 - 성공")
    void getIndexStatus_Success() throws Exception {
        // given
        String indexName = "test_index";
        Map<String, Object> statusData = Map.of(
            "exists", true,
            "indexName", indexName,
            "settings", "{ \"number_of_shards\": 1 }",
            "mappings", "{ \"properties\": { \"content\": { \"type\": \"text\" } } }"
        );

        when(indexManagementApplicationService.getIndexStatus(indexName)).thenReturn(statusData);

        // when & then
        mockMvc.perform(get("/api/admin/elasticsearch/index/{indexName}/status", indexName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.exists").value(true))
                .andExpect(jsonPath("$.data.indexName").value(indexName));

        verify(indexManagementApplicationService).getIndexStatus(indexName);
    }

    @Test
    @DisplayName("인덱스 상태 조회 - 실패")
    void getIndexStatus_Failure() throws Exception {
        // given
        String indexName = "test_index";

        when(indexManagementApplicationService.getIndexStatus(indexName))
            .thenThrow(new RuntimeException("인덱스 상태 조회에 실패했습니다"));

        // when & then
        mockMvc.perform(get("/api/admin/elasticsearch/index/{indexName}/status", indexName))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("인덱스 상태 조회 실패: 인덱스 상태 조회에 실패했습니다"))
                .andExpect(jsonPath("$.indexName").value(indexName))
                .andExpect(jsonPath("$.error").value("RuntimeException"));

        verify(indexManagementApplicationService).getIndexStatus(indexName);
    }

    @Test
    @DisplayName("인덱스 삭제 - 성공")
    void deleteIndex_Success() throws Exception {
        // given
        String indexName = "test_index";

        doNothing().when(indexManagementApplicationService).deleteIndex(indexName);

        // when & then
        mockMvc.perform(delete("/api/admin/elasticsearch/index/{indexName}", indexName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("인덱스가 성공적으로 삭제되었습니다"))
                .andExpect(jsonPath("$.indexName").value(indexName));

        verify(indexManagementApplicationService).deleteIndex(indexName);
    }

    @Test
    @DisplayName("인덱스 삭제 - 실패")
    void deleteIndex_Failure() throws Exception {
        // given
        String indexName = "test_index";

        doThrow(new RuntimeException("인덱스 삭제에 실패했습니다"))
            .when(indexManagementApplicationService).deleteIndex(indexName);

        // when & then
        mockMvc.perform(delete("/api/admin/elasticsearch/index/{indexName}", indexName))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("인덱스 삭제 실패: 인덱스 삭제에 실패했습니다"))
                .andExpect(jsonPath("$.indexName").value(indexName))
                .andExpect(jsonPath("$.error").value("RuntimeException"));

        verify(indexManagementApplicationService).deleteIndex(indexName);
    }

    @Test
    @DisplayName("기본 성경 구절 인덱스 생성 - 성공")
    void createDefaultBibleVersesIndex_Success() throws Exception {
        // given
        doNothing().when(indexManagementApplicationService).createIndexWithSynonyms(anyString(), any());

        // when & then
        mockMvc.perform(post("/api/admin/elasticsearch/index/bible-verses/create-default"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("기본 성경 구절 인덱스가 생성되었습니다"))
                .andExpect(jsonPath("$.indexName").exists())
                .andExpect(jsonPath("$.synonymGroups").value(10))
                .andExpect(jsonPath("$.recommendation").value("이제 VerseIndexing API로 데이터를 인덱싱하세요"));

        verify(indexManagementApplicationService).createIndexWithSynonyms(anyString(), any());
    }

    @Test
    @DisplayName("기본 성경 구절 인덱스 생성 - 실패")
    void createDefaultBibleVersesIndex_Failure() throws Exception {
        // given
        doThrow(new RuntimeException("ElasticSearch 연결 실패"))
            .when(indexManagementApplicationService).createIndexWithSynonyms(anyString(), any());

        // when & then
        mockMvc.perform(post("/api/admin/elasticsearch/index/bible-verses/create-default"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("기본 인덱스 생성 실패: ElasticSearch 연결 실패"))
                .andExpect(jsonPath("$.error").value("RuntimeException"));

        verify(indexManagementApplicationService).createIndexWithSynonyms(anyString(), any());
    }
}