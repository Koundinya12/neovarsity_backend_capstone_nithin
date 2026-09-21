package com.ecom.orderservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemDto {
    private Long productId;
    private String productName;
    private double price;
    private int quantity;
}
