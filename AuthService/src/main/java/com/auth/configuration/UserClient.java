package com.auth.configuration;

import com.auth.dtos.UserResponseDto;
import com.auth.models.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@FeignClient(name = "user-service",path="/users", configuration = FeignConfig.class)
public interface UserClient {

    @PostMapping("/register")
    UserResponseDto registerUser(@RequestBody UserResponseDto user);
}