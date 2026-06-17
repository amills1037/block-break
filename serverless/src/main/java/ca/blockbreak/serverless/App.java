package ca.blockbreak.serverless;

import java.util.HashMap;
import java.util.Map;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketResponse;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.apigatewaymanagementapi.ApiGatewayManagementApiClient;
import software.amazon.awssdk.services.apigatewaymanagementapi.model.PostToConnectionRequest;
import java.net.URI;

import software.amazon.awssdk.services.apigatewaymanagementapi.model.GoneException;

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
        System.out.println("event.getBody(): " + event.getBody());

        String eventType = event.getRequestContext().getEventType();
        System.out.println("requestContext.getEventType(): " + eventType);

        String routeKey = event.getRequestContext().getRouteKey();
        System.out.println("requestContext.getRouteKey(): " + routeKey);

        //https://{api-id}.execute-api.{region}.amazonaws.com/{stage}/@connections/{connection_id}
        String callbackURL = String.format("https://%s.execute-api.%s.amazonaws.com/%s",
            event.getRequestContext().getApiId(),
            System.getenv("AWS_REGION"),
            event.getRequestContext().getStage());
        System.out.println("callbackURL: " + callbackURL);

        ApiGatewayManagementApiClient client = ApiGatewayManagementApiClient.builder()
            .endpointOverride(URI.create(callbackURL))
            .build();
        // System.out.println("client: " + client);

        String data;
        if ("CONNECT".equals(eventType)) {
            data = "{ \"message\": \"Connected\" }";
        } else if ("DISCONNECT".equals(eventType)) {
            data = "{ \"message\": \"Disconnected\" }";
        } else {
            data = "{ \"message\": \"Message received\" }";
        }
        System.out.println("data: " + data);
        String connectionId = event.getRequestContext().getConnectionId();
        System.out.println("requestContext.getConnectionId(): " + connectionId);

        PostToConnectionRequest request = PostToConnectionRequest.builder()
            .connectionId(event.getRequestContext().getConnectionId())
            .data(SdkBytes.fromUtf8String(data))
            .build();
        // System.out.println("request: " + request);

        try {
            client.postToConnection(request);
        } catch (GoneException ge) {
            System.out.println("GoneException: " + ge);
        }

        APIGatewayV2WebSocketResponse response =
            new APIGatewayV2WebSocketResponse();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        response.setHeaders(headers);
        response.setStatusCode(200);
        response.setBody(data);

        return response;
    }

}
