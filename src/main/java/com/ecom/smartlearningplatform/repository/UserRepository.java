package com.ecom.smartlearningplatform.repository;

import com.ecom.smartlearningplatform.entity.UserEntity;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByUserId(String userId);

    boolean existsByEmail(String email);

    long countByRole(String role);

    // Recent signups (used for “new signups per week”)
    List<UserEntity> findByRoleAndCreatedAtAfter(String role, Timestamp createdAt);

}

