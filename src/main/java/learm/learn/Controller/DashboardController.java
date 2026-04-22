package learm.learn.Controller;


import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class DashboardController {

    @GetMapping("/student/dashboard")
    public String studentDashboard() {
        return "Welcome to Student Dashboard — access granted";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "Welcome to Admin Dashboard — access granted";
    }
}