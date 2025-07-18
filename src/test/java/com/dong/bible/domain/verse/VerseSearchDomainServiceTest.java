package com.dong.bible.domain.verse;

import com.dong.bible.domain.book.BookName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * VerseSearchDomainService 단위 테스트
 * 
 * 도메인 서비스는 순수한 비즈니스 로직만 포함하므로:
 * - Mock 없이 실제 도메인 객체를 사용
 * - 입력과 출력에 집중한 테스트
 * - 검색 비즈니스 로직의 정확성 검증
 */
class VerseSearchDomainServiceTest {
    
    private VerseSearchDomainService domainService;
    
    // 테스트용 도메인 객체들
    private BibleVerse 창세기1장1절;
    private BibleVerse 창세기1장2절;
    private BibleVerse 요한복음3장16절;
    private BibleVerse 시편23편1절;
    private BibleVerse 마태복음5장3절;
    
    @BeforeEach
    void setUp() {
        domainService = new VerseSearchDomainService();
        
        // 테스트 데이터 생성
        창세기1장1절 = BibleVerse.of(
            1L,
            VerseReference.of("창세기", 1, 1),
            VerseContent.of("태초에 하나님이 천지를 창조하시니라")
        );
        
        창세기1장2절 = BibleVerse.of(
            2L,
            VerseReference.of("창세기", 1, 2),
            VerseContent.of("땅이 혼돈하고 공허하며 흑암이 깊음 위에 있고 하나님의 영은 수면에 운행하시니라")
        );
        
        요한복음3장16절 = BibleVerse.of(
            3L,
            VerseReference.of("요한복음", 3, 16),
            VerseContent.of("하나님이 세상을 이처럼 사랑하사 독생자를 주셨으니")
        );
        
        시편23편1절 = BibleVerse.of(
            4L,
            VerseReference.of("시편", 23, 1),
            VerseContent.of("여호와는 나의 목자시니 내게 부족함이 없으리로다")
        );
        
        마태복음5장3절 = BibleVerse.of(
            5L,
            VerseReference.of("마태복음", 5, 3),
            VerseContent.of("심령이 가난한 자는 복이 있나니 천국이 그들의 것임이라")
        );
    }
    
    @Test
    void 키워드_검색_정상_동작() {
        // Given
        List<BibleVerse> verses = Arrays.asList(
            창세기1장1절, 창세기1장2절, 요한복음3장16절, 시편23편1절, 마태복음5장3절
        );
        String keyword = "하나님";
        
        // When
        List<BibleVerse> result = domainService.searchByKeyword(verses, keyword);
        
        // Then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyInAnyOrder(창세기1장1절, 창세기1장2절, 요한복음3장16절);
    }
    
    @Test
    void 키워드_검색_대소문자_구분_없음() {
        // Given
        List<BibleVerse> verses = Arrays.asList(창세기1장1절, 요한복음3장16절);
        String keyword = "하나님";
        
        // When
        List<BibleVerse> result = domainService.searchByKeywordIgnoreCase(verses, keyword);
        
        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(창세기1장1절, 요한복음3장16절);
    }
    
    @Test
    void 완전_일치_검색() {
        // Given
        List<BibleVerse> verses = Arrays.asList(창세기1장1절, 시편23편1절);
        String keyword = "태초에 하나님이 천지를 창조하시니라";
        
        // When
        List<BibleVerse> result = domainService.searchByExactMatch(verses, keyword);
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(창세기1장1절);
    }
    
    @Test
    void 여러_키워드_AND_검색() {
        // Given
        List<BibleVerse> verses = Arrays.asList(
            창세기1장1절, 창세기1장2절, 요한복음3장16절, 시편23편1절, 마태복음5장3절
        );
        List<String> keywords = Arrays.asList("하나님", "사랑");
        
        // When
        List<BibleVerse> result = domainService.searchByMultipleKeywords(verses, keywords);
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(요한복음3장16절);
    }
    
    @Test
    void 관련성_순_정렬() {
        // Given - "하나님"이 2번 나오는 구절과 1번 나오는 구절
        BibleVerse 하나님_두번 = BibleVerse.of(
            10L,
            VerseReference.of("테스트", 1, 1),
            VerseContent.of("하나님이 하나님의 영광을 나타내시니")
        );
        
        List<BibleVerse> verses = Arrays.asList(창세기1장1절, 하나님_두번, 요한복음3장16절);
        String keyword = "하나님";
        
        // When
        List<BibleVerse> result = domainService.sortByRelevance(verses, keyword);
        
        // Then
        assertThat(result).hasSize(3);
        assertThat(result.get(0)).isEqualTo(하나님_두번); // 2번 나오는 구절이 첫 번째
        // 나머지 두 구절은 모두 1번씩 나오므로 원본 순서 유지
    }
    
    @Test
    void 빈_목록_처리() {
        // Given
        List<BibleVerse> verses = Collections.emptyList();
        String keyword = "하나님";
        
        // When
        List<BibleVerse> result = domainService.searchByKeyword(verses, keyword);
        
        // Then
        assertThat(result).isEmpty();
    }
    
    @Test
    void 빈_키워드_처리() {
        // Given
        List<BibleVerse> verses = Arrays.asList(창세기1장1절, 요한복음3장16절);
        String keyword = "";
        
        // When
        List<BibleVerse> result = domainService.searchByKeyword(verses, keyword);
        
        // Then
        assertThat(result).isEmpty();
    }
    
    @Test
    void null_키워드_처리() {
        // Given
        List<BibleVerse> verses = Arrays.asList(창세기1장1절, 요한복음3장16절);
        String keyword = null;
        
        // When
        List<BibleVerse> result = domainService.searchByKeyword(verses, keyword);
        
        // Then
        assertThat(result).isEmpty();
    }
    
    @Test
    void 공백_키워드_처리() {
        // Given
        List<BibleVerse> verses = Arrays.asList(창세기1장1절, 요한복음3장16절);
        String keyword = "   ";
        
        // When
        List<BibleVerse> result = domainService.searchByKeyword(verses, keyword);
        
        // Then
        assertThat(result).isEmpty();
    }
    
    @Test
    void 찾는_키워드_없음() {
        // Given
        List<BibleVerse> verses = Arrays.asList(창세기1장1절, 요한복음3장16절);
        String keyword = "존재하지않는키워드";
        
        // When
        List<BibleVerse> result = domainService.searchByKeyword(verses, keyword);
        
        // Then
        assertThat(result).isEmpty();
    }
    
    @Test
    void 여러_키워드_중_빈_키워드_필터링() {
        // Given
        List<BibleVerse> verses = Arrays.asList(창세기1장1절, 요한복음3장16절);
        List<String> keywords = Arrays.asList("하나님", "", null, "   ", "사랑");
        
        // When
        List<BibleVerse> result = domainService.searchByMultipleKeywords(verses, keywords);
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(요한복음3장16절);
    }
    
    @Test
    void 모든_키워드가_빈값인_경우() {
        // Given
        List<BibleVerse> verses = Arrays.asList(창세기1장1절, 요한복음3장16절);
        List<String> keywords = Arrays.asList("", null, "   ");
        
        // When
        List<BibleVerse> result = domainService.searchByMultipleKeywords(verses, keywords);
        
        // Then
        assertThat(result).isEmpty();
    }
    
    @Test
    void 관련성_점수_계산_검증() {
        // Given - "하나님"이 3번 나오는 구절
        BibleVerse 하나님_세번 = BibleVerse.of(
            11L,
            VerseReference.of("테스트", 1, 1),
            VerseContent.of("하나님이 하나님의 사랑으로 하나님을 나타내시니")
        );
        
        List<BibleVerse> verses = Arrays.asList(창세기1장1절, 하나님_세번);
        String keyword = "하나님";
        
        // When
        List<BibleVerse> result = domainService.sortByRelevance(verses, keyword);
        
        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo(하나님_세번); // 3번 나오는 구절이 첫 번째
        assertThat(result.get(1)).isEqualTo(창세기1장1절); // 1번 나오는 구절이 두 번째
    }
}