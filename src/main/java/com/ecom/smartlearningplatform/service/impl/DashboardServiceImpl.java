package com.ecom.smartlearningplatform.service.impl;

import com.ecom.smartlearningplatform.entity.CourseEnrollmentEntity;
import com.ecom.smartlearningplatform.entity.UserEntity;
import com.ecom.smartlearningplatform.io.*;
import com.ecom.smartlearningplatform.repository.CourseEnrollmentRepository;
import com.ecom.smartlearningplatform.repository.UserRepository;
import com.ecom.smartlearningplatform.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;

    @Override
    public StudentDashboardResponse getStudentDashboard(String userEmail) {

        UserEntity userEntity = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found" + userEmail));

        long coursesInProgress = courseEnrollmentRepository.countByUserAndStatus(userEntity, "IN_PROGRESS");

        int lessonsCompleted = Optional.of(courseEnrollmentRepository.sumLessonsCompletedByUser(userEntity)).orElse(0);

        int quizzesTaken = Optional.of(courseEnrollmentRepository.sumQuizzesTakenByUser(userEntity)).orElse(0);

        OverviewStats overviewStats = OverviewStats
                .builder()
                .coursesInProgress(coursesInProgress)
                .lessonsCompleted(lessonsCompleted)
                .quizzesTaken(quizzesTaken)
                .build();

        List<CourseEnrollmentEntity> enrollments = courseEnrollmentRepository.findByUserOrderByUpdatedAtDesc(userEntity);

        ContinueLearningCard continueLearningCard = null;
        if (!enrollments.isEmpty()) {
            CourseEnrollmentEntity latest = enrollments.get(0);
            continueLearningCard = ContinueLearningCard
                    .builder()
                    .courseId(latest.getCourse().getCourseId())
                    .courseTitle(latest.getCourse().getTitle())
                    .progressPercent(latest.getProgressPercent())
                    .build();
        }

        List<CourseProgressSummary> courseProgressSummaries = enrollments.stream()
                .map(e -> CourseProgressSummary.builder()
                        .courseId(e.getCourse().getCourseId())
                        .courseTitle(e.getCourse().getTitle())
                        .status(e.getStatus())
                        .progressPercent(e.getProgressPercent())
                        .lessonsCompleted(e.getLessonsCompleted())
                        .totalLessons(e.getTotalLessons())
                        .build())
                .collect(Collectors.toList());

        List<RecentActivityItem> recentActivity = enrollments.stream()
                .sorted(Comparator.comparing(CourseEnrollmentEntity::getUpdatedAt).reversed())
                .limit(5)
                .map(e -> {
                    String description;
                    switch (e.getStatus()) {
                        case "COMPLETED" ->
                                description = "Completed this course";
                        case "IN_PROGRESS" ->
                                description = "Reached " + e.getProgressPercent() + "% progress";
                        default ->
                                description = "Enrolled in this course";
                    }
                    Instant ts = e.getLastAccessedAt() != null
                            ? e.getLastAccessedAt().toInstant()
                            : e.getUpdatedAt().toInstant();

                    return RecentActivityItem.builder()
                            .type("COURSE")
                            .title(e.getCourse().getTitle())
                            .description(description)
                            .timestamp(ts)
                            .build();
                })
                .collect(Collectors.toList());

        return StudentDashboardResponse.builder()
                .overview(overviewStats)
                .continueLearning(continueLearningCard)
                .courses(courseProgressSummaries)
                .recentActivity(recentActivity)
                .build();
    }

    // (Optional) helper you can use later to auto-enroll a user in a course
    public CourseEnrollmentEntity createEnrollment(UserEntity user, Long coursePk, String status) {
        CourseEnrollmentEntity enrollment = CourseEnrollmentEntity.builder()
                .enrollmentId(UUID.randomUUID().toString())
                .user(user)
                // set course via repository before calling this
                .status(status)
                .progressPercent(0)
                .lessonsCompleted(0)
                .totalLessons(0)
                .quizzesTaken(0)
                .build();
        return courseEnrollmentRepository.save(enrollment);
    }
}