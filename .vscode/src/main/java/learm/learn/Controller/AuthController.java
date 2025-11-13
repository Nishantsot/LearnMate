package learm.learn.Controller;

import learm.learn.Entity.User;
import learm.learn.Services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
// ✅ Either remove this line (recommended) 
// ✅ OR use explicit origin — no wildcard allowed with allowCredentials(true)
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AuthController {

    @Autowired
    private AuthService authService;

    // ✅ REGISTER
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            String msg = authService.register(user);
            return ResponseEntity.ok(Map.of("message", msg));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Registration failed: " + e.getMessage()));
        }
    }

    // ✅ RESEND OTP
    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestParam String email) {
        try {
            String msg = authService.resendOtp(email);
            if (msg.toLowerCase().contains("failed") || msg.toLowerCase().contains("not found")) {
                return ResponseEntity.badRequest().body(Map.of("message", msg));
            }
            return ResponseEntity.ok(Map.of("message", msg));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error while resending OTP: " + e.getMessage()));
        }
    }

    // ✅ VERIFY OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        try {
            String msg = authService.verifyOtp(email, otp);
            if (msg.toLowerCase().contains("success")) {
                return ResponseEntity.ok(Map.of("message", msg));
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", msg));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error while verifying OTP: " + e.getMessage()));
        }
    }

    // ✅ LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String password = payload.get("password");
        try {
            String tokenOrMsg = authService.login(email, password);

            // If looks like a JWT token (has 3 parts separated by '.')
            if (tokenOrMsg != null && tokenOrMsg.contains(".") && tokenOrMsg.split("\\.").length == 3) {
                return ResponseEntity.ok(Map.of("token", tokenOrMsg));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", tokenOrMsg));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Login failed: " + e.getMessage()));
        }
    }

    // ✅ FORGOT PASSWORD
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        try {
            String msg = authService.forgotPassword(email);
            return ResponseEntity.ok(Map.of("message", msg));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error while sending reset OTP: " + e.getMessage()));
        }
    }

    // ✅ RESET PASSWORD
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> payload) {
        try {
            String email = payload.get("email");
            String otp = payload.get("otp");
            String newPassword = payload.get("newPassword");
            String msg = authService.resetPassword(email, otp, newPassword);

            if (msg.toLowerCase().contains("successful")) {
                return ResponseEntity.ok(Map.of("message", msg));
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", msg));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error while resetting password: " + e.getMessage()));
        }
    }
}
