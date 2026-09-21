package com.ecom.paymentservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;


@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @Id
    private Long id;

    private Long orderId;

    private String userId;

    private double amount;

    private String status;

    private String method;

    private String gateway;
}
