package ca.blockbreak.server.service;

import org.springframework.stereotype.Service;

import ca.blockbreak.server.database.MariaDBDAO;
import ca.blockbreak.server.database.MongoDBDAO;
import ca.blockbreak.server.database.PostgreSQLDAO;
import ca.blockbreak.server.database.SecretsManager;

/**
 * DatabaseService implementation
 * A service to acess all the different databases.  Using a service instead of a factory because the code is simpler.
 */
@Service
public final class DatabaseService implements AutoCloseable {
    /**
     * Enumeration of the databases
     */
    public enum Database {
        MARIADB,
        POSTGRESQL,
        MONGODB
    }

    private SecretsManager secretsManager;

    private MariaDBDAO mariaDBDAO;
    private MongoDBDAO mongoDBDAO;
    private PostgreSQLDAO postgreSQLDAO;

    /**
     * Constructor
     * Creates a service object.  Only one service object should be created per application.
     */
    public DatabaseService() {
        secretsManager = new SecretsManager();

        mariaDBDAO = new MariaDBDAO(secretsManager);
        mongoDBDAO = new MongoDBDAO(secretsManager);
        postgreSQLDAO = new PostgreSQLDAO(secretsManager);

    }

    /**
     * Increment the global counter from a specific database.
     *
     * @param db database to use to increment
     */
    public int incrementGlobalCount(Database db) {
        int count = -1;

        switch (db) {
            case Database.MARIADB:
                count = mariaDBDAO.incrementGlobalCount();
                break;
            case Database.MONGODB:
                count = mongoDBDAO.incrementGlobalCount();
                break;
            case Database.POSTGRESQL:
                count = postgreSQLDAO.incrementGlobalCount();
                break;
        }

        return count;
    }

    /**
     * Get the global counter from a specific database.
     *
     * @param db database to use to increment
     */
    public int getGlobalCount(Database db) {
        int count = -1;
        switch (db) {
            case Database.MARIADB:
                count = mariaDBDAO.getGlobalCount();
                break;
            case Database.MONGODB:
                count = mongoDBDAO.getGlobalCount();
                break;
            case Database.POSTGRESQL:
                count = postgreSQLDAO.getGlobalCount();
                break;
        }

        return count;
    }

    /**
     * Close all the databases.
     */
    @Override
    public void close() {
        try {
            mariaDBDAO.close();
        } finally {
            try {
                mongoDBDAO.close();
            } finally {
                try {
                    postgreSQLDAO.close();
                } finally {
                    secretsManager.close();
                }
            }
        }
    }
}
