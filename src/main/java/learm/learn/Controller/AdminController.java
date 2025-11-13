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

    // ✅ Dashboard Stats
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    // ✅ Pending Tutors List
    @GetMapping("/pending-tutors")
    public ResponseEntity<List<Map<String, Object>>> getPendingTutors() {
        return ResponseEntity.ok(adminService.getPendingTutors());
    }

    // ✅ Approve Tutor
    @PutMapping("/approve-tutor/{id}")
    public ResponseEntity<Map<String, String>> approveTutor(@PathVariable Long id) {
        String message = adminService.approveTutor(id);
        return ResponseEntity.ok(Map.of("message", message));
    }

    // ✅ Reject Tutor
    @DeleteMapping("/reject-tutor/{id}")
    public ResponseEntity<Map<String, String>> rejectTutor(@PathVariable Long id) {
        String message = adminService.rejectTutor(id);
        return ResponseEntity.ok(Map.of("message", message));
    }
}
