package com.dong.bible.infrastructure.search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.index.Settings;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ElasticsearchIndexRepository 테스트")
class ElasticsearchIndexRepositoryTest {

    @Mock
    private ElasticsearchOperations elasticsearchOperations;

    @Mock
    private IndexOperations indexOperations;

    @InjectMocks
    private ElasticsearchIndexRepository elasticsearchIndexRepository;

    @BeforeEach
    void setUp() {
        when(elasticsearchOperations.indexOps(any(IndexCoordinates.class))).thenReturn(indexOperations);
    }

    @Test
    @DisplayName("동의어가 포함된 인덱스 생성 - 성공 (기존 인덱스 없음)")
    void createIndexWithSynonyms_Success_NoExistingIndex() {
        // given
        String indexName = "test_index";
        List<String> synonyms = Arrays.asList(
            "하나님,하느님,여호와",
            "예수,그리스도,메시아"
        );

        when(indexOperations.exists()).thenReturn(false);
        when(indexOperations.create(any(Document.class))).thenReturn(true);
        when(indexOperations.putMapping(any(Document.class))).thenReturn(true);

        // when
        assertThatNoException().isThrownBy(() -> 
            elasticsearchIndexRepository.createIndexWithSynonyms(indexName, synonyms));

        // then
        verify(indexOperations).exists();
        verify(indexOperations, never()).delete();
        verify(indexOperations).create(any(Document.class));
        verify(indexOperations).putMapping(any(Document.class));
    }

    @Test
    @DisplayName("동의어가 포함된 인덱스 생성 - 성공 (기존 인덱스 있음)")
    void createIndexWithSynonyms_Success_ExistingIndex() {
        // given
        String indexName = "test_index";
        List<String> synonyms = Arrays.asList("하나님,하느님,여호와");

        when(indexOperations.exists()).thenReturn(true);
        when(indexOperations.delete()).thenReturn(true);
        when(indexOperations.create(any(Document.class))).thenReturn(true);
        when(indexOperations.putMapping(any(Document.class))).thenReturn(true);

        // when
        assertThatNoException().isThrownBy(() -> 
            elasticsearchIndexRepository.createIndexWithSynonyms(indexName, synonyms));

        // then
        verify(indexOperations).exists();
        verify(indexOperations).delete();
        verify(indexOperations).create(any(Document.class));
        verify(indexOperations).putMapping(any(Document.class));
    }

    @Test
    @DisplayName("동의어가 포함된 인덱스 생성 - 실패")
    void createIndexWithSynonyms_Failure() {
        // given
        String indexName = "test_index";
        List<String> synonyms = Arrays.asList("하나님,하느님,여호와");

        when(indexOperations.exists()).thenReturn(false);
        when(indexOperations.create(any(Document.class))).thenThrow(new RuntimeException("ElasticSearch 연결 실패"));

        // when & then
        assertThatThrownBy(() -> 
            elasticsearchIndexRepository.createIndexWithSynonyms(indexName, synonyms))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("인덱스 생성에 실패했습니다: ElasticSearch 연결 실패");

        verify(indexOperations).exists();
        verify(indexOperations).create(any(Document.class));
        verify(indexOperations, never()).putMapping(any(Document.class));
    }

    @Test
    @DisplayName("리인덱싱 - 성공")
    void reindexData_Success() {
        // given
        String sourceIndex = "old_index";
        String targetIndex = "new_index";

        IndexOperations sourceOps = mock(IndexOperations.class);
        IndexOperations targetOps = mock(IndexOperations.class);

        when(elasticsearchOperations.indexOps(IndexCoordinates.of(sourceIndex))).thenReturn(sourceOps);
        when(elasticsearchOperations.indexOps(IndexCoordinates.of(targetIndex))).thenReturn(targetOps);
        when(sourceOps.exists()).thenReturn(true);
        when(targetOps.exists()).thenReturn(true);

        // when
        assertThatNoException().isThrownBy(() -> 
            elasticsearchIndexRepository.reindexData(sourceIndex, targetIndex));

        // then
        verify(sourceOps).exists();
        verify(targetOps).exists();
    }

    @Test
    @DisplayName("리인덱싱 - 소스 인덱스 없음")
    void reindexData_SourceIndexNotExists() {
        // given
        String sourceIndex = "nonexistent_source";
        String targetIndex = "target_index";

        IndexOperations sourceOps = mock(IndexOperations.class);

        when(elasticsearchOperations.indexOps(IndexCoordinates.of(sourceIndex))).thenReturn(sourceOps);
        when(sourceOps.exists()).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> 
            elasticsearchIndexRepository.reindexData(sourceIndex, targetIndex))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("리인덱싱에 실패했습니다: 소스 인덱스가 존재하지 않습니다: " + sourceIndex);

        verify(sourceOps).exists();
    }

    @Test
    @DisplayName("리인덱싱 - 타겟 인덱스 없음")
    void reindexData_TargetIndexNotExists() {
        // given
        String sourceIndex = "source_index";
        String targetIndex = "nonexistent_target";

        IndexOperations sourceOps = mock(IndexOperations.class);
        IndexOperations targetOps = mock(IndexOperations.class);

        when(elasticsearchOperations.indexOps(IndexCoordinates.of(sourceIndex))).thenReturn(sourceOps);
        when(elasticsearchOperations.indexOps(IndexCoordinates.of(targetIndex))).thenReturn(targetOps);
        when(sourceOps.exists()).thenReturn(true);
        when(targetOps.exists()).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> 
            elasticsearchIndexRepository.reindexData(sourceIndex, targetIndex))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("리인덱싱에 실패했습니다: 타겟 인덱스가 존재하지 않습니다: " + targetIndex);

        verify(sourceOps).exists();
        verify(targetOps).exists();
    }

    @Test
    @DisplayName("인덱스 상태 조회 - 성공 (인덱스 존재)")
    void getIndexStatus_Success_IndexExists() {
        // given
        String indexName = "test_index";
        Settings mockSettings = mock(Settings.class);
        Map<String, Object> mockMappings = Map.of("properties", Map.of("content", Map.of("type", "text")));

        when(indexOperations.exists()).thenReturn(true);
        when(indexOperations.getSettings()).thenReturn(mockSettings);
        when(indexOperations.getMapping()).thenReturn(mockMappings);
        when(mockSettings.toString()).thenReturn("{ \"number_of_shards\": 1 }");

        // when
        Map<String, Object> result = elasticsearchIndexRepository.getIndexStatus(indexName);

        // then
        assertThat(result.get("exists")).isEqualTo(true);
        assertThat(result.get("indexName")).isEqualTo(indexName);
        assertThat(result.get("settings")).isEqualTo("{ \"number_of_shards\": 1 }");
        assertThat(result.get("mappings")).isNotNull();

        verify(indexOperations).exists();
        verify(indexOperations).getSettings();
        verify(indexOperations).getMapping();
    }

    @Test
    @DisplayName("인덱스 상태 조회 - 인덱스 존재하지 않음")
    void getIndexStatus_IndexNotExists() {
        // given
        String indexName = "nonexistent_index";

        when(indexOperations.exists()).thenReturn(false);

        // when
        Map<String, Object> result = elasticsearchIndexRepository.getIndexStatus(indexName);

        // then
        assertThat(result.get("exists")).isEqualTo(false);
        assertThat(result.get("message")).isEqualTo("인덱스가 존재하지 않습니다");

        verify(indexOperations).exists();
        verify(indexOperations, never()).getSettings();
        verify(indexOperations, never()).getMapping();
    }

    @Test
    @DisplayName("인덱스 상태 조회 - 실패")
    void getIndexStatus_Failure() {
        // given
        String indexName = "test_index";

        when(indexOperations.exists()).thenThrow(new RuntimeException("ElasticSearch 연결 실패"));

        // when & then
        assertThatThrownBy(() -> elasticsearchIndexRepository.getIndexStatus(indexName))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("인덱스 상태 조회에 실패했습니다: ElasticSearch 연결 실패");

        verify(indexOperations).exists();
    }

    @Test
    @DisplayName("인덱스 삭제 - 성공")
    void deleteIndex_Success() {
        // given
        String indexName = "test_index";

        when(indexOperations.exists()).thenReturn(true);
        when(indexOperations.delete()).thenReturn(true);

        // when
        assertThatNoException().isThrownBy(() -> 
            elasticsearchIndexRepository.deleteIndex(indexName));

        // then
        verify(indexOperations).exists();
        verify(indexOperations).delete();
    }

    @Test
    @DisplayName("인덱스 삭제 - 인덱스 존재하지 않음")
    void deleteIndex_IndexNotExists() {
        // given
        String indexName = "nonexistent_index";

        when(indexOperations.exists()).thenReturn(false);

        // when
        assertThatNoException().isThrownBy(() -> 
            elasticsearchIndexRepository.deleteIndex(indexName));

        // then
        verify(indexOperations).exists();
        verify(indexOperations, never()).delete();
    }

    @Test
    @DisplayName("인덱스 삭제 - 실패")
    void deleteIndex_Failure() {
        // given
        String indexName = "test_index";

        when(indexOperations.exists()).thenReturn(true);
        when(indexOperations.delete()).thenThrow(new RuntimeException("ElasticSearch 연결 실패"));

        // when & then
        assertThatThrownBy(() -> elasticsearchIndexRepository.deleteIndex(indexName))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("인덱스 삭제에 실패했습니다: ElasticSearch 연결 실패");

        verify(indexOperations).exists();
        verify(indexOperations).delete();
    }

    @Test
    @DisplayName("인덱스 존재 확인 - 존재함")
    void indexExists_True() {
        // given
        String indexName = "test_index";

        when(indexOperations.exists()).thenReturn(true);

        // when
        boolean result = elasticsearchIndexRepository.indexExists(indexName);

        // then
        assertThat(result).isTrue();
        verify(indexOperations).exists();
    }

    @Test
    @DisplayName("인덱스 존재 확인 - 존재하지 않음")
    void indexExists_False() {
        // given
        String indexName = "nonexistent_index";

        when(indexOperations.exists()).thenReturn(false);

        // when
        boolean result = elasticsearchIndexRepository.indexExists(indexName);

        // then
        assertThat(result).isFalse();
        verify(indexOperations).exists();
    }

    @Test
    @DisplayName("인덱스 존재 확인 - 예외 발생시 false 반환")
    void indexExists_ExceptionReturnsFalse() {
        // given
        String indexName = "test_index";

        when(indexOperations.exists()).thenThrow(new RuntimeException("ElasticSearch 연결 실패"));

        // when
        boolean result = elasticsearchIndexRepository.indexExists(indexName);

        // then
        assertThat(result).isFalse();
        verify(indexOperations).exists();
    }

    @Test
    @DisplayName("빈 동의어 리스트로 인덱스 생성")
    void createIndexWithSynonyms_EmptySynonyms() {
        // given
        String indexName = "test_index";
        List<String> emptySynonyms = Arrays.asList();

        when(indexOperations.exists()).thenReturn(false);
        when(indexOperations.create(any(Document.class))).thenReturn(true);
        when(indexOperations.putMapping(any(Document.class))).thenReturn(true);

        // when
        assertThatNoException().isThrownBy(() -> 
            elasticsearchIndexRepository.createIndexWithSynonyms(indexName, emptySynonyms));

        // then
        verify(indexOperations).create(any(Document.class));
        verify(indexOperations).putMapping(any(Document.class));
    }
}