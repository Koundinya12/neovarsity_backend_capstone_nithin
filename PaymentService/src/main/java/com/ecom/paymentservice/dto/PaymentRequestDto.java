package com.ecom.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDto {
    private String userId;
    private Long orderId;
    private double amount;
    private String method; // CARD, UPI etc.
}
