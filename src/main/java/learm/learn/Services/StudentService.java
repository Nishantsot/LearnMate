package learm.learn.Services;

import learm.learn.Dto.PaymentStatus;
import learm.learn.Entity.*;
import learm.learn.Repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class StudentService {

    private final UserRepository userRepo;
    private final CourseRepository courseRepo;
    private final PaymentRepository paymentRepo;
    private final ClassSessionRepository sessionRepo;
    private final ReviewRepository reviewRepo;
    private final MaterialRepository materialRepo;

    public StudentService(
            UserRepository userRepo,
            CourseRepository courseRepo,
            PaymentRepository paymentRepo,
            ClassSessionRepository sessionRepo,
            ReviewRepository reviewRepo,
            MaterialRepository materialRepo
    ) {
        this.userRepo = userRepo;
        this.courseRepo = courseRepo;
        this.paymentRepo = paymentRepo;
        this.sessionRepo = sessionRepo;
        this.reviewRepo = reviewRepo;
        this.materialRepo = materialRepo;
    }

    // =====================================================
    // GET LOGGED-IN STUDENT
    // =====================================================

    private User getStudent(String email) {

        User student = userRepo.findByEmail(email)
                .orElseThrow(
                        () -> new RuntimeException("Student not found")
                );

        if (student.getRole() != Role.STUDENT) {
            throw new RuntimeException("Access denied");
        }

        return student;
    }


    // =====================================================
    // STUDENT DASHBOARD
    // =====================================================

    public Map<String, Object> dashboard(String email) {

        User student = getStudent(email);

        /*
         * Using the same methods that already power
         * My Courses and Student Classes.
         *
         * This keeps dashboard counts consistent with
         * the actual lists shown in the UI.
         */

        List<Payment> enrolledCourses =
                paymentRepo.findByStudent(student);

        List<ClassSession> upcomingClasses =
                sessionRepo.findStudentUpcoming(
                        student,
                        LocalDateTime.now()
                );

        long totalCourses = enrolledCourses == null
                ? 0
                : enrolledCourses.stream()
                    .filter(Objects::nonNull)
                    .filter(payment ->
                            payment.getStatus() == PaymentStatus.SUCCESS
                    )
                    .count();

        long totalUpcomingClasses =
                upcomingClasses == null
                        ? 0
                        : upcomingClasses.stream()
                            .filter(Objects::nonNull)
                            .count();

        Map<String, Object> response = new HashMap<>();

        response.put("totalCourses", totalCourses);
        response.put(
                "upcomingClasses",
                totalUpcomingClasses
        );

        System.out.println(
                "Student Dashboard Email: " + email
        );

        System.out.println(
                "My Courses Count: " + totalCourses
        );

        System.out.println(
                "Upcoming Classes Count: "
                        + totalUpcomingClasses
        );

        return response;
    }


    // =====================================================
    // ALL APPROVED COURSES
    // =====================================================

    public List<Course> allCourses() {

        return courseRepo.findByStatus(
                CourseStatus.APPROVED
        );
    }


    // =====================================================
    // GET COURSE
    // =====================================================

    public Course getCourse(Long id) {

        return courseRepo.findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Course not found"
                        )
                );
    }


    // =====================================================
    // ENROLL COURSE
    // =====================================================

    public String enroll(
            String email,
            Long courseId
    ) {

        User student = getStudent(email);

        Course course = courseRepo.findById(courseId)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Course not found"
                        )
                );

        /*
         * Prevent duplicate enrollment.
         */
        List<Payment> studentPayments =
                paymentRepo.findByStudent(student);

        boolean alreadyEnrolled =
                studentPayments != null
                        && studentPayments.stream()
                        .filter(Objects::nonNull)
                        .anyMatch(payment ->
                                payment.getCourse() != null
                                &&
                                Objects.equals(
                                        payment.getCourse().getId(),
                                        courseId
                                )
                                &&
                                payment.getStatus()
                                        == PaymentStatus.SUCCESS
                        );

        if (alreadyEnrolled) {
            return "Already enrolled in this course";
        }

        Payment payment = Payment.builder()
                .student(student)
                .tutor(course.getTutor())
                .course(course)
                .amount(course.getPrice())
                .transactionId(
                        UUID.randomUUID().toString()
                )
                .status(PaymentStatus.SUCCESS)
                .build();

        paymentRepo.save(payment);

        return "Course enrolled successfully";
    }


    // =====================================================
    // MY COURSES
    // =====================================================

    public List<Payment> myCourses(String email) {

        User student = getStudent(email);

        List<Payment> payments =
                paymentRepo.findByStudent(student);

        if (payments == null) {
            return Collections.emptyList();
        }

        return payments.stream()
                .filter(Objects::nonNull)
                .filter(payment ->
                        payment.getStatus()
                                == PaymentStatus.SUCCESS
                )
                .toList();
    }


    // =====================================================
    // UPCOMING LIVE CLASSES
    // =====================================================

    public List<ClassSession> upcomingClasses(
            String email
    ) {

        User student = getStudent(email);

        List<ClassSession> classes =
                sessionRepo.findStudentUpcoming(
                        student,
                        LocalDateTime.now()
                );

        if (classes == null) {
            return Collections.emptyList();
        }

        return classes;
    }


    // =====================================================
    // COURSE MATERIALS
    // =====================================================

    public List<Material> materials(Long courseId) {

        Course course = courseRepo.findById(courseId)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Course not found"
                        )
                );

        return materialRepo.findByCourse(course);
    }


    // =====================================================
    // ADD REVIEW
    // =====================================================

    public String addReview(
            String email,
            Long courseId,
            int rating,
            String comment
    ) {

        User student = getStudent(email);

        Course course = courseRepo.findById(courseId)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Course not found"
                        )
                );

        if (rating < 1 || rating > 5) {
            throw new RuntimeException(
                    "Rating must be between 1 and 5"
            );
        }

        Review review = Review.builder()
                .student(student)
                .course(course)
                .rating(rating)
                .comment(comment)
                .build();

        reviewRepo.save(review);

        return "Review added";
    }
}