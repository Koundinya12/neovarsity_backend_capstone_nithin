package com.ecom.orderservice.configuration;

import com.ecom.orderservice.dtos.CartResponseDTO;
import com.ecom.orderservice.dtos.PaymentRequestDto;
import com.ecom.orderservice.dtos.PaymentResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@Component
@FeignClient(name = "payment-service", path = "/payments", configuration = FeignConfig.class)
public interface PaymentClient {

    @PostMapping("/pay")
    PaymentResponseDTO processPayment(@RequestBody PaymentRequestDto paymentRequestDto);

}