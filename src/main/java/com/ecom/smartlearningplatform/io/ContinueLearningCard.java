package com.ecom.smartlearningplatform.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContinueLearningCard {

    private String courseId;
    private String courseTitle;
    private int progressPercent;
}