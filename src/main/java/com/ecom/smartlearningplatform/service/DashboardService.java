package com.ecom.smartlearningplatform.service;

import com.ecom.smartlearningplatform.io.StudentDashboardResponse;

public interface DashboardService {

    StudentDashboardResponse getStudentDashboard(String userEmail);

}