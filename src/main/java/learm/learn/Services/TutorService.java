// learm/learn/Services/TutorService.java
package learm.learn.Services;

import learm.learn.Dto.CreateCourseRequest;
import learm.learn.Dto.ScheduleClassRequest;
import learm.learn.Entity.*;
import learm.learn.Repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class TutorService {

    private final UserRepository userRepo;
    private final CourseRepository courseRepo;
    private final ClassSessionRepository sessionRepo;
    private final PaymentRepository paymentRepo;
    private final ReviewRepository reviewRepo;
    private final MaterialRepository materialRepo;

    public TutorService(UserRepository userRepo,
                        CourseRepository courseRepo,
                        ClassSessionRepository sessionRepo,
                        PaymentRepository paymentRepo,
                        ReviewRepository reviewRepo,
                        MaterialRepository materialRepo) {
        this.userRepo = userRepo;
        this.courseRepo = courseRepo;
        this.sessionRepo = sessionRepo;
        this.paymentRepo = paymentRepo;
        this.reviewRepo = reviewRepo;
        this.materialRepo = materialRepo;
    }

    private User getTutorByEmail(String email){
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Tutor not found"));
    }

    /* Dashboard stats */
    public Map<String,Object> dashboard(String tutorEmail){
        User tutor = getTutorByEmail(tutorEmail);
        Map<String,Object> map = new LinkedHashMap<>();
        map.put("totalCourses", courseRepo.countByTutor(tutor));
        map.put("approvedCourses", courseRepo.countByTutorAndStatus(tutor, CourseStatus.APPROVED));
        map.put("pendingCourses", courseRepo.countByTutorAndStatus(tutor, CourseStatus.PENDING));
        map.put("upcomingSessions", sessionRepo.findByTutorAndStartTimeAfter(tutor, LocalDateTime.now()).size());
        map.put("earnings", Optional.ofNullable(paymentRepo.totalEarningsByTutor(tutor)).orElse(0.0));
        map.put("avgRating", Optional.ofNullable(reviewRepo.avgRatingForTutor(tutor)).orElse(0.0));
        return map;
    }

    /* Courses */
    public List<Course> myCourses(String tutorEmail){
        return courseRepo.findByTutor(getTutorByEmail(tutorEmail));
    }

    public Course createCourse(String tutorEmail, CreateCourseRequest req){
        User tutor = getTutorByEmail(tutorEmail);
        Course c = Course.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .category(req.getCategory())
                .price(req.getPrice())
                .durationMinutes(req.getDurationMinutes())
                .status(CourseStatus.PENDING) // admin will approve
                .tutor(tutor)
                .build();
        return courseRepo.save(c);
    }

    public Course updateCourse(String tutorEmail, Long courseId, CreateCourseRequest req){
        Course c = courseRepo.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));
        if(!c.getTutor().getEmail().equalsIgnoreCase(tutorEmail)) throw new RuntimeException("Forbidden");
        c.setTitle(req.getTitle());
        c.setDescription(req.getDescription());
        c.setCategory(req.getCategory());
        c.setPrice(req.getPrice());
        c.setDurationMinutes(req.getDurationMinutes());
        c.setStatus(CourseStatus.PENDING); // editing resets to review (optional)
        return courseRepo.save(c);
    }

    public void deleteCourse(String tutorEmail, Long courseId){
        Course c = courseRepo.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));
        if(!c.getTutor().getEmail().equalsIgnoreCase(tutorEmail)) throw new RuntimeException("Forbidden");
        courseRepo.delete(c);
    }

    /* Schedule / Live sessions */
    public ClassSession scheduleClass(String tutorEmail, ScheduleClassRequest req, String roomId){
        User tutor = getTutorByEmail(tutorEmail);
        Course course = courseRepo.findById(req.getCourseId()).orElseThrow(() -> new RuntimeException("Course not found"));
        if(!course.getTutor().getId().equals(tutor.getId())) throw new RuntimeException("Forbidden");

        ClassSession s = ClassSession.builder()
                .course(course)
                .tutor(tutor)
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .roomId(roomId)
                .status(SessionStatus.SCHEDULED)
                .build();
        return sessionRepo.save(s);
    }

    public List<ClassSession> upcomingSessions(String tutorEmail){
        return sessionRepo.findByTutorAndStartTimeAfter(getTutorByEmail(tutorEmail), LocalDateTime.now());
    }

    public Optional<ClassSession> findByRoom(String roomId){
        return sessionRepo.findByRoomId(roomId);
    }

    public void markCompleted(Long sessionId, String tutorEmail){
        ClassSession s = sessionRepo.findById(sessionId).orElseThrow(() -> new RuntimeException("Session not found"));
        if(!s.getTutor().getEmail().equalsIgnoreCase(tutorEmail)) throw new RuntimeException("Forbidden");
        s.setStatus(SessionStatus.COMPLETED);
        sessionRepo.save(s);
    }

    /* Materials */
    public Material addMaterial(String tutorEmail, Long courseId, String title, String url){
        Course c = courseRepo.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));
        if(!c.getTutor().getEmail().equalsIgnoreCase(tutorEmail)) throw new RuntimeException("Forbidden");
        Material m = Material.builder().course(c).title(title).url(url).build();
        return materialRepo.save(m);
    }

    public List<Material> listMaterials(Long courseId){
        Course c = courseRepo.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));
        return materialRepo.findByCourse(c);
    }
}
