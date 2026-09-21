package com.ecom.cartservice.controllers;

import com.ecom.cartservice.dtos.CartResponseDTO;
import com.ecom.cartservice.exceptions.CartNotFoundException;
import com.ecom.cartservice.exceptions.EmptyCartException;
import com.ecom.cartservice.exceptions.ProductNotInCartException;
import com.ecom.cartservice.security.JwtService;
import com.ecom.cartservice.services.CartService;
import com.ecom.cartservice.utils.AuthUtils;
import org.junit.jupiter.api.BeforeEach;
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

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AuthUtils authUtils;

    private CartResponseDTO mockCartResponse;

    @BeforeEach
    void setUp() {
        mockCartResponse = new CartResponseDTO();
        mockCartResponse.setCartId(123L);
    }

    @Test
    void testAddToCart() throws Exception {
        Mockito.when(authUtils.getUsername(any(HttpServletRequest.class))).thenReturn("user123");
        Mockito.when(cartService.addToCart(eq("user123"), eq(1L), eq(2)))
                .thenReturn(mockCartResponse);

        mockMvc.perform(post("/cart/add")
                        .param("productId", "1")
                        .param("qty", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value("123"));
    }

    @Test
    void testRemoveFromCart() throws Exception {
        Mockito.when(authUtils.getUsername(any(HttpServletRequest.class))).thenReturn("user123");
        Mockito.when(cartService.removeFromCart(eq("user123"), eq(1L), eq(1)))
                .thenReturn(mockCartResponse);

        mockMvc.perform(put("/cart/remove")
                        .param("productId", "1")
                        .param("qty", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value("123"));
    }

    @Test
    void testGetCart() throws Exception {
        Mockito.when(authUtils.getUsername(any(HttpServletRequest.class))).thenReturn("user123");
        Mockito.when(cartService.getCart("user123")).thenReturn(mockCartResponse);

        mockMvc.perform(get("/cart/get-cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value("123"));
    }

    @Test
    void testGetCartById() throws Exception {
        Mockito.when(cartService.getCart("cart123")).thenReturn(mockCartResponse);

        mockMvc.perform(get("/cart/get-cart-id/{id}", "cart123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value("123"));
    }

    @Test
    void testClearCart() throws Exception {
        Mockito.when(authUtils.getUsername(any(HttpServletRequest.class))).thenReturn("user123");
        Mockito.when(cartService.clearCart("user123")).thenReturn(mockCartResponse);

        mockMvc.perform(delete("/cart/clear"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value("123"));
    }

    @Test
    void testClearCartById() throws Exception {
        Mockito.when(cartService.clearCart("cart123")).thenReturn(mockCartResponse);

        mockMvc.perform(delete("/cart/clear-cart/{id}", "cart123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value("123"));
    }

    @Test
    void testGetCartThrowsEmptyCartException() throws Exception {
        Mockito.when(authUtils.getUsername(any(HttpServletRequest.class))).thenReturn("user123");
        Mockito.when(cartService.getCart("user123")).thenThrow(new EmptyCartException("Cart is empty"));

        mockMvc.perform(get("/cart/get-cart"))
                .andExpect(status().isBadRequest()); // Adjust based on your @ExceptionHandler
    }

    @Test
    void testRemoveFromCartThrowsProductNotInCartException() throws Exception {
        Mockito.when(authUtils.getUsername(any(HttpServletRequest.class))).thenReturn("user123");
        Mockito.when(cartService.removeFromCart(anyString(), anyLong(), anyInt()))
                .thenThrow(new ProductNotInCartException("Product not found"));

        mockMvc.perform(put("/cart/remove")
                        .param("productId", "5")
                        .param("qty", "1"))
                .andExpect(status().isBadRequest()); // Adjust based on exception mapping
    }
}
