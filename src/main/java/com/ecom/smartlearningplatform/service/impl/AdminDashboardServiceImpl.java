package com.ecom.smartlearningplatform.service.impl;


import com.ecom.smartlearningplatform.entity.CourseEnrollmentEntity;
import com.ecom.smartlearningplatform.entity.CourseEntity;
import com.ecom.smartlearningplatform.entity.UserEntity;
import com.ecom.smartlearningplatform.io.*;
import com.ecom.smartlearningplatform.repository.CourseEnrollmentRepository;
import com.ecom.smartlearningplatform.repository.CourseRepository;
import com.ecom.smartlearningplatform.repository.UserRepository;
import com.ecom.smartlearningplatform.security.AppRole;
import com.ecom.smartlearningplatform.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {
    private final UserRepository userRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final CourseRepository courseRepository;

    @Override
    public AdminDashboardResponse getAdminDashboard(String adminEmail) {
        UserEntity admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found"));

        // KPI Cards
        AdminKpiStats kpis = buildKpiStats();

        // Course performance table
        List<CoursePerformanceSummary> coursePerformance = buildCoursePerformance();
        
        // Engagement & cohorts
        EngagementSection engagement = buildEngagementSection();
        
        // Pending actions
        PendingActionsSection pendingActions = buildPendingActionsSection();

        return AdminDashboardResponse.builder()
                .kpis(kpis)
                .coursePerformance(coursePerformance)
                .engagement(engagement)
                .pendingActions(pendingActions)
                .build();
    }

    private PendingActionsSection buildPendingActionsSection() {

        // 1) Courses pending review: treat unpublished as “pending” for now
        List<CourseEntity> pendingCourses = courseRepository.findByPublishedFalse();

        List<PendingCourseReviewSummary> courseReviews = pendingCourses.stream()
                .map(c -> PendingCourseReviewSummary.builder()
                        .courseId(c.getCourseId())
                        .courseTitle(c.getTitle())
                        .instructorName(
                                c.getInstructor() != null
                                        ? c.getInstructor().getName()
                                        : "Unknown"
                        )
                        .submittedAt(
                                c.getCreatedAt() != null
                                        ? c.getCreatedAt().toInstant()
                                        : Instant.now()
                        )
                        .build())
                .collect(Collectors.toList());

        // 2) Instructor / role-change requests – not implemented yet
        List<PendingInstructorRequestSummary> instructorRequests = List.of();

        // 3) Flagged content – not implemented yet
        List<FlaggedContentSummary> flaggedContent = List.of();

        return PendingActionsSection.builder()
                .coursesPendingReview(courseReviews)
                .instructorRequests(instructorRequests)
                .flaggedContent(flaggedContent)
                .build();
    }

    private EngagementSection buildEngagementSection() {
        Instant thirtyDaysAgo = Instant.now().minus(30, ChronoUnit.DAYS);

        // 1) Activity (active learners per day) – simple version using lastAccessedAt
        Timestamp thirtyDaysAgoTs = Timestamp.from(thirtyDaysAgo);
        List<CourseEnrollmentEntity> recent = courseEnrollmentRepository
                .findByLastAccessedAtAfter(thirtyDaysAgoTs);

        Map<LocalDate, Set<Long>> activePerDay = new HashMap<>();

        for (CourseEnrollmentEntity e : recent) {
            if (e.getLastAccessedAt() == null) continue;

            LocalDate day = e.getLastAccessedAt()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            activePerDay
                    .computeIfAbsent(day, d -> new HashSet<>())
                    .add(e.getUser().getId());
        }

        List<EngagementPoint> activityPoints = activePerDay.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> EngagementPoint.builder()
                        .date(entry.getKey())
                        .activeLearners(entry.getValue().size())
                        .build())
                .collect(Collectors.toList());

        // 2) New signups per week (for learners only)
        String learnerRole = AppRole.ROLE_STUDENT.name();
        Timestamp sinceTs = Timestamp.from(thirtyDaysAgo);

        List<UserEntity> recentLearners = userRepository
                .findByRoleAndCreatedAtAfter(learnerRole, sinceTs);

        // group by date (simple) – you can later group by ISO week if you prefer
        Map<LocalDate, Long> signupsPerDay = recentLearners.stream()
                .collect(Collectors.groupingBy(
                        u -> u.getCreatedAt()
                                .toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate(),
                        Collectors.counting()
                ));

        List<EngagementPoint> weeklySignups = signupsPerDay.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> EngagementPoint.builder()
                        .date(e.getKey())
                        .activeLearners(e.getValue()) // reuse field for “count”
                        .build())
                .collect(Collectors.toList());

        // 3) Cohorts (orgs) – not implemented yet (no Org entity), keep empty
        List<CohortSummary> topOrgs = List.of();

        return EngagementSection.builder()
                .activityLast30d(activityPoints)
                .topOrgs(topOrgs)
                .weeklySignups(weeklySignups)
                .build();
    }

    private List<CoursePerformanceSummary> buildCoursePerformance() {

        List<CourseEntity> courses = courseRepository.findAll();

        return courses.stream().map(course -> {

            long enrolled = courseEnrollmentRepository.countByCourse(course);
            long completed = courseEnrollmentRepository.countByCourseAndStatus(course, "COMPLETED");

            double completionRatio = 0.0;
            if (enrolled > 0) {
                completionRatio = (completed * 100.0) /  enrolled;
            }

            double averageProgress = courseEnrollmentRepository.avgProgressPercentByCourse(course);

            return CoursePerformanceSummary.builder()
                    .courseId(course.getCourseId())
                    .courseTitle(course.getTitle())
                    .instructorName(course.getInstructor() != null ? course.getInstructor().getName() : "Unknown")
                    .enrolledLearners(enrolled)
                    .completionRate(completionRatio)
                    .averageQuizScore(averageProgress)
                    .status(course.isPublished() ? "PUBLISHED" : "UNPUBLISHED")
                    .build();
        }).sorted(Comparator.comparingLong(CoursePerformanceSummary::getEnrolledLearners).reversed())
                .collect(Collectors.toList());
    }

    private AdminKpiStats buildKpiStats() {

        String learnerRole = AppRole.ROLE_STUDENT.name();
        long totalLearners = userRepository.countByRole(learnerRole);

        Instant sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        Timestamp sevenDaysAgoTs = Timestamp.from(sevenDaysAgo);
        long activeLeaners7d = courseEnrollmentRepository.countActiveLearnersSince(sevenDaysAgoTs);


        long totalCourses = courseRepository.count();
        long publishedCourses = courseRepository.countByPublished(true);
        long draftCourses = courseRepository.countByPublished(false);

        return AdminKpiStats.builder()
                .totalLearners(totalLearners)
                .activeLearners7d(activeLeaners7d)
                .totalCourses(totalCourses)
                .publishedCourses(publishedCourses)
                .draftCourses(draftCourses)
                .build();
    }
}
