package ca.blockbreak.serverless;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;

// import software.amazon.awssdk.core.SdkBytes;
// import software.amazon.awssdk.services.apigatewaymanagementapi.ApiGatewayManagementApiClient;
// import software.amazon.awssdk.services.apigatewaymanagementapi.model.GoneException;
// import software.amazon.awssdk.services.apigatewaymanagementapi.model.PostToConnectionRequest;

public class GlobalIncrementHandler
    implements RequestHandler<DynamodbEvent, String>
{

    @Override
    public String handleRequest(DynamodbEvent ddbEvent, Context context) {
        for (DynamodbStreamRecord record : ddbEvent.getRecords()) {
            if ("MODIFY".equals(record.getEventName())) {
                System.out.println(
                    "record.getEventID(): " + record.getEventID()
                );

                var oldImage = record.getDynamodb().getOldImage();
                var newImage = record.getDynamodb().getNewImage();

                System.out.println("Old item state: " + oldImage.toString());
                System.out.println("New item state: " + newImage.toString());
            }
        }

        return "Processed " + ddbEvent.getRecords().size() + " records.";
    }
}

// //Get list of connections and send
// //https://{api-id}.execute-api.{region}.amazonaws.com/{stage}/@connections/{connection_id}
// String callbackURL = String.format(
//     "https://%s.execute-api.%s.amazonaws.com/%s",
//     event.getRequestContext().getApiId(),
//     System.getenv("AWS_REGION"),
//     event.getRequestContext().getStage()
// );
// // System.out.println("callbackURL: " + callbackURL);

// ApiGatewayManagementApiClient client =
//     ApiGatewayManagementApiClient.builder()
//         .endpointOverride(URI.create(callbackURL))
//         .build();

// String data;
// if ("CONNECT".equals(eventType)) {
//     data = "{ \"message\": \"Connected\" }";
// } else if ("DISCONNECT".equals(eventType)) {
//     data = "{ \"message\": \"Disconnected\" }";
// } else {
//     // if () {

//     // }

//     data = "{ \"message\": \"Message received\" }";
// }
// System.out.println("data: " + data);
// String connectionId = event.getRequestContext().getConnectionId();
// System.out.println("requestContext.getConnectionId(): " + connectionId);

// PostToConnectionRequest request = PostToConnectionRequest.builder()
//     .connectionId(event.getRequestContext().getConnectionId())
//     .data(SdkBytes.fromUtf8String(data))
//     .build();

// try {
//     client.postToConnection(request);
// } catch (GoneException ge) {
//     System.out.println("GoneException: " + ge);
// }

// try (StatsDAO stateDAO = new StatsDAO(secretManager)) {
//     int globalCounter = statsDAO.incrementGlobalCounter();

// }
