package com.dong.bible.common.response;

import com.dong.bible.common.error.ApiException;
import com.dong.bible.common.error.BizException;
import com.dong.bible.common.error.ResError;
import com.dong.bible.common.utils.ObjectUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * RestAPI Response
 */
@Slf4j
@Getter
@Builder
@ToString
public class AppResponse<T> {

    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ResMeta meta;

    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String guid;
    private int status;
    private String code;

    private String message;

    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String timestamp;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object payload;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ResError error;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ResPaging paging;

    public static <T> AppResponse<T> ok(){
        return of(ResponseCode.OK);
    }

    public static <T> AppResponse<T> ok(T payload){
        return AppResponse.of(payload);
    }

    public static <T> AppResponse<T> ok(Page<?> page, Class<?> dtoType){
        return AppResponse.of(page, dtoType);
    }

    public static <T> AppResponse<T> ok(List<?> payload, ResPaging page, Class<?> dtoType){
        return AppResponse.of(payload, page, dtoType);
    }
    
    public static <T> AppResponse<List<T>> ok(Page<?> page, List<T> mappedPayload){
        return AppResponse.<List<T>>builder()
                .status(ResponseCode.OK.status().value())
                .code(ResponseCode.OK.code())
                .message(ResponseCode.OK.message())
                .payload(mappedPayload)
                .paging(ResPaging.of(page))
                .build();
    }

    public static <T> AppResponse<T> of(T payload, ResponseCode responseCode, String message){
        return AppResponse.<T>builder()
                .status(responseCode.status().value())
                .code(responseCode.code())
                .message(message)
                .payload(payload)
                .build();
    }

    public static <T> AppResponse<T> of(ResponseCode responseCode){
        return AppResponse.of(responseCode, null);
    }
    public static <T> AppResponse<T> of(ResponseCode responseCode, T payload){
        return AppResponse.<T>builder()
                .status(responseCode.status().value())
                .code(responseCode.code())
                .message(responseCode.message())
                .payload(payload)
                .build();
    }

    public static <T> AppResponse<T> of(T payload){
        if(org.springframework.util.ObjectUtils.isEmpty(payload )){
            return of(ResponseCode.OK_NO_RECORD);
        }
        return of(ResponseCode.OK, payload);
    }

    public static <T> AppResponse<T> of(Page<?> page, Class<?> dtoType){
        return AppResponse.<T>builder()
                .status(ResponseCode.OK.status().value())
                .code(ResponseCode.OK.code())
                .message(ResponseCode.OK.message())
                .payload(page.get().map(entity -> ObjectUtils.map(entity, dtoType)))
                .paging(ResPaging.of(page))
                .build();
    }

    public static <T> AppResponse<T> of(List<?> payload, ResPaging page, Class<?> dtoType){
        return AppResponse.<T>builder()
                .status(ResponseCode.OK.status().value())
                .code(ResponseCode.OK.code())
                .message(ResponseCode.OK.message())
                .payload(payload.stream().map(entity -> ObjectUtils.map(entity, dtoType)))
                .paging(page)
                .build();
    }

    public static <T> AppResponse<T> error(ResError error, String message){
        return AppResponse.<T>builder()
                .status(error.getResponseCode().status().value())
                .code(error.getResponseCode().code())
                .message(message)
                .error(error)
                .build();
    }

    public static <T> AppResponse<T> error(BizException exception) {
        return AppResponse.<T>builder()
                .status(exception.getResponseCode().status().value())
                .code(exception.getResponseCode().code())
                .message(exception.getMessage())
                .error(ResError.of(exception))
                .meta(ResMeta.generate())
                .build();
    }

    public static <T> AppResponse<T> error(ApiException exception) {
        return AppResponse.<T>builder()
                .status(exception.getResponseCode().status().value())
                .code(exception.getResponseCode().code())
                .message(exception.getMessage())
                .error(ResError.of(exception))
                .meta(ResMeta.generate())
                .build();
    }

    public static <T> AppResponse<T> error(Exception exception, ResponseCode responseCode) {
        return AppResponse.error(exception, responseCode, responseCode.message());
    }

    public static <T> AppResponse<T> error(Exception exception, ResponseCode responseCode, String message) {
        return AppResponse.<T>builder()
                .status(responseCode.status().value())
                .code(responseCode.code())
                .message(message)
                .error(ResError.of(exception, responseCode))
                .meta(ResMeta.generate())
                .build();
    }
    public static <T> AppResponse<T> error(T payload, ResponseCode responseCode, String message) {
        return AppResponse.<T>builder()
                .status(responseCode.status().value())
                .code(responseCode.code())
                .message(message)
                .payload(payload)
                .meta(ResMeta.generate())
                .build();
    }

    public static <T> AppResponse<T> error(Exception exception) {
        return AppResponse.<T>builder()
                .status(ResponseCode.SYS_SERVER_ERROR.status().value())
                .code(ResponseCode.SYS_SERVER_ERROR.code())
                .message("시스템 오류가 발생하였습니다. 관리자에게 문의하세요.")
                .error(ResError.of(exception, ResponseCode.SYS_SERVER_ERROR))
                .meta(ResMeta.generate())
                .build();
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> toMap(){
        return (new ObjectMapper()).convertValue(this, Map.class);
    }
}
