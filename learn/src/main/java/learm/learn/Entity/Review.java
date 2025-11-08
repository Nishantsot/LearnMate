// learm/learn/Entity/Review.java
package learm.learn.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name="reviews")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) private Course course;
    @ManyToOne(optional = false) private User student;

    private int rating; // 1..5
    @Column(length = 2000) private String comment;

    private LocalDateTime createdAt = LocalDateTime.now();
}
