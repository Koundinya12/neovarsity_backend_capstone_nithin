package com.ecom.userservice.controllers;

import com.ecom.userservice.dtos.UserResponseDto;
import com.ecom.userservice.exceptions.UserNameAlreadyExistsException;
import com.ecom.userservice.exceptions.UsernameNotFoundException;
import com.ecom.userservice.models.User;
import com.ecom.userservice.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public UserResponseDto getUserDetails(@PathVariable("id") String id) throws UsernameNotFoundException {
        log.info("get user details");
        UserResponseDto user= userService.getUserDetails(id);
        return user;
    }

    @PostMapping("/register")
    public UserResponseDto registerUser(@RequestBody UserResponseDto user) throws UserNameAlreadyExistsException {
        log.info("register user");
        UserResponseDto savedUser=userService.saveUser(user);
        return savedUser;
    }


}