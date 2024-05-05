package tn.esprit.pi.services;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true); // Enable multipart support for attachments

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true); // Set as HTML if needed

            mailSender.send(message);
        } catch (jakarta.mail.MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    // Method to send email with a PDF attachment
    public void sendEmailWithAttachment(String to, String subject, String text, MultipartFile attachment) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true); // Enable multipart support

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);

            // Add the attachment
            helper.addAttachment(attachment.getOriginalFilename(), attachment); // Use the original filename

            mailSender.send(message);
        } catch (jakarta.mail.MessagingException e) {
            throw new RuntimeException("Failed to send email with attachment", e);
        }
    }
}
