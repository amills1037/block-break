package ca.blockbreak.serverless.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

/**
 * Implements a wrapper around the connection table.  Underlying implmentation should be indepenedent.
 *
 */
public final class ConnectionDAO implements java.lang.AutoCloseable {

    private static final String TABLE_ROOT_NAME = "block-break-connection";

    private static final String KEY_CONNECTION_ID = "ConnectionId";

    private DynamoDbClient dbClient;

    private String tableName;

    public ConnectionDAO(SecretsManager sm) {
        // AWS Region should not be hardcoded
        // Region region = Region.CA_CENTRAL_1;
        dbClient =
            DynamoDbClient.builder()
                // .region(region)
                .build();

        tableName = getTableName(sm);
    }

    public void addConnection(String connectionId) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(
            KEY_CONNECTION_ID,
            AttributeValue.builder().s(connectionId).build()
        );

        var putRequest = PutItemRequest.builder()
            .tableName(tableName)
            .item(item)
            .build();

        dbClient.putItem(putRequest);
    }

    public boolean connectionExists(String connectionId) {
        Map<String, AttributeValue> primaryKey = new HashMap<>();
        primaryKey.put(
            KEY_CONNECTION_ID,
            AttributeValue.builder().s(connectionId).build()
        );

        GetItemRequest request = GetItemRequest.builder()
            .tableName(tableName)
            .key(primaryKey)
            .build();

        GetItemResponse response = dbClient.getItem(request);

        // System.err.println("response~~~~~~~~~~~");
        // System.err.println(response.item());
        // System.err.println("~~~~~~~~~~~response");

        return response.hasItem();
    }

    public void deleteConnection(String connectionId) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(
            KEY_CONNECTION_ID,
            AttributeValue.builder().s(connectionId).build()
        );

        var deleteRequest = DeleteItemRequest.builder()
            .tableName(tableName)
            .key(item)
            .build();

        var response = dbClient.deleteItem(deleteRequest);
    }

    public List<String> getConnections() {
        var connections = new ArrayList<String>();

        var scanRequest = ScanRequest.builder().tableName(tableName).build();

        ScanResponse response = dbClient.scan(scanRequest);
        for (Map<String, AttributeValue> item : response.items()) {
            AttributeValue value = item.get(KEY_CONNECTION_ID);

            System.err.println("value~~~~~~~~~~~");
            System.err.println(value.s());
            System.err.println("~~~~~~~~~~~value");

            connections.add(value.s());
        }

        return connections;
    }

    /**
     * Close any open resources
     */

    public void close() {
        // close the dynamo db client connection.
        dbClient.close();
    }

    private String getTableName(SecretsManager sm) {
        //Table name depends on the stack and region
        return sm.getStack().toLowerCase() + "-" + TABLE_ROOT_NAME;
    }
}
