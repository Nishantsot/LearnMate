package learm.learn.Repository;

import learm.learn.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ✅ For authentication & login
    Optional<User> findByEmail(String email);

    // ✅ For registration - check if email already exists
    boolean existsByEmail(String email);

    // ✅ For Admin Dashboard Stats
    @Query("SELECT COUNT(u) FROM User u")
    long countAllUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'TUTOR'")
    long countTutors();

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'STUDENT'")
    long countStudents();

    // ✅ For Pending Tutors Table
    @Query("SELECT u FROM User u WHERE u.role = 'TUTOR' AND u.verified = false")
    List<User> findPendingTutors();
}
