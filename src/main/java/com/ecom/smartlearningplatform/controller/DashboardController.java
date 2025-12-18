package com.ecom.smartlearningplatform.controller;

import com.ecom.smartlearningplatform.io.AdminDashboardResponse;
import com.ecom.smartlearningplatform.io.StudentDashboardResponse;
import com.ecom.smartlearningplatform.service.AdminDashboardService;
import com.ecom.smartlearningplatform.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class DashboardController {

    private final DashboardService dashboardService;
    private final AdminDashboardService adminDashboardService;

    @GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentDashboardResponse> getStudentDashboard(Authentication authentication) throws Exception {

        System.out.println("Get Student Dashboard");
        String email = authentication.getName(); // set by Spring Security from JWT
        StudentDashboardResponse response = dashboardService.getStudentDashboard(email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')") // or PLATFORM_ADMIN depending on your roles
    public ResponseEntity<AdminDashboardResponse> getAdminDashboard(Authentication authentication) {
        String email = authentication.getName();
        AdminDashboardResponse resp = adminDashboardService.getAdminDashboard(email);
        return ResponseEntity.ok(resp);
    }
}