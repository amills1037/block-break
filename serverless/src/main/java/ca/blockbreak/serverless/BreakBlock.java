package ca.blockbreak.serverless;

import ca.blockbreak.serverless.database.SecretsManager;
import ca.blockbreak.serverless.database.StatsDAO;

public class BreakBlock {

    private SecretsManager secretManager;

    public record Message(long timeSinceEpoc, int type, int x, int y, int z) {}

    public BreakBlock(SecretsManager sm) {
        secretManager = sm;
    }

    public void processMessage(Message m) {
        System.out.print("Break block: (" + m.x + "," + m.y + "," + m.z + ")");

        try (StatsDAO statsDAO = new StatsDAO(secretManager)) {
            statsDAO.incrementGlobalCounter();
        }
    }
}
