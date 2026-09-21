package com.ecom.orderservice.mappers;

import com.ecom.orderservice.dtos.OrderResponseDTO;
import com.ecom.orderservice.models.Order;

public class OrderMapper {

    public static OrderResponseDTO toDto(Order order) {
        return OrderResponseDTO.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .totalAmount(order.getTotalAmount())
                .orderDate(order.getOrderDate())
                .status(order.getOrderStatus().toString())
                .method(order.getMethod())
                .createdAt(order.getCreatedAt())
                .items(order.getItems().stream().map(item ->
                        OrderResponseDTO.OrderItemDTO.builder()
                                .productId(item.getProductId())
                                .productName(item.getProductName())
                                .price(item.getPrice())
                                .quantity(item.getQuantity())
                                .build()
                ).toList())
                .build();
    }
}