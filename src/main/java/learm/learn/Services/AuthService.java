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

    public String register(User user) {

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            return "Email is required";
        }

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            return "Password is required";
        }

        Optional<User> existingOpt = userRepository.findByEmail(user.getEmail());

        if (existingOpt.isPresent()) {
            User existing = existingOpt.get();

            if (existing.isVerified() && existing.getRole() == user.getRole()) {
                return "Email already registered and verified";
            }

            if (existing.isVerified() && existing.getRole() != user.getRole()) {
                String otp = generateOtp();
                existing.setOtp(otp);
                existing.setOtpGeneratedAt(Instant.now().toEpochMilli());
                existing.setVerified(false);
                existing.setRole(user.getRole());
                existing.setPassword(passwordEncoder.encode(user.getPassword()));

                userRepository.save(existing);
                emailService.sendOtp(existing.getEmail(), otp, "Role Change Verification");

                return "Role updated. OTP sent for verification";
            }

            if (!existing.isVerified()) {
                String otp = generateOtp();
                existing.setOtp(otp);
                existing.setOtpGeneratedAt(Instant.now().toEpochMilli());
                userRepository.save(existing);

                emailService.sendOtp(existing.getEmail(), otp, "Email Verification (Resent)");
                return "Account not verified. OTP resent";
            }
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setVerified(false);
        user.setRole(user.getRole() == null ? Role.STUDENT : user.getRole());

        String otp = generateOtp();
        user.setOtp(otp);
        user.setOtpGeneratedAt(Instant.now().toEpochMilli());

        userRepository.save(user);
        emailService.sendOtp(user.getEmail(), otp, "Email Verification");

        return "OTP sent to your email";
    }

   public String verifyOtp(String email, String otp) {

    Optional<User> opt = userRepository.findByEmail(email);
    if (opt.isEmpty()) return "User not found";

    User user = opt.get();

    if (user.isVerified()) return "User already verified";
    if (user.getOtp() == null) return "No OTP requested";
    if (!user.getOtp().equals(otp)) return "Invalid OTP";

    if (Instant.now().toEpochMilli() - user.getOtpGeneratedAt() > OTP_EXPIRY_MS) {
        return "OTP expired";
    }

    user.setVerified(true);

    user.setActive(true);   

    user.setOtp(null);
    user.setOtpGeneratedAt(null);

    userRepository.save(user);

    return "Account verified successfully";
}

    public String login(String email, String rawPassword) {

        Optional<User> opt = userRepository.findByEmail(email);
        if (opt.isEmpty()) return "User not found";

        User user = opt.get();

        if (!user.isVerified()) return "Please verify your email first";
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            return "Invalid credentials";
        }

        return jwtUtil.generateToken(user.getEmail(), user.getRole().name());
    }

    // ================= FORGOT PASSWORD =================
    public String forgotPassword(String email) {

        Optional<User> opt = userRepository.findByEmail(email);
        if (opt.isEmpty()) return "User not found";

        User user = opt.get();
        String otp = generateOtp();

        user.setOtp(otp);
        user.setOtpGeneratedAt(Instant.now().toEpochMilli());
        userRepository.save(user);

        emailService.sendOtp(user.getEmail(), otp, "Password Reset OTP");
        return "Password reset OTP sent";
    }

    public String resetPassword(String email, String otp, String newPassword) {

        if (newPassword == null || newPassword.isBlank()) {
            return "New password is required";
        }

        Optional<User> opt = userRepository.findByEmail(email);
        if (opt.isEmpty()) return "User not found";

        User user = opt.get();

        if (user.getOtp() == null || !user.getOtp().equals(otp)) {
            return "Invalid OTP";
        }

        if (Instant.now().toEpochMilli() - user.getOtpGeneratedAt() > OTP_EXPIRY_MS) {
            return "OTP expired";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setOtp(null);
        user.setOtpGeneratedAt(null);
        userRepository.save(user);

        return "Password reset successful";
    }

    public String resendOtp(String email) {

        Optional<User> opt = userRepository.findByEmail(email);
        if (opt.isEmpty()) return "User not found";

        User user = opt.get();
        if (user.isVerified()) return "User already verified";

        String otp = generateOtp();
        user.setOtp(otp);
        user.setOtpGeneratedAt(Instant.now().toEpochMilli());
        userRepository.save(user);

        emailService.sendOtp(user.getEmail(), otp, "Resent Verification OTP");
        return "New OTP sent";
    }

   private String generateOtp() {

    String otp = String.valueOf(new Random().nextInt(900000) + 100000);

    System.out.println("================================");
    System.out.println("OTP GENERATED: " + otp);
    System.out.println("================================");

    return otp;
}
}
