package com.ecom.smartlearningplatform.io;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EngagementSection {
    private List<EngagementPoint> activityLast30d;   // line/area chart
    private List<CohortSummary> topOrgs;            // top 5 orgs by activity
    private List<EngagementPoint> weeklySignups;    // signups per week (or per day)
}