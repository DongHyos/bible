package com.dong.bible.common.error;

import com.dong.bible.common.response.AppResponse;
import com.dong.bible.common.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * App global exception handler for Rest API
 */
@Slf4j
@ConditionalOnProperty(name = "app.error.use.advice", havingValue = "true")
@RestControllerAdvice(annotations = RestController.class)
@RequiredArgsConstructor
public class AppExceptionHandler {

    /**
     * BindException handler (ex. spring validation)
     *
     * @param ex BindException
     * @return AppResponse
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public AppResponse handleBindException(BindException ex) {
        ResponseCode responseCode = ResponseCode.REQ_BAD_REQUEST;
        List<AppError> errorDetails = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError ->
                        AppError.builder()
                                .field(fieldError.getField())
                                .domain(fieldError.getObjectName())
                                .message(fieldError.getDefaultMessage())
                                .value(fieldError.getRejectedValue())
                                .build())
                .collect(Collectors.toList());

        return AppResponse.error(
                ResError.builder()
                        .responseCode(responseCode)
                        .errors(errorDetails)
                        .error(responseCode.status().getReasonPhrase())
                        .message(String.format("Field verification failed for object = '%s', Error count: %d",
                                ex.getBindingResult().getObjectName(), ex.getBindingResult().getFieldErrorCount()))
                        .build(),
                responseCode.message()
        );
    }

    /**
     * 404 Not found exception handler
     * <br/> Required setting
     * <br/> spring.mvc.throw-exception-if-no-handler-found: true # for 404 error handling
     * <br/> sprint.web.resources.add-mappings: false  # for 404 error handling
     * <br/> if add-mappings == false, cannot use static resource
     *
     * @param ex NoHandlerFoundException
     * @return AppResponse
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    protected AppResponse handleNoHandlerFoundException(NoHandlerFoundException ex) {
        return AppResponse.error(ex, ResponseCode.REQ_NOT_FOUND);
    }

    /**
     * Method not allowed exception handler
     *
     * @param ex HttpRequestMethodNotSupportedException
     * @return AppResponse
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
    @ResponseBody
    protected AppResponse handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        return AppResponse.error(ex, ResponseCode.REQ_METHOD_NOT_ALLOWED);
    }

    /**
     * BizException handler
     *
     * @param ex BizException
     * @return AppResponse
     */
    @ExceptionHandler(BizException.class)
    @ResponseBody
    public final ResponseEntity<AppResponse> handleBizException(BizException ex) {
        return ResponseEntity
                .status(ex.getResponseCode().status())
                .body(AppResponse.error(ex));
    }

    /**
     * ApiException handler
     *
     * @param ex ApiException
     * @return AppResponse
     */
    @ExceptionHandler(ApiException.class)
    @ResponseBody
    public final AppResponse handleApiException(ApiException ex) {
        return AppResponse.error(ex);
    }

    /**
     * Base Exception handler
     *
     * @param ex Exception
     * @return AppResponse
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public final AppResponse handleException(Exception ex) {
        return AppResponse.error(ex);
    }
}
