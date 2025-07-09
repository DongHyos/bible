package com.dong.bible.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 거래그룹명세 및 거래명세 저장용 Annotation
 * </br> TransactionLogAspect 내에서 DB 처리
 * </br> 1. @RequestBody 객체는 BaseRequestDTO 상속
 * </br> 2. TransactionLogAspect.proceed 내 거래그룹번호, 거래번호 set
 * </br> 3. DealingService.save 내 거래명세 저장
 *
 * @author Won Gilho
 * @since Version 1.0
 * <pre>
 * ===================== Change history ======================
 * DATE          AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2023-08-24    Won Gilho     최초 생성
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TransactionLog {
}