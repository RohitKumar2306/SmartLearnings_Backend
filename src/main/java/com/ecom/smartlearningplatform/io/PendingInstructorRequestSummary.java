package com.ecom.smartlearningplatform.io;

import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingInstructorRequestSummary {
    private Long requestId;
    private String userName;
    private String userEmail;
    private String requestedRole;   // e.g. INSTRUCTOR
    private Instant requestedAt;
}