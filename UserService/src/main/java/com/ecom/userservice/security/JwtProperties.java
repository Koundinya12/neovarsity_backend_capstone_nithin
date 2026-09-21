package com.ecom.userservice.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "security.jwt")
@Data
public class JwtProperties {
    private String secret;
    private long expirationMs;
}