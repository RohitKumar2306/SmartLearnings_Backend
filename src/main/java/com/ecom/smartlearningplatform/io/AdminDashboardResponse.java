package com.ecom.smartlearningplatform.io;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponse {

    private AdminKpiStats kpis;
    private List<CoursePerformanceSummary> coursePerformance;
    private EngagementSection engagement;
    private PendingActionsSection pendingActions;
}