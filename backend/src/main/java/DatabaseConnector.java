import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.net.SocketFactory;

public class DatabaseConnector {

    private static final String DB_HOST = "restaurant-407220:us-central1:restaurant-database";
    private static final String DB_NAME = "restaurant";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "restaurant";

    public static void main(String[] args) {

        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM User");

            while (resultSet.next()) {
                String columnValue = resultSet.getString("UserEmail");
                System.out.println("Value from column_name: " + columnValue);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Connection getConnection() throws SQLException {
        String jdbcUrl = String.format("jdbc:mysql://google/%s?cloudSqlInstance=%s&socketFactory=%s",
                DB_NAME, DB_HOST, SocketFactory.class.getName());

        return DriverManager.getConnection(jdbcUrl, DB_USER, DB_PASSWORD);
    }
}
