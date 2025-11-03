package learm.learn.Services;

import learm.learn.Entity.Role;
import learm.learn.Entity.User;
import learm.learn.Repository.UserRepository;
import learm.learn.Secutity.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private static final long OTP_EXPIRY_MS = 5 * 60 * 1000; // 5 minutes

    // ✅ REGISTER
    public String register(User user) {
        try {
            if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                return "Email already registered";
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setVerified(false);
            String otp = generateOtp();
            user.setOtp(otp);
            user.setOtpGeneratedAt(Instant.now().toEpochMilli());

            // default role if null
            if (user.getRole() == null) user.setRole(Role.STUDENT);

            userRepository.save(user);

            System.out.println("✅ OTP for " + user.getEmail() + " = " + otp);
            // Uncomment to send via email
            // emailService.sendOtp(user.getEmail(), otp, "Email Verification");

            return "OTP sent to your email. Please verify your account.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Registration failed: " + e.getMessage();
        }
    }

    // ✅ VERIFY OTP
    public String verifyOtp(String email, String otp) {
        try {
            Optional<User> opt = userRepository.findByEmail(email);
            if (opt.isEmpty()) return "User not found";

            User user = opt.get();

            if (user.isVerified()) return "User already verified";
            if (user.getOtp() == null) return "No OTP requested";
            if (!user.getOtp().equals(otp)) return "Invalid OTP";

            if (Instant.now().toEpochMilli() - user.getOtpGeneratedAt() > OTP_EXPIRY_MS)
                return "OTP expired";

            user.setVerified(true);
            user.setOtp(null);
            user.setOtpGeneratedAt(null);
            userRepository.save(user);
            return "Account verified successfully";
        } catch (Exception e) {
            e.printStackTrace();
            return "Verification failed: " + e.getMessage();
        }
    }

    // ✅ LOGIN
    public String login(String email, String rawPassword) {
        try {
            Optional<User> opt = userRepository.findByEmail(email);
            if (opt.isEmpty()) return "User not found";

            User user = opt.get();

            if (!user.isVerified()) return "Please verify your email first";
            if (!passwordEncoder.matches(rawPassword, user.getPassword())) return "Invalid credentials";

            return jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        } catch (Exception e) {
            e.printStackTrace();
            return "Login failed: " + e.getMessage();
        }
    }

    // ✅ FORGOT PASSWORD
public String forgotPassword(String email) {
    try {
        Optional<User> opt = userRepository.findByEmail(email);
        if (opt.isEmpty()) return "User not found";

        User user = opt.get();
        String otp = generateOtp();
        user.setOtp(otp);
        user.setOtpGeneratedAt(Instant.now().toEpochMilli());
        userRepository.save(user);

        System.out.println("🔑 Password Reset OTP for " + email + " = " + otp); // 👈 add this line

        // emailService.sendOtp(user.getEmail(), otp, "Password Reset OTP");
        return "Password reset OTP sent to your email";
    } catch (Exception e) {
        e.printStackTrace();
        return "Error while sending password reset OTP: " + e.getMessage();
    }
}

    // ✅ RESET PASSWORD
    public String resetPassword(String email, String otp, String newPassword) {
        try {
            Optional<User> opt = userRepository.findByEmail(email);
            if (opt.isEmpty()) return "User not found";

            User user = opt.get();
            if (user.getOtp() == null || !user.getOtp().equals(otp)) return "Invalid OTP";
            if (Instant.now().toEpochMilli() - user.getOtpGeneratedAt() > OTP_EXPIRY_MS) return "OTP expired";

            user.setPassword(passwordEncoder.encode(newPassword));
            user.setOtp(null);
            user.setOtpGeneratedAt(null);
            userRepository.save(user);

            return "Password reset successful";
        } catch (Exception e) {
            e.printStackTrace();
            return "Password reset failed: " + e.getMessage();
        }
    }

    // ✅ RESEND OTP
    public String resendOtp(String email) {
        try {
            Optional<User> opt = userRepository.findByEmail(email);
            if (opt.isEmpty()) return "User not found";

            User user = opt.get();
            if (user.isVerified()) return "User already verified";

            String otp = generateOtp();
            user.setOtp(otp);
            user.setOtpGeneratedAt(Instant.now().toEpochMilli());
            userRepository.save(user);

            emailService.sendOtp(user.getEmail(), otp, "Resent Verification OTP");
            System.out.println("🔁 Resent OTP for " + user.getEmail() + " = " + otp);

            return "New OTP sent to your email.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error while resending OTP: " + e.getMessage();
        }
    }

    // ✅ OTP GENERATOR
    private String generateOtp() {
        int val = new Random().nextInt(900000) + 100000;
        return String.valueOf(val);
    }
}
