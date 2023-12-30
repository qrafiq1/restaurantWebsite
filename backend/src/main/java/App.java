import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

public class App {
    public static void main(String[] args) throws SQLException {
        // Get a new datasource from the method we defined before
        DataSource dataSource = DatabaseConnector.createConnectionPool();

        System.out.println(dataSource.getConnection());

        // Run a query and get the result
        ResultSet rs = dataSource.getConnection().prepareStatement("select * from User").executeQuery();

        // print the results to the console
        while (rs.next()) {
            System.out
                    .println("Email: " + rs.getString("UserEmail") + " Name: " + rs.getString("FirstName"));
        }
    }
}
