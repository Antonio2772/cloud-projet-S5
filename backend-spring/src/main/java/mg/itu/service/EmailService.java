package mg.itu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.baseUrl}")
    private String baseUrl;

    public void sendTransactionValidationEmail(String toEmail, String userName, String validationToken, Long transactionId) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("Validate Your Transaction");

            String validationLink = baseUrl + "/api/transactions/validate?token=" + validationToken + "&transactionId=" + transactionId;

            String emailContent = """
                <html>
                    <body>
                        <h2>Hello %s,</h2>
                        <p>Please click the link below to validate your transaction:</p>
                        <a href="%s">Validate Transaction</a>
                        <p>This link will expire in 1 hour.</p>
                        <p>If you didn't make this transaction, please ignore this email.</p>
                    </body>
                </html>
                """.formatted(userName, validationLink);

            helper.setText(emailContent, true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error sending validation email", e);
        }
    }
}