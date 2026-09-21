package com.auth.dtos;

import com.auth.models.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    @NotNull
    private String username;

    @Email
    @NotNull
    private String email;

    @NotNull
    private String password;

    private Role role; // optional; default CUSTOMER
}