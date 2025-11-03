package learm.learn.Controller;


import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class DashboardController {

    // accessible to authenticated users with ROLE_STUDENT
    @GetMapping("/student/dashboard")
    public String studentDashboard() {
        return "Welcome to Student Dashboard — access granted";
    }

    // accessible to authenticated users with ROLE_ADMIN
    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "Welcome to Admin Dashboard — access granted";
    }
}
