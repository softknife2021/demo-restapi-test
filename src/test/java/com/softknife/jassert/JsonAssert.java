package com.softknife.jassert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softknife.resources.ConfigProvider;
import org.assertj.core.api.AbstractAssert;
import java.util.Map;
import java.util.Objects;

/**
 * @author amatsaylo on 3/24/25
 * @project demo-restapi-test
 */

public class JsonAssert extends AbstractAssert<JsonAssert, String> {

    private static final ObjectMapper objectMapper = ConfigProvider.getInstance().getMapper();
    private final JsonNode jsonNode;

    private JsonAssert(String json) {
        super(json, JsonAssert.class);
        this.jsonNode = parseJson(json);
    }

    // Static factory method
    public static JsonAssert assertThat(String json) {
        return new JsonAssert(json);
    }

    // Parse JSON and cache the JsonNode
    private JsonNode parseJson(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            failWithMessage("Invalid JSON: %s", e.getMessage());
            return null; // Unreachable
        }
    }

    // --- Single Field Assertions ---
    public JsonAssert hasField(String fieldPath, Object expectedValue) {
        Object actualValue = getFieldValue(fieldPath);
        if (!Objects.equals(actualValue, expectedValue)) {
            failWithMessage("Expected field <%s> to be <%s> but was <%s>",
                    fieldPath, expectedValue, actualValue);
        }
        return this;
    }

    // --- Nested Field Support ---
    private Object getFieldValue(String fieldPath) {
        try {
            JsonNode currentNode = jsonNode;
            for (String part : fieldPath.split("\\.")) {
                currentNode = currentNode.path(part);
                if (currentNode.isMissingNode()) {
                    failWithMessage("Field <%s> not found in JSON", fieldPath);
                }
            }
            return objectMapper.treeToValue(currentNode, Object.class);
        } catch (Exception e) {
            failWithMessage("Failed to extract field <%s>: %s", fieldPath, e.getMessage());
            return null; // Unreachable
        }
    }

    // --- Batch Assertions ---
    public JsonAssert hasFields(Map<String, Object> expectedFields) {
        expectedFields.forEach(this::hasField);
        return this;
    }
}
