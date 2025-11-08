// learm/learn/Controller/TutorController.java
package learm.learn.Controller;

import learm.learn.Dto.CreateCourseRequest;
import learm.learn.Dto.ScheduleClassRequest;
import learm.learn.Entity.ClassSession;
import learm.learn.Entity.Course;
import learm.learn.Entity.Material;
import learm.learn.Services.TutorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/tutor")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@PreAuthorize("hasRole('TUTOR')")
public class TutorController {

    private final TutorService tutorService;
    public TutorController(TutorService tutorService){ this.tutorService = tutorService; }

    // 🔹 Dashboard stats
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String,Object>> dashboard(Principal principal){
        return ResponseEntity.ok(tutorService.dashboard(principal.getName()));
    }

    // 🔹 My courses
    @GetMapping("/courses")
    public ResponseEntity<List<Course>> myCourses(Principal p){
        return ResponseEntity.ok(tutorService.myCourses(p.getName()));
    }

    @PostMapping("/courses")
    public ResponseEntity<Course> createCourse(@RequestBody CreateCourseRequest req, Principal p){
        return ResponseEntity.ok(tutorService.createCourse(p.getName(), req));
    }

    @PutMapping("/courses/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable Long id, @RequestBody CreateCourseRequest req, Principal p){
        return ResponseEntity.ok(tutorService.updateCourse(p.getName(), id, req));
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<Map<String,String>> deleteCourse(@PathVariable Long id, Principal p){
        tutorService.deleteCourse(p.getName(), id);
        return ResponseEntity.ok(Map.of("message","Course deleted"));
    }

    // 🔹 Schedule & live classes
    @PostMapping("/classes/schedule")
    public ResponseEntity<ClassSession> schedule(@RequestBody ScheduleClassRequest req,
                                                 @RequestParam(required = false) String roomId,
                                                 Principal p){
        String rid = (roomId==null || roomId.isBlank()) ? UUID.randomUUID().toString() : roomId;
        return ResponseEntity.ok(tutorService.scheduleClass(p.getName(), req, rid));
    }

    @GetMapping("/classes/upcoming")
    public ResponseEntity<List<ClassSession>> upcoming(Principal p){
        return ResponseEntity.ok(tutorService.upcomingSessions(p.getName()));
    }

    @PostMapping("/classes/{sessionId}/complete")
    public ResponseEntity<Map<String,String>> complete(@PathVariable Long sessionId, Principal p){
        tutorService.markCompleted(sessionId, p.getName());
        return ResponseEntity.ok(Map.of("message","Session marked Completed"));
    }

    @GetMapping("/classes/room/{roomId}")
    public ResponseEntity<?> byRoom(@PathVariable String roomId){
        return tutorService.findByRoom(roomId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).body(Map.of("message","Room not found")));
    }

    // 🔹 Materials
    @PostMapping("/materials")
    public ResponseEntity<Material> addMaterial(@RequestParam Long courseId,
                                                @RequestParam String title,
                                                @RequestParam String url,
                                                Principal p){
        return ResponseEntity.ok(tutorService.addMaterial(p.getName(), courseId, title, url));
    }

    @GetMapping("/materials")
    public ResponseEntity<List<Material>> list(@RequestParam Long courseId){
        return ResponseEntity.ok(tutorService.listMaterials(courseId));
    }
}
