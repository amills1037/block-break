package ca.blockbreak.serverless.database;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

/**
 * Unit tests for connection database table.
 */

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public final class ConnectionDAOTest {

    /**
     * For connecting to local dynamoddb.
     export AWS_ACCESS_KEY_ID='DUMMYIDEXAMPLE'
     export AWS_SECRET_ACCESS_KEY='DUMMYEXAMPLEKEY'
     export AWS_REGION="ca-central-1"
     export AWS_ENDPOINT_URL_DYNAMODB=http://external:8000

    aws dynamodb create-table \
        --table-name development-block-break-connection \
        --attribute-definitions \
            AttributeName=ConnectionId,AttributeType=S \
        --key-schema AttributeName=ConnectionId,KeyType=HASH \
        --billing-mode PAY_PER_REQUEST \
        --table-class STANDARD

    aws dynamodb scan \
        --table-name development-block-break-connection \
     */

    @BeforeAll
    public static void beforeAll() {
        final var tableName = "development-block-break-connection";
        final String connectionId = "ConnectionId";

        try (var secrets = new SecretsManager()) {
            try (var dbClient = DynamoDbClient.builder().build()) {
                var createRequest = CreateTableRequest.builder()
                    .tableName(tableName)
                    .keySchema(
                        KeySchemaElement.builder()
                            .attributeName(connectionId)
                            .keyType(KeyType.HASH) // Partition key
                            .build()
                    )
                    .attributeDefinitions(
                        AttributeDefinition.builder()
                            .attributeName(connectionId)
                            .attributeType(ScalarAttributeType.S) // String type
                            .build()
                    )
                    .billingMode(BillingMode.PAY_PER_REQUEST)
                    .build();
                try {
                    dbClient.createTable(createRequest);
                } catch (ResourceInUseException e) {
                    //ignore exception, we just want a table to test with
                }
            }
        }
    }

    /**
     * Test adding a connection, should not error
     */
    @Test
    @Order(1)
    public void shouldAddConnection() {
        try (var secrets = new SecretsManager()) {
            try (var connectionDAO = new ConnectionDAO(secrets)) {
                connectionDAO.addConnection("devel-connection-test");
            }
        }
    }

    /**
     * Test adding a connection, should not error
     */
    @Test
    @Order(1)
    public void shouldAddConnection2() {
        try (var secrets = new SecretsManager()) {
            try (var connectionDAO = new ConnectionDAO(secrets)) {
                connectionDAO.addConnection("devel-connection-test-2");
            }
        }
    }

    /**
     * Test whether a conenection exists in the database, should not error
     */
    @Test
    @Order(2)
    public void shouldConnectionExist() {
        try (var secrets = new SecretsManager()) {
            try (var connectionDAO = new ConnectionDAO(secrets)) {
                boolean exists = connectionDAO.connectionExists(
                    "devel-connection-test"
                );
                assertTrue(exists);
            }
        }
    }

    /**
     * Should return a list of all the connections
     */
    @Test
    @Order(2)
    public void shouldGetConnections() {
        try (var secrets = new SecretsManager()) {
            try (var connectionDAO = new ConnectionDAO(secrets)) {
                List<String> connections = connectionDAO.getConnections();

                assertTrue(connections.size() >= 2);

                assertTrue(connections.contains("devel-connection-test"));
                assertTrue(connections.contains("devel-connection-test-2"));
            }
        }
    }

    /**
     * Should delete connection
     */
    @Test
    @Order(3)
    public void shouldDeleteConnection() {
        try (var secrets = new SecretsManager()) {
            try (var connectionDAO = new ConnectionDAO(secrets)) {
                connectionDAO.deleteConnection("devel-connection-test");
            }
        }
    }

    /**
     * Should delete connection
     */
    @Test
    @Order(3)
    public void shouldDeleteConnection2() {
        try (var secrets = new SecretsManager()) {
            try (var connectionDAO = new ConnectionDAO(secrets)) {
                connectionDAO.deleteConnection("devel-connection-test-2");
            }
        }
    }

    /**
     * Should delete nonexistent connection without error
     */
    @Test
    @Order(3)
    public void shouldDeleteConnectionNonexistent() {
        try (var secrets = new SecretsManager()) {
            try (var connectionDAO = new ConnectionDAO(secrets)) {
                connectionDAO.deleteConnection("devel-connection-test-3");
            }
        }
    }
}
