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

        return response;
    }

    public static void main(String[] args) {
        System.out.println("Hello World!");
    }
}
