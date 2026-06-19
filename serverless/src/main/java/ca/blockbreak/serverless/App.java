package ca.blockbreak.serverless;

import ca.blockbreak.serverless.database.SecretsManager;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * WebSocket
 */
public class App
    implements
        RequestHandler<
            APIGatewayV2WebSocketEvent,
            APIGatewayV2WebSocketResponse
        >
{

    @Override
    public APIGatewayV2WebSocketResponse handleRequest(
        APIGatewayV2WebSocketEvent event,
        Context context
    ) {
        System.out.println("event.getBody(): " + event.getBody());

        String eventType = event.getRequestContext().getEventType();
        System.out.println("requestContext.getEventType(): " + eventType);

        String routeKey = event.getRequestContext().getRouteKey();
        System.out.println("requestContext.getRouteKey(): " + routeKey);

        String connectionId = event.getRequestContext().getConnectionId();
        System.out.println("requestContext.getConnectionId(): " + connectionId);

        String data;
        if ("CONNECT".equals(eventType)) {
            data = "{ \"message\": \"Connected\" }";
        } else if ("DISCONNECT".equals(eventType)) {
            data = "{ \"message\": \"Disconnected\" }";
        } else {
            data = "{ \"message\": \"Disconnected\" }";
            try (SecretsManager sm = new SecretsManager()) {
                if ("connect".equals(routeKey)) {
                    var connect = new Connect(sm);
                    Connect.Message message = new Connect.Message(
                        event.getRequestContext().getConnectionId()
                    );
                    connect.processMessage(message);
                } else if ("breakblock".equals(routeKey)) {
                    var breakBlock = new BreakBlock(sm);
                    //bad will throw null pointer exception
                    breakBlock.processMessage(
                        new BreakBlock.Message(0l, 1, 1, 2, 3)
                    );
                }
            }
        }
        System.out.println("data: " + data);

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
