package com.ecom.smartlearningplatform.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OverviewStats {

    private long coursesInProgress;
    private int lessonsCompleted;
    private int quizzesTaken;

}