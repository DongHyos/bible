package com.dong.bible.common.utils;

import com.dong.bible.common.annotation.Record;
import com.dong.bible.common.error.AppError;
import com.dong.bible.common.error.BizException;
import com.dong.bible.common.response.AppResponse;
import com.dong.bible.common.response.ResponseCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.util.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * Application Utils
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
@Slf4j
public class AppUtils {
    /**
     * Mask string.
     *
     * @param source       the source text
     * @param targetFields the target fields
     * @param maskingText  the masking text
     * @return the string
     */
    public static String mask(String source, String[] targetFields, String maskingText) {
        if (ObjectUtils.isEmpty(targetFields)) {
            return source;
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(source);
            maskFields(jsonNode, targetFields, maskingText);
            return objectMapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            return source;
        }
    }

    public static String maskUrl(String url, String[] targetFields, String maskingText) {
        if (ObjectUtils.isEmpty(targetFields)) {
            return url;
        }

        try {
            for (String targetField : targetFields) {
                String regex = targetField + "=([^&]+)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(url);

                url = matcher.replaceAll(targetField + "=" + maskingText);
            }
            return url;
        } catch (RuntimeException e) {
            return url;
        }
    }

    private static void maskFields(JsonNode jsonNode, String[] targetFields, String maskingText) {
        for (String field : targetFields) {
            field = field.trim();
            maskField(jsonNode, field, maskingText);
        }
    }

    private static void maskField(JsonNode jsonNode, String targetField, String maskingText) {
        if (jsonNode.isObject()) {
            ObjectNode objectNode = (ObjectNode) jsonNode;
            if (objectNode.has(targetField)) {
                objectNode.put(targetField, maskingText);
            }
            objectNode.fields().forEachRemaining(entry -> maskField(entry.getValue(), targetField, maskingText));
        } else if (jsonNode.isArray()) {
            jsonNode.forEach(node -> maskField(node, targetField, maskingText));
        }
    }

    /**
     * Get number Long.
     *
     * @param src the src
     * @return the integer
     */
    public static BigDecimal getBigDecimal(String src){
        if(StringUtils.hasLength(src)){
            return new BigDecimal(src);
        }
        return null;
    }

    /**
     * Parse local date time local date time.
     *
     * @param src the src
     * @return the local date time
     */
    public static LocalDateTime parseLocalDateTime(String src) {
        String regex = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{7}[+-]\\d{2}:\\d{2}$";

        if (StringUtils.hasLength(src)) {
            if(Pattern.compile(regex).matcher(src).matches()){
                OffsetDateTime offsetDateTime = OffsetDateTime.parse(src, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                return offsetDateTime.toLocalDateTime();
            }
            String format = "yyyy-MM-dd HH:mm:ss.SSS";
            if(src.length() == 8){
                return LocalDateTime.parse(src + "000000", DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            } else if (src.length() == 19){
                String datePattern = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$";
                Pattern pattern = Pattern.compile(datePattern);
                Matcher matcher = pattern.matcher(src);
                if (matcher.matches()) {
                    return LocalDateTime.parse(src);
                }
                format = "yyyy-MM-dd HH:mm:ss";
            }
            return LocalDateTime.parse(src, DateTimeFormatter.ofPattern(format));
        }
        return null;
    }

    /**
     * Is matched uri boolean.
     *
     * @param requestURI the request uri
     * @param excludes   the excludes
     * @return the boolean
     */
    public static boolean isMatchedUri(String requestURI, String[] excludes) {
        if(!ObjectUtils.isEmpty(excludes)){
            AntPathMatcher pathMatcher = new AntPathMatcher();
            return Arrays.stream(excludes).anyMatch(url -> pathMatcher.match(url, requestURI));
        }
        return false;
    }

    public static AppResponse getAppResponse(Object result) {
        if (result instanceof ResponseEntity) {
            ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;
            if (responseEntity.getBody() instanceof AppResponse) {
                return (AppResponse) responseEntity.getBody();
            }
        }
        throw new BizException(ResponseCode.SYS_SERVER_ERROR, "거래 Master 저장 중 오류가 발생하였습니다 - 지원하지 않는 ResponseType: " + result.getClass().getName());
    }

    /**
     * 목록에서 중복된 값이 있을 경우 Exception 발생 처리
     *
     * @param list    the list
     * @param domain  the domain
     * @param field   the field
     * @param message the message
     */
    public static void duplicatedFieldException(List<String> list, String domain, String field, String message){
        List<String> duplicated = list.stream().filter(s -> Collections.frequency(list, s) > 1).collect(Collectors.toList());
        makeException(list, domain, field, message, duplicated);
    }

    /**
     * 목록에서 빈값이 있을 경우 Exception 발생 처리
     *
     * @param list    the list
     * @param domain  the domain
     * @param field   the field
     * @param message the message
     */
    public static void emptyFieldException(List<String> list, String domain, String field, String message){
        List<String> empty = list.stream().filter(s -> !StringUtils.hasLength(s)).collect(Collectors.toList());
        makeException(list, domain, field, message, empty);
    }

    /**
     * 빈값이 있을 경우 Exception 발생 처리
     *
     * @param source    the source
     * @param domain  the domain
     * @param field   the field
     * @param message the message
     */
    public static void emptyFieldException(String source, String domain, String field, String message){
        if(!StringUtils.hasLength(source)){
            BizException be = new BizException(ResponseCode.REQ_BAD_REQUEST, "필수값이 누락되었습니다.");
            be.addError(
                    AppError.builder()
                            .domain(domain)
                            .field(field)
                            .value(source)
                            .message(message)
                            .build());
            throw be;
        }
    }

    private static void makeException(List<String> list, String domain, String field, String message, List<String> empty) {
        if(!empty.isEmpty()){
            BizException be = new BizException(ResponseCode.REQ_BAD_REQUEST, message);
            for(int idx=0; idx < list.size(); idx++){
                if(empty.contains(list.get(idx))){
                    be.addError(
                            AppError.builder()
                                    .domain(domain + "[" + idx + "]")
                                    .field(field)
                                    .value(list.get(idx))
                                    .message(message)
                                    .build());
                }
            }
            throw be;
        }
    }

    public static String substringByBytes(String source, int cutLength) {
        if (source == null || source.isEmpty()) {
            return "";
        }

        source = source.trim();
        if (source.getBytes().length <= cutLength) {
            return source;
        }

        StringBuilder sb = new StringBuilder();
        int cnt = 0;
        for (char ch : source.toCharArray()) {
            int charBytes = String.valueOf(ch).getBytes().length;
            if (cnt + charBytes > cutLength) {
                break;
            }
            sb.append(ch);
            cnt += charBytes;
        }
        return sb.toString();
    }

    /**
     * check value is non-zero BigDecimal
     *
     * @param value source
     * @return has non-zero value
     */
    public static boolean hasBigDecimalValue(String value){
        if(StringUtils.hasLength(value)){
            return (new BigDecimal(value).compareTo(BigDecimal.ZERO) != 0);
        }
        return false;
    }

    /**
     * Gets server ip.
     *
     * @return the server ip
     */
    public static String getServerIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "";
        }
    }

    /**
     * Convert multi value map multi value map.
     *
     * @param obj the obj
     * @return the multi value map
     */
    public static MultiValueMap<String, String> convertMultiValueMap(Object obj){
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> maps = objectMapper.convertValue(obj, new TypeReference<Map<String, String>>() {});
        parameters.setAll(maps);

        return parameters;
    }

    /**
     * Gets client ip addr.
     *
     * @param request the request
     * @return the client ip addr
     */
    public static String getClientIpAddr(HttpServletRequest request) {
        String[] headers = {"X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }
        log.debug("requestIp:{}",request.getRemoteAddr());
        return request.getRemoteAddr();
    }

    /**
     * 카드번호 Masking
     *
     * @param cardNo the card no
     * @return the string
     */
    public static String cardMasking(String cardNo){
        if(!StringUtils.hasLength(cardNo) || cardNo.contains("*")) {
            return cardNo;
        }
        // 카드번호 16자리 또는 15자리 '-'포함/미포함 상관없음
        String regex = "(\\d{4})-?(\\d{4})-?(\\d{4})-?(\\d{3,4})$";

        Matcher matcher = Pattern.compile(regex).matcher(cardNo);
        if(matcher.find()) {
            String target = matcher.group(2) + matcher.group(3);
            int length = target.length();
            char[] c = new char[length];
            Arrays.fill(c, '*');

            return cardNo.replace(target, String.valueOf(c));
        }
        return cardNo;
    }

    /**
     * Gets host name.
     *
     * @return the host name
     */
    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "UnknownHost";
        }
    }

    public static <T> T parseRecord(String message, Class<T> clazz) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        T result = clazz.getDeclaredConstructor().newInstance();
        Field[] fields = result.getClass().getDeclaredFields();
        for(Field field: fields){
            Record record = field.getAnnotation(Record.class);
            if(record == null){
                continue;
            }
            field.setAccessible(true);
            String str = message.substring(record.offset(), record.offset() + record.length());
            Object value;
            if(field.getType().equals(String.class)) {
                if (record.trim()) {
                    str = str.trim();
                }
                value = str;
            } else if(field.getType().equals(Long.class)){
                value = Long.valueOf(str.trim());
            } else if(field.getType().equals(BigDecimal.class)){
                value = StringUtils.hasLength(str.trim()) ? new BigDecimal(str.trim()) : BigDecimal.ZERO;
            } else if(field.getType().equals(LocalDateTime.class)){
                value = LocalDateTime.parse(str, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            } else {
                value = str;
            }

            field.set(result, value);
        }
        return result;
    }
}
