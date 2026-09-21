package com.ecom.userservice.models;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    @Id
    private String id;
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private String type; // e.g., "home", "work"
}
