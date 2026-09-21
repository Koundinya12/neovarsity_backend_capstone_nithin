package com.ecom.cartservice.services;

import com.ecom.cartservice.dtos.CartResponseDTO;
import com.ecom.cartservice.exceptions.CartNotFoundException;
import com.ecom.cartservice.exceptions.EmptyCartException;
import com.ecom.cartservice.exceptions.ProductNotInCartException;
import com.ecom.cartservice.models.Cart;
import org.springframework.stereotype.Service;

@Service
public interface CartService{
    public CartResponseDTO addToCart(String username, Long productId, int quantity);
    public CartResponseDTO getCart(String username) throws EmptyCartException;
    public CartResponseDTO toDto(Cart cart);
    public CartResponseDTO removeFromCart(String username, Long productId, int quantity) throws CartNotFoundException, ProductNotInCartException;
    public CartResponseDTO clearCart(String userName);
}
