package learm.learn.Repository;

import learm.learn.Entity.Course;
import learm.learn.Entity.CourseStatus;
import learm.learn.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    // 🔹 Admin Dashboard
    @Query("SELECT COUNT(c) FROM Course c WHERE c.status = 'APPROVED'")
    long countApprovedCourses();

    @Query("SELECT COUNT(c) FROM Course c WHERE c.status = 'PENDING'")
    long countPendingCourses();

    // 🔹 Tutor Dashboard — total courses
    long countByTutor(User tutor);

    // 🔹 Tutor Dashboard — count courses by tutor and status
    long countByTutorAndStatus(User tutor, CourseStatus status); // ✅ FIXED

    // 🔹 Lists
    List<Course> findByTutor(User tutor);

    List<Course> findByStatus(CourseStatus status);

    // 🔹 Tutor-specific list (e.g., all pending tutor courses)
    List<Course> findByTutorAndStatus(User tutor, CourseStatus status);
}