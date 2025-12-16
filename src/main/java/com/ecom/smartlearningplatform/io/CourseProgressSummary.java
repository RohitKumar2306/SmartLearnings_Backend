package com.ecom.smartlearningplatform.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseProgressSummary {

    private String courseId;
    private String courseTitle;
    private String status;          // NOT_STARTED / IN_PROGRESS / COMPLETED
    private int progressPercent;    // 0–100
    private int lessonsCompleted;
    private int totalLessons;
}