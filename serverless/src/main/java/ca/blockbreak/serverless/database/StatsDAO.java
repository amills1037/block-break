package ca.blockbreak.serverless.database;

import java.util.HashMap;
import java.util.Map;
// import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ReturnValue;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemResponse;

/**
 * Implements a wrapper around the stats database.  Underlying implmentation should be indepenedent.
 *
 * Need to add item to dynamoddb table.  Should use a custom resource to put the item in the database.
 *   aws dynamodb put-item \
 *    --table-name ProductionServerlessStatsCentral \
 *    --item '{"PlayerId": {"S": "global"}, "StatName": {"S": "blocksBroken"}, "stat": {"N": "0"}}'
 *
 */
public final class StatsDAO implements java.lang.AutoCloseable {

    private static final String TABLE_ROOT_NAME = "ServerlessStats";

    private static final String KEY_PLAYER_ID = "PlayerId";
    private static final String KEY_STAT_NAME = "StatName";
    private static final String KEY_STAT = "Stat";

    private static final String GLOBAL_PLAYER_ID = "global";
    private static final String BLOCKS_BROKEN_STAT = "blocksBroken";

    private static final String UPDATE_INCREMENT_EXP =
        "SET " + KEY_STAT + " = " + KEY_STAT + " + :inc";

    private DynamoDbClient dbClient;

    private String tableName;

    public StatsDAO(SecretsManager sm) {
        // AWS Region should not be hardcoded
        // Region region = Region.CA_CENTRAL_1;
        dbClient =
            DynamoDbClient.builder()
                // .region(region)
                .build();

        tableName = getTableName(sm);
    }

    public int incrementGlobalCounter() {
        Map<String, AttributeValue> primaryKey = new HashMap<>();
        primaryKey.put(
            KEY_PLAYER_ID,
            AttributeValue.builder().s(GLOBAL_PLAYER_ID).build()
        );
        primaryKey.put(
            KEY_STAT_NAME,
            AttributeValue.builder().s(BLOCKS_BROKEN_STAT).build()
        );

        // Define expression attribute values (:inc is the increment amount)
        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":inc", AttributeValue.builder().n("1").build());

        UpdateItemRequest request = UpdateItemRequest.builder()
            .tableName(tableName)
            .key(primaryKey)
            // Use SET to increment: stat + :inc
            .updateExpression(UPDATE_INCREMENT_EXP)
            .expressionAttributeValues(expressionValues)
            .returnValues(ReturnValue.UPDATED_NEW) // Returns the new value
            .build();

        UpdateItemResponse response = dbClient.updateItem(request);
        String stat = response.attributes().get(KEY_STAT).n();
        return Integer.parseInt(stat);
    }

    /**
     * Close any open resources
     */

    public void close() {
        // close the dynamo db client connection.
        dbClient.close();
    }

    public void initDatabase() {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(
            KEY_PLAYER_ID,
            AttributeValue.builder().s(GLOBAL_PLAYER_ID).build()
        );
        item.put(
            KEY_STAT_NAME,
            AttributeValue.builder().s(BLOCKS_BROKEN_STAT).build()
        );
        item.put(KEY_STAT, AttributeValue.builder().n("0").build());

        var putRequest = PutItemRequest.builder()
            .tableName(tableName)
            .item(item)
            .build();

        dbClient.putItem(putRequest);
    }

    public String getTableName(SecretsManager sm) {
        //Table name depends on the stack and region
        return sm.getStack() + TABLE_ROOT_NAME + sm.getRegion();
    }
}
