package com.dong.bible.common.filter.wrapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

/**
 * Json body request wrapper for modify request json object
 *
 * @author Won Gilho
 * @since Version 1.0
 * <pre>
 * ===================== Change history ======================
 * DATE          AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2023-08-02    Won Gilho     최초 생성
 * </pre>
 */
public class JsonBodyRequestWrapper extends HttpServletRequestWrapper {

    private final String jsonBody;

    public JsonBodyRequestWrapper(HttpServletRequest request, Map<String, Object> additionalData)
            throws IOException {
        super(request);
        this.jsonBody = appendToJsonBody(request, additionalData);
    }

    @Override
    public ServletInputStream getInputStream(){
        return new ServletInputStreamWrapper(jsonBody.getBytes(StandardCharsets.UTF_8));
    }

    private String appendToJsonBody(HttpServletRequest request, Map<String, Object> additionalData)
            throws IOException {
        String existingJsonBody = readRequestBody(request);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(existingJsonBody);

        // Append the additionalData to the JSON node
        ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
        Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            objectNode.set(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, Object> entry : additionalData.entrySet()) {
            objectNode.set(entry.getKey(), objectMapper.valueToTree(entry.getValue()));
        }

        return objectMapper.writeValueAsString(objectNode);
    }

    private String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        try (ServletInputStream inputStream = request.getInputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                requestBody.append(new String(buffer, 0, bytesRead, StandardCharsets.UTF_8));
            }
        }
        return requestBody.toString();
    }

    private static class ServletInputStreamWrapper extends ServletInputStream {

        private final ByteArrayInputStream inputStream;

        public ServletInputStreamWrapper(byte[] bytes) {
            this.inputStream = new ByteArrayInputStream(bytes);
        }

        @Override
        public int read() {
            return inputStream.read();
        }

        @Override
        public boolean isFinished() {
            return inputStream.available() <= 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {

        }
    }
}
