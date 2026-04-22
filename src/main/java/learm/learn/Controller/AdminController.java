package learm.learn.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import learm.learn.Entity.Course;
import learm.learn.Entity.User;
import learm.learn.Services.AdminService;

import java.util.*;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AdminController {

    @Autowired
    private AdminService adminService;
    @GetMapping("/courses")
public ResponseEntity<?> allCourses() {
    return ResponseEntity.ok(adminService.getAllCourses());
}


    // ✅ Dashboard Stats
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    // // ✅ Pending Courses
@GetMapping("/courses/pending")
public ResponseEntity<List<Course>> pendingCourses(){

    return ResponseEntity.ok(
        adminService.getPendingCourses()
    );

}


// ✅ Approve Course
@PutMapping("/course/approve/{id}")
public ResponseEntity<Map<String,String>> approveCourse(
@PathVariable Long id){

    return ResponseEntity.ok(
        Map.of("message",
        adminService.approveCourse(id))
    );

}
@GetMapping("/admin/students")
public ResponseEntity<List<User>> getStudents(){

    return ResponseEntity.ok(adminService.getAllStudents());

}


// ✅ Reject Course
@PutMapping("/course/reject/{id}")
public ResponseEntity<Map<String,String>> rejectCourse(
@PathVariable Long id){

    return ResponseEntity.ok(
        Map.of("message",
        adminService.rejectCourse(id))
    );

}
}