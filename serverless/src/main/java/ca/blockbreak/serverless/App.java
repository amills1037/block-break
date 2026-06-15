package ca.blockbreak.serverless;

import java.util.Map;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketResponse;

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
        APIGatewayV2WebSocketResponse response =
            new APIGatewayV2WebSocketResponse();
        response.setStatusCode(200);
        response.setBody("{ \"message\": \"Message received!\" }");

        Map<String, String> headers = event.getHeaders();
        if (headers != null) {
            for (var header : headers.entrySet()) {
                System.out.println(header.getKey() + ": " + header.getValue());
            }
        }
        System.out.println("event.getBody(): " + event.getBody());

        System.out.println("getAccountId(): " + event.getRequestContext().getAccountId());
        System.out.println("getApiId(): " + event.getRequestContext().getApiId());
        // Map<String,Object> 	getAuthorizer(): " + event.getBody());
        System.out.println("getConnectedAt(): " + event.getRequestContext().getConnectedAt());
        System.out.println("getConnectionId(): " + event.getRequestContext().getConnectionId());
        System.out.println("getDomainName(): " + event.getRequestContext().getDomainName());
        System.out.println("getError(): " + event.getRequestContext().getError());
        System.out.println("getEventType(): " + event.getRequestContext().getEventType());
        System.out.println("getExtendedRequestId(): " + event.getRequestContext().getExtendedRequestId());
        System.out.println("getHttpMethod(): " + event.getRequestContext().getHttpMethod());
        // APIGatewayV2WebSocketEvent.RequestIdentity 	getIdentity()
        System.out.println("getIntegrationLatency(): " + event.getRequestContext().getIntegrationLatency());
        System.out.println("getMessageDirection(): " + event.getRequestContext().getMessageDirection());
        System.out.println("getMessageId(): " + event.getRequestContext().getMessageId());
        System.out.println("getRequestId(): " + event.getRequestContext().getRequestId());
        System.out.println("getRequestTime(): " + event.getRequestContext().getRequestTime());
        System.out.println("getRequestTimeEpoch(): " + event.getRequestContext().getRequestTimeEpoch());
        System.out.println("getResourceId(): " + event.getRequestContext().getResourceId());
        System.out.println("getResourcePath(): " + event.getRequestContext().getResourcePath());
        System.out.println("getRouteKey(): " + event.getRequestContext().getRouteKey());
        System.out.println("getStage(): " + event.getRequestContext().getStage());
        System.out.println("getStatus(): " + event.getRequestContext().getStatus());

        return response;
    }

    public static void main(String[] args) {
        System.out.println("Hello World!");
    }
}
