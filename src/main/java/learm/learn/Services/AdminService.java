package learm.learn.Services;


import learm.learn.Entity.Course;
import learm.learn.Entity.CourseStatus;
import learm.learn.Entity.User;
import learm.learn.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AdminService {

    @Autowired private UserRepository userRepo;
    @Autowired private CourseRepository courseRepo;
    @Autowired private BookingRepository bookingRepo;
    @Autowired private PaymentRepository paymentRepo;
    @Autowired(required = false)
    private SimpMessagingTemplate messagingTemplate; // for WebSocket notifications

    // ✅ Dashboard Summary
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalUsers", userRepo.countAllUsers());
        stats.put("tutors", userRepo.countTutors());
        stats.put("students", userRepo.countStudents());
        stats.put("approvedCourses", courseRepo.countApprovedCourses());
        stats.put("pendingCourses", courseRepo.countPendingCourses());
        stats.put("activeBookings", bookingRepo.countActiveBookings());
        stats.put("completedBookings", bookingRepo.countCompletedBookings());
        stats.put("revenue", Optional.ofNullable(paymentRepo.totalRevenue()).orElse(0.0));

        return stats;
    }
       public List<Course> getAllCourses() {
        return courseRepo.findAll();
    }

    // ✅ Get All Pending Tutors
    public List<Map<String, Object>> getPendingTutors() {
        List<User> tutors = userRepo.findPendingTutors();
        List<Map<String, Object>> list = new ArrayList<>();

        for (User t : tutors) {
            Map<String, Object> tutor = new HashMap<>();
            tutor.put("id", t.getId());
            tutor.put("name", t.getName());
            tutor.put("email", t.getEmail());
            tutor.put("subject", t.getSubject());
            tutor.put("bio", t.getBio());
            tutor.put("createdAt", t.getCreatedAt());
            list.add(tutor);
        }

        return list;
    }

    // ✅ Approve Tutor
public String approveTutor(Long id) {

    User tutor = userRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Tutor not found"));

    tutor.setVerified(true);      // mark verified
    tutor.setActive(true);        // optional if you use active flag

    userRepo.save(tutor);

    return "Tutor approved successfully";
}



public String rejectTutor(Long id) {

    User tutor = userRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Tutor not found"));

    userRepo.delete(tutor);   // OR setActive(false) if you don't want delete

    return "Tutor rejected successfully";
}


}