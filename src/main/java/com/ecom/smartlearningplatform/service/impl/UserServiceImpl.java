package com.ecom.smartlearningplatform.service.impl;

import com.ecom.smartlearningplatform.entity.UserEntity;
import com.ecom.smartlearningplatform.io.RegisterRequest;
import com.ecom.smartlearningplatform.io.RegisterResponse;
import com.ecom.smartlearningplatform.repository.UserRepository;
import com.ecom.smartlearningplatform.security.AppRole;
import com.ecom.smartlearningplatform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public RegisterResponse createUser(RegisterRequest registerRequest) {
        String email = registerRequest.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("Email already in use");
        }

        String roleName = registerRequest.getRole() != null && !registerRequest.getRole().isEmpty()
                ? registerRequest.getRole() : AppRole.ROLE_STUDENT.name();

        AppRole.valueOf(roleName);

        UserEntity userEntity = UserEntity.builder()
                .userId(UUID.randomUUID().toString())
                .name(registerRequest.getName())
                .email(email)
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(roleName)
                .timezone(registerRequest.getTimezone())
                .build();

        UserEntity savedUser = userRepository.save(userEntity);

        return convertToResponse(savedUser);
    }

    private RegisterResponse convertToResponse(UserEntity savedUser) {
        return RegisterResponse.builder()
                .userId(savedUser.getUserId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .timezone(savedUser.getTimezone())
                .build();
    }

    @Override
    public String getUserRole(String email) {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return userEntity.getRole();
    }

    @Override
    public RegisterResponse getUserByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return convertToResponse(userEntity);
    }

    @Override
    public List<RegisterResponse> readUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(String userId) {
        UserEntity existingUser = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        userRepository.delete(existingUser);

    }
}
