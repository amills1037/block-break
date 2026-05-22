package ca.blockbreak.serverless.database;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
public final class SecretsManagerTest {

    /**
     * Test the stack name, default is "Development"
     */
    @Test
    public void shouldEqualDevelopment() {
        try (var secrets = new SecretsManager()) {
            assertEquals("Development", secrets.getStack());
        }
    }

    /**
     * Test the region name, default is "Central"
     */
    @Test
    public void shouldEqualCentral() {
        try (var secrets = new SecretsManager()) {
            assertEquals("Central", secrets.getRegion());
        }
    }
}
