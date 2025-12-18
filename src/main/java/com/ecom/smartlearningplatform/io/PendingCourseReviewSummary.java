package com.ecom.smartlearningplatform.io;


import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingCourseReviewSummary {
    private String courseId;
    private String courseTitle;
    private String instructorName;
    private Instant submittedAt;
}