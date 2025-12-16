package com.ecom.smartlearningplatform.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecentActivityItem {

    private String type;        // e.g. "COURSE"
    private String title;       // e.g. course name
    private String description; // e.g. "Reached 42% in this course"
    private Instant timestamp;
}