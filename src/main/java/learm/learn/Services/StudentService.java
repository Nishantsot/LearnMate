package learm.learn.Services;

import learm.learn.Entity.*;
import learm.learn.Repository.*;
import learm.learn.Entity.PaymentStatus;

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

    public StudentService(UserRepository userRepo,
                          CourseRepository courseRepo,
                          PaymentRepository paymentRepo,
                          ClassSessionRepository sessionRepo,
                          ReviewRepository reviewRepo,
                          MaterialRepository materialRepo) {

        this.userRepo = userRepo;
        this.courseRepo = courseRepo;
        this.paymentRepo = paymentRepo;
        this.sessionRepo = sessionRepo;
        this.reviewRepo = reviewRepo;
        this.materialRepo = materialRepo;
    }

    private User getStudent(String email){

        User student = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if(student.getRole() != Role.STUDENT)
            throw new RuntimeException("Access denied");

        return student;
    }


    public Map<String,Object> dashboard(String email){

        User student = getStudent(email);

        Map<String,Object> map = new HashMap<>();

        map.put("totalCourses",
                paymentRepo.countByStudent(student));

        map.put("upcomingClasses",
                sessionRepo.countStudentUpcoming(student, LocalDateTime.now()));

        return map;
    }



    public List<Course> allCourses(){

        return courseRepo.findByStatus(CourseStatus.APPROVED);
    }

    public Course getCourse(Long id){

        return courseRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
    }



    public String enroll(String email, Long courseId){

        User student = getStudent(email);

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Payment payment = Payment.builder()
                .student(student)
                .tutor(course.getTutor())
                .course(course)
                .amount(course.getPrice())
                .transactionId(UUID.randomUUID().toString())
        .status(PaymentStatus.SUCCESS) // ✅ FIX

                .build();

        paymentRepo.save(payment);

        return "Course enrolled successfully";
    }


    public List<Payment> myCourses(String email){

        User student = getStudent(email);

        return paymentRepo.findByStudent(student);
    }



    public List<ClassSession> upcomingClasses(String email){

        User student = getStudent(email);

        return sessionRepo.findStudentUpcoming(student, LocalDateTime.now());
    }



    public List<Material> materials(Long courseId){

        Course course = courseRepo.findById(courseId)
                .orElseThrow();

        return materialRepo.findByCourse(course);
    }



    public String addReview(String email,
                            Long courseId,
                            int rating,
                            String comment){

        User student = getStudent(email);

        Course course = courseRepo.findById(courseId)
                .orElseThrow();

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