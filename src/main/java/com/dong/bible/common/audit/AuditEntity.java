package com.dong.bible.common.audit;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * Audit fields entity
 *
 * @author Won Gilho
 * @since Version 1.0
 * <pre>
 * ===================== Change history ======================
 * DATE          AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2023-08-01    Won Gilho     최초 생성
 * </pre>
 */
@Getter
@SuperBuilder
@NoArgsConstructor
public abstract class AuditEntity {
    private String frstRgtrId;
    private LocalDateTime frstRegDt;
    private String lastMdfrId;
    private LocalDateTime lastMdfcnDt;
}
