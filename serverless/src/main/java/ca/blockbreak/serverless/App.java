package ca.blockbreak.serverless;

import java.util.Map;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketResponse;

/**
 * WebSocket
 */
public class App
    implements
        RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2WebSocketResponse>
{

    public APIGatewayV2WebSocketResponse handleRequest(
        APIGatewayV2HTTPEvent event,
        Context context
    ) {
        APIGatewayV2WebSocketResponse response =
            new APIGatewayV2WebSocketResponse();
        response.setStatusCode(200);
        response.setBody("{ \"message\": \"Message received!\" }");

        Map<String, String> headers = event.getHeaders();
        for (var header : headers.entrySet()) {
            System.out.println(header.getKey() + ": " + header.getValue());
        }

        System.out.println("event.getBody(): " + event.getBody());

        return response;
    }

    public static void main(String[] args) {
        System.out.println("Hello World!");
    }
}
