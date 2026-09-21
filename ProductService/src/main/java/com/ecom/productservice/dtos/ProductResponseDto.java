package com.ecom.productservice.dtos;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductResponseDto {
    private Long id;
    private String name;
    private String description;
    private double price;
}