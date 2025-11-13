package learm.learn.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtp(String to, String otp, String purpose) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("LearnMate - " + purpose);
        msg.setText("Your OTP is: " + otp + "\nThis OTP will expire in 5 minutes.");
        mailSender.send(msg);
    }
}
