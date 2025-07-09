package com.dong.bible.common.response;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

/**
 * API response code(enum)
 */
@Getter
@Accessors(fluent = true)
public enum ResponseCode {
    // ### Successful responses (S000~S999): 정상 처리해야할 응답 부분 정의, OK_xxx
    OK(HttpStatus.OK, "S000", "정상적으로 처리되었습니다."),
    OK_CREATED(HttpStatus.CREATED, "S001", "정상적으로 생성되었습니다."),
    OK_ACCEPTED(HttpStatus.ACCEPTED, "S002", "정상적으로 수신되었으나, 비동기 처리로 진행중이기 때문에 처리 결과는 알 수 없습니다. "),
    OK_NO_RECORD(HttpStatus.NO_CONTENT, "S003", "조회된 결과가 없습니다."),

    // ### Client request error (R000~–R999): Client(사용자 요청) 오류, REQ_xxx
    // 1. Validation (R001 ~ R099)
    REQ_BAD_REQUEST(HttpStatus.BAD_REQUEST, "R000", "잘못된 요청 값입니다."), // field validation error
    REQ_INVALID_FIELD(HttpStatus.BAD_REQUEST, "R010", "정의되지 않은 항목입니다."), // field validation error
    REQ_INVALID_DATA(HttpStatus.BAD_REQUEST, "R011", "요청 값이 잘못되었습니다."), // field validation error
    REQ_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "R012", "요청 값의 형식이 잘못되었습니다."), // field validation error
    REQ_NOT_FOUND(HttpStatus.NOT_FOUND, "R044", "Request url not found."),
    REQ_METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "R044", "Request method not allowed"),

    // 2. 인증/권한 관련 오류 (R100~R199)
    REQ_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "R101", "인증되지 않은 요청입니다."),
    REQ_FORBIDDEN(HttpStatus.FORBIDDEN, "R111", "권한이 없습니다."),
    REQ_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "R121", "Invalid token"),
    REQ_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "R122", "Expired token"),

    // 3. 중복 요청 등 요청관련 오류 (R200~R299)
    REQ_TO_MANY_REQUEST(HttpStatus.TOO_MANY_REQUESTS, "R231", "단기간에 너무 많이 요청되었습니다. 잠시 후 다시 시도해 주세요."),
    REQ_DUPLICATED(HttpStatus.BAD_REQUEST, "R232", "동일한 내용이 중복으로 요청되었습니다."),
    REQ_BAD_DUPLICATED_TUID(HttpStatus.BAD_REQUEST, "R233", "중복된 거래고유번호입니다."),

    // 4. 업무 처리 오류(R301~)
    REQ_NO_DATA(HttpStatus.BAD_REQUEST, "R301", "처리 대상 데이터가 없습니다."), // 잘못된 데이터에 대한 요청

    // 5. 기타(R900~)
    REQ_BAD_REQUEST_ETC(HttpStatus.BAD_REQUEST, "R900", "서버 오류가 발생하였습니다."), // 기타 요청 값 오류

    // ### Server error (E000~): 시스템 오류, SYS_xxx
    SYS_NO_DATA(HttpStatus.INTERNAL_SERVER_ERROR, "E101", "처리할 데이터가 없습니다."),

    SYS_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E999", "시스템 오류입니다. 담당자에게 문의하세요."), // 기타 서버 오류,

    // ### External server error (X000~): 연계 솔루션이나 서버에서 오류가 발생한 경우 정의, EXT_(서버구분)_xxx
    LINKED_CONNECTION_TIMEOUT(HttpStatus.INTERNAL_SERVER_ERROR, "X999", "연계 서버 통신 중 거래시간이 초과하였습니다."),
    LINKED_COMMUNICATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "X991", "외부 연계 서버 통신 중 오류가 발생하였습니다."),
    LINKED_BIZ_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "X901", "연계 서버 처리 결과가 정상이 아닙니다."),

    // DB 에러 코드 명시
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "D999", "Validation failed"),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "D901", "DB 저장 실패"),
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "X9999", "시스템 오류입니다. 담당자에게 문의하세요."),
    ;

    /**
     * http status code
     * <br/> Exception 처리 응답 시 사용할 http 상태코드
     * <br/> Informational responses (100 – 199):  요청을 받고, 처리 중에 있음
     * <br/> Successful responses (200 – 299): 요청을 정상적으로 처리함
     * <br/> Redirection messages (300 – 399): 요청을 완료를 위해 추가 동작 필요
     * <br/> Client error responses (400 – 499): Client 요청을 처리할 수 없어 오류 발생
     * <br/> Server error responses (500 – 599): Server 처리 중 오류 발생
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status">HTTP response status codes</a>
     */
    private final HttpStatus status;

    /**
     * API response code - 4 alphanumerics
     * <br/> Successful responses(S000–S999): 정상 처리
     * <br/> Client error(R000–R999): 사용자 요청 오류
     * <br/> Server error(E000–E999): API 서버 오류
     * <br/> External server error(X000–X999): 외부 서버, 모듈 통신 오류
     */
    private final String code;

    /**
     * response code
     */
    private final String message;


    ResponseCode(HttpStatus status, String code, String description){
        this.status = status;
        this.code = code;
        this.message = description;
    }
}
