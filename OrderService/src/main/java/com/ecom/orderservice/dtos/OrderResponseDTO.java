package com.ecom.orderservice.dtos;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderResponseDTO {
    private Long orderId;
    private String userId;
    private double totalAmount;
    private LocalDateTime orderDate;
    private String status;
    private List<OrderItemDTO> items;
    private String method;
    private LocalDateTime createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDTO {
        private Long productId;
        private String productName;
        private double price;
        private int quantity;
    }
}