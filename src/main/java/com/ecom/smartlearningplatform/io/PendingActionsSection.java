package com.ecom.smartlearningplatform.io;


import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingActionsSection {
    private List<PendingCourseReviewSummary> coursesPendingReview;
    private List<PendingInstructorRequestSummary> instructorRequests;
    private List<FlaggedContentSummary> flaggedContent;
}