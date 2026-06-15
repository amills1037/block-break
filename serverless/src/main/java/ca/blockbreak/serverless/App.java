package ca.blockbreak.serverless;

import java.util.HashMap;
import java.util.Map;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
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
        LambdaLogger logger = context.getLogger();
        logger.log("event.getBody(): " + event.getBody());

        String eventType = event.getRequestContext().getEventType();
        logger.log("requestContext.getBody(): " + eventType);

        String routeKey = event.getRequestContext().getRouteKey();
        logger.log("requestContext.getBody(): " + routeKey);

        APIGatewayV2WebSocketResponse response =
            new APIGatewayV2WebSocketResponse();

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        response.setHeaders(headers);
        response.setStatusCode(200);

        if ("CONNECT".equals(eventType)) {
            response.setBody("{ \"message\": \"Connected\" }");
        } else if ("DISCONNECT".equals(eventType)) {
            response.setBody("{ \"message\": \"Disconnected\" }");
        } else {
            response.setBody("{ \"message\": \"Message received\" }");
        }

        return response;
    }

}
