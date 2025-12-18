package com.ecom.smartlearningplatform.io;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CohortSummary {
    private String orgId;
    private String orgName;
    private long activeLearners7d;    // active learners from this org
    private long newSignups7d;        // new users in last 7 days
}