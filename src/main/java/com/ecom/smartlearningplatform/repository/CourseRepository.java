package com.ecom.smartlearningplatform.repository;

import com.ecom.smartlearningplatform.entity.CourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface CourseRepository extends JpaRepository<CourseEntity, Long> {

    Optional<CourseEntity> findByCourseId(String courseId);

    // For KPI cards
    long countByPublished(boolean published);

    // “pending review” = not published for now
    List<CourseEntity> findByPublishedFalse();
}
