package com.ecom.smartlearningplatform.io;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminKpiStats {

    private long totalLearners;
    private long activeLearners7d;
    private long totalCourses;
    private long publishedCourses;
    private long draftCourses;
}
