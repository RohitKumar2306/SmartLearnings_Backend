package com.ecom.smartlearningplatform.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import java.util.*;

@Component
public class JwtUtil {

    @Value("${jwt.secret.key}")
    private String SECRET_KEY;

    private Key getSigningKey() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        // For HS256, key should be at least 32 bytes
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // ===== Token creation =====
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = buildClaims(userDetails);
        return createToken(claims, userDetails.getUsername());
    }

    private Map<String, Object> buildClaims(UserDetails userDetails) {
        // split ROLE_* vs other authorities
        List<String> all = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .distinct()
                .toList();

        List<String> roles = all.stream()
                .filter(a -> a != null && a.startsWith("ROLE_"))
                .toList();

        List<String> authorities = all.stream()
                .filter(a -> a != null && !a.startsWith("ROLE_"))
                .toList();

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        claims.put("authorities", authorities);
        return claims;
    }

    private String createToken(Map<String, Object> claims, String username) {
        // 10 hours validity (same as your previous project)
        Date now = new Date(System.currentTimeMillis());
        Date exp = new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 10);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ===== Token parsing =====

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public List<String> extractRoles(String token) {
        return extractStringListClaim(token, "roles");
    }

    public List<String> extractAuthorities(String token) {
        return extractStringListClaim(token, "authorities");
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    @SuppressWarnings("unchecked")
    private List<String> extractStringListClaim(String token, String key) {
        Claims claims = extractAllClaims(token);
        Object raw = claims.get(key);
        if (raw == null) return Collections.emptyList();
        if (raw instanceof List<?>) {
            return ((List<?>) raw).stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .collect(Collectors.toList());
        }
        return Collections.singletonList(raw.toString());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

}
