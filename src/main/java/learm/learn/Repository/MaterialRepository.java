package learm.learn.Repository;

import learm.learn.Entity.Material;
import learm.learn.Entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaterialRepository extends JpaRepository<Material, Long> {

    // 🔹 Get all materials for a course
    List<Material> findByCourse(Course course);
}
