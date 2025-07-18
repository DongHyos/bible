package com.dong.bible.domain.sermon;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SermonDomainServiceTest {

    @Mock
    private SermonRepository sermonRepository;

    @InjectMocks
    private SermonDomainService sermonDomainService;

    // === 조회 메서드 테스트 ===

    @Test
    void 설교_ID로_조회_성공() {
        // Given
        Long sermonId = 1L;
        Sermon mockSermon = mock(Sermon.class);
        when(sermonRepository.getById(sermonId)).thenReturn(Optional.of(mockSermon));

        // When
        Optional<Sermon> result = sermonDomainService.getSermonById(sermonId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(mockSermon);
        verify(sermonRepository).getById(sermonId);
    }

    @Test
    void 설교_ID로_조회_ID가_null인_경우_예외() {
        // When & Then
        assertThatThrownBy(() -> sermonDomainService.getSermonById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("설교 ID는 null일 수 없습니다");

        verify(sermonRepository, never()).getById(any());
    }

    @Test
    void 특정_구절_설교_조회_성공() {
        // Given
        Integer bookId = 43;
        Short chapter = 3;
        Short verse = 16;
        Sermon mockSermon = mock(Sermon.class);
        List<Sermon> mockSermons = Arrays.asList(mockSermon);
        when(sermonRepository.findByVerse(bookId, chapter, verse)).thenReturn(mockSermons);

        // When
        List<Sermon> result = sermonDomainService.getSermonsByVerse(bookId, chapter, verse);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(mockSermon);
        verify(sermonRepository).findByVerse(bookId, chapter, verse);
    }

    @Test
    void 특정_구절_설교_조회_잘못된_파라미터_예외() {
        // When & Then
        assertThatThrownBy(() -> sermonDomainService.getSermonsByVerse(null, (short) 1, (short) 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("올바른 책 ID를 입력해주세요");

        assertThatThrownBy(() -> sermonDomainService.getSermonsByVerse(0, (short) 1, (short) 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("올바른 책 ID를 입력해주세요");

        assertThatThrownBy(() -> sermonDomainService.getSermonsByVerse(43, null, (short) 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("올바른 장 번호를 입력해주세요");

        assertThatThrownBy(() -> sermonDomainService.getSermonsByVerse(43, (short) 0, (short) 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("올바른 장 번호를 입력해주세요");

        verify(sermonRepository, never()).findByVerse(any(), any(), any());
    }

    @Test
    void 설교자별_설교_조회_성공() {
        // Given
        String pastorName = "김목사";
        Sermon mockSermon = mock(Sermon.class);
        List<Sermon> mockSermons = Arrays.asList(mockSermon);
        when(sermonRepository.findByPastorNameContaining(pastorName)).thenReturn(mockSermons);

        // When
        List<Sermon> result = sermonDomainService.getSermonsByPastor(pastorName);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(mockSermon);
        verify(sermonRepository).findByPastorNameContaining(pastorName);
    }

    @Test
    void 설교자별_설교_조회_빈_이름_예외() {
        // When & Then
        assertThatThrownBy(() -> sermonDomainService.getSermonsByPastor(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("설교자명은 비어있을 수 없습니다");

        assertThatThrownBy(() -> sermonDomainService.getSermonsByPastor(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("설교자명은 비어있을 수 없습니다");

        assertThatThrownBy(() -> sermonDomainService.getSermonsByPastor("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("설교자명은 비어있을 수 없습니다");

        verify(sermonRepository, never()).findByPastorNameContaining(any());
    }

    @Test
    void 교회별_설교_조회_성공() {
        // Given
        String churchName = "새싹교회";
        Sermon mockSermon = mock(Sermon.class);
        List<Sermon> mockSermons = Arrays.asList(mockSermon);
        when(sermonRepository.findByChurchNameContaining(churchName)).thenReturn(mockSermons);

        // When
        List<Sermon> result = sermonDomainService.getSermonsByChurch(churchName);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(mockSermon);
        verify(sermonRepository).findByChurchNameContaining(churchName);
    }

    @Test
    void 교회별_설교_조회_빈_이름_예외() {
        // When & Then
        assertThatThrownBy(() -> sermonDomainService.getSermonsByChurch(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("교회명은 비어있을 수 없습니다");

        verify(sermonRepository, never()).findByChurchNameContaining(any());
    }

    @Test
    void 제목으로_설교_검색_성공() {
        // Given
        String title = "복음";
        Sermon mockSermon = mock(Sermon.class);
        List<Sermon> mockSermons = Arrays.asList(mockSermon);
        when(sermonRepository.findByTitleContaining(title)).thenReturn(mockSermons);

        // When
        List<Sermon> result = sermonDomainService.searchSermonsByTitle(title);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(mockSermon);
        verify(sermonRepository).findByTitleContaining(title);
    }

    @Test
    void 제목으로_설교_검색_빈_제목_예외() {
        // When & Then
        assertThatThrownBy(() -> sermonDomainService.searchSermonsByTitle(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("검색할 제목은 비어있을 수 없습니다");

        verify(sermonRepository, never()).findByTitleContaining(any());
    }

    @Test
    void 인기_설교_조회_성공() {
        // Given
        Sermon mockSermon = mock(Sermon.class);
        List<Sermon> mockSermons = Arrays.asList(mockSermon);
        when(sermonRepository.findTopByViewCount(10)).thenReturn(mockSermons);

        // When
        List<Sermon> result = sermonDomainService.getPopularSermons();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(mockSermon);
        verify(sermonRepository).findTopByViewCount(10);
    }

    @Test
    void 최신_설교_조회_성공() {
        // Given
        Sermon mockSermon = mock(Sermon.class);
        List<Sermon> mockSermons = Arrays.asList(mockSermon);
        when(sermonRepository.findLatestSermons(10)).thenReturn(mockSermons);

        // When
        List<Sermon> result = sermonDomainService.getLatestSermons();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(mockSermon);
        verify(sermonRepository).findLatestSermons(10);
    }

    @Test
    void 관련_설교_추천_조회_성공() {
        // Given
        Long baseSermonId = 1L;
        Sermon baseSermon = mock(Sermon.class);
        Sermon relatedSermon = mock(Sermon.class);
        List<Sermon> relatedSermons = Arrays.asList(relatedSermon);
        
        when(sermonRepository.getById(baseSermonId)).thenReturn(Optional.of(baseSermon));
        when(sermonRepository.findRelatedSermons(baseSermon, 5)).thenReturn(relatedSermons);

        // When
        List<Sermon> result = sermonDomainService.getRelatedSermons(baseSermonId);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(relatedSermon);
        verify(sermonRepository).getById(baseSermonId);
        verify(sermonRepository).findRelatedSermons(baseSermon, 5);
    }

    @Test
    void 관련_설교_추천_조회_기준_설교_없음_예외() {
        // Given
        Long baseSermonId = 999L;
        when(sermonRepository.getById(baseSermonId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> sermonDomainService.getRelatedSermons(baseSermonId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("기준 설교를 찾을 수 없습니다: 999");

        verify(sermonRepository).getById(baseSermonId);
        verify(sermonRepository, never()).findRelatedSermons(any(), eq(5));
    }

    @Test
    void 트렌딩_설교_조회_성공() {
        // Given
        Sermon mockSermon = mock(Sermon.class);
        List<Sermon> mockSermons = Arrays.asList(mockSermon);
        when(sermonRepository.findTrendingSermons(30, 10)).thenReturn(mockSermons);

        // When
        List<Sermon> result = sermonDomainService.getTrendingSermons();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(mockSermon);
        verify(sermonRepository).findTrendingSermons(30, 10);
    }

    @Test
    void 베스트_설교_조회_성공() {
        // Given
        Sermon mockSermon = mock(Sermon.class);
        List<Sermon> mockSermons = Arrays.asList(mockSermon);
        when(sermonRepository.findRecommendedSermons(10)).thenReturn(mockSermons);

        // When
        List<Sermon> result = sermonDomainService.getBestSermons();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(mockSermon);
        verify(sermonRepository).findRecommendedSermons(10);
    }

    @Test
    void 특정_기간_설교_조회_성공() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        Sermon mockSermon = mock(Sermon.class);
        List<Sermon> mockSermons = Arrays.asList(mockSermon);
        when(sermonRepository.findByDateRange(startDate, endDate)).thenReturn(mockSermons);

        // When
        List<Sermon> result = sermonDomainService.getSermonsByDateRange(startDate, endDate);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(mockSermon);
        verify(sermonRepository).findByDateRange(startDate, endDate);
    }

    @Test
    void 특정_기간_설교_조회_잘못된_날짜_예외() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 12, 31);
        LocalDate endDate = LocalDate.of(2024, 1, 1);

        // When & Then
        assertThatThrownBy(() -> sermonDomainService.getSermonsByDateRange(startDate, endDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("시작일은 종료일보다 이후일 수 없습니다");

        verify(sermonRepository, never()).findByDateRange(any(), any());
    }

    @Test
    void 특정_연도_설교_조회_성공() {
        // Given
        int year = 2024;
        Sermon mockSermon = mock(Sermon.class);
        List<Sermon> mockSermons = Arrays.asList(mockSermon);
        when(sermonRepository.findByYear(year)).thenReturn(mockSermons);

        // When
        List<Sermon> result = sermonDomainService.getSermonsByYear(year);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(mockSermon);
        verify(sermonRepository).findByYear(year);
    }

    @Test
    void 특정_연도_설교_조회_잘못된_연도_예외() {
        // When & Then
        assertThatThrownBy(() -> sermonDomainService.getSermonsByYear(1800))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("올바른 연도를 입력해주세요");

        verify(sermonRepository, never()).findByYear(1800);
    }

    // === 명령 메서드 테스트 ===

    @Test
    void 설교_저장_성공() {
        // Given
        Sermon mockSermon = mock(Sermon.class);
        when(sermonRepository.store(mockSermon)).thenReturn(mockSermon);

        // When
        Sermon result = sermonDomainService.saveSermon(mockSermon);

        // Then
        assertThat(result).isEqualTo(mockSermon);
        verify(sermonRepository).store(mockSermon);
    }

    @Test
    void 설교_저장_null_예외() {
        // When & Then
        assertThatThrownBy(() -> sermonDomainService.saveSermon(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("설교 정보는 null일 수 없습니다");

        verifyNoInteractions(sermonRepository);
    }

    @Test
    void 설교_삭제_성공() {
        // Given
        Long sermonId = 1L;
        when(sermonRepository.exists(sermonId)).thenReturn(true);

        // When
        sermonDomainService.deleteSermon(sermonId);

        // Then
        verify(sermonRepository).exists(sermonId);
        verify(sermonRepository).removeById(sermonId);
    }

    @Test
    void 설교_삭제_존재하지_않는_ID_예외() {
        // Given
        Long sermonId = 999L;
        when(sermonRepository.exists(sermonId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> sermonDomainService.deleteSermon(sermonId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("설교를 찾을 수 없습니다: 999");

        verify(sermonRepository).exists(sermonId);
        verify(sermonRepository, never()).removeById(999L);
    }

    @Test
    void 조회수_증가_성공() {
        // Given
        Long sermonId = 1L;
        when(sermonRepository.exists(sermonId)).thenReturn(true);

        // When
        sermonDomainService.incrementViewCount(sermonId);

        // Then
        verify(sermonRepository).exists(sermonId);
        verify(sermonRepository).incrementViewCount(sermonId);
    }

    @Test
    void 조회수_증가_미구현_기능_경고() {
        // Given
        Long sermonId = 1L;
        when(sermonRepository.exists(sermonId)).thenReturn(true);
        doThrow(new UnsupportedOperationException("조회수 증가 기능 미구현"))
                .when(sermonRepository).incrementViewCount(sermonId);

        // When (예외가 발생하지 않아야 함)
        sermonDomainService.incrementViewCount(sermonId);

        // Then
        verify(sermonRepository).exists(sermonId);
        verify(sermonRepository).incrementViewCount(sermonId);
    }

    // === 통계 메서드 테스트 ===

    @Test
    void 전체_설교_개수_조회_성공() {
        // Given
        long totalCount = 100L;
        when(sermonRepository.getTotalCount()).thenReturn(totalCount);

        // When
        long result = sermonDomainService.getTotalSermonCount();

        // Then
        assertThat(result).isEqualTo(totalCount);
        verify(sermonRepository).getTotalCount();
    }

    @Test
    void 설교자별_설교_개수_조회_성공() {
        // Given
        String pastorName = "김목사";
        long count = 25L;
        when(sermonRepository.countByPastor(pastorName)).thenReturn(count);

        // When
        long result = sermonDomainService.getSermonCountByPastor(pastorName);

        // Then
        assertThat(result).isEqualTo(count);
        verify(sermonRepository).countByPastor(pastorName);
    }

    @Test
    void 교회별_설교_개수_조회_성공() {
        // Given
        String churchName = "새싹교회";
        long count = 50L;
        when(sermonRepository.countByChurch(churchName)).thenReturn(count);

        // When
        long result = sermonDomainService.getSermonCountByChurch(churchName);

        // Then
        assertThat(result).isEqualTo(count);
        verify(sermonRepository).countByChurch(churchName);
    }
}