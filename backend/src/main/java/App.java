import spark.Spark;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class App {

    public static void main(String[] args) throws IOException, SQLException {
        // Set up Spark to run on port 8080
        Spark.port(8080);

        // Create a connection pool using a DataSource
        DataSource dataSource = DatabaseConnector.createConnectionPool();

        // Enable Cross-Origin Resource Sharing (CORS) for all routes
        enableCORS();

        // Initialize a handler for database operations
        DatabaseHandler databaseHandler = new DatabaseHandler(dataSource);

        // Print a message indicating the system is running
        System.out.println("System is running");

        // Define a route to respond with "Hello, World!"
        Spark.get("/hello", (req, res) -> "Hello, World!");

        // Define a route to handle queries
        Spark.post("/query", (req, res) -> handleQuery(databaseHandler, req.body()));

        // Define a route to handle booking submissions
        Spark.post("/submitBooking", (req, res) -> handleBooking(databaseHandler, req.body(), res));
    }

    // Enable CORS for all routes
    private static void enableCORS() {
        // Set up CORS headers for preflight requests
        Spark.options("/*", (request, response) -> {
            response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type, Authorization, Accept");
            response.header("Access-Control-Allow-Credentials", "true");
            return "OK";
        });

        // Set up CORS headers for all requests
        Spark.before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));
    }

    // Handle queries and return availability information
    private static String handleQuery(DatabaseHandler databaseHandler, String requestBody) {
        try {
            // Parse JSON request body to extract location and group information
            JsonObject jsonBody = new Gson().fromJson(requestBody, JsonObject.class);
            String location = jsonBody.get("location").getAsString();
            int group = jsonBody.get("group").getAsInt();

            // Query the database for availability information
            List<String> resultList = databaseHandler.getAvailabilty(location, group);

            // Convert the result list to JSON and return
            return new Gson().toJson(resultList);
        } catch (SQLException e) {
            // Handle database errors and return an error message
            e.printStackTrace();
            return "Internal Server Error";
        }
    }

    // Handle booking submissions
    private static String handleBooking(DatabaseHandler databaseHandler, String requestBody, spark.Response res) {
        try {
            // Parse JSON request body to extract booking information
            JsonObject jsonBody = new Gson().fromJson(requestBody, JsonObject.class);
            String location = jsonBody.get("location").getAsString();
            String date = jsonBody.get("date").getAsString();
            String email = jsonBody.get("email").getAsString();
            String firstName = jsonBody.get("firstName").getAsString();
            String lastName = jsonBody.get("lastName").getAsString();

            // Check if the user already exists in the database and get the user ID
            int userID = databaseHandler.checkUser(email, firstName, lastName);

            // Update the booking in the database
            databaseHandler.updateBooking(userID, date, location);

            // Send a confirmation email to the user
            EmailSender.sendEmail(email, firstName, lastName, location, date);

            // Return a success message in JSON format
            return new Gson().toJson("Booking submitted!");
        } catch (SQLException e) {
            // Handle database errors, set HTTP status to 500, and return an error message
            e.printStackTrace();
            res.status(500);
            return "Internal Server Error";
        }
    }
}
