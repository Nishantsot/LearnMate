package learm.learn.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // ✅ Pending Tutors List
@GetMapping("/tutors/pending")
    public ResponseEntity<List<Map<String, Object>>> getPendingTutors() {
        return ResponseEntity.ok(adminService.getPendingTutors());
    }

  // ✅ Approve Course
@PutMapping("/tutor/approve/{id}")
public ResponseEntity<Map<String, String>> approveTutor(@PathVariable Long id) {
    String message = adminService.approveTutor(id);
    return ResponseEntity.ok(Map.of("message", message));
}


// ❌ Reject Course
@PutMapping("/tutor/reject/{id}")
public ResponseEntity<Map<String, String>> rejectTutor(@PathVariable Long id) {
    String message = adminService.rejectTutor(id);
    return ResponseEntity.ok(Map.of("message", message));
}

}