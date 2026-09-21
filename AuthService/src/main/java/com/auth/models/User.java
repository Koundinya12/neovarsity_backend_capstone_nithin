package com.auth.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.Type;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@Data
public class User implements UserDetails {

    @Id
    private String id=UUID.randomUUID().toString();
    private String username;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role = Role.CUSTOMER;


    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override public String getUsername() { return username; }
    @Override public boolean isAccountNonExpired(){return true;}
    @Override public boolean isAccountNonLocked(){return true;}
    @Override public boolean isCredentialsNonExpired(){return true;}
    @Override public boolean isEnabled(){return true;}
}