package com.ecom.orderservice.repositories;

import com.ecom.orderservice.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> getOrderByUserId(String userId);

    Order getOrderById(Long orderId);
}
