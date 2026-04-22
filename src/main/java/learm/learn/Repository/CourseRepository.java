package learm.learn.Repository;

import learm.learn.Entity.Course;
import learm.learn.Entity.CourseStatus;
import learm.learn.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("SELECT COUNT(c) FROM Course c WHERE c.status = 'APPROVED'")
    long countApprovedCourses();

    @Query("SELECT COUNT(c) FROM Course c WHERE c.status = 'PENDING'")
    long countPendingCourses();

    long countByTutor(User tutor);

    long countByTutorAndStatus(User tutor, CourseStatus status); 

    List<Course> findByTutor(User tutor);

    List<Course> findByStatus(CourseStatus status);

    List<Course> findByTutorAndStatus(User tutor, CourseStatus status);
    
}