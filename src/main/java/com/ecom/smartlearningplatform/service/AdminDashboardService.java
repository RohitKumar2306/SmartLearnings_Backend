package com.ecom.smartlearningplatform.service;

import com.ecom.smartlearningplatform.io.AdminDashboardResponse;

public interface AdminDashboardService {
    AdminDashboardResponse getAdminDashboard(String adminEmail);
}