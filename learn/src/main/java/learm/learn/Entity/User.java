package learm.learn.Entity;

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
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    // ✅ Store role as string (e.g., ADMIN / STUDENT / TUTOR)
    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private Role role = Role.STUDENT;

    private boolean verified = false;

    // ✅ OTP details
    private String otp;
    private Long otpGeneratedAt; // epoch milliseconds

    // ✅ Created timestamp
    private LocalDateTime createdAt = LocalDateTime.now();

    // ✅ User status
    private boolean active = true;

    // ✅ Optional tutor details
    private String subject;      // For tutors (e.g., "Math, Science")
    @Column(length = 1000)
    private String bio;          // Tutor description
    private String profilePhoto; // URL or base64 image link
}
