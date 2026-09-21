package com.auth.security;

import com.auth.dtos.AuthResponse;
import com.auth.dtos.LoginRequest;
import com.auth.dtos.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("POST /auth/register - success")
    void register_success() throws Exception {
        RegisterRequest req = new RegisterRequest();
        // Set required fields for RegisterRequest
        AuthResponse resp = new AuthResponse();
        Mockito.when(authService.register(Mockito.any())).thenReturn(resp);
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /auth/login - success")
    void login_success() throws Exception {
        LoginRequest req = new LoginRequest();
        // Set required fields for LoginRequest
        AuthResponse resp = new AuthResponse();
        Mockito.when(authService.login(Mockito.any())).thenReturn(resp);
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /auth/register - validation error")
    void register_validationError() throws Exception {
        RegisterRequest req = new RegisterRequest(); // missing required fields
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /auth/login - validation error")
    void login_validationError() throws Exception {
        LoginRequest req = new LoginRequest(); // missing required fields
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }
}

