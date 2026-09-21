package com.ecom.orderservice.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartResponseDTO {
    private Long cartId;
    private List<CartItemDto> items;
    private double totalPrice;
}
