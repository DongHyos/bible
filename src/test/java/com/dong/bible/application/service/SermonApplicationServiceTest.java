package com.dong.bible.application.service;

import com.dong.bible.application.dto.query.SermonDetailQuery;
import com.dong.bible.application.dto.query.SermonSummaryQuery;
import com.dong.bible.domain.sermon.Sermon;
import com.dong.bible.domain.sermon.SermonDomainService;
import com.dong.bible.domain.sermon.SermonInfo;
import com.dong.bible.domain.sermon.PastorInfo;
import com.dong.bible.domain.sermon.SermonStats;
import com.dong.bible.domain.sermon.SermonTags;
import com.dong.bible.domain.sermon.SermonMedia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SermonApplicationServiceTest {

    @Mock
    private SermonDomainService sermonDomainService;

    @InjectMocks
    private SermonApplicationService sermonApplicationService;

    // @BeforeEach에서 Mock 생성 제거 - 각 테스트에서 필요한 것만 생성

    // === 상세 조회 테스트 ===

    @Test
    void 설교_ID로_상세_조회_성공() {
        // Given
        Long sermonId = 1L;
        Sermon detailSermon = createDetailMockSermon(1L, "은혜의 복음", "김목사", "새싹교회", "설교 내용1");
        when(sermonDomainService.getSermonById(sermonId)).thenReturn(Optional.of(detailSermon));

        // When
        SermonDetailQuery result = sermonApplicationService.getSermonById(sermonId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("은혜의 복음");
        assertThat(result.getPastorName()).isEqualTo("김목사");
        assertThat(result.getChurchName()).isEqualTo("새싹교회");
        assertThat(result.getContent()).isEqualTo("설교 내용1"); // DetailDto는 content 포함
        
        verify(sermonDomainService).getSermonById(sermonId);
    }

    @Test
    void 설교_ID로_상세_조회_ID가_null인_경우_예외() {
        // Given
        when(sermonDomainService.getSermonById(null))
                .thenThrow(new IllegalArgumentException("설교 ID는 null일 수 없습니다"));

        // When & Then
        assertThatThrownBy(() -> sermonApplicationService.getSermonById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("설교 ID는 null일 수 없습니다");

        verify(sermonDomainService).getSermonById(null);
    }

    @Test
    void 설교_ID로_상세_조회_설교가_없는_경우_예외() {
        // Given
        Long sermonId = 999L;
        when(sermonDomainService.getSermonById(sermonId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> sermonApplicationService.getSermonById(sermonId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("설교를 찾을 수 없습니다: 999");

        verify(sermonDomainService).getSermonById(sermonId);
    }

    // === 구절별 조회 테스트 ===

    @Test
    void 구절별_설교_조회_성공() {
        // Given
        Integer bookId = 43;
        Short chapter = 3;
        Short verse = 16;
        Sermon sermon1 = createSummaryMockSermon(1L, "은혜의 복음", "김목사", "새싹교회");
        Sermon sermon2 = createSummaryMockSermon(2L, "사랑의 실천", "이목사", "소망교회");
        List<Sermon> sermons = Arrays.asList(sermon1, sermon2);

        when(sermonDomainService.getSermonsByVerse(bookId, chapter, verse)).thenReturn(sermons);

        // When
        List<SermonSummaryQuery> result = sermonApplicationService.getSermonsByVerse(bookId, chapter, verse);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getTitle()).isEqualTo("은혜의 복음");

        verify(sermonDomainService).getSermonsByVerse(bookId, chapter, verse);
    }

    @Test
    void 구절별_설교_조회_잘못된_책ID_예외() {
        // When & Then
        when(sermonDomainService.getSermonsByVerse(null, (short) 1, (short) 1))
                .thenThrow(new IllegalArgumentException("올바른 책 ID를 입력해주세요"));
        when(sermonDomainService.getSermonsByVerse(0, (short) 1, (short) 1))
                .thenThrow(new IllegalArgumentException("올바른 책 ID를 입력해주세요"));

        assertThatThrownBy(() -> sermonApplicationService.getSermonsByVerse(null, (short) 1, (short) 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("올바른 책 ID를 입력해주세요");

        assertThatThrownBy(() -> sermonApplicationService.getSermonsByVerse(0, (short) 1, (short) 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("올바른 책 ID를 입력해주세요");

        verify(sermonDomainService).getSermonsByVerse(null, (short) 1, (short) 1);
        verify(sermonDomainService).getSermonsByVerse(0, (short) 1, (short) 1);
    }

    @Test
    void 구절별_설교_조회_잘못된_장번호_예외() {
        // When & Then
        when(sermonDomainService.getSermonsByVerse(43, null, (short) 1))
                .thenThrow(new IllegalArgumentException("올바른 장 번호를 입력해주세요"));
        when(sermonDomainService.getSermonsByVerse(43, (short) 0, (short) 1))
                .thenThrow(new IllegalArgumentException("올바른 장 번호를 입력해주세요"));

        assertThatThrownBy(() -> sermonApplicationService.getSermonsByVerse(43, null, (short) 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("올바른 장 번호를 입력해주세요");

        assertThatThrownBy(() -> sermonApplicationService.getSermonsByVerse(43, (short) 0, (short) 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("올바른 장 번호를 입력해주세요");

        verify(sermonDomainService).getSermonsByVerse(43, null, (short) 1);
        verify(sermonDomainService).getSermonsByVerse(43, (short) 0, (short) 1);
    }

    @Test
    void 구절별_설교_조회_잘못된_절번호_예외() {
        // When & Then
        when(sermonDomainService.getSermonsByVerse(43, (short) 1, null))
                .thenThrow(new IllegalArgumentException("올바른 절 번호를 입력해주세요"));
        when(sermonDomainService.getSermonsByVerse(43, (short) 1, (short) 0))
                .thenThrow(new IllegalArgumentException("올바른 절 번호를 입력해주세요"));

        assertThatThrownBy(() -> sermonApplicationService.getSermonsByVerse(43, (short) 1, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("올바른 절 번호를 입력해주세요");

        assertThatThrownBy(() -> sermonApplicationService.getSermonsByVerse(43, (short) 1, (short) 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("올바른 절 번호를 입력해주세요");

        verify(sermonDomainService).getSermonsByVerse(43, (short) 1, null);
        verify(sermonDomainService).getSermonsByVerse(43, (short) 1, (short) 0);
    }

    // === 설교자별 조회 테스트 ===

    @Test
    void 설교자별_설교_조회_성공() {
        // Given
        String pastorName = "김목사";
        Sermon sermon = createSummaryMockSermon(1L, "은혜의 복음", "김목사", "새싹교회");
        List<Sermon> sermons = Arrays.asList(sermon);

        when(sermonDomainService.getSermonsByPastor(pastorName)).thenReturn(sermons);

        // When
        List<SermonSummaryQuery> result = sermonApplicationService.getSermonsByPastor(pastorName);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPastorName()).isEqualTo("김목사");

        verify(sermonDomainService).getSermonsByPastor(pastorName);
    }

    @Test
    void 설교자별_설교_조회_빈_이름_예외() {
        // When & Then
        when(sermonDomainService.getSermonsByPastor(null))
                .thenThrow(new IllegalArgumentException("설교자명은 비어있을 수 없습니다"));
        when(sermonDomainService.getSermonsByPastor(""))
                .thenThrow(new IllegalArgumentException("설교자명은 비어있을 수 없습니다"));
        when(sermonDomainService.getSermonsByPastor("   "))
                .thenThrow(new IllegalArgumentException("설교자명은 비어있을 수 없습니다"));

        assertThatThrownBy(() -> sermonApplicationService.getSermonsByPastor(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("설교자명은 비어있을 수 없습니다");

        assertThatThrownBy(() -> sermonApplicationService.getSermonsByPastor(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("설교자명은 비어있을 수 없습니다");

        assertThatThrownBy(() -> sermonApplicationService.getSermonsByPastor("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("설교자명은 비어있을 수 없습니다");

        verify(sermonDomainService).getSermonsByPastor(null);
        verify(sermonDomainService).getSermonsByPastor("");
        verify(sermonDomainService).getSermonsByPastor("   ");
    }

    // === 교회별 조회 테스트 ===

    @Test
    void 교회별_설교_조회_성공() {
        // Given
        String churchName = "새싹교회";
        Sermon sermon = createSummaryMockSermon(1L, "은혜의 복음", "김목사", "새싹교회");
        List<Sermon> sermons = Arrays.asList(sermon);

        when(sermonDomainService.getSermonsByChurch(churchName)).thenReturn(sermons);

        // When
        List<SermonSummaryQuery> result = sermonApplicationService.getSermonsByChurch(churchName);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getChurchName()).isEqualTo("새싹교회");

        verify(sermonDomainService).getSermonsByChurch(churchName);
    }

    @Test
    void 교회별_설교_조회_빈_이름_예외() {
        // When & Then
        when(sermonDomainService.getSermonsByChurch(null))
                .thenThrow(new IllegalArgumentException("교회명은 비어있을 수 없습니다"));

        assertThatThrownBy(() -> sermonApplicationService.getSermonsByChurch(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("교회명은 비어있을 수 없습니다");

        verify(sermonDomainService).getSermonsByChurch(null);
    }

    // === 제목 검색 테스트 ===

    @Test
    void 제목으로_설교_검색_성공() {
        // Given
        String title = "복음";
        Sermon sermon = createSummaryMockSermon(1L, "은혜의 복음", "김목사", "새싹교회");
        List<Sermon> sermons = Arrays.asList(sermon);

        when(sermonDomainService.searchSermonsByTitle(title)).thenReturn(sermons);

        // When
        List<SermonSummaryQuery> result = sermonApplicationService.searchSermonsByTitle(title);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).contains("복음");

        verify(sermonDomainService).searchSermonsByTitle(title);
    }

    @Test
    void 제목으로_설교_검색_빈_제목_예외() {
        // When & Then
        when(sermonDomainService.searchSermonsByTitle(null))
                .thenThrow(new IllegalArgumentException("검색할 제목은 비어있을 수 없습니다"));

        assertThatThrownBy(() -> sermonApplicationService.searchSermonsByTitle(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("검색할 제목은 비어있을 수 없습니다");

        verify(sermonDomainService).searchSermonsByTitle(null);
    }

    // === 인기 설교 조회 테스트 ===

    @Test
    void 인기_설교_조회_성공() {
        // Given
        Sermon sermon1 = createSummaryMockSermon(1L, "은혜의 복음", "김목사", "새싹교회");
        Sermon sermon2 = createSummaryMockSermon(2L, "사랑의 실천", "이목사", "소망교회");
        Sermon sermon3 = createSummaryMockSermon(3L, "믿음의 여정", "박목사", "은혜교회");
        
        // 인기 설교용 조회수 설정
        when(sermon3.getStats().getViewCount()).thenReturn(1200); // 가장 높은 조회수
        when(sermon1.getStats().getViewCount()).thenReturn(800);
        when(sermon2.getStats().getViewCount()).thenReturn(600);
        
        List<Sermon> popularSermons = Arrays.asList(sermon3, sermon1, sermon2); // 조회수 순

        when(sermonDomainService.getPopularSermons()).thenReturn(popularSermons);

        // When
        List<SermonSummaryQuery> result = sermonApplicationService.getPopularSermons();

        // Then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getViewCount()).isEqualTo(1200); // 가장 높은 조회수

        verify(sermonDomainService).getPopularSermons();
    }

    // === 최신 설교 조회 테스트 ===

    @Test
    void 최신_설교_조회_성공() {
        // Given
        Sermon sermon1 = createSummaryMockSermon(1L, "은혜의 복음", "김목사", "새싹교회");
        Sermon sermon2 = createSummaryMockSermon(2L, "사랑의 실천", "이목사", "소망교회");
        Sermon sermon3 = createSummaryMockSermon(3L, "믿음의 여정", "박목사", "은혜교회");
        
        List<Sermon> latestSermons = Arrays.asList(sermon3, sermon2, sermon1); // 날짜 순

        when(sermonDomainService.getLatestSermons()).thenReturn(latestSermons);

        // When
        List<SermonSummaryQuery> result = sermonApplicationService.getLatestSermons();

        // Then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getId()).isEqualTo(3L); // 가장 최신

        verify(sermonDomainService).getLatestSermons();
    }

    // === 조회수 증가 테스트 ===

    @Test
    void 조회수_증가_성공() {
        // Given
        Long sermonId = 1L;

        // When
        sermonApplicationService.incrementViewCount(sermonId);

        // Then
        verify(sermonDomainService).incrementViewCount(sermonId);
    }

    @Test
    void 조회수_증가_ID가_null인_경우_예외() {
        // When & Then
        doThrow(new IllegalArgumentException("설교 ID는 null일 수 없습니다"))
                .when(sermonDomainService).incrementViewCount(null);

        assertThatThrownBy(() -> sermonApplicationService.incrementViewCount(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("설교 ID는 null일 수 없습니다");

        verify(sermonDomainService).incrementViewCount(null);
    }

    @Test
    void 조회수_증가_설교가_없는_경우_예외() {
        // Given
        Long sermonId = 999L;
        doThrow(new IllegalArgumentException("설교를 찾을 수 없습니다: 999"))
                .when(sermonDomainService).incrementViewCount(sermonId);

        // When & Then
        assertThatThrownBy(() -> sermonApplicationService.incrementViewCount(sermonId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("설교를 찾을 수 없습니다: 999");

        verify(sermonDomainService).incrementViewCount(sermonId);
    }

    @Test
    void 조회수_증가_기능_미구현_경고() {
        // Given
        Long sermonId = 1L;
        doNothing().when(sermonDomainService).incrementViewCount(sermonId);

        // When (예외가 발생하지 않아야 함)
        sermonApplicationService.incrementViewCount(sermonId);

        // Then
        verify(sermonDomainService).incrementViewCount(sermonId);
    }

    // === 헬퍼 메서드 ===

    /**
     * Summary DTO용 Mock Sermon (SermonSummaryQuery.from()에서 필요한 메서드만)
     */
    private Sermon createSummaryMockSermon(Long id, String title, String pastorName, String churchName) {
        Sermon sermon = mock(Sermon.class);
        when(sermon.getId()).thenReturn(id);
        
        // SermonInfo Mock
        SermonInfo sermonInfo = mock(SermonInfo.class);
        when(sermonInfo.getTitle()).thenReturn(title);
        when(sermonInfo.getSermonDate()).thenReturn(LocalDate.now());
        when(sermon.getInfo()).thenReturn(sermonInfo);
        
        // PastorInfo Mock
        PastorInfo pastorInfo = mock(PastorInfo.class);
        when(pastorInfo.getPastorName()).thenReturn(pastorName);
        when(pastorInfo.getChurchName()).thenReturn(churchName);
        when(sermon.getPastor()).thenReturn(pastorInfo);
        
        // SermonMedia Mock
        SermonMedia media = mock(SermonMedia.class);
        when(media.getYoutubeUrl()).thenReturn(null);
        when(media.getThumbnailUrl()).thenReturn(null);
        when(media.getDurationMinutes()).thenReturn(null);
        when(sermon.getMedia()).thenReturn(media);
        
        // SermonTags Mock
        SermonTags tags = mock(SermonTags.class);
        when(tags.getTagList()).thenReturn(Collections.emptyList());
        when(sermon.getTags()).thenReturn(tags);
        
        // SermonStats Mock
        SermonStats stats = mock(SermonStats.class);
        when(stats.getViewCount()).thenReturn(0);
        when(stats.getLikeCount()).thenReturn(0);
        when(sermon.getStats()).thenReturn(stats);
        
        return sermon;
    }
    
    /**
     * Detail DTO용 Mock Sermon (SermonDetailQuery.from()에서 필요한 메서드만)
     */
    private Sermon createDetailMockSermon(Long id, String title, String pastorName, String churchName, String content) {
        Sermon sermon = createSummaryMockSermon(id, title, pastorName, churchName);
        
        // Detail에서 추가로 필요한 메서드들
        when(sermon.getContent()).thenReturn(content);
        when(sermon.getVerseList()).thenReturn(Collections.emptyList());
        
        return sermon;
    }
}
