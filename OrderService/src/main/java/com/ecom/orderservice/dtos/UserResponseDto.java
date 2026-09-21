package com.ecom.orderservice.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class UserResponseDto {
    private String id;
    private String name;
    private String email;
}
