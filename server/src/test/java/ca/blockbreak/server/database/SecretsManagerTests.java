package ca.blockbreak.server.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SecretsManagerTests {


    @Test
    public void shouldGetStack() {
        try (SecretsManager sm = new SecretsManager()) {
            assertEquals("Development", sm.getStack());
        }
    }

    /**
     * Return the region where code is running.
     *
     * @return execution region
     */
     @Test
    public void shouldGetRegion() {
        try (SecretsManager sm = new SecretsManager()) {
            assertEquals("Central", sm.getRegion());
        }
    }

    @Test
    public void shouldGetMariaDBHost() {
        try (SecretsManager sm = new SecretsManager()) {
            assertEquals("10.214.0.3", sm.getMariaDBHost());
        }
    }

    @Test
    public void shouldGetMariaDBDatabase() {
        try (SecretsManager sm = new SecretsManager()) {
            assertEquals("block_break_dev", sm.getMariaDBDatabase());
        }
    }

    @Test
    public void shouldGetMariaDBUsername() {
        try (SecretsManager sm = new SecretsManager()) {
            assertEquals("root", sm.getMariaDBUsername());
        }
    }

    @Test
    public void shouldGetMariaDBPassword() {
        try (SecretsManager sm = new SecretsManager()) {
            assertNotNull(sm.getMariaDBPassword());
        }
    }

    @Test
    public void shouldGetPostgreSQLHost() {
        try (SecretsManager sm = new SecretsManager()) {
            assertEquals("10.214.0.5", sm.getPostgreSQLHost());
        }
    }

    @Test
    public void shouldGetPostgreSQLDatabase() {
        try (SecretsManager sm = new SecretsManager()) {
            assertEquals("block_break_dev", sm.getPostgreSQLDatabase());
        }
    }

    @Test
    public void shouldGetPostgreSQLUsername() {
        try (SecretsManager sm = new SecretsManager()) {
            assertEquals("postgres", sm.getPostgreSQLUsername());
        }
    }

    @Test
    public void shouldGetPostgreSQLPassword() {
        try (SecretsManager sm = new SecretsManager()) {
            assertNotNull(sm.getPostgreSQLPassword());
        }
    }

    /**
     * Should close any open resources
     */
     @Test
    public void shoulClose() {
        //noop
    }

}
