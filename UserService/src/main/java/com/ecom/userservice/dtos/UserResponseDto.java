package com.ecom.userservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UserResponseDto{
    private String id;
    private String name;
    private String email;

    public UserResponseDto() {

    }
}
