package com.dong.bible.domain.sermon;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

import java.util.Objects;

/**
 * 설교 통계 정보(조회수, 좋아요)를 나타내는 Value Object
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Value
public class SermonStats {
    
    Integer viewCount;
    Integer likeCount;
    
    /**
     * 정적 팩토리 메서드 - 조회수와 좋아요 수로 생성
     */
    public static SermonStats of(Integer viewCount, Integer likeCount) {
        Integer validatedViewCount = validateCount(viewCount, "조회수");
        Integer validatedLikeCount = validateCount(likeCount, "좋아요");
        
        return new SermonStats(validatedViewCount, validatedLikeCount);
    }
    
    /**
     * 정적 팩토리 메서드 - 초기 통계 (0, 0)
     */
    public static SermonStats initial() {
        return new SermonStats(0, 0);
    }
    
    /**
     * 정적 팩토리 메서드 - 조회수만으로 생성 (좋아요는 0)
     */
    public static SermonStats withViewOnly(Integer viewCount) {
        return of(viewCount, 0);
    }
    
    /**
     * 카운트 유효성 검증
     */
    private static Integer validateCount(Integer count, String fieldName) {
        if (count == null) {
            return 0;
        }
        
        if (count < 0) {
            throw new IllegalArgumentException(fieldName + "는 0 이상이어야 합니다: " + count);
        }
        
        if (count > 999999999) { // 10억 제한
            throw new IllegalArgumentException(fieldName + "는 999,999,999를 초과할 수 없습니다: " + count);
        }
        
        return count;
    }
    
    /**
     * 조회수 증가
     */
    public SermonStats incrementViewCount() {
        return new SermonStats(viewCount + 1, likeCount);
    }
    
    /**
     * 조회수 N개 증가
     */
    public SermonStats addViewCount(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("증가할 조회수는 0 이상이어야 합니다: " + count);
        }
        
        return new SermonStats(viewCount + count, likeCount);
    }
    
    /**
     * 좋아요 증가
     */
    public SermonStats incrementLikeCount() {
        return new SermonStats(viewCount, likeCount + 1);
    }
    
    /**
     * 좋아요 감소
     */
    public SermonStats decrementLikeCount() {
        int newLikeCount = Math.max(0, likeCount - 1);
        return new SermonStats(viewCount, newLikeCount);
    }
    
    /**
     * 인기 설교인지 확인 (조회수 기준)
     */
    public boolean isPopular() {
        return viewCount >= 1000;
    }
    
    /**
     * 매우 인기 설교인지 확인
     */
    public boolean isVeryPopular() {
        return viewCount >= 10000;
    }
    
    /**
     * 좋아요 비율 계산 (조회수 대비 좋아요 비율)
     */
    public double getLikeRatio() {
        if (viewCount == 0) {
            return 0.0;
        }
        
        return (double) likeCount / viewCount;
    }
    
    /**
     * 좋아요 비율이 높은지 확인 (5% 이상)
     */
    public boolean hasHighLikeRatio() {
        return getLikeRatio() >= 0.05;
    }
    
    /**
     * 통계가 비어있는지 확인 (둘 다 0)
     */
    public boolean isEmpty() {
        return viewCount == 0 && likeCount == 0;
    }
    
    /**
     * 조회수를 포맷된 문자열로 반환 (1,234 형태)
     */
    public String getFormattedViewCount() {
        return formatNumber(viewCount);
    }
    
    /**
     * 좋아요를 포맷된 문자열로 반환
     */
    public String getFormattedLikeCount() {
        return formatNumber(likeCount);
    }
    
    /**
     * 숫자를 포맷팅 (1,000 단위 콤마 또는 K/M 단위)
     */
    private String formatNumber(int number) {
        if (number >= 1000000) {
            return String.format("%.1fM", number / 1000000.0);
        } else if (number >= 1000) {
            return String.format("%.1fK", number / 1000.0);
        } else {
            return String.valueOf(number);
        }
    }
    
    /**
     * 좋아요 비율을 퍼센트로 포맷
     */
    public String getFormattedLikeRatio() {
        return String.format("%.1f%%", getLikeRatio() * 100);
    }
    
    /**
     * 통계 등급 반환
     */
    public String getPopularityGrade() {
        if (viewCount >= 100000) {
            return "S급";
        } else if (viewCount >= 50000) {
            return "A급";
        } else if (viewCount >= 10000) {
            return "B급";
        } else if (viewCount >= 1000) {
            return "C급";
        } else {
            return "일반";
        }
    }
    
    /**
     * 표시용 문자열 생성
     */
    public String toDisplayString() {
        return String.format("조회수 %s, 좋아요 %s", 
                           getFormattedViewCount(), 
                           getFormattedLikeCount());
    }
    
    /**
     * 간단한 표시용 문자열 (조회수만)
     */
    public String getSimpleDisplay() {
        return "조회수 " + getFormattedViewCount();
    }
    
    /**
     * 다른 통계와 비교
     */
    public int compareByViewCount(SermonStats other) {
        if (other == null) {
            return 1;
        }
        
        return Integer.compare(this.viewCount, other.viewCount);
    }
    
    /**
     * 좋아요 비율로 비교
     */
    public int compareByLikeRatio(SermonStats other) {
        if (other == null) {
            return 1;
        }
        
        return Double.compare(this.getLikeRatio(), other.getLikeRatio());
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SermonStats that = (SermonStats) o;
        return Objects.equals(viewCount, that.viewCount) && 
               Objects.equals(likeCount, that.likeCount);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(viewCount, likeCount);
    }
    
    @Override
    public String toString() {
        return toDisplayString();
    }
}