package com.ecom.orderservice.service;

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
import com.ecom.orderservice.services.OrderService;
import com.ecom.orderservice.services.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserClient userClient;

    @Mock
    private CartClient cartClient;

    @Mock
    private PaymentClient paymentClient;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @InjectMocks
    private OrderServiceImpl orderService;

    private CartResponseDTO mockCartResponse;
    private PaymentResponseDTO mockPaymentResponse;
    private Order mockOrder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock Redis hash operations
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);

        // Mock CartResponseDTO
        CartItemDto item = new CartItemDto();
        item.setProductId(1L);
        item.setProductName("Laptop");
        item.setPrice(1000.0);
        item.setQuantity(1);

        mockCartResponse = new CartResponseDTO();
        mockCartResponse.setItems(List.of(item));
        mockCartResponse.setTotalPrice(1000.0);
        mockCartResponse.setCartId(123L);

        // Mock payment response
        mockPaymentResponse = new PaymentResponseDTO();
        mockPaymentResponse.setPaymentStatus(PaymentStatus.SUCCESS);

        // Mock order
        mockOrder = new Order();
        mockOrder.setId(123L);
        mockOrder.setUserId("user123");
        mockOrder.setTotalAmount(1000.0);
        mockOrder.setOrderStatus(OrderStatus.INTIATED);
        mockOrder.setOrderDate(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should place order successfully when cart is valid")
    void testPlaceOrder_Success() throws CartNotFoundException, EmptyCartException {
        // Arrange
        ResponseEntity<CartResponseDTO> cartResponseEntity = ResponseEntity.ok(mockCartResponse);

        when(cartClient.getCartById("user123")).thenReturn(cartResponseEntity);
        when(paymentClient.processPayment(any(PaymentRequestDto.class))).thenReturn(mockPaymentResponse);
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        // Act
        OrderResponseDTO result = orderService.placeOrder("user123", "CARD");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo("user123");

        verify(cartClient, times(1)).getCartById("user123");
        verify(paymentClient, times(1)).processPayment(any());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(hashOperations, times(1)).put(eq("ORDERS"), contains("ORDER_"), any(OrderResponseDTO.class));
        verify(cartClient, times(1)).clearCartById("user123");
    }

    @Test
    @DisplayName("Should throw EmptyCartException when cart is empty")
    void testPlaceOrder_EmptyCart() {
        // Arrange
        ResponseEntity<CartResponseDTO> cartResponse =
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        when(cartClient.getCartById("user123")).thenReturn(cartResponse);

        // Act + Assert
        assertThatThrownBy(() -> orderService.placeOrder("user123", "CARD"))
                .isInstanceOf(EmptyCartException.class)
                .hasMessageContaining("Cart is empty");

        verify(cartClient, times(1)).getCartById("user123");
    }

    @Test
    @DisplayName("Should get orders from Redis if present")
    void testGetOrders_FromRedis() {
        // Arrange
        OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
        orderResponseDTO.setOrderId(1L);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.values("ORDERS")).thenReturn(List.of(orderResponseDTO));

        // Act
        List<OrderResponseDTO> orders = orderService.getOrders("user123");

        // Assert
        assertThat(orders).hasSize(1);
        verify(hashOperations, times(1)).values("ORDERS");
        verify(orderRepository, never()).getOrderByUserId(any());
    }

    @Test
    @DisplayName("Should fetch orders from DB when Redis is empty")
    void testGetOrders_FromDB() {
        // Arrange
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.values("ORDERS")).thenReturn(List.of());
        when(orderRepository.getOrderByUserId("user123")).thenReturn(List.of(mockOrder));

        // Act
        List<OrderResponseDTO> orders = orderService.getOrders("user123");

        // Assert
        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).getUserId()).isEqualTo("user123");
        verify(orderRepository, times(1)).getOrderByUserId("user123");
    }

    @Test
    @DisplayName("Should update order status on payment event")
    void testUpdateOrderStatus() {
        // Arrange
        PaymentEvent event = new PaymentEvent();
        event.setOrderId(123L);

        mockOrder.setOrderStatus(OrderStatus.INTIATED);

        when(orderRepository.getOrderById(123L)).thenReturn(mockOrder);
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        // Act
        OrderResponseDTO response = orderService.updateOrderStatus(event);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OrderStatus.PLACED.name());

        verify(orderRepository, times(1)).getOrderById(123L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }
}
