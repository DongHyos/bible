package com.dong.bible.common.error;

import com.dong.bible.common.response.AppResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Error report web
 * <br/> Request logging
 * <br/> Required app.option.report = true
 */
@Slf4j
@ConditionalOnProperty(name = "app.option.report", havingValue = "true")
@RestController
@RequestMapping("${app.api.report-url:/error/report}")
public class ErrorReportController {
    @PostMapping("/{uuid}")
    public ResponseEntity<AppResponse> report(@PathVariable String uuid, @RequestBody Map<String, Object> req){
        log.debug("\n########## report error: {}", uuid);
        log.debug("request data: {} \n##########", req);
        return ResponseEntity.ok(AppResponse.ok());
    }
}
