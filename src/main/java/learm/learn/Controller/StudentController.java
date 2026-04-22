package learm.learn.Controller;

import learm.learn.Entity.*;
import learm.learn.Services.StudentService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/student")
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service){
        this.service = service;
    }


    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard(Principal p){

        return ResponseEntity.ok(
                service.dashboard(p.getName())
        );
    }



    @GetMapping("/courses")
    public ResponseEntity<?> courses(){

        return ResponseEntity.ok(
                service.allCourses()
        );
    }


    @PostMapping("/enroll/{courseId}")
    public ResponseEntity<?> enroll(@PathVariable Long courseId,
                                    Principal p){

        return ResponseEntity.ok(
                Map.of("message",
                        service.enroll(p.getName(), courseId))
        );
    }


    @GetMapping("/my-courses")
    public ResponseEntity<?> myCourses(Principal p){

        return ResponseEntity.ok(
                service.myCourses(p.getName())
        );
    }



    @GetMapping("/classes")
    public ResponseEntity<?> classes(Principal p){

        return ResponseEntity.ok(
                service.upcomingClasses(p.getName())
        );
    }



    @GetMapping("/materials/{courseId}")
    public ResponseEntity<?> materials(@PathVariable Long courseId){

        return ResponseEntity.ok(
                service.materials(courseId)
        );
    }



    @PostMapping("/review")
    public ResponseEntity<?> review(
            @RequestParam Long courseId,
            @RequestParam int rating,
            @RequestParam String comment,
            Principal p){

        return ResponseEntity.ok(
                Map.of("message",
                        service.addReview(
                                p.getName(),
                                courseId,
                                rating,
                                comment))
        );
    }

}