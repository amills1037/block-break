package ca.blockbreak.serverless.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

/**
 * Connection data access object implementation
 * Used to access WebSocket connection to send messages
 * Implements a wrapper around the connection table.  Underlying implmentation should be indepenedent.
 *
 */
public final class ConnectionDAO implements java.lang.AutoCloseable {

    private static final String TABLE_ROOT_NAME = "block-break-connection";

    private static final String KEY_CONNECTION_ID = "ConnectionId";

    private DynamoDbClient dbClient;

    private String tableName;

    /**
     * Constructor
     *
     * @param sm secrets manager used to create the table name
     *
     */
    public ConnectionDAO(SecretsManager sm) {
        // AWS Region should not be hardcoded
        // Region region = Region.CA_CENTRAL_1;
        dbClient =
            DynamoDbClient.builder()
                // .region(region)
                .build();

        tableName = getTableName(sm);
    }

    /**
     * Add the WebSocket connection id to the connection table
     *
     * @param connectionId conenction id to add
     *
     */
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

    /**
     * Checks if the connection id exists in the connections table
     *
     * @param connectionId connection id to check
     * @return true if the connection id exists in the connection table
     *
     */
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

    /**
     * Remove the connection id from the connection table
     *
     * @param connectionId connection id to remove from the connection table
     *
     */
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

        dbClient.deleteItem(deleteRequest);
    }

    /**
     * Get the connection ids in the connection table
     *
     * @return the list of connectin ids from the connection table
     *
     */
    public List<String> getConnections() {
        var connections = new ArrayList<String>();

        var scanRequest = ScanRequest.builder().tableName(tableName).build();

        ScanResponse response = dbClient.scan(scanRequest);
        for (Map<String, AttributeValue> item : response.items()) {
            AttributeValue value = item.get(KEY_CONNECTION_ID);
            connections.add(value.s());
        }

        return connections;
    }

    /**
     * Close any open resources
     *
     */
    public void close() {
        // close the dynamo db client connection.
        dbClient.close();
    }

    /**
     * Returns the table name by combining the lower case stack name with the table root name
     *
     * @param sm secrets manager used to create the table name
     * @return the table name
     *
     */
    private String getTableName(SecretsManager sm) {
        //Table name depends on the stack and region
        return sm.getStack().toLowerCase() + "-" + TABLE_ROOT_NAME;
    }
}
