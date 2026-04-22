// learm/learn/Entity/ClassSession.java
package learm.learn.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name="class_sessions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ClassSession {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomId; 

    @ManyToOne(optional = false)
    private Course course;

    @ManyToOne(optional = false)
    private User tutor;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private SessionStatus status = SessionStatus.SCHEDULED;
}