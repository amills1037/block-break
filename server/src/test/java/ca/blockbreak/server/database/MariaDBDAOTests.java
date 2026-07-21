package ca.blockbreak.server.database;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MariaDBDAOTests {

	/**
     * Test incrementing the global counter.  Should return a value greater than 0
     */
    @Test
    public void shouldIncrementGlobalCount() {
        try (var secrets = new SecretsManager()) {
            try (var mariaDBDAO = new MariaDBDAO(secrets)) {
                int globalCount = mariaDBDAO.incrementGlobalCount();
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
            try (var mariaDBDAO = new MariaDBDAO(secrets)) {
                int globalCount = mariaDBDAO.getGlobalCount();
                System.out.println("get globalCount: " + globalCount);
                assertTrue(globalCount > 0);
            }
        }
    }
}
