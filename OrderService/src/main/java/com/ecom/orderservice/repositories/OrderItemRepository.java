package com.ecom.orderservice.repositories;

import com.ecom.orderservice.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {}