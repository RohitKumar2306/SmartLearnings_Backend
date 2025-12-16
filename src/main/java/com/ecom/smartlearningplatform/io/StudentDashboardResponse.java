package com.ecom.smartlearningplatform.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentDashboardResponse {

    private OverviewStats overview;
    private ContinueLearningCard continueLearning;
    private List<CourseProgressSummary> courses;
    private List<RecentActivityItem> recentActivity;
}