package com.dong.bible.infrastructure.search.document;

import com.dong.bible.domain.verse.BibleVerse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * ElasticSearch용 성경 구절 검색 Document
 * Infrastructure Layer - 검색 기술 구현을 담당
 */
@Document(indexName = "bible_verses_target")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerseSearchDocument {
    
    @Id
    private String id; // "bookId:chapter:verse" 형태의 복합 키
    
    @Field(type = FieldType.Integer)
    private Integer bookId;
    
    @Field(type = FieldType.Text)
    private String bookName;
    
    @Field(type = FieldType.Integer)
    private Integer chapter;
    
    @Field(type = FieldType.Integer)
    private Integer verse;
    
    @Field(type = FieldType.Text, analyzer = "nori")
    private String content; // 한국어 분석기로 검색할 구절 내용
    
    @Field(type = FieldType.Text)
    private String displayReference; // "창세기 1:1" 형태의 표시용 참조
    
    /**
     * Domain 객체에서 검색 Document로 변환
     */
    public static VerseSearchDocument from(BibleVerse verse, Integer bookId) {
        String bookName = verse.getReference().getBookName();
        Integer chapter = verse.getReference().getChapter();
        Integer verseNum = verse.getReference().getVerse();
        String content = verse.getContent().getText();
        
        return VerseSearchDocument.builder()
                .id(createId(bookId, chapter, verseNum))
                .bookId(bookId)
                .bookName(bookName)
                .chapter(chapter)
                .verse(verseNum)
                .content(content)
                .displayReference(bookName + " " + chapter + ":" + verseNum)
                .build();
    }
    
    /**
     * 복합 ID 생성 로직
     */
    private static String createId(Integer bookId, Integer chapter, Integer verse) {
        return bookId + ":" + chapter + ":" + verse;
    }
}