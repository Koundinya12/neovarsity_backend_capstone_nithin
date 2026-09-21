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
class OrderItemRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setUserId("user123");
        order.setOrderStatus(OrderStatus.INTIATED);
        order.setTotalAmount(300.0);
        order.setOrderDate(LocalDateTime.now());
        order.setMethod("UPI");

        order = orderRepository.save(order);

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProductId(101L);
        item.setProductName("Product A");
        item.setPrice(150.0);
        item.setQuantity(2);

        orderItemRepository.save(item);
    }

    @Test
    void testSaveOrderItem_ShouldPersistItem() {
        List<OrderItem> items = orderItemRepository.findAll();

        assertThat(items).isNotEmpty();
        assertThat(items.get(0).getProductName()).isEqualTo("Product A");
        assertThat(items.get(0).getOrder().getUserId()).isEqualTo("user123");
    }

    @Test
    void testDeleteOrderItem_ShouldRemoveFromDB() {
        OrderItem item = orderItemRepository.findAll().get(0);
        orderItemRepository.delete(item);

        List<OrderItem> items = orderItemRepository.findAll();
        assertThat(items).isEmpty();
    }
}
