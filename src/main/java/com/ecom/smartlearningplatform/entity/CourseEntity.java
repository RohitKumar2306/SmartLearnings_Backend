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
@Table(name = "tbl_courses")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // public course identifier (for URLs, frontend)
    @Column(nullable = false, unique = true, length = 64)
    private String courseId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 64)
    private String level;  // BEGINNER / INTERMEDIATE / ADVANCED, etc.

    @Column(length = 255)
    private String thumbnailUrl;

    @Column(nullable = false)
    private boolean published;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id")
    private UserEntity instructor;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

}
