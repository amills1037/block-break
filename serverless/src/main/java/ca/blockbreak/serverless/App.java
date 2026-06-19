package ca.blockbreak.serverless;

import ca.blockbreak.serverless.database.ConnectionDAO;
import ca.blockbreak.serverless.database.SecretsManager;
import ca.blockbreak.serverless.database.StatsDAO;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketResponse;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.apigatewaymanagementapi.ApiGatewayManagementApiClient;
import software.amazon.awssdk.services.apigatewaymanagementapi.model.PostToConnectionRequest;

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
            //Do not create the table record here. Use "connect" action.
        } else if ("DISCONNECT".equals(eventType)) {
            data = "{ \"message\": \"Disconnected\" }";
            try (SecretsManager sm = new SecretsManager()) {
                try (ConnectionDAO connectionDAO = new ConnectionDAO(sm)) {
                    connectionDAO.deleteConnection(connectionId);
                }
            }
        } else {
            try (SecretsManager sm = new SecretsManager()) {
                // Use "connect" action to create the record because of a race condition with CONNECT.
                // CONNECT does not establish the connection until you return status 200.  You can create
                // the record in the table before the the connection is establed, and the increment handler
                // can remove the record before CONNECT establishes the connection.
                if ("connect".equals(routeKey)) {
                    var connect = new Connect(sm);
                    Connect.Message message = new Connect.Message(
                        event.getRequestContext().getConnectionId()
                    );
                    connect.processMessage(message);

                    try (StatsDAO statsDAO = new StatsDAO(sm)) {
                        int count = statsDAO.getGlobalCounter();

                        String callbackURL = String.format(
                            "https://%s.execute-api.%s.amazonaws.com/%s",
                            event.getRequestContext().getApiId(),
                            System.getenv("AWS_REGION"),
                            event.getRequestContext().getStage()
                        );
                        System.out.println("callbackURL: " + callbackURL);

                        ApiGatewayManagementApiClient client =
                            ApiGatewayManagementApiClient.builder()
                                .endpointOverride(URI.create(callbackURL))
                                .build();

                        data =
                            "{\"action\": \"global\", \"data\": {\"count\": " +
                            count +
                            " }}";
                        PostToConnectionRequest request =
                            PostToConnectionRequest.builder()
                                .connectionId(
                                    event.getRequestContext().getConnectionId()
                                )
                                .data(SdkBytes.fromUtf8String(data))
                                .build();

                        client.postToConnection(request);
                    }
                } else if ("breakblock".equals(routeKey)) {
                    data = "{ \"message\": \"received\" }";

                    var breakBlock = new BreakBlock(sm);
                    //bad will throw null pointer exception
                    breakBlock.processMessage(
                        new BreakBlock.Message(0l, 1, 1, 2, 3)
                    );
                } else {
                    data = "{ \"message\": \"received\" }";
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
