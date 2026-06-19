package ca.blockbreak.serverless;

import ca.blockbreak.serverless.database.ConnectionDAO;
import ca.blockbreak.serverless.database.SecretsManager;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;
import java.net.URI;
import java.util.List;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.apigatewaymanagementapi.ApiGatewayManagementApiClient;
import software.amazon.awssdk.services.apigatewaymanagementapi.model.GoneException;
import software.amazon.awssdk.services.apigatewaymanagementapi.model.PostToConnectionRequest;

public class GlobalIncrementHandler
    implements RequestHandler<DynamodbEvent, String>
{

    @Override
    public String handleRequest(DynamodbEvent ddbEvent, Context context) {
        try (SecretsManager secretsManager = new SecretsManager()) {
            try (
                ConnectionDAO connectionDAO = new ConnectionDAO(secretsManager)
            ) {
                List<String> connections = connectionDAO.getConnections();

                String callbackURL = System.getenv("CONNECTION_URL");
                ApiGatewayManagementApiClient client =
                    ApiGatewayManagementApiClient.builder()
                        .endpointOverride(URI.create(callbackURL))
                        .build();

                for (DynamodbStreamRecord record : ddbEvent.getRecords()) {
                    if ("MODIFY".equals(record.getEventName())) {
                        System.out.println(
                            "record.getEventID(): " + record.getEventID()
                        );

                        var oldImage = record.getDynamodb().getOldImage();
                        var newImage = record.getDynamodb().getNewImage();
                        System.out.println("oldImage: " + oldImage.toString());
                        System.out.println("newImage: " + newImage.toString());

                        int count = Integer.parseInt(
                            newImage.get("Stat").getN()
                        );

                        String data =
                            "{\"action\": \"global\", \"data\": { \"count\": " +
                            count +
                            " }}";

                        for (String connectionId : connections) {
                            PostToConnectionRequest request =
                                PostToConnectionRequest.builder()
                                    .connectionId(connectionId)
                                    .data(SdkBytes.fromUtf8String(data))
                                    .build();

                            try {
                                client.postToConnection(request);
                            } catch (GoneException ge) {
                                System.out.println("GoneException: " + ge);
                                connectionDAO.deleteConnection(connectionId);
                            }
                        }
                    }
                }
            }
        }

        return "Processed " + ddbEvent.getRecords().size() + " records.";
    }
}
