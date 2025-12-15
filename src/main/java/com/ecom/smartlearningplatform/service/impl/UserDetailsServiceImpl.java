package com.ecom.smartlearningplatform.service.impl;

import com.ecom.smartlearningplatform.entity.UserEntity;
import com.ecom.smartlearningplatform.repository.UserRepository;
import com.ecom.smartlearningplatform.security.AppRole;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        try {
            AppRole role = AppRole.valueOf(userEntity.getRole());
            grantedAuthorities.add(new SimpleGrantedAuthority(role.name()));

            role.getAuthorities().forEach(authority -> grantedAuthorities.add(new SimpleGrantedAuthority(authority.name())));
        } catch (IllegalArgumentException e) {
            throw new UsernameNotFoundException("Invalid role for user: " + e.getMessage());
        }

        return new User(
                userEntity.getEmail(),
                userEntity.getPassword(),
                true, true, true, true,
                grantedAuthorities
        );

    }
}
