package com.dong.bible.application.service;

import com.dong.bible.infrastructure.search.ElasticsearchIndexRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("IndexManagementApplicationService 테스트")
class IndexManagementApplicationServiceTest {

    @Mock
    private ElasticsearchIndexRepository elasticsearchIndexRepository;

    @InjectMocks
    private IndexManagementApplicationService indexManagementApplicationService;

    @Test
    @DisplayName("동의어 설정이 포함된 인덱스 생성 - 성공")
    void createIndexWithSynonyms_Success() {
        // given
        String indexName = "test_index";
        List<String> synonyms = Arrays.asList(
            "하나님,하느님,여호와",
            "예수,그리스도,메시아"
        );

        doNothing().when(elasticsearchIndexRepository).createIndexWithSynonyms(indexName, synonyms);

        // when
        assertThatNoException().isThrownBy(() -> 
            indexManagementApplicationService.createIndexWithSynonyms(indexName, synonyms));

        // then
        verify(elasticsearchIndexRepository).createIndexWithSynonyms(indexName, synonyms);
    }

    @Test
    @DisplayName("동의어 설정이 포함된 인덱스 생성 - 실패")
    void createIndexWithSynonyms_Failure() {
        // given
        String indexName = "test_index";
        List<String> synonyms = Arrays.asList("하나님,하느님,여호와");
        
        doThrow(new RuntimeException("ElasticSearch 연결 실패"))
            .when(elasticsearchIndexRepository).createIndexWithSynonyms(indexName, synonyms);

        // when & then
        assertThatThrownBy(() -> 
            indexManagementApplicationService.createIndexWithSynonyms(indexName, synonyms))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("ElasticSearch 연결 실패");

        verify(elasticsearchIndexRepository).createIndexWithSynonyms(indexName, synonyms);
    }

    @Test
    @DisplayName("리인덱싱 - 성공")
    void reindexData_Success() {
        // given
        String sourceIndex = "old_index";
        String targetIndex = "new_index";

        doNothing().when(elasticsearchIndexRepository).reindexData(sourceIndex, targetIndex);

        // when
        assertThatNoException().isThrownBy(() -> 
            indexManagementApplicationService.reindexData(sourceIndex, targetIndex));

        // then
        verify(elasticsearchIndexRepository).reindexData(sourceIndex, targetIndex);
    }

    @Test
    @DisplayName("리인덱싱 - 실패")
    void reindexData_Failure() {
        // given
        String sourceIndex = "old_index";
        String targetIndex = "new_index";
        
        doThrow(new RuntimeException("소스 인덱스가 존재하지 않습니다"))
            .when(elasticsearchIndexRepository).reindexData(sourceIndex, targetIndex);

        // when & then
        assertThatThrownBy(() -> 
            indexManagementApplicationService.reindexData(sourceIndex, targetIndex))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("소스 인덱스가 존재하지 않습니다");

        verify(elasticsearchIndexRepository).reindexData(sourceIndex, targetIndex);
    }

    @Test
    @DisplayName("인덱스 상태 조회 - 성공")
    void getIndexStatus_Success() {
        // given
        String indexName = "test_index";
        Map<String, Object> expectedStatus = Map.of(
            "exists", true,
            "indexName", indexName,
            "settings", "{ \"number_of_shards\": 1 }",
            "mappings", "{ \"properties\": { \"content\": { \"type\": \"text\" } } }"
        );

        when(elasticsearchIndexRepository.getIndexStatus(indexName)).thenReturn(expectedStatus);

        // when
        Map<String, Object> result = indexManagementApplicationService.getIndexStatus(indexName);

        // then
        assertThat(result).isEqualTo(expectedStatus);
        assertThat(result.get("exists")).isEqualTo(true);
        assertThat(result.get("indexName")).isEqualTo(indexName);
        verify(elasticsearchIndexRepository).getIndexStatus(indexName);
    }

    @Test
    @DisplayName("인덱스 상태 조회 - 인덱스가 존재하지 않는 경우")
    void getIndexStatus_IndexNotExists() {
        // given
        String indexName = "nonexistent_index";
        Map<String, Object> expectedStatus = Map.of(
            "exists", false,
            "message", "인덱스가 존재하지 않습니다"
        );

        when(elasticsearchIndexRepository.getIndexStatus(indexName)).thenReturn(expectedStatus);

        // when
        Map<String, Object> result = indexManagementApplicationService.getIndexStatus(indexName);

        // then
        assertThat(result).isEqualTo(expectedStatus);
        assertThat(result.get("exists")).isEqualTo(false);
        verify(elasticsearchIndexRepository).getIndexStatus(indexName);
    }

    @Test
    @DisplayName("인덱스 상태 조회 - 실패")
    void getIndexStatus_Failure() {
        // given
        String indexName = "test_index";
        
        when(elasticsearchIndexRepository.getIndexStatus(indexName))
            .thenThrow(new RuntimeException("인덱스 상태 조회에 실패했습니다"));

        // when & then
        assertThatThrownBy(() -> indexManagementApplicationService.getIndexStatus(indexName))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("인덱스 상태 조회에 실패했습니다");

        verify(elasticsearchIndexRepository).getIndexStatus(indexName);
    }

    @Test
    @DisplayName("인덱스 삭제 - 성공")
    void deleteIndex_Success() {
        // given
        String indexName = "test_index";

        doNothing().when(elasticsearchIndexRepository).deleteIndex(indexName);

        // when
        assertThatNoException().isThrownBy(() -> 
            indexManagementApplicationService.deleteIndex(indexName));

        // then
        verify(elasticsearchIndexRepository).deleteIndex(indexName);
    }

    @Test
    @DisplayName("인덱스 삭제 - 실패")
    void deleteIndex_Failure() {
        // given
        String indexName = "test_index";
        
        doThrow(new RuntimeException("인덱스 삭제에 실패했습니다"))
            .when(elasticsearchIndexRepository).deleteIndex(indexName);

        // when & then
        assertThatThrownBy(() -> indexManagementApplicationService.deleteIndex(indexName))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("인덱스 삭제에 실패했습니다");

        verify(elasticsearchIndexRepository).deleteIndex(indexName);
    }

    @Test
    @DisplayName("빈 동의어 리스트로 인덱스 생성")
    void createIndexWithSynonyms_EmptySynonyms() {
        // given
        String indexName = "test_index";
        List<String> emptySynonyms = Arrays.asList();

        doNothing().when(elasticsearchIndexRepository).createIndexWithSynonyms(indexName, emptySynonyms);

        // when
        assertThatNoException().isThrownBy(() -> 
            indexManagementApplicationService.createIndexWithSynonyms(indexName, emptySynonyms));

        // then
        verify(elasticsearchIndexRepository).createIndexWithSynonyms(indexName, emptySynonyms);
    }

    @Test
    @DisplayName("null 동의어 리스트로 인덱스 생성")
    void createIndexWithSynonyms_NullSynonyms() {
        // given
        String indexName = "test_index";
        List<String> nullSynonyms = null;

        doNothing().when(elasticsearchIndexRepository).createIndexWithSynonyms(indexName, nullSynonyms);

        // when
        assertThatNoException().isThrownBy(() -> 
            indexManagementApplicationService.createIndexWithSynonyms(indexName, nullSynonyms));

        // then
        verify(elasticsearchIndexRepository).createIndexWithSynonyms(indexName, nullSynonyms);
    }
}