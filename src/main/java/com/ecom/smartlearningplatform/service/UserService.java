package com.ecom.smartlearningplatform.service;

import com.ecom.smartlearningplatform.io.RegisterRequest;
import com.ecom.smartlearningplatform.io.RegisterResponse;

import java.util.List;

public interface UserService {

    RegisterResponse createUser(RegisterRequest registerRequest);

    String getUserRole(String email);

    RegisterResponse getUserByEmail(String email);

    List<RegisterResponse> readUsers();

    void deleteUser(String userId);

}
