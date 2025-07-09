package com.dong.bible.common.error;

import com.dong.bible.common.response.ResponseCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 업무처리 Exception (Rollback 처리용)
 */
@Getter
public class BizException extends RuntimeException{
    private final ResponseCode responseCode;

    private final UUID uuid = UUID.randomUUID();

    private List<AppError> errors;

    public void addError(AppError error){
        if(errors == null){
            errors = new ArrayList<>();
        }
        errors.add(error);
    }

    public boolean hasError(){
        return errors != null && !errors.isEmpty();
    }

    /**
     * Get http status code
     *
     * @return http status code
     */
    public int getStatus(){
        return responseCode.status().value();
    }

    /**
     * Get response code
     *
     * @return response code
     */
    public String getCode(){
        return responseCode.code();
    }

    public BizException(ResponseCode responseCode) {
        super(responseCode.message());
        this.responseCode = responseCode;
    }

    public BizException(ResponseCode responseCode, Throwable cause) {
        super(responseCode.message(), cause);
        this.responseCode = responseCode;
    }

    public BizException(ResponseCode responseCode, String message) {
        super(message);
        this.responseCode = responseCode;
    }

    public BizException(ResponseCode responseCode, String message, Throwable cause) {
        super(message, cause);
        this.responseCode = responseCode;
    }

    public BizException(ResponseCode responseCode, List<AppError> errors, Throwable cause) {
        super(cause);
        this.responseCode = responseCode;
        this.errors = errors;
    }

    public BizException(ResponseCode responseCode, List<AppError> errors, String message) {
        super(message);
        this.responseCode = responseCode;
        this.errors = errors;
    }

    public BizException(ResponseCode responseCode, List<AppError> errors, String message, Throwable cause) {
        super(message, cause);
        this.responseCode = responseCode;
        this.errors = errors;
    }
}
