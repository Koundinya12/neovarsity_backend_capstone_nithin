package com.ecom.orderservice.models;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "orders")
public class Order extends BaseClass implements Serializable {
    @Id
    private Long id;

    private String userId;
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus = OrderStatus.PLACED; // PENDING, PAID, SHIPPED

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items = new ArrayList<>();

    private double totalAmount;

    private String method;
}