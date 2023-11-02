package com.tianqj.tjstorageservice.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;
import com.google.gson.Gson;
import com.tianqj.tjstorageservice.utils.CommonUtils;
import com.tianqj.tjstorageservice.client.OpenSearchClient;
import lombok.extern.log4j.Log4j2;
import org.opensearch.action.index.IndexResponse;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Map;


@Log4j2
public class DdbEventHandler implements RequestHandler<DynamodbEvent, String> {

    private final Gson gson;
    private final OpenSearchClient openSearchClient;
    @Inject
    public DdbEventHandler(final Gson gson, final OpenSearchClient openSearchClient) {
        this.gson = gson;
        this.openSearchClient = openSearchClient;
    }

    @Override
    public String handleRequest(DynamodbEvent dynamodbEvent, Context context) {
        for (DynamodbEvent.DynamodbStreamRecord record : dynamodbEvent.getRecords()) {
            log.info("EventName : " + record.getEventName());
            log.info("EventName : " + gson.toJson(record.getDynamodb()));
            try {
                switch (record.getEventName()) {
                    case "INSERT":
                        Map<String, AttributeValue> newImage = record.getDynamodb().getNewImage();
                        final String index = CommonUtils.getIndexName(newImage);
                        final String documentId = CommonUtils.generateDocumentId(newImage);
                        log.info("Create document for index - {}, documentId - {}, newImage - {}",
                                index, documentId, newImage);
                        IndexResponse response = this.openSearchClient.createDocument(index, documentId, newImage);
                        log.info("The response from OpenSearch {}", response.toString());
                    default:
                        log.info("Nothing need to be done");
                }

            } catch (IOException e) {
                log.error(e);
                throw new RuntimeException(e);
            }

        }
        return "Successfullyprocessed";
    }
}
