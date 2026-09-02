package learm.learn.Services;

import learm.learn.Entity.Course;
import learm.learn.Entity.CourseStatus;
import learm.learn.Entity.User;
import learm.learn.Repository.*;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AdminService {

    private final UserRepository userRepo;
    private final CourseRepository courseRepo;
    private final BookingRepository bookingRepo;
    private final PaymentRepository paymentRepo;

    private final SimpMessagingTemplate messagingTemplate;

    public AdminService(
            UserRepository userRepo,
            CourseRepository courseRepo,
            BookingRepository bookingRepo,
            PaymentRepository paymentRepo,
            Optional<SimpMessagingTemplate> messagingTemplate
    ) {
        this.userRepo = userRepo;
        this.courseRepo = courseRepo;
        this.bookingRepo = bookingRepo;
        this.paymentRepo = paymentRepo;
        this.messagingTemplate = messagingTemplate.orElse(null);
    }

    // ADMIN DASHBOARD
    public Map<String, Object> getDashboardStats() {

        Map<String, Object> stats = new HashMap<>();

        long totalUsers = userRepo.countAllUsers();
        long tutors = userRepo.countTutors();
        long students = userRepo.countStudents();

        long approvedCourses =
                courseRepo.countApprovedCourses();

        long pendingCourses =
                courseRepo.countPendingCourses();

        long activeBookings =
                bookingRepo.countActiveBookings();

        long completedBookings =
                bookingRepo.countCompletedBookings();

        double revenue =
                Optional.ofNullable(
                        paymentRepo.totalRevenue()
                ).orElse(0.0);

        stats.put("totalUsers", totalUsers);
        stats.put("tutors", tutors);
        stats.put("students", students);

        stats.put(
                "approvedCourses",
                approvedCourses
        );

        stats.put(
                "pendingCourses",
                pendingCourses
        );

        stats.put(
                "activeBookings",
                activeBookings
        );

        stats.put(
                "completedBookings",
                completedBookings
        );

        stats.put("revenue", revenue);

        System.out.println("===== ADMIN DASHBOARD =====");
        System.out.println("Total Users: " + totalUsers);
        System.out.println("Tutors: " + tutors);
        System.out.println("Students: " + students);
        System.out.println(
                "Approved Courses: "
                        + approvedCourses
        );
        System.out.println(
                "Pending Courses: "
                        + pendingCourses
        );
        System.out.println(
                "Active Bookings: "
                        + activeBookings
        );
        System.out.println(
                "Completed Bookings: "
                        + completedBookings
        );
        System.out.println("Revenue: " + revenue);

        return stats;
    }

    // ALL COURSES
    public List<Course> getAllCourses() {
        return courseRepo.findAll();
    }

    // PENDING COURSES
    public List<Course> getPendingCourses() {

        return courseRepo.findByStatus(
                CourseStatus.PENDING
        );
    }

    // APPROVE COURSE
    public String approveCourse(Long id) {

        Course course =
                courseRepo.findById(id)
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "Course not found"
                                        )
                        );

        course.setStatus(
                CourseStatus.APPROVED
        );

        courseRepo.save(course);

        return "Course approved successfully";
    }

    
    public String rejectCourse(Long id) {

        Course course =
                courseRepo.findById(id)
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "Course not found"
                                        )
                        );

        course.setStatus(
                CourseStatus.REJECTED
        );

        courseRepo.save(course);

        return "Course rejected successfully";
    }

    // ALL STUDENTS
    public List<User> getAllStudents() {

        return userRepo.findByRole(
                "STUDENT"
        );
    }
}