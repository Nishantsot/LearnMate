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
@Autowired
private UserRepository userRepository;
    @Autowired private UserRepository userRepo;
    @Autowired private CourseRepository courseRepo;
    @Autowired private BookingRepository bookingRepo;
    @Autowired private PaymentRepository paymentRepo;
    @Autowired(required = false)
    private SimpMessagingTemplate messagingTemplate; 
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

public List<Course> getPendingCourses(){

    return courseRepo.findByStatus(CourseStatus.PENDING);

}


public String approveCourse(Long id){

    Course course = courseRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Course not found"));

    course.setStatus(CourseStatus.APPROVED);

    courseRepo.save(course);

    return "Course approved successfully";

}
public String rejectCourse(Long id){

    Course course = courseRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Course not found"));

    course.setStatus(CourseStatus.REJECTED);

    courseRepo.save(course);

    return "Course rejected successfully";

}
public List<User> getAllStudents(){

    return userRepository.findByRole("STUDENT");

}
}