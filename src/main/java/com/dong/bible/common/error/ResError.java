package com.dong.bible.common.error;

import com.dong.bible.common.AppProperties;
import com.dong.bible.common.response.ResponseCode;
import com.dong.bible.common.utils.BeanUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Error response for AppResponse
 */
@Slf4j
@Builder
@Getter
public class ResError {

    @JsonIgnore
    private ResponseCode responseCode;
    private String error;

    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String exception;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String trace;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID uuid;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String reportUri;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Singular
    private List<AppError> errors;

    /**
     * Create ResError using ResponseCode
     *
     * @param responseCode ResponseCode
     * @return ResError
     */
    public static ResError of(ResponseCode responseCode) {
        return ResError.builder()
                .responseCode(responseCode)
                .error(responseCode.status().getReasonPhrase())
                .message(responseCode.message())
                .build();
    }

    /**
     * Create ResError using BizException
     *
     * @param exception BizException
     * @return ResError
     */
    public static ResError of(BizException exception){
        AppProperties properties = BeanUtils.getBean(AppProperties.class);
        return ResError.builder()
                .responseCode(exception.getResponseCode())
                .error(exception.getResponseCode().status().getReasonPhrase())
                .message(exception.getMessage())
                .exception(properties.getOption().exception()?exception.getClass().getName():null)
                .trace(properties.getOption().trace()? ExceptionUtils.getStackTrace(exception):null)
                .errors(properties.getOption().errors() && exception.getErrors() != null ? exception.getErrors(): new ArrayList<>())
                .uuid(properties.getOption().uuid() ? exception.getUuid() : null)
                .build();
    }

    /**
     * Create ResError using ApiException
     *
     * @param exception BizException
     * @return ResError
     */
    public static ResError of(ApiException exception){
        AppProperties properties = BeanUtils.getBean(AppProperties.class);
        return ResError.builder()
                .responseCode(exception.getResponseCode())
                .error(exception.getResponseCode().status().getReasonPhrase())
                .message(exception.getMessage())
                .exception(properties.getOption().exception()?exception.getClass().getName():null)
                .trace(properties.getOption().trace()?ExceptionUtils.getStackTrace(exception):null)
                .errors(properties.getOption().errors() && exception.getErrors() != null ? exception.getErrors(): new ArrayList<>())
                .uuid(properties.getOption().uuid() ? exception.getUuid() : null)
                .build();
    }

    /**
     * Create ResError using Exception & ResponseCode
     * @param exception Exception
     * @param responseCode ResponseCode
     * @return ResError
     */
    public static ResError of(Exception exception, ResponseCode responseCode) {
        AppProperties properties = BeanUtils.getBean(AppProperties.class);
        String message = (exception instanceof BizException || exception instanceof ApiException) ? exception.getMessage() : "시스템 오류가 발생하였습니다.";
        UUID uuid = UUID.randomUUID();
        return ResError.builder()
                .responseCode(responseCode)
                .error(responseCode.status().getReasonPhrase())
                .message(message)
                .exception(properties.getOption().exception()?exception.getClass().getName():null)
                .trace(properties.getOption().trace()?ExceptionUtils.getStackTrace(exception):null)
                .uuid(properties.getOption().uuid() ? uuid : null)
                .build();
    }
}
