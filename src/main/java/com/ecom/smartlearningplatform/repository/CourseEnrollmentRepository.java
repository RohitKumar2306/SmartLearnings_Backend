package com.ecom.smartlearningplatform.repository;

import com.ecom.smartlearningplatform.entity.CourseEnrollmentEntity;
import com.ecom.smartlearningplatform.entity.CourseEntity;
import com.ecom.smartlearningplatform.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollmentEntity, Long> {

    List<CourseEnrollmentEntity> findByUserOrderByUpdatedAtDesc(UserEntity user);

    long countByUserAndStatus(UserEntity user, String status);

    @Query("select coalesce(sum(e.lessonsCompleted), 0) " +
            "from CourseEnrollmentEntity e where e.user = :user")
    int sumLessonsCompletedByUser(@Param("user") UserEntity user);

    @Query("select coalesce(sum(e.quizzesTaken), 0) " +
            "from CourseEnrollmentEntity e where e.user = :user")
    int sumQuizzesTakenByUser(@Param("user") UserEntity user);

    // 👇 NEW: course-level stats for admin

    long countByCourse(CourseEntity course);

    long countByCourseAndStatus(CourseEntity course, String status);

    @Query("select coalesce(avg(e.progressPercent), 0) " +
            "from CourseEnrollmentEntity e where e.course = :course")
    double avgProgressPercentByCourse(@Param("course") CourseEntity course);

    // Engagement – distinct active learners since date
    @Query("select count(distinct e.user.id) " +
            "from CourseEnrollmentEntity e " +
            "where e.lastAccessedAt >= :since")
    long countActiveLearnersSince(@Param("since") Timestamp since);

    // For activity-by-day (simple version)
    @Query("select e from CourseEnrollmentEntity e " +
            "where e.lastAccessedAt >= :since")
    List<CourseEnrollmentEntity> findByLastAccessedAtAfter(@Param("since") Timestamp since);
}
