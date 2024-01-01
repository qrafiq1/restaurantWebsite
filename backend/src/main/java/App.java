import spark.Spark;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.sql.DataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) throws IOException, SQLException {

        Spark.port(getPort());

        // Set up database connection pool
        DataSource dataSource = DatabaseConnector.createConnectionPool();

        // Enable CORS for all routes
        Spark.options("/*",
                (request, response) -> {

                    String accessControlRequestHeaders = request
                            .headers("Access-Control-Request-Headers");
                    if (accessControlRequestHeaders != null) {
                        response.header("Access-Control-Allow-Headers",
                                accessControlRequestHeaders);
                    }

                    String accessControlRequestMethod = request
                            .headers("Access-Control-Request-Method");
                    if (accessControlRequestMethod != null) {
                        response.header("Access-Control-Allow-Methods",
                                accessControlRequestMethod);
                    }

                    return "OK";
                });

        Spark.before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));

        // Define routes
        Spark.get("/hello", (req, res) -> "Hello, World!");

        Spark.post("/query", (req, res) -> {
            try (Connection connection = dataSource.getConnection()) {
                JsonObject jsonBody = new Gson().fromJson(req.body(), JsonObject.class);
                String location = jsonBody.get("location").getAsString();
                String group = jsonBody.get("group").getAsString();

                String sqlQuery = "SELECT b.BookingTime " +
                        "FROM Booking b " +
                        "JOIN Restaurant r ON b.RestaurantID = r.RestaurantID " +
                        "WHERE r.RestaurantName = '" + location + "' AND b.isAvailable = 1";

                // Use the connection to query the database
                PreparedStatement statement = connection.prepareStatement(sqlQuery);

                System.out.println(statement.toString());

                ResultSet resultSet = statement.executeQuery();

                List<String> resultList = new ArrayList<>();
                while (resultSet.next()) {
                    System.out.println(resultSet.getString("BookingTime"));
                    resultList.add(resultSet.getString("BookingTime"));
                }

                res.type("application/json");
                return new Gson().toJson(resultList);
            } catch (SQLException e) {
                e.printStackTrace();
                res.status(500);
                return "Internal Server Error";
            }
        });

        Spark.post("/submitBooking", (req, res) -> {
            try (Connection connection = dataSource.getConnection()) {
                String location = req.queryParams("location");
                String group = req.queryParams("group");
                String date = req.queryParams("date");
                String email = req.queryParams("email");
                String firstName = req.queryParams("firstName");
                String lastName = req.queryParams("lastName");

                // Use the connection to perform the booking operation
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM User");
                ResultSet resultSet = statement.executeQuery();

                // Process the result set

                // Return a JSON response (example)
                return new Gson().toJson("Booking submitted!");
            } catch (SQLException e) {
                e.printStackTrace();
                res.status(500);
                return "Internal Server Error";
            }
        });
    }

    private static int getPort() {
        // For local testing, use a specific port
        // For App Engine deployment, use the PORT environment variable
        String portStr = System.getenv("PORT");
        return (portStr != null) ? Integer.parseInt(portStr) : 8080;
    }
}