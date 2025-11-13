package learm.learn.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 📘 Basic info
    private String title;

    @Column(length = 3000)
    private String description;

    private String category;

    private Double price;

    // ⏱ Duration in minutes (optional)
    private Integer durationMinutes;

    // 🔍 Approval status (Admin can approve/reject)
    @Enumerated(EnumType.STRING)
    private CourseStatus status = CourseStatus.PENDING;

    // 🕒 Auto-set when created
    private LocalDateTime createdAt = LocalDateTime.now();

    // 👨‍🏫 Linked tutor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id")
    private User tutor;
}
