package com.ecom.smartlearningplatform.repository;

import com.ecom.smartlearningplatform.entity.CourseEnrollmentEntity;
import com.ecom.smartlearningplatform.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

}
