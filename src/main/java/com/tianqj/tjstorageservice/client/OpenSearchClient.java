package com.tianqj.tjstorageservice.client;

import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;
import com.fasterxml.jackson.databind.JsonNode;
import com.tianqj.tjstorageservice.utils.jackson.JacksonConverter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.index.IndexResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.common.xcontent.XContentType;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Map;

@Log4j2
public class OpenSearchClient {
    private final RestHighLevelClient delegate;
    private final JacksonConverter jacksonConverter;

    @Inject
    public OpenSearchClient(final RestHighLevelClient client,
                            final JacksonConverter jacksonConverter) {
        this.delegate = client;
        this.jacksonConverter = jacksonConverter;
    }

    public IndexResponse createDocument(@NonNull final String index,
                                        @NonNull final String documentId,
                                        @NonNull final Map<String, AttributeValue> source) throws IOException {

        JsonNode jsonNode = this.jacksonConverter.mapToJsonObject(source);
        String sourceInStringFormat = jsonNode.toString();
        IndexRequest indexRequest = new IndexRequest()
                .index(index)
                .id(documentId)
                .source(sourceInStringFormat, XContentType.JSON);

        return this.delegate.index(indexRequest, RequestOptions.DEFAULT);
    }
}
