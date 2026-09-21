package com.ecom.productservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequestDto {
    private Long productId;
    private String name;
    private String description;
    private double price;
    private Long categoryId;
}
