import spark.Spark;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.sql.DataSource;

import java.sql.Statement;
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
                JsonObject jsonBody = new Gson().fromJson(req.body(), JsonObject.class);

                String location = jsonBody.get("location").getAsString();
                String date = jsonBody.get("date").getAsString();
                String email = jsonBody.get("email").getAsString();
                String firstName = jsonBody.get("firstName").getAsString();
                String lastName = jsonBody.get("lastName").getAsString();

                int userID = 0;

                String bookingUpdateQuery = "UPDATE Booking b JOIN Restaurant r ON b.restaurantID = r.restaurantID SET UserID = ?, isAvailable = 0 WHERE BookingTime = ? AND isAvailable = 1 AND restaurantName = ?;";
                String checkUserQuery = "SELECT * FROM User WHERE UserEmail = ?";
                String addUserQuery = "INSERT INTO User (FirstName, LastName, UserEmail) VALUES (?, ?, ?)";

                try (PreparedStatement checkUserStatement = connection.prepareStatement(checkUserQuery)) {
                    checkUserStatement.setString(1, email);
                    ResultSet resultSet = checkUserStatement.executeQuery();

                    if (resultSet.next()) {
                        System.out.println("User exists");
                        userID = resultSet.getInt("UserID");
                        System.out.println("existing user ID: " + userID);
                    } else {
                        // Add user if not exists
                        try (PreparedStatement addUserStatement = connection.prepareStatement(addUserQuery,
                                Statement.RETURN_GENERATED_KEYS)) {
                            addUserStatement.setString(1, firstName);
                            addUserStatement.setString(2, lastName);
                            addUserStatement.setString(3, email);

                            // gets new users ID
                            int affectedRows = addUserStatement.executeUpdate();

                            if (affectedRows > 0) {
                                // Retrieve the generated keys
                                try (ResultSet generatedKeys = addUserStatement.getGeneratedKeys()) {
                                    if (generatedKeys.next()) {
                                        userID = generatedKeys.getInt(1);
                                        System.out.println("New user added with UserID: " + userID);
                                    } else {
                                        System.out.println("Error retrieving UserID after insertion.");
                                    }
                                }
                            } else {
                                System.out.println("No rows were affected by the insertion.");
                            }
                        }
                    }
                }

                try (PreparedStatement addBooking = connection.prepareStatement(bookingUpdateQuery)) {
                    addBooking.setInt(1, userID);
                    addBooking.setString(2, date);
                    addBooking.setString(3, location);

                    addBooking.executeUpdate();

                    System.out.println("booking added!");
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("failed");
                }

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