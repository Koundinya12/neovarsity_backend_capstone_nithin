package com.ecom.orderservice.configuration;

import com.ecom.orderservice.dtos.CartResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(name = "cart-service",path = "/cart", configuration = FeignConfig.class)
public interface CartClient {

    @GetMapping("/get-cart-id/{id}")
    ResponseEntity<CartResponseDTO> getCartById(@PathVariable("id") String id);

    @GetMapping("/get-cart")
    ResponseEntity<CartResponseDTO> getCart(HttpServletRequest request);



    @DeleteMapping("/clear-cart/{id}")
    void clearCartById(@PathVariable("id") String id);
}