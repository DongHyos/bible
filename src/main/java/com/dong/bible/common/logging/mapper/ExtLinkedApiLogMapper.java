package com.dong.bible.common.logging.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 외부 연계 거래로그 JPA Repository interface(원천 DB)
 *
 * @author Won Gilho
 * @since Version 1.0
 * <pre>
 * ===================== Change history ======================
 * DATE          AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2024-02-19    Won Gilho     최초 생성
 * </pre>
 */
@Mapper
@Transactional(propagation = Propagation.REQUIRES_NEW) // 기존 트랜잭션이 있더라도 이를 무시하고, 새로운 트랜잭션을 생성
public interface ExtLinkedApiLogMapper {
}
