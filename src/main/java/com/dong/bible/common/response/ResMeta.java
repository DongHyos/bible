package com.dong.bible.common.response;

import com.dong.bible.common.AppProperties;
import com.dong.bible.common.utils.BeanUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Meta response for AppResponse
 */
@Getter
@Builder
@ToString
public class ResMeta {
    private String path;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss.SSS")
    private LocalDateTime timestamp;

    @JsonFormat(pattern = "mm:ss.SSS")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalTime timeTaken;

    public static ResMeta generate() {
        AppProperties properties = BeanUtils.getBean(AppProperties.class);
        if(properties.getOption().meta()){
            HttpServletRequest req = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
            return ResMeta.builder()
                    .path(req.getRequestURI())
                    .timestamp(LocalDateTime.now())
                    .build();
        }
        return null;
    }
}
