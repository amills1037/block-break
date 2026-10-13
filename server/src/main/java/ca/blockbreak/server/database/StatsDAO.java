package ca.blockbreak.server.database;

/**
 * StatsDAO interface
 * Interface that all DAOs should implement.  Each database has it's own global count.
 */
public interface StatsDAO extends java.lang.AutoCloseable {

    /**
     * Increment the global count and return the new value.
     */
    public int incrementGlobalCount();

    /**
     * Return the global count and return the new value.
     */
    public int getGlobalCount();

}
