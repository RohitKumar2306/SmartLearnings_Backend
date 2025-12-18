package com.ecom.smartlearningplatform.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoursePerformanceSummary {
    private String courseId;
    private String courseTitle;
    private String instructorName;

    private long enrolledLearners;
    private double completionRate;
    private double averageQuizScore;

    private String status;
}
