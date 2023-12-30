import javax.sql.DataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseConnector {

    private static final String INSTANCE_CONNECTION_NAME = "restaurant-407220:us-central1:restaurant-database";
    private static final String DB_USER = "client";
    private static final String DB_PASS = "password";
    private static final String DB_NAME = "restaurant";

    public static DataSource createConnectionPool() {
        // create a new configuration and set the database credentials
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:mysql:///%s", DB_NAME));
        config.setUsername(DB_USER);
        config.setPassword(DB_PASS);
        config.addDataSourceProperty("socketFactory", "com.google.cloud.sql.mysql.SocketFactory");
        config.addDataSourceProperty("cloudSqlInstance", INSTANCE_CONNECTION_NAME);

        // Initialize the connection pool using the configuration object.
        return new HikariDataSource(config);
    }
}