package learm.learn.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtp(String to, String otp, String purpose) {

        SimpleMailMessage msg = new SimpleMailMessage();

        msg.setFrom(fromEmail);
        msg.setTo(to);
        msg.setSubject("LearnMate - " + purpose);

        msg.setText(
            "Your OTP is: " + otp +
            "\n\nThis OTP will expire in 5 minutes."
        );

        mailSender.send(msg);
    }
}