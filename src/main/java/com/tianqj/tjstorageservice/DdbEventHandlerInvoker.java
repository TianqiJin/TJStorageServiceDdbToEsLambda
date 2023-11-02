package com.tianqj.tjstorageservice;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.tianqj.tjstorageservice.dagger.DaggerLambdaComponent;
import com.tianqj.tjstorageservice.dagger.LambdaComponent;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DdbEventHandlerInvoker implements RequestHandler<DynamodbEvent, String> {
    private static final LambdaComponent component = DaggerLambdaComponent.create();
    public String handleRequest(DynamodbEvent dynamodbEvent, Context context) {
        return component.getDdbEventHandler().handleRequest(dynamodbEvent, context);
    }
}
