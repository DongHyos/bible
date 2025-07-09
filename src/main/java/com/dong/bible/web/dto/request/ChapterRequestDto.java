package com.dong.bible.web.dto.request;

import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
public class ChapterRequestDto {
    
    @NotNull(message = "책 ID는 필수입니다")
    @Min(value = 1, message = "책 ID는 1 이상이어야 합니다")
    private Integer bookId;
    
    @NotNull(message = "장 번호는 필수입니다")
    @Min(value = 1, message = "장 번호는 1 이상이어야 합니다")
    private Integer chapter;
}
