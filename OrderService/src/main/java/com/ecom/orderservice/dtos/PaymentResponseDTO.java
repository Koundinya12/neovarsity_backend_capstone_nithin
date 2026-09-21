package com.ecom.orderservice.dtos;

import com.ecom.orderservice.models.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentResponseDTO {
    private String userId;
    private Long paymentId;
    private PaymentStatus paymentStatus;
    private double amount;
}
