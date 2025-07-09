package com.dong.bible.domain.verse;

import java.util.List;
import java.util.Optional;

public interface BibleVerseRepository {
    // === 단일 구절 조회 ===

    /**
     * 구절 참조로 특정 구절 조회
     * @param reference 구절 참조 (책:장:절)
     * @return 해당 구절 (없으면 Optional.empty())
     */
    Optional<BibleVerse> findByReference(VerseReference reference);

    /**
     * ID로 구절 조회
     * @param id 구절 ID
     * @return 해당 구절 (없으면 Optional.empty())
     */
    Optional<BibleVerse> findById(Long id);

    // === 다중 구절 조회 ===

    /**
     * 특정 장의 모든 구절 조회 (구절 순서대로 정렬)
     * @param bookName 책 이름 (예: "요한복음")
     * @param chapter 장 번호
     * @return 해당 장의 모든 구절들 (구절 순서대로)
     */
    List<BibleVerse> findByChapter(String bookName, Integer chapter);

    /**
     * 특정 장의 구절 범위 조회
     * @param bookName 책 이름
     * @param chapter 장 번호
     * @param startVerse 시작 구절 번호
     * @param endVerse 끝 구절 번호
     * @return 해당 범위의 구절들 (구절 순서대로)
     */
    List<BibleVerse> findByChapterRange(String bookName, Integer chapter, Integer startVerse, Integer endVerse);

    /**
     * 특정 책의 모든 구절 조회
     * @param bookName 책 이름
     * @return 해당 책의 모든 구절들 (장:절 순서대로)
     */
    List<BibleVerse> findByBook(String bookName);

    /**
     * 신약/구약 구절 조회
     * @param isNewTestament true면 신약, false면 구약
     * @return 해당 성경의 모든 구절들
     */
    List<BibleVerse> findByTestament(boolean isNewTestament);

    // === 저장/삭제 ===

    /**
     * 구절 저장 (생성/수정)
     * @param verse 저장할 구절
     * @return 저장된 구절 (ID 포함)
     */
    BibleVerse save(BibleVerse verse);

    /**
     * 여러 구절 일괄 저장
     * @param verses 저장할 구절들
     * @return 저장된 구절들 (ID 포함)
     */
    List<BibleVerse> saveAll(List<BibleVerse> verses);

    /**
     * 구절 삭제
     * @param verse 삭제할 구절
     */
    void delete(BibleVerse verse);

    /**
     * ID로 구절 삭제
     * @param id 삭제할 구절 ID
     */
    void deleteById(Long id);

    // === 존재 여부 확인 ===

    /**
     * 구절이 존재하는지 확인
     * @param reference 구절 참조
     * @return 존재하면 true
     */
    boolean existsByReference(VerseReference reference);

    /**
     * ID로 구절 존재 여부 확인
     * @param id 구절 ID
     * @return 존재하면 true
     */
    boolean existsById(Long id);

    // === 개수 조회 ===

    /**
     * 특정 장의 구절 개수
     * @param bookName 책 이름
     * @param chapter 장 번호
     * @return 구절 개수
     */
    long countByChapter(String bookName, Integer chapter);

    /**
     * 특정 책의 총 구절 개수
     * @param bookName 책 이름
     * @return 구절 개수
     */
    long countByBook(String bookName);

    /**
     * 전체 구절 개수
     * @return 총 구절 개수
     */
    long count();

    // === 유틸리티 메서드 ===

    /**
     * 모든 구절 조회 (페이징 없음 - 주의!)
     * @return 모든 구절들
     */
    List<BibleVerse> findAll();

    /**
     * 여러 ID로 구절들 조회
     * @param ids 구절 ID 목록
     * @return 해당 구절들
     */
    List<BibleVerse> findAllById(List<Long> ids);
}
