package com.ecom.smartlearningplatform.filters;

import com.ecom.smartlearningplatform.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        String token = null;
        String username = null;

        // 1) Extract Bearer token from header
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(token);
            } catch (Exception ignored) {
                // Invalid token → just don't authenticate; request may still be allowed if endpoint is public
            }
        }

        // 2) If we got a username and no auth is set yet, validate and set authentication
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load user (keeps account flags/lock status consistent)
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(token, userDetails)) {

                // Build authorities from JWT claims (roles + extra permissions)
                List<GrantedAuthority> authorities = new ArrayList<>();

                // These methods should exist in your JwtUtil (same style as retail project)
                List<String> roles = jwtUtil.extractRoles(token);          // e.g. ["ROLE_STUDENT"]
                List<String> authz = jwtUtil.extractAuthorities(token);    // e.g. ["DASHBOARD_READ"]

                roles.forEach(r -> authorities.add(new SimpleGrantedAuthority(r)));
                authz.forEach(a -> authorities.add(new SimpleGrantedAuthority(a)));

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Put it into the SecurityContext so @PreAuthorize and hasRole() work
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 3) Continue filter chain
        filterChain.doFilter(request, response);
    }
}
