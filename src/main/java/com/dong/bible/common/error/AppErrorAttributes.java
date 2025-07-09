package com.dong.bible.common.error;

import com.dong.bible.common.response.AppResponse;
import com.dong.bible.common.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * ErrorAttributes for 404 error handling
 */
@Slf4j
@RequiredArgsConstructor
public class AppErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        Integer status = (Integer) webRequest.getAttribute("javax.servlet.error.status_code", RequestAttributes.SCOPE_REQUEST);
        if(status != null && status == HttpStatus.NOT_FOUND.value()){
            return AppResponse.error(ResError.of(ResponseCode.REQ_NOT_FOUND), "Request url not found - " + super.getErrorAttributes(webRequest, options).get("path")).toMap();
        }
        return super.getErrorAttributes(webRequest, options);
    }
}
