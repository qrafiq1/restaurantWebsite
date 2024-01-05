import spark.Spark;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class App {
    public static void main(String[] args) throws IOException, SQLException {

        Spark.port(8080);

        DataSource dataSource = DatabaseConnector.createConnectionPool();

        enableCORS();

        DatabaseHandler databaseHandler = new DatabaseHandler(dataSource);

        System.out.println("System is running");

        Spark.get("/hello", (req, res) -> "Hello, World!");

        Spark.post("/query", (req, res) -> handleQuery(databaseHandler, req.body()));

        Spark.post("/submitBooking", (req, res) -> handleBooking(databaseHandler, req.body(), res));
    }

    // Enable CORS for all routes
    private static void enableCORS() {
        Spark.options("/*", (request, response) -> {
            response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type, Authorization, Accept");
            response.header("Access-Control-Allow-Credentials", "true");
            return "OK";
        });

        Spark.before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));
    }

    private static String handleQuery(DatabaseHandler databaseHandler, String requestBody) {
        try {
            JsonObject jsonBody = new Gson().fromJson(requestBody, JsonObject.class);
            String location = jsonBody.get("location").getAsString();
            int group = jsonBody.get("group").getAsInt();

            List<String> resultList = databaseHandler.getAvailabilty(location, group);

            return new Gson().toJson(resultList);
        } catch (SQLException e) {
            e.printStackTrace();
            return "Internal Server Error";
        }
    }

    private static String handleBooking(DatabaseHandler databaseHandler, String requestBody, spark.Response res) {
        try {
            JsonObject jsonBody = new Gson().fromJson(requestBody, JsonObject.class);
            String location = jsonBody.get("location").getAsString();
            String date = jsonBody.get("date").getAsString();
            String email = jsonBody.get("email").getAsString();
            String firstName = jsonBody.get("firstName").getAsString();
            String lastName = jsonBody.get("lastName").getAsString();

            int userID = databaseHandler.checkUser(email, firstName, lastName);

            databaseHandler.updateBooking(userID, date, location);

            EmailSender.sendEmail(email, firstName, lastName, location, date);

            return new Gson().toJson("Booking submitted!");
        } catch (SQLException e) {
            e.printStackTrace();
            res.status(500);
            return "Internal Server Error";
        }
    }

    private static int getPort() {
        // For local testing, use a specific port
        // For App Engine deployment, use the PORT environment variable
        String portStr = System.getenv("PORT");
        return (portStr != null) ? Integer.parseInt(portStr) : 8080;
    }
}