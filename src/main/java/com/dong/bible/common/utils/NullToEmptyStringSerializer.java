package com.dong.bible.common.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * JSON serializer to replace null with a space character
 * <br/> WebConfig.MappingJackson2HttpMessageConverter 설정 필요: 현재 미사용
 * @author Won Gilho
 * @since Version 1.0
 * <pre>
 * ===================== Change history ======================
 * DATE          AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2023-08-18    Won Gilho     최초 생성
 * </pre>
 */
public class NullToEmptyStringSerializer extends StdSerializer<Object> {

    public NullToEmptyStringSerializer(Class<Object> t) {
        super(t);
    }

    public NullToEmptyStringSerializer() {
        this(null);
    }

    @Override
    public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString("");
    }
}