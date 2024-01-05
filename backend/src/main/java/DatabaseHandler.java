import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {

    private DataSource dataSource;

    /**
     * Constructs a DatabaseHandler with the specified DataSource.
     *
     * @param dataSource The DataSource used for database connections.
     */
    public DatabaseHandler(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Retrieves the availability of bookings for a given location and group size.
     *
     * @param location  The name of the restaurant location.
     * @param groupSize The size of the dining group.
     * @return A list of booking times available for the specified location and
     *         group size.
     * @throws SQLException If a database access error occurs.
     */
    public List<String> getAvailabilty(String location, int groupSize) throws SQLException {
        String getAvailabiltyQuery = "SELECT b.bookingTime FROM Booking b JOIN Restaurant r ON b.RestaurantID = r.RestaurantID JOIN DiningTable dt ON b.TableID = dt.TableID WHERE r.RestaurantName = ? AND dt.TableCapacity >= ? AND b.isAvailable = 1";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement getAvailabiltyStatement = connection.prepareStatement(getAvailabiltyQuery)) {
            getAvailabiltyStatement.setString(1, location);
            getAvailabiltyStatement.setInt(2, groupSize);

            ResultSet resultSet = getAvailabiltyStatement.executeQuery();

            List<String> resultList = new ArrayList<>();
            while (resultSet.next()) {
                resultList.add(resultSet.getString("BookingTime"));
            }
            return resultList;
        }
    }

    /**
     * Checks if a user with the given email exists. If not, adds the user to the
     * database.
     *
     * @param email     The email address of the user.
     * @param firstName The first name of the user.
     * @param lastName  The last name of the user.
     * @return The user ID.
     * @throws SQLException If a database access error occurs.
     */
    public int checkUser(String email, String firstName, String lastName) throws SQLException {
        String checkUserQuery = "SELECT * FROM User WHERE UserEmail = ?";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement checkUserStatement = connection.prepareStatement(checkUserQuery)) {

            checkUserStatement.setString(1, email);
            ResultSet resultSet = checkUserStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("UserID");
            } else {
                // User does not exist, add the user to the database
                return addUser(email, firstName, lastName);
            }
        }
    }

    /**
     * Adds a new user to the database.
     *
     * @param email     The email address of the user.
     * @param firstName The first name of the user.
     * @param lastName  The last name of the user.
     * @return The user ID.
     * @throws SQLException If a database access error occurs.
     */
    public int addUser(String email, String firstName, String lastName) throws SQLException {
        String addUserQuery = "INSERT INTO User (FirstName, LastName, UserEmail) VALUES (?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement addUserStatement = connection.prepareStatement(addUserQuery,
                        PreparedStatement.RETURN_GENERATED_KEYS)) {

            addUserStatement.setString(1, firstName);
            addUserStatement.setString(2, lastName);
            addUserStatement.setString(3, email);

            int affectedRows = addUserStatement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = addUserStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Error retrieving UserID after insertion.");
                    }
                }
            } else {
                throw new SQLException("No rows were affected by the insertion.");
            }
        }
    }

    /**
     * Updates a booking in the database with the specified user ID, date, and
     * location.
     *
     * @param userID   The user ID associated with the booking.
     * @param date     The booking date.
     * @param location The restaurant location.
     * @throws SQLException If a database access error occurs.
     */
    public void updateBooking(int userID, String date, String location) throws SQLException {
        String updateBookingQuery = "UPDATE Booking b JOIN Restaurant r ON b.restaurantID = r.restaurantID SET UserID = ?, isAvailable = 0 WHERE BookingTime = ? AND isAvailable = 1 AND restaurantName = ?;";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement updateBookingStatement = connection.prepareStatement(updateBookingQuery)) {
            updateBookingStatement.setInt(1, userID);
            updateBookingStatement.setString(2, date);
            updateBookingStatement.setString(3, location);

            updateBookingStatement.executeUpdate();
        }
    }
}
