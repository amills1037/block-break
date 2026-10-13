package ca.blockbreak.serverless;

import ca.blockbreak.serverless.database.ConnectionDAO;
import ca.blockbreak.serverless.database.SecretsManager;

/**
 * Connect message implementation
 * Processes connect messages
 */
public class Connect {

    private SecretsManager secretManager;

    public record Message(String connectionId) {}

    /**
     * Constructor
     *
     * @param sm secrets manager used to create the the DAO
     */
    public Connect(SecretsManager sm) {
        secretManager = sm;
    }

    /**
     * Adds connection id to the connection table
     *
     * @param message message to process
     */
    public void processMessage(Message message) {
        System.out.print("connect: " + message.connectionId);

        try (ConnectionDAO connectionDAO = new ConnectionDAO(secretManager)) {
            connectionDAO.addConnection(message.connectionId);
        }
    }
}
