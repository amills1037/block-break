package ca.blockbreak.server.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.postgresql.ds.PGConnectionPoolDataSource;

public final class PostgreSQLDAO implements StatsDAO {
    private static final String GLOBAL_PLAYER_ID = "global";
    private static final String BLOCKS_BROKEN_STAT = "blocksBroken";

    private static final Object lock = new Object();
    private static volatile PGConnectionPoolDataSource dataSource;

    private Connection sqlConnection;

    public PostgreSQLDAO(SecretsManager sm) {
        if (dataSource == null) {
            synchronized(lock) {
                if (dataSource == null) {
                    String host = sm.getPostgreSQLHost();
                    String database = sm.getPostgreSQLDatabase();
                    String username = sm.getPostgreSQLUsername();
                    String password = sm.getPostgreSQLPassword();

                    //Should consider using Apache Commons DBCP
                    String url = "jdbc:postgresql://" + host + ":5432/" + database + "?maxPoolSize=5";
                    dataSource = new PGConnectionPoolDataSource();
                    dataSource.setURL(url);
                    dataSource.setUser(username);
                    dataSource.setPassword(password);
                }
            }
        }

        try {
            sqlConnection = dataSource.getConnection();
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

    public static void closePool() {
        //NOOP
        // No close for the data source
    }
}
