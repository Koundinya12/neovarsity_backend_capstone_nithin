package com.auth.security;

import com.auth.configuration.UserClient;
import com.auth.dtos.AuthResponse;
import com.auth.dtos.LoginRequest;
import com.auth.dtos.RegisterRequest;
import com.auth.dtos.UserResponseDto;
import com.auth.exceptions.UserNameAlreadyExists;
import com.auth.models.User;
import com.auth.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// services/AuthService.java
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserClient userClient;

    public AuthResponse register(RegisterRequest req) {
        if (userRepo.existsByUsername(req.getUsername())) throw new UserNameAlreadyExists("Username taken");
        if (userRepo.existsByEmail(req.getEmail())) throw new RuntimeException("Email taken");

        User u = new User();
        u.setUsername(req.getUsername());
        u.setEmail(req.getEmail());
        u.setPassword(encoder.encode(req.getPassword()));

        if (req.getRole() != null) u.setRole(req.getRole());
        userRepo.save(u);
        userClient.registerUser(new UserResponseDto(u.getId(),u.getUsername(),u.getEmail()));
        String token = jwtService.generateToken(u);
        return new AuthResponse(token, u.getRole().name());
    }

    public AuthResponse login(LoginRequest req) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword());
        authManager.authenticate(auth); // throws if bad creds
        User user = userRepo.findByUsername(req.getUsername()).orElseThrow();
        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getRole().name());
    }
}
