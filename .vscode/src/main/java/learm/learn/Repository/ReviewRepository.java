// learm/learn/Repository/ReviewRepository.java
package learm.learn.Repository;

import learm.learn.Entity.Review;
import learm.learn.Entity.User;
import learm.learn.Entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("select coalesce(avg(r.rating),0) from Review r where r.course.tutor = :tutor")
    Double avgRatingForTutor(User tutor);
    long countByCourse(Course course);
}
