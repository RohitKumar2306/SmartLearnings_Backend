package com.ecom.smartlearningplatform.security;

import java.util.Set;

import static com.ecom.smartlearningplatform.security.AppAuthority.*;

public enum AppRole {

    ROLE_STUDENT(Set.of(
            DASHBOARD_READ,
            COURSES_READ,
            QUIZZES_READ
    )),

    ROLE_INSTRUCTOR(Set.of(
            DASHBOARD_READ,
            COURSES_READ, COURSES_WRITE,
            QUIZZES_READ, QUIZZES_WRITE
    )),

    ROLE_ORG_ADMIN(Set.of(
            DASHBOARD_READ,
            COURSES_READ, COURSES_WRITE,
            QUIZZES_READ, QUIZZES_WRITE,
            USERS_MANAGE
    )),

    ROLE_PLATFORM_ADMIN(Set.of(
            DASHBOARD_READ,
            COURSES_READ, COURSES_WRITE,
            QUIZZES_READ, QUIZZES_WRITE,
            USERS_MANAGE,
            ORGS_MANAGE
    ));

    private final Set<AppAuthority> authorities;

    AppRole(Set<AppAuthority> authorities) {
        this.authorities = authorities;
    }

    public Set<AppAuthority> getAuthorities() {
        return authorities;
    }

}
