package com.ecom.paymentservice.Gateway;

import com.ecom.paymentservice.Exceptions.PaymentException;
import com.ecom.paymentservice.dto.PaymentRequestDto;
import com.ecom.paymentservice.dto.PaymentResponseDTO;

import com.ecom.paymentservice.models.Payment;
import org.springframework.stereotype.Component;

@Component
public interface PaymentGateway {
    Payment processPayment(String userId,Long orderId, double amount,String method) throws PaymentException;
}
