package com.ecom.productservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProductRequestDto {
    private Long productId;
    private String name;
    private String description;
    private Double price;
    private Long categoryId;
}
