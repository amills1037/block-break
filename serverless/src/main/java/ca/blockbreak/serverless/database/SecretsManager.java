package ca.blockbreak.serverless.database;

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
        return "Development";
    }

    /**
     * Return the region where code is running.
     *
     * @return execution region
     */
    public String getRegion() {
        return "Central";
    }

    /**
     * Close any open resources
     */
    public void close() {
        //noop
    }
}
