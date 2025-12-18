package com.ecom.smartlearningplatform.io;

import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlaggedContentSummary {
    private Long flagId;
    private String courseTitle;
    private String lessonTitle;
    private long reportsCount;
    private Instant lastReportedAt;
}