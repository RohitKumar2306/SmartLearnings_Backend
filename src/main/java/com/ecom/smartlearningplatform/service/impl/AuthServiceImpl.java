package com.ecom.smartlearningplatform.service.impl;

import com.ecom.smartlearningplatform.io.AuthRequest;
import com.ecom.smartlearningplatform.io.AuthResponse;
import com.ecom.smartlearningplatform.io.RegisterRequest;
import com.ecom.smartlearningplatform.io.RegisterResponse;
import com.ecom.smartlearningplatform.service.AuthService;
import com.ecom.smartlearningplatform.service.UserService;
import com.ecom.smartlearningplatform.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse login(AuthRequest authRequest) {
        String email = authRequest.getEmail().trim().toLowerCase();
        String password = authRequest.getPassword();

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        }  catch (DisabledException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is disabled");
        }  catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email or password is invalid");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        String token = jwtUtil.generateToken(userDetails);

        String role = userService.getUserRole(email);
        RegisterResponse registerResponse = userService.getUserByEmail(email);

        List<String> authorities = jwtUtil.extractAuthorities(token);

        return new AuthResponse(
                email,
                token,
                role,
                registerResponse.getUserId(),
                registerResponse.getName(),
                authorities
        );

    }

    @Override
    public AuthResponse register(RegisterRequest registerRequest) {
        RegisterResponse registerResponse = userService.createUser(registerRequest);

        UserDetails userDetails = userDetailsService.loadUserByUsername(registerResponse.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        List<String> authorities = jwtUtil.extractAuthorities(token);

        return new AuthResponse(
                registerRequest.getEmail(),
                token,
                registerRequest.getRole(),
                registerResponse.getName(),
                registerResponse.getUserId(),
                authorities
        );
    }
}
