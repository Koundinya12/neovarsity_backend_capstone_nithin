package com.auth.security;


import com.auth.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    private Key key() { return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)); }

    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + jwtProperties.getExpirationMs());
        User user=(User) userDetails;
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("userId", user.getId().toString())
                .claim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .setIssuedAt(now).setExpiration(exp)
                .signWith(key(), SignatureAlgorithm.HS256).compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean isValid(String token, UserDetails user) {
        Claims c = Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody();
        return user.getUsername().equals(c.getSubject()) && c.getExpiration().after(new Date());
    }
}
