package ca.blockbreak.server.database;

import java.util.Objects;

/**
 * Manager for accessing secret and enviornment variables.  Values should not change after first use.
 *
 */
public final class SecretsManager implements AutoCloseable {

    /**
     * Return the software stack:  "Development", "Sandbox" or "Production".
     *
     * @return softwre stack
     */
    public String getStack() {
        return Objects.requireNonNullElse(System.getenv("STACK"), "Development");
    }

    /**
     * Return the region where code is running.
     *
     * @return execution region
     */
    public String getRegion() {
        return "Central";
    }

    public String getMariaDBHost() {
        return "10.214.0.12";
    }

    public String getMariaDBDatabase() {
        return "block_break_dev";
    }

    public String getMariaDBUsername() {
        return "root";
    }

    public String getMariaDBPassword() {
        return System.getenv("MARIADB_PASSWORD");
    }

    public String getMongoDBHost() {
        return "10.214.0.14";
    }

    public String getMongoDBDatabase() {
        return "block_break_dev";
    }

    public String getMongoDBUsername() {
        return "root";
    }

    public String getMongoDBPassword() {
        return System.getenv("MONGODB_PASSWORD");
    }

    public String getPostgreSQLHost() {
        return "10.214.0.13";
    }

    public String getPostgreSQLDatabase() {
        return "block_break_dev";
    }

    public String getPostgreSQLUsername() {
        return "postgres";
    }

    public String getPostgreSQLPassword() {
        return System.getenv("POSTGRES_PASSWORD");
    }

    /**
     * Close any open resources
     */
    public void close() {
        //noop
    }
}
