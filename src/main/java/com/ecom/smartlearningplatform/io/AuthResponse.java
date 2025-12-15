package com.ecom.smartlearningplatform.io;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AuthResponse {

    private String email;
    private String token;
    private String role;
    private String userId;
    private String name;
    private List<String> authorities;

}