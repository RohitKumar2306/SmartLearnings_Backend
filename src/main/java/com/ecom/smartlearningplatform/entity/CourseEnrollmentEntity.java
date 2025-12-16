package com.ecom.smartlearningplatform.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Table(
        name = "tbl_course_enrollments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_enrollment_user_course",
                        columnNames = {"user_id", "course_fk_id"}
                )
        }
)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseEnrollmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // public identifier for this enrollment
    @Column(nullable = false, unique = true, length = 64)
    private String enrollmentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_fk_id")
    private CourseEntity course;

    /**
     * Enrollment status:
     * NOT_STARTED / IN_PROGRESS / COMPLETED
     */
    @Column(nullable = false, length = 32)
    private String status;

    /**
     * Overall progress 0–100 for this course.
     */
    @Column(nullable = false)
    private int progressPercent;

    /**
     * Denormalized counters – good enough for dashboard.
     */
    @Column(nullable = false)
    private int lessonsCompleted;

    @Column(nullable = false)
    private int totalLessons;

    @Column(nullable = false)
    private int quizzesTaken;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;
    private Timestamp lastAccessedAt;

}
