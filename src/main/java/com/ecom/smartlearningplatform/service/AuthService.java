package com.ecom.smartlearningplatform.service;

import com.ecom.smartlearningplatform.io.AuthRequest;
import com.ecom.smartlearningplatform.io.AuthResponse;
import com.ecom.smartlearningplatform.io.RegisterRequest;

public interface AuthService {

    AuthResponse login(AuthRequest authRequest);

    AuthResponse register(RegisterRequest registerRequest);

}
