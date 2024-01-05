import javax.sql.DataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseConnector {

    // Cloud SQL instance connection details
    private static final String INSTANCE_CONNECTION_NAME = "restaurant-407220:us-central1:restaurant-database";
    private static final String DB_USER = "client";
    private static final String DB_PASS = "password";
    private static final String DB_NAME = "restaurant";

    /**
     * Creates and returns a connection pool using HikariCP with the specified
     * database credentials and Cloud SQL instance connection details.
     *
     * @return DataSource representing the connection pool.
     */
    public static DataSource createConnectionPool() {
        // Create a new HikariCP configuration
        HikariConfig config = new HikariConfig();

        // Set the JDBC URL, username, and password
        config.setJdbcUrl(String.format("jdbc:mysql:///%s", DB_NAME));
        config.setUsername(DB_USER);
        config.setPassword(DB_PASS);

        // Add additional properties for Cloud SQL connection
        config.addDataSourceProperty("socketFactory", "com.google.cloud.sql.mysql.SocketFactory");
        config.addDataSourceProperty("cloudSqlInstance", INSTANCE_CONNECTION_NAME);

        // Initialize the connection pool using the configuration object
        return new HikariDataSource(config);
    }
}
