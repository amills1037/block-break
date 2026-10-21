package ca.blockbreak.server.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * PostgreSQLDAO implementation
 * Used access the PostgreSQL database
 */
public final class PostgreSQLDAO implements StatsDAO {
    private static final String GLOBAL_PLAYER_ID = "global";
    private static final String BLOCKS_BROKEN_STAT = "blocksBroken";

    private static final Object lock = new Object();
    private static volatile HikariDataSource dataSource;

    /**
     * Constructor
     *
     * @param sm secrets manager to get the connection parameters
     */
    public PostgreSQLDAO(SecretsManager sm) {
        if (dataSource == null) {
            synchronized(lock) {
                if (dataSource == null) {
                    String host = sm.getPostgreSQLHost();
                    String database = sm.getPostgreSQLDatabase();
                    String username = sm.getPostgreSQLUsername();
                    String password = sm.getPostgreSQLPassword();

                    String url = "jdbc:postgresql://" + host + ":5432/" + database;

                    HikariConfig config = new HikariConfig();
                    config.setJdbcUrl(url);
                    config.setUsername(username);
                    config.setPassword(password);

                    // Set pool properties
                    config.setMaximumPoolSize(10);
                    config.setMinimumIdle(10);
                    config.setConnectionTimeout(30000); // 30 seconds
                    config.setIdleTimeout(300000);      //5 minutes
                    config.setMaxLifetime(570000);      // 9.5 minutes
                    config.setKeepaliveTime(60000);     // 1 minute

                    dataSource = new HikariDataSource(config);
                }
            }
        }

    }

    public int incrementGlobalCount() {
        String query = "update stats set stat = stat + 1, update_date = now() where player_id = ? and stat_name = ? returning stat";

        try (var sqlConnection = dataSource.getConnection()) {
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
        } catch (SQLException sqle) {
            throw new DataAccessException(sqle.getMessage(), sqle);
        }
    }

    public int getGlobalCount() {
        String query = "select stat from stats where player_id=? and stat_name=?";
        try (var sqlConnection = dataSource.getConnection()) {

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
        } catch (SQLException sqle) {
            throw new DataAccessException(sqle.getMessage(), sqle);
        }
    }

    public void close() {
        //noop
    }

    public static void closePool() {
        if (dataSource != null) {
            synchronized(lock) {
                if (dataSource == null) {
                    dataSource.close();
                    dataSource = null;
                }
            }
        }
    }
}
