package learm.learn.Repository;

import learm.learn.Entity.ClassSession;
import learm.learn.Entity.SessionStatus;
import learm.learn.Entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ClassSessionRepository
        extends JpaRepository<ClassSession, Long> {

    // Tutor upcoming
    List<ClassSession> findByTutorAndStartTimeAfter(
            User tutor,
            LocalDateTime after
    );

    // Tutor all sessions latest first
    List<ClassSession> findByTutorOrderByStartTimeDesc(
            User tutor
    );

    // Count tutor sessions by status
    long countByTutorAndStatus(
            User tutor,
            SessionStatus status
    );

    // Find room
    Optional<ClassSession> findByRoomId(
            String roomId
    );


    @Query("""
        SELECT COUNT(DISTINCT s.id)
        FROM ClassSession s
        JOIN Payment p
            ON p.course.id = s.course.id
        WHERE p.student = :student
          AND s.endTime > :now
          AND s.status <> learm.learn.Entity.SessionStatus.COMPLETED
    """)
    long countStudentUpcoming(
            @Param("student") User student,
            @Param("now") LocalDateTime now
    );


    @Query("""
        SELECT DISTINCT s
        FROM ClassSession s
        JOIN Payment p
            ON p.course.id = s.course.id
        WHERE p.student = :student
          AND s.endTime > :now
          AND s.status <> learm.learn.Entity.SessionStatus.COMPLETED
        ORDER BY s.startTime ASC
    """)
    List<ClassSession> findStudentUpcoming(
            @Param("student") User student,
            @Param("now") LocalDateTime now
    );
}