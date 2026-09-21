package com.ecom.productservice.mappers;


import com.ecom.productservice.dtos.ProductResponseDto;
import com.ecom.productservice.models.Product;

public class ProductMapper {

    public static ProductResponseDto toDto(Product product) {
        return ProductResponseDto.builder()
                .id(product.getProductId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}