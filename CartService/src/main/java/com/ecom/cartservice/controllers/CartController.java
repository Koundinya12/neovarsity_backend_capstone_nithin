package com.ecom.cartservice.controllers;

import com.ecom.cartservice.dtos.CartResponseDTO;
import com.ecom.cartservice.exceptions.CartNotFoundException;
import com.ecom.cartservice.exceptions.EmptyCartException;
import com.ecom.cartservice.exceptions.ProductNotInCartException;
import com.ecom.cartservice.security.JwtService;
import com.ecom.cartservice.services.CartService;
import com.ecom.cartservice.utils.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final JwtService jwtService;
    private final AuthUtils authUtils;

    private static final Logger log = LoggerFactory.getLogger(CartController.class);

    public CartController(CartService cartService, JwtService jwtService, AuthUtils authUtils) {
        this.cartService = cartService;
        this.jwtService = jwtService;
        this.authUtils = authUtils;
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponseDTO> addToCart(@RequestParam Long productId,
                                                     @RequestParam int qty, HttpServletRequest request) {


        // Extract userId from token (assuming you store it as claim "userId")
        log.info("Inside cart controller, adding product to cart");
        String userId = authUtils.getUsername(request);
        CartResponseDTO cart=cartService.addToCart(userId,productId,qty);
        return ResponseEntity.ok(cart);
    }


    @PutMapping("/remove")
    public ResponseEntity<CartResponseDTO> removeFromCart(@RequestParam Long productId,
                                               @RequestParam int qty,HttpServletRequest request) throws ProductNotInCartException, CartNotFoundException {
        log.info("Inside cart controller, removing product from cart");
        String userId = authUtils.getUsername(request);
        CartResponseDTO cartResponseDTO=cartService.removeFromCart(userId,productId,qty);
        return ResponseEntity.ok(cartResponseDTO);
    }

    @GetMapping("/get-cart")
    public ResponseEntity<CartResponseDTO> getCart(HttpServletRequest request) throws EmptyCartException {
        log.info("Inside cart controller, retrieving items from cart");
        String userId = authUtils.getUsername(request);
        CartResponseDTO cart=cartService.getCart(userId);
        return ResponseEntity.ok(cart);
    }

    @GetMapping("/get-cart-id/{id}")
    public ResponseEntity<CartResponseDTO> getCartById(@PathVariable("id") String id) throws EmptyCartException {
        log.info("Inside cart controller, retrieving items from cart with cart id "+id);
        CartResponseDTO cart=cartService.getCart(id);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<CartResponseDTO> clearCart(HttpServletRequest request) {
        log.info("Inside cart controller, clearing cart");
        String userId = authUtils.getUsername(request);
        CartResponseDTO updatedCart = cartService.clearCart(userId);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/clear-cart/{id}")
    public ResponseEntity<CartResponseDTO> clearCartById(@PathVariable("id") String id) {
         log.info("Inside cart controller, clearing cart with cart id "+id);
        CartResponseDTO updatedCart = cartService.clearCart(id);
        return ResponseEntity.ok(updatedCart);
    }
}