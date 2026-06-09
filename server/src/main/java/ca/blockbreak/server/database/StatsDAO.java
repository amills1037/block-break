package ca.blockbreak.server.database;

public interface StatsDAO extends java.lang.AutoCloseable {

    public int incrementGlobalCounter();

}
