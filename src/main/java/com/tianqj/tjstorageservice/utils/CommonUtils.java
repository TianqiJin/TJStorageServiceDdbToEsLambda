package com.tianqj.tjstorageservice.utils;

import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;
import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.Objects;

@UtilityClass
public class CommonUtils {
    private static final String RESOURCE_TYPE_FIELD = "resourceType";
    private static final String ID_FIELD = "id";

    public String getIndexName(final Map<String, AttributeValue> document) {
        return document.get(RESOURCE_TYPE_FIELD).getS().toLowerCase();
    }

    public String generateDocumentId(final Map<String, AttributeValue> document) {
        final String resourceType = Objects.requireNonNull(document.get(RESOURCE_TYPE_FIELD).getS());
        final String id = Objects.requireNonNull(document.get(ID_FIELD).getS());

        return String.format("%s.%s", resourceType, id);
    }
}
