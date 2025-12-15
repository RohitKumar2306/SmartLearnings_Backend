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
@Table(name = "tbl_users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_tbl_users_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_tbl_users_user_id", columnNames = "userId")
        })
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // public identifier, used in URLs etc.
    @Column(nullable = false, unique = true, length = 64)
    private String userId;

    @Column(nullable = false, unique = true, length = 180)
    private String email;

    @Column(nullable = false)
    private String password;

    // AppRole enum name, e.g. ROLE_STUDENT, ROLE_INSTRUCTOR
    @Column(nullable = false, length = 40)
    private String role;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 64)
    private String timezone;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;
}