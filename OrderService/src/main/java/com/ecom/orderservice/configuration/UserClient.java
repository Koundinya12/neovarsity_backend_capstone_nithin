package com.ecom.orderservice.configuration;

import com.ecom.orderservice.dtos.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Component
@FeignClient(name = "user-service", path="/users", configuration = FeignConfig.class)
public interface UserClient {

    @GetMapping("/{id}")
    UserResponseDto getUserById(@PathVariable("id") String id);
}