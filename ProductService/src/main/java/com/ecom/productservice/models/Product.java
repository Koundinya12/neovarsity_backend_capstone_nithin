package com.ecom.productservice.models;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Product extends BaseClass implements Serializable   {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;
    private String name;
    private String description;
    private double price;
    private String brand;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

}
