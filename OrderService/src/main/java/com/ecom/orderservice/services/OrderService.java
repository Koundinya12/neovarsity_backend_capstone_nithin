package com.ecom.orderservice.services;

import com.ecom.common.events.PaymentEvent;
import com.ecom.orderservice.dtos.OrderResponseDTO;
import com.ecom.orderservice.exceptions.CartNotFoundException;
import com.ecom.orderservice.exceptions.EmptyCartException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderService{
    public OrderResponseDTO placeOrder(String id,String method) throws CartNotFoundException, EmptyCartException;
    public List<OrderResponseDTO> getOrders(String userId);

    public OrderResponseDTO updateOrderStatus(PaymentEvent event);
}
