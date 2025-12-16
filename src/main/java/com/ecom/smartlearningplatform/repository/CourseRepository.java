package com.ecom.smartlearningplatform.repository;

import com.ecom.smartlearningplatform.entity.CourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<CourseEntity, Long> {

    Optional<CourseEntity> findByCourseId(String courseId);

}
