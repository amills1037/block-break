package ca.blockbreak.serverless;

import ca.blockbreak.serverless.database.ConnectionDAO;
import ca.blockbreak.serverless.database.SecretsManager;

public class Connect {

    private SecretsManager secretManager;

    public record Message(String connectionId) {}

    public Connect(SecretsManager sm) {
        secretManager = sm;
    }

    public void processMessage(Message message) {
        System.out.print("connect: " + message.connectionId);

        try (ConnectionDAO connectionDAO = new ConnectionDAO(secretManager)) {
            connectionDAO.addConnection(message.connectionId);
        }
    }
}
