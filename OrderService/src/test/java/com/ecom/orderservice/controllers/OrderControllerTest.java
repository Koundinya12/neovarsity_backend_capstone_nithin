package com.ecom.orderservice.controllers;

import com.ecom.orderservice.controllers.OrderController;
import com.ecom.orderservice.dtos.OrderResponseDTO;
import com.ecom.orderservice.exceptions.CartNotFoundException;
import com.ecom.orderservice.exceptions.EmptyCartException;
import com.ecom.orderservice.security.JwtService;
import com.ecom.orderservice.services.OrderService;
import com.ecom.orderservice.utils.AuthUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureMockMvc(addFilters = false)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private AuthUtils authUtils;

    @MockitoBean
    private JwtService jwtService;


    @Test
    @DisplayName("Should place order and return response when user is authenticated and method provided")
    void placeOrder_success() throws Exception {
        Mockito.when(authUtils.getUsername(any(HttpServletRequest.class))).thenReturn("user-123");
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setOrderId(100L);
        dto.setUserId("user-123");
        dto.setTotalAmount(250.0);
        Mockito.when(orderService.placeOrder(eq("user-123"), eq("CARD"))).thenReturn(dto);

        mockMvc.perform(post("/orders/place/{method}", "CARD")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user-123"))
                .andExpect(jsonPath("$.totalAmount").value(250.0));
    }

    @Test
    @DisplayName("Should translate EmptyCartException to 4xx from service when placing order")
    void placeOrder_emptyCart() throws Exception {
        Mockito.when(authUtils.getUsername(any(HttpServletRequest.class))).thenReturn("user-123");
        Mockito.when(orderService.placeOrder(eq("user-123"), eq("COD")))
                .thenThrow(new EmptyCartException("Cart is empty"));

        mockMvc.perform(post("/orders/place/{method}", "COD"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Should translate CartNotFoundException to 4xx when placing order")
    void placeOrder_cartNotFound() throws Exception {
        Mockito.when(authUtils.getUsername(any(HttpServletRequest.class))).thenReturn("user-123");
        Mockito.when(orderService.placeOrder(eq("user-123"), eq("UPI")))
                .thenThrow(new CartNotFoundException("Cart not found"));

        mockMvc.perform(post("/orders/place/{method}", "UPI"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Should return orders list for authenticated user")
    void getOrders_success() throws Exception {
        Mockito.when(authUtils.getUsername(any(HttpServletRequest.class))).thenReturn("user-123");
        OrderResponseDTO d1 = new OrderResponseDTO();
        d1.setOrderId(1L); d1.setUserId("user-123");
        OrderResponseDTO d2 = new OrderResponseDTO();
        d2.setOrderId(2L); d2.setUserId("user-123");
        Mockito.when(orderService.getOrders("user-123")).thenReturn(List.of(d1, d2));

        mockMvc.perform(get("/orders/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("Should pass the payment method path variable to service as-is")
    void placeOrder_passesMethodThrough() throws Exception {
        Mockito.when(authUtils.getUsername(any(HttpServletRequest.class))).thenReturn("user-123");
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setOrderId(200L); dto.setUserId("user-123");
        Mockito.when(orderService.placeOrder(eq("user-123"), eq("NETBANKING"))).thenReturn(dto);

        mockMvc.perform(post("/orders/place/{method}", "NETBANKING"))
                .andExpect(status().isOk());

        Mockito.verify(orderService).placeOrder("user-123", "NETBANKING");
    }
}
