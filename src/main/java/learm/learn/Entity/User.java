package learm.learn.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    // 🔐 NEVER expose password
    
    @Column(nullable = false)
    private String password;

    // ✅ ADMIN / STUDENT / TUTOR
    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private Role role = Role.STUDENT;

    private boolean verified = false;

    // 🔒 OTP should NEVER be exposed

    private String otp;

    
    private Long otpGeneratedAt;

    // 🕒 Created timestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // ✅ User status
    private boolean active = true;

    // 🎓 Tutor info
    private String subject;

    @Column(length = 1000)
    private String bio;

    private String profilePhoto;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
