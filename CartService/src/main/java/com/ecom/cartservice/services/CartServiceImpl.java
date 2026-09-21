package com.ecom.cartservice.services;

import com.ecom.cartservice.configuration.ProductClient;
import com.ecom.cartservice.dtos.CartItemDto;
import com.ecom.cartservice.dtos.CartResponseDTO;
import com.ecom.cartservice.dtos.ProductDTO;
import com.ecom.cartservice.exceptions.CartNotFoundException;
import com.ecom.cartservice.exceptions.EmptyCartException;
import com.ecom.cartservice.exceptions.ProductNotInCartException;
import com.ecom.cartservice.models.Cart;
import com.ecom.cartservice.models.CartItem;

import com.ecom.cartservice.repositories.CartRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductClient productClient;
    private final RedisTemplate redisTemplate;
    private static final Logger log = LoggerFactory.getLogger(CartServiceImpl.class);
        public CartResponseDTO addToCart(String userId, Long productId, int quantity) {
            log.info("Adding product "+productId+" to cart");
            Cart cart = cartRepository.findByUserId(userId)
                    .orElseGet(() -> {
                        Cart newCart = new Cart();
                        newCart.setUserId(userId);
                        return cartRepository.save(newCart);
                    });

            ProductDTO product = productClient.getProductById(productId);

            // check if item already exists
            CartItem item = cart.getItems().stream()
                    .filter(i -> i.getProductId().equals(productId))
                    .findFirst()
                    .orElse(null);

            if (item == null) {
                item = new CartItem();
                item.setCart(cart);
                item.setProductId(product.getId());
                item.setProductName(product.getName());
                item.setPrice(product.getPrice());
                item.setQuantity(quantity);
                cart.getItems().add(item);
            } else {
                item.setQuantity(item.getQuantity() + quantity);
            }

            Cart saved = cartRepository.save(cart);
            log.info("Product "+product.getId()+" added to cart");
            return toDto(saved);
        }


        public CartResponseDTO getCart(String userId) throws EmptyCartException {
            //CartResponseDTO cartResp = (CartResponseDTO) redisTemplate.opsForHash().get("CART", "CART_" + userId);
           // if(cartResp!=null) return cartResp;
            log.info("Getting items from cart");
            Cart cart = cartRepository.findByUserId(userId)
                    .orElseThrow(() -> new EmptyCartException("Cart not found"));
            CartResponseDTO dto=toDto(cart);
            //redisTemplate.opsForHash().put("CART", "CART_" + userId,dto);
            return toDto(cart);
        }

        public CartResponseDTO toDto(Cart cart) {
            CartResponseDTO dto = new CartResponseDTO();
            dto.setCartId(cart.getId());

            List<CartItemDto> items = cart.getItems().stream().map(item -> {
                CartItemDto i = new CartItemDto();
                i.setProductId(item.getProductId());
                i.setProductName(item.getProductName());
                i.setPrice(item.getPrice());
                i.setQuantity(item.getQuantity());
                return i;
            }).toList();

            dto.setItems(items);
            dto.setTotalPrice(items.stream().mapToDouble(i -> i.getPrice() * i.getQuantity()).sum());
            return dto;
        }

    public CartResponseDTO removeFromCart(String userId, Long productId, int quantity) throws CartNotFoundException, ProductNotInCartException {
            log.info("Removing product "+productId+" from cart");
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found"));

        // find product in cart
        CartItem item = cart.getItems().stream()
                .filter(ci -> ci.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ProductNotInCartException("Product not in cart"));

        // reduce quantity
        if (item.getQuantity() > quantity) {
            item.setQuantity(item.getQuantity() - quantity);
        } else {
            // remove item completely if requested qty >= current qty
            cart.getItems().remove(item);
        }

        cartRepository.save(cart);
        log.info("Product "+productId+" removed from cart");
        return toDto(cart);
    }

    @Override
    public CartResponseDTO clearCart(String userId) {
            log.info("Clearing cart");
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        // clear items
        cart.getItems().clear();

       cartRepository.save(cart);
       log.info("Cart cleared");
       return toDto(cart);
    }
}