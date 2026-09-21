package com.ecom.orderservice.repositories;

import com.ecom.orderservice.models.Order;
import com.ecom.orderservice.models.OrderItem;
import com.ecom.orderservice.models.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EntityScan(basePackages = "com.ecom.orderservice.models")
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(1L);
        order.setUserId("user123");
        order.setOrderStatus(OrderStatus.INTIATED);
        order.setTotalAmount(200.0);
        order.setUpdatedAt(LocalDateTime.now());
        order.setMethod("COD");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        order = orderRepository.save(order);
    }

    @Test
    void testGetOrderByUserId_ShouldReturnOrders() {
        List<Order> orders = orderRepository.getOrderByUserId("user123");

        assertThat(orders).isNotEmpty();
        assertThat(orders.get(0).getUserId()).isEqualTo("user123");
    }

    @Test
    void testGetOrderByUserId_NoOrders_ShouldReturnEmptyList() {
        List<Order> orders = orderRepository.getOrderByUserId("unknownUser");

        assertThat(orders).isEmpty();
    }

    @Test
    void testGetOrderById_ShouldReturnOrder() {
        Order foundOrder = orderRepository.getOrderById(order.getId());

        assertThat(foundOrder).isNotNull();
        assertThat(foundOrder.getId()).isEqualTo(order.getId());
    }
}
