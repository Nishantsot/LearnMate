// learm/learn/Repository/ClassSessionRepository.java
package learm.learn.Repository;

import learm.learn.Entity.ClassSession;
import learm.learn.Entity.SessionStatus;
import learm.learn.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ClassSessionRepository extends JpaRepository<ClassSession, Long> {
    List<ClassSession> findByTutorAndStartTimeAfter(User tutor, LocalDateTime after);
    long countByTutorAndStatus(User tutor, SessionStatus status);
    Optional<ClassSession> findByRoomId(String roomId);
}
