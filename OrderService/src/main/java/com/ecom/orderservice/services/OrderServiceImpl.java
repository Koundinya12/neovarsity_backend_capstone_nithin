package com.ecom.orderservice.services;

import com.ecom.common.events.PaymentEvent;
import com.ecom.orderservice.configuration.CartClient;
import com.ecom.orderservice.configuration.PaymentClient;
import com.ecom.orderservice.configuration.UserClient;
import com.ecom.orderservice.dtos.*;
import com.ecom.orderservice.exceptions.CartNotFoundException;
import com.ecom.orderservice.exceptions.EmptyCartException;
import com.ecom.orderservice.mappers.OrderMapper;
import com.ecom.orderservice.models.*;
import com.ecom.orderservice.repositories.OrderRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class OrderServiceImpl implements  OrderService{
    private final OrderRepository orderRepository;
    private final UserClient userClient;
    private final CartClient cartClient;
    private final PaymentClient paymentClient;
    private final RedisTemplate redisTemplate;

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    public OrderServiceImpl(OrderRepository orderRepository, UserClient userClient, CartClient cartClient, PaymentClient paymentClient, RedisTemplate redisTemplate) {
        this.orderRepository = orderRepository;
        this.userClient = userClient;
        this.cartClient = cartClient;
        this.paymentClient = paymentClient;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public OrderResponseDTO placeOrder(String id,String method) throws CartNotFoundException, EmptyCartException {
        log.info("Placing order for user "+id);
        ResponseEntity<CartResponseDTO> cartResponse = cartClient.getCartById(id);

//        if (cart == null) {
//            throw new CartNotFoundException("Cart not found");
//        }
        if (cartResponse.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(400))) {
            throw new EmptyCartException("Cart is empty");
        }

        CartResponseDTO cart = cartResponse.getBody();

        // ✅ Build and save order
        Long orderId=(Math.abs(new Random().nextLong()));
        Order order = new Order();
        order.setUserId(id);
        order.setItems(
                cart.getItems().stream().map(item -> {
                    OrderItem oi = new OrderItem();
                    oi.setProductId(item.getProductId());
                    oi.setProductName(item.getProductName());
                    oi.setPrice(item.getPrice());
                    oi.setQuantity(item.getQuantity());
                    oi.setOrder(order);
                    return oi;
                }).toList()
        );
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus(OrderStatus.INTIATED);
        order.setOrderDate(LocalDateTime.now());
        order.setId(orderId);
        order.setMethod(method);

        PaymentRequestDto paymentRequestDto = new PaymentRequestDto();
        paymentRequestDto.setAmount(cart.getTotalPrice());
        paymentRequestDto.setOrderId(orderId);
        paymentRequestDto.setUserId(id);
        paymentRequestDto.setMethod(method);

        PaymentResponseDTO paymentResponseDTO=paymentClient.processPayment(paymentRequestDto);

        Order savedOrder = orderRepository.save(order);
        log.info("Order Placed ");

        OrderResponseDTO orderDto = OrderMapper.toDto(savedOrder);
        redisTemplate.opsForHash().put("ORDERS", "ORDER_" + savedOrder.getId(), orderDto);


        // clear cart after placing order
        cartClient.clearCartById(id);

        return OrderMapper.toDto(savedOrder);
    }

    public List<OrderResponseDTO> getOrders(String userId) {

        log.info("Retrieving orders for user "+userId);
        List<OrderResponseDTO> orders = redisTemplate.opsForHash()
                .values("ORDERS")
                .stream()
                .map(obj -> (OrderResponseDTO) obj)
                .toList();
        if (!orders.isEmpty()) return orders;
        List<Order> ordersList = orderRepository.getOrderByUserId(userId);

        return ordersList.stream()
                .map(OrderMapper::toDto)
                .toList();
    }

    @Override
    public OrderResponseDTO updateOrderStatus(PaymentEvent event) {
        log.info("Updating order "+event.getOrderId());
        Order order=orderRepository.getOrderById(event.getOrderId());
        order.setOrderStatus(OrderStatus.PLACED);
        log.info("Order Updated to status  "+order.getOrderStatus());
        return OrderMapper.toDto(orderRepository.save(order));
    }
}
