package com.ecom.paymentservice.utils;


import com.ecom.paymentservice.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class AuthUtils {
    private final JwtService jwtService;

    public AuthUtils(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public String getUsername(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        // Extract userId from token (assuming you store it as claim "userId")
        String userId = jwtService.extractUserId(token);
        return userId;
    }
}
