package ca.blockbreak.server.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mariadb.jdbc.MariaDbPoolDataSource;

public final class MariaDBDAO implements StatsDAO {
    private static final String GLOBAL_PLAYER_ID = "global";
    private static final String BLOCKS_BROKEN_STAT = "blocksBroken";

    private static final Object lock = new Object();
    private static volatile MariaDbPoolDataSource dataSource;

    private Connection sqlConnection;

    public MariaDBDAO(SecretsManager sm) {
        if (dataSource == null) {
            synchronized(lock) {
                if (dataSource == null) {
                    try {
                        String host = sm.getMariaDBHost();
                        String database = sm.getMariaDBDatabase();
                        String username = sm.getMariaDBUsername();
                        String password = sm.getMariaDBPassword();

                        //Should consider using Apache Commons DBCP
                        String url = "jdbc:mariadb://" + host + ":3306/" + database + "?user=" + username + "&password=" + password + "&maxPoolSize=5";
                        dataSource = new MariaDbPoolDataSource(url);

                        //Setting the username and password after setting the url will cause the pool to log errors, they are not actual errors
                        // String url = "jdbc:mariadb://" + host + ":3306/" + database + "?maxPoolSize=5";
                        // dataSource = new MariaDbPoolDataSource(url);
                        // dataSource.setUser(username);
                        // dataSource.setPassword(password);

                    } catch (SQLException sqle) {
                        throw new DataAccessException(sqle.getMessage(), sqle);
                    }
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
        //no support for returning until MariaDB >= 13.0
        String query = "update stats set stat = stat + 1, update_date = now() where player_id = ? and stat_name = ?";

        try (var ps = sqlConnection.prepareStatement(query)) {
           ps.setString(1, GLOBAL_PLAYER_ID);
           ps.setString(2, BLOCKS_BROKEN_STAT);

           int rowsAffected = ps.executeUpdate();

           if (rowsAffected == 0) {
               throw new DataAccessException("incrementGlobalCount did not increment stat");
           }

           //This does not return the value of stat from this update, but the current value when run
           return getGlobalCount();
        } catch (SQLException sqle) {
            throw new DataAccessException(sqle.getMessage(), sqle);
        }
    }

    public int getGlobalCount() {
        String query = "select stat from stats where player_id=? and stat_name=?";

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
        //Should we syncronize?
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
    }
}
