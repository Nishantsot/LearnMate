package learm.learn.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 👨‍🎓 Student who made the payment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User student;

    // 👨‍🏫 Tutor who owns the course
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id")
    private User tutor;

    // 📘 Related course
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    // 💰 Payment info
    private double amount;

    // e.g. SUCCESS, PENDING, FAILED, REFUNDED
    private String status = "SUCCESS";

    // 🕒 Timestamp
    private LocalDateTime paymentDate = LocalDateTime.now();

    // 📄 Transaction / order ID (for display or Razorpay integration)
    @Column(unique = true)
    private String transactionId;
}