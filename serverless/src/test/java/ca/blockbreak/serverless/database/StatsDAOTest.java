package ca.blockbreak.serverless.database;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

/**
 * Unit tests for stats database table.
 */
public final class StatsDAOTest {

    /**
     * For connecting to local dynamoddb.
     export AWS_ACCESS_KEY_ID='DUMMYIDEXAMPLE'
     export AWS_SECRET_ACCESS_KEY='DUMMYEXAMPLEKEY'
     export AWS_REGION="ca-central-1"
     export AWS_ENDPOINT_URL_DYNAMODB=http://dynamodb-service:8000

    aws dynamodb create-table \
        --table-name development-block-break-stats \
        --attribute-definitions \
            AttributeName=PlayerId,AttributeType=S \
            AttributeName=StatName,AttributeType=S \
        --key-schema AttributeName=PlayerId,KeyType=HASH AttributeName=StatName,KeyType=RANGE \
        --billing-mode PAY_PER_REQUEST \
        --table-class STANDARD

        aws dynamodb put-item \
            --table-name development-block-break-stats \
            --item '{"PlayerId": {"S": "global"}, "StatName": {"S": "blocksBroken"}, "stat": {"N": "0"}}'

        aws dynamodb get-item \
            --table-name development-block-break-stats \
            --key '{"PlayerId": {"S": "global"}, "StatName": {"S": "blocksBroken"}}'
     */

    @BeforeAll
    public static void beforeAll() {
        final var tableName = "development-block-break-stats";
        final String playerId = "PlayerId";
        final String statName = "StatName";
        final String stat = "Stat";

        final String global = "global";
        final String blocksBroken = "blocksBroken";

        try (var secrets = new SecretsManager()) {
            try (var dbClient = DynamoDbClient.builder().build()) {
                var createRequest = CreateTableRequest.builder()
                    .tableName(tableName)
                    .keySchema(
                        KeySchemaElement.builder()
                            .attributeName(playerId)
                            .keyType(KeyType.HASH) // Partition key
                            .build(),
                        KeySchemaElement.builder()
                            .attributeName(statName)
                            .keyType(KeyType.RANGE) // Sort key
                            .build()
                    )
                    .attributeDefinitions(
                        AttributeDefinition.builder()
                            .attributeName(playerId)
                            .attributeType(ScalarAttributeType.S) // String type
                            .build(),
                        AttributeDefinition.builder()
                            .attributeName(statName)
                            .attributeType(ScalarAttributeType.S)
                            .build()
                    )
                    .billingMode(BillingMode.PAY_PER_REQUEST)
                    .build();
                try {
                    dbClient.createTable(createRequest);
                } catch (ResourceInUseException e) {
                    //ignore exception, we just want a table to test with
                } finally {
                    try {
                        Map<String, AttributeValue> item = new HashMap<>();
                        item.put(
                            playerId,
                            AttributeValue.builder().s(global).build()
                        );
                        item.put(
                            statName,
                            AttributeValue.builder().s(blocksBroken).build()
                        );
                        item.put(stat, AttributeValue.builder().n("0").build());

                        var putRequest = PutItemRequest.builder()
                            .tableName(tableName)
                            .item(item)
                            .build();

                        dbClient.putItem(putRequest);
                    } catch (ResourceInUseException e) {
                        //ignore this error, we just want the item to exists
                    }
                }
            }
        }
    }

    /**
     * Test incrementing the global counter.  Should return a value greater than 0
     */
    @Test
    public void shouldIncrementGlobalCount() {
        try (var secrets = new SecretsManager()) {
            try (var statsDAO = new StatsDAO(secrets)) {
                int globalCount = statsDAO.incrementGlobalCount();
                System.out.println("increment globalCount: " + globalCount);
                assertTrue(globalCount > 0);
            }
        }
    }

    /**
     * Test get the global counter.  Should return a value greater than 0
     */
    @Test
    public void shouldGetGlobalCount() {
        try (var secrets = new SecretsManager()) {
            try (var statsDAO = new StatsDAO(secrets)) {
                int globalCount = statsDAO.getGlobalCount();
                System.out.println("get globalCount: " + globalCount);
                assertTrue(globalCount > 0);
            }
        }
    }

    // public void close() {
    // }

    // public String getTableName(SecretsManager sm) {
    // }
}
