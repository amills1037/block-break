package ca.blockbreak.serverless;

import java.util.HashMap;
import java.util.Map;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketResponse;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.apigatewaymanagementapi.ApiGatewayManagementApiClient;
import software.amazon.awssdk.services.apigatewaymanagementapi.model.PostToConnectionRequest;
import java.net.URI;


/**
 * WebSocket
 */
public class App
    implements
        RequestHandler<APIGatewayV2WebSocketEvent, APIGatewayV2WebSocketResponse>
{

    public APIGatewayV2WebSocketResponse handleRequest(
        APIGatewayV2WebSocketEvent event,
        Context context
    ) {
        LambdaLogger logger = context.getLogger();
        logger.log("event.getBody(): " + event.getBody());

        String eventType = event.getRequestContext().getEventType();
        logger.log("requestContext.getBody(): " + eventType);

        String routeKey = event.getRequestContext().getRouteKey();
        logger.log("requestContext.getBody(): " + routeKey);

        String callbackURL = String.format("https://%s/%s", event.getRequestContext().getDomainName(), event.getRequestContext().getStage());
        logger.log("callbackURL: " + callbackURL);

        String data;
        if ("CONNECT".equals(eventType)) {
            data = "{ \"message\": \"Connected\" }";
        } else if ("DISCONNECT".equals(eventType)) {
            data = "{ \"message\": \"Disconnected\" }";
        } else {
            data = "{ \"message\": \"Message received\" }";
        }
        logger.log("data: " + data);

        ApiGatewayManagementApiClient client = ApiGatewayManagementApiClient.builder()
            .endpointOverride(URI.create(callbackURL))
            .build();
        logger.log("client: " + client);

        PostToConnectionRequest request = PostToConnectionRequest.builder()
            .connectionId(event.getRequestContext().getConnectionId())
            .data(SdkBytes.fromUtf8String(data))
            .build();
        logger.log("request: " + request);

        // client.postToConnection(request);

        APIGatewayV2WebSocketResponse response =
            new APIGatewayV2WebSocketResponse();
        response.setStatusCode(200);

        return response;
    }

}
