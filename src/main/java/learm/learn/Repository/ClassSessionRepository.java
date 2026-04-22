// learm/learn/Repository/ClassSessionRepository.java

package learm.learn.Repository;

import learm.learn.Entity.ClassSession;
import learm.learn.Entity.SessionStatus;
import learm.learn.Entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ClassSessionRepository extends JpaRepository<ClassSession, Long> {

    // OLD (keep if needed)
    List<ClassSession> findByTutorAndStartTimeAfter(User tutor, LocalDateTime after);


    // ✅ ADD THIS (MOST IMPORTANT FIX)
    List<ClassSession> findByTutorOrderByStartTimeDesc(User tutor);


    // Existing
    long countByTutorAndStatus(User tutor, SessionStatus status);

    Optional<ClassSession> findByRoomId(String roomId);
    @Query("select count(s) from ClassSession s join s.course c join Payment p on p.course = c where p.student = :student and s.startTime > :now")
long countStudentUpcoming(User student, LocalDateTime now);


@Query("select s from ClassSession s join s.course c join Payment p on p.course = c where p.student = :student and s.startTime > :now")
List<ClassSession> findStudentUpcoming(User student, LocalDateTime now);

}