import java.util.Properties;
import java.util.UUID;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {

    // Email configuration constants
    private static final String fromEmail = "resturantbooking298@gmail.com";
    private static final String password = "pajf zsxi ptcq qdox";
    private static final String smtp_host = "smtp.gmail.com";

    /**
     * Sends a confirmation email to the specified recipient with booking details.
     *
     * @param toEmail   The recipient's email address.
     * @param firstName The first name of the recipient.
     * @param lastName  The last name of the recipient.
     * @param location  The booked restaurant location.
     * @param date      The booked date.
     */
    public static void sendEmail(String toEmail, String firstName, String lastName, String location, String date) {

        // Configure email properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", smtp_host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.ssl.trust", smtp_host);

        // Create a mail session with authentication
        Session session = javax.mail.Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        // Generate a unique booking number
        String bookingNumber = generateBookingNumber();

        try {
            // Create a new MimeMessage
            Message message = new MimeMessage(session);

            // Set the sender address
            message.setFrom(new InternetAddress(fromEmail));

            // Set the recipient address
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));

            // Set the email subject
            message.setSubject("Booking Confirmation");

            // Set the email content
            message.setText("Dear " + firstName + " " + lastName + ",\n\nYour booking at " + location +
                    " for the date " + date + " has been confirmed.\nBooking Number: " + bookingNumber
                    + "\n\nThank you!");

            // Send the email
            Transport.send(message);

            // Log success
            System.out.println("Email sent successfully to " + toEmail);
        } catch (MessagingException e) {
            // Log and propagate the exception
            e.printStackTrace();
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates a random booking number using UUID.
     *
     * @return A unique booking number.
     */
    private static String generateBookingNumber() {
        // Generate a random booking number using UUID
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8).toUpperCase();
    }
}
