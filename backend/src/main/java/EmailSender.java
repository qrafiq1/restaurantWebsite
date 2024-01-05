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
    private static final String fromEmail = "resturantbooking298@gmail.com";
    private static final String password = "pajf zsxi ptcq qdox";
    private static final String smtp_host = "smtp.gmail.com";

    public static void sendEmail(String toEmail, String firstName, String lastName, String location, String date) {

        Properties properties = new Properties();
        properties.put("mail.smtp.host", smtp_host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.ssl.trust", smtp_host);

        Session session = javax.mail.Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        String bookingNumber = generateBookingNumber();

        try {

            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(fromEmail));

            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));

            message.setSubject("Booking Confirmation");

            message.setText("Dear " + firstName + " " + lastName + ",\n\nYour booking at " + location +
                    " for the date " + date + " has been confirmed.\nBooking Number: " + bookingNumber
                    + "\n\nThank you!");

            Transport.send(message);

            System.out.println("Email sent successfully to " + toEmail);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static String generateBookingNumber() {
        // Generate a random booking number using UUID
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8).toUpperCase();
    }
}