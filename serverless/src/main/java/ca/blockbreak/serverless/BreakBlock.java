package ca.blockbreak.serverless;

import ca.blockbreak.serverless.database.SecretsManager;
import ca.blockbreak.serverless.database.StatsDAO;

/**
 * BreakBlock message implementation.
 * Processes break block messages.
 */
public class BreakBlock {

    private SecretsManager secretManager;

    public record Message(long timeSinceEpoc, int type, int x, int y, int z) {}

    /**
     * Constructor
     *
     * @param sm secrets manager used to create the the DAO
     */
    public BreakBlock(SecretsManager sm) {
        secretManager = sm;
    }

    /**
     * Increments the global counter.
     *
     * @param m message to process
     */
    public void processMessage(Message m) {
        System.out.print("Break block: (" + m.x + "," + m.y + "," + m.z + ")");

        try (StatsDAO statsDAO = new StatsDAO(secretManager)) {
            statsDAO.incrementGlobalCount();
        }
    }
}
