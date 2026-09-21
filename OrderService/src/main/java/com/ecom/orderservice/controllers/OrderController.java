package com.ecom.orderservice.controllers;



import com.ecom.orderservice.dtos.OrderResponseDTO;
import com.ecom.orderservice.exceptions.*;
import com.ecom.orderservice.security.JwtService;
import com.ecom.orderservice.services.OrderService;
import com.ecom.orderservice.services.OrderServiceImpl;
import com.ecom.orderservice.utils.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")

public class OrderController {
    private final OrderService orderService;
    private final JwtService jwtService;
    private final AuthUtils authUtils;

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    public OrderController(OrderService orderService, JwtService jwtService, AuthUtils authUtils) {
        this.orderService = orderService;
        this.jwtService = jwtService;
        this.authUtils = authUtils;
    }

    @PostMapping("/place/{method}")
    public OrderResponseDTO placeOrder(HttpServletRequest request,@PathVariable String method) throws EmptyCartException, CartNotFoundException {
        log.info("Inside order controller, placing order");
        String userId = authUtils.getUsername(request);
        return orderService.placeOrder(userId,method);
    }

    @GetMapping("/")
    public List<OrderResponseDTO> getOrders(HttpServletRequest request){
        log.info("Inside order controller, retrieving orders");
        String userId = authUtils.getUsername(request);
        return orderService.getOrders(userId);
    }
}