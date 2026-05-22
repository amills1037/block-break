package ca.blockbreak.serverless.database;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
public final class StatsDAOTest {

    /**
     * For connecting to local dynamoddb.
     export AWS_ACCESS_KEY_ID='DUMMYIDEXAMPLE'
     export AWS_SECRET_ACCESS_KEY='DUMMYEXAMPLEKEY'
     export AWS_REGION="ca-central-1"
     export AWS_ENDPOINT_URL_DYNAMODB=http://external:8000

    aws dynamodb create-table \
        --table-name DevelopmentServerlessStatsCentral \
        --attribute-definitions \
            AttributeName=PlayerId,AttributeType=S \
            AttributeName=StatName,AttributeType=S \
        --key-schema AttributeName=PlayerId,KeyType=HASH AttributeName=StatName,KeyType=RANGE \
        --billing-mode PAY_PER_REQUEST \
        --table-class STANDARD

        aws dynamodb put-item \
            --table-name DevelopmentServerlessStatsCentral \
            --item '{"PlayerId": {"S": "global"}, "StatName": {"S": "blocksBroken"}, "stat": {"N": "0"}}'

        aws dynamodb get-item \
            --table-name DevelopmentServerlessStatsCentral \
            --key '{"PlayerId": {"S": "global"}, "StatName": {"S": "blocksBroken"}}'
     */

    /**
     * Test incrementing the global counter.  Should return a value greater than 0
     */
    @Test
    public void shouldAnswerWithTrue() {
        try (var secrets = new SecretsManager()) {
            try (var statsDAO = new StatsDAO(secrets)) {
                assertTrue(statsDAO.incrementGlobalCounter() > 0);
            }
        }
    }
}
