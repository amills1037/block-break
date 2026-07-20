package ca.blockbreak.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class PostgreSQLDAO implements StatsDAO {
    private static final String GLOBAL_PLAYER_ID = "global";
    private static final String BLOCKS_BROKEN_STAT = "blocksBroken";

    private Connection sqlConnection;

    public PostgreSQLDAO(SecretsManager sm) {
        String host = sm.getPostgreSQLHost();
        String database = sm.getPostgreSQLDatabase();

        String url = "jdbc:postgresql://" + host + ":5432/" + database;
        String username = sm.getPostgreSQLUsername();
        String password = sm.getPostgreSQLPassword();

        try {
            sqlConnection = DriverManager.getConnection(url, username, password);
        } catch (SQLException sqle) {
            throw new DataAccessException(sqle.getMessage(), sqle);
        }
    }

    public int incrementGlobalCount() {
        String query = "update stats set stat = stat + 1, update_date = now() where player_id = ? and stat_name = ? returning stat";

        try (var ps = sqlConnection.prepareStatement(query)) {
           ps.setString(1, GLOBAL_PLAYER_ID);
           ps.setString(2, BLOCKS_BROKEN_STAT);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int count = rs.getInt(1);

                return count;
            } else {
                throw new DataAccessException("getGlobalCount did not return a value");
            }
        } catch (SQLException sqle) {
            throw new DataAccessException(sqle.getMessage(), sqle);
        }

        } catch (SQLException sqle) {
            throw new DataAccessException(sqle.getMessage(), sqle);
        }
    }

    public int getGlobalCount() {
        String query = "select stat from stats where player_id=? and stat_name=?";

        try (var ps = sqlConnection.prepareStatement(query)) {
           ps.setString(1, "global");
           ps.setString(2, BLOCKS_BROKEN_STAT);

           try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);

                    return count;
                } else {
                    throw new DataAccessException("getGlobalCount did not return a value");
                }
           } catch (SQLException sqle) {
               throw new DataAccessException(sqle.getMessage(), sqle);
           }
        } catch (SQLException sqle) {
            throw new DataAccessException(sqle.getMessage(), sqle);
        }
    }

    public void close() {
        if (sqlConnection != null) {
            try {
                sqlConnection.close();
            } catch (SQLException sqle) {
                throw new DataAccessException(sqle.getMessage(), sqle);
            }
        }
    }
}
