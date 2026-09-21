package com.ecom.userservice.services;

import com.ecom.userservice.dtos.UserResponseDto;
import com.ecom.userservice.exceptions.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface UserService {
    //public UserResponseDto getUserDetails(String username) throws UsernameNotFoundException;
    public UserResponseDto getUserDetails(String id) throws UsernameNotFoundException;
    public UserResponseDto saveUser(UserResponseDto user);
}
