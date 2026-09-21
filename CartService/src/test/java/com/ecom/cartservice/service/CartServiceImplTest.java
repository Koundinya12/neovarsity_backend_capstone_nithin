package com.ecom.cartservice.service;

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
import com.ecom.cartservice.services.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductClient productClient;

    @Mock
    private RedisTemplate redisTemplate;

    @InjectMocks
    private CartServiceImpl cartService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        cartService = new CartServiceImpl(cartRepository, productClient, redisTemplate);
    }

    private Cart newEmptyCart(Long id, String userId) {
        Cart c = new Cart();
        c.setId(id);
        c.setUserId(userId);
        c.setItems(new ArrayList<>());
        return c;
    }

    private CartItem newItem(Cart cart, Long productId, String name, double price, int qty) {
        CartItem i = new CartItem();
        i.setCart(cart);
        i.setProductId(productId);
        i.setProductName(name);
        i.setPrice(price);
        i.setQuantity(qty);
        return i;
    }

    private ProductDTO product(long id, String name, double price) {
        ProductDTO p = new ProductDTO();
        p.setId(id);
        p.setName(name);
        p.setPrice(price);
        return p;
    }

    @Test
    @DisplayName("Should create new cart and add product when no cart exists")
    void addToCart_createsCartAndAddsItem() {
        String userId = "u1";
        Long productId = 101L;

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        // when saving new cart, return cart with id
        ArgumentCaptor<Cart> captor = ArgumentCaptor.forClass(Cart.class);
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> {
            Cart c = invocation.getArgument(0);
            if (c.getId() == null) c.setId(123L);
            return c;
        });
        when(productClient.getProductById(productId)).thenReturn(product(productId, "P101", 9.5));

        CartResponseDTO dto = cartService.addToCart(userId, productId, 2);

        verify(cartRepository, atLeastOnce()).save(captor.capture());
        Cart saved = captor.getValue();
        assertEquals("u1", saved.getUserId());
        assertEquals(1, saved.getItems().size());
        CartItem savedItem = saved.getItems().get(0);
        assertEquals(productId, savedItem.getProductId());
        assertEquals(2, savedItem.getQuantity());
        assertEquals("P101", savedItem.getProductName());
        assertEquals(9.5, savedItem.getPrice(), 0.0001);

        assertEquals(123L, dto.getCartId());
        assertEquals(1, dto.getItems().size());
        assertEquals(19.0, dto.getTotalPrice(), 0.0001);
    }

    @Test
    @DisplayName("Should increment quantity when adding same product again")
    void addToCart_incrementsExistingItem() {
        String userId = "u2";
        Long productId = 202L;

        Cart cart = newEmptyCart(123L, userId);
        cart.getItems().add(newItem(cart, productId, "P202", 5.0, 1));

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(productClient.getProductById(productId)).thenReturn(product(productId, "P202", 5.0));
        when(cartRepository.save(cart)).thenReturn(cart);

        CartResponseDTO dto = cartService.addToCart(userId, productId, 3);

        verify(cartRepository).save(cart);
        assertEquals(1, cart.getItems().size());
        assertEquals(4, cart.getItems().get(0).getQuantity());
        assertEquals(20.0, dto.getTotalPrice(), 0.0001);
    }

    @Test
    @DisplayName("Should populate item details from ProductClient on first add")
    void addToCart_populatesFromProductClient() {
        String userId = "u3";
        Long productId = 303L;

        Cart cart = newEmptyCart(123L, userId);
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(productClient.getProductById(productId)).thenReturn(product(productId, "Name303", 7.25));
        when(cartRepository.save(cart)).thenReturn(cart);

        CartResponseDTO dto = cartService.addToCart(userId, productId, 2);

        assertEquals(1, cart.getItems().size());
        CartItem item = cart.getItems().get(0);
        assertEquals(productId, item.getProductId());
        assertEquals("Name303", item.getProductName());
        assertEquals(7.25, item.getPrice(), 0.0001);
        assertEquals(14.5, dto.getTotalPrice(), 0.0001);
    }

    @Test
    @DisplayName("Should throw EmptyCartException when cart is missing on getCart")
    void getCart_throwsWhenMissing() {
        String userId = "missing";
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(EmptyCartException.class, () -> cartService.getCart(userId));
    }

    @Test
    @DisplayName("Should return cart with items and correct total on getCart")
    void getCart_successWithTotals() throws EmptyCartException {
        String userId = "u4";
        Cart cart = newEmptyCart(123L, userId);
        cart.getItems().add(newItem(cart, 1L, "A", 2.0, 3)); // 6
        cart.getItems().add(newItem(cart, 2L, "B", 1.5, 4)); // 6

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        CartResponseDTO dto = cartService.getCart(userId);

        assertEquals(123L, dto.getCartId());
        assertEquals(2, dto.getItems().size());
        assertEquals(12.0, dto.getTotalPrice(), 0.0001);
    }

    @Test
    @DisplayName("Should reduce quantity when remove qty is less than current qty")
    void removeFromCart_reducesQuantity() throws Exception {
        String userId = "u5";
        Long productId = 505L;

        Cart cart = newEmptyCart(123L, userId);
        cart.getItems().add(newItem(cart, productId, "P505", 4.0, 5));

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        CartResponseDTO dto = cartService.removeFromCart(userId, productId, 2);

        verify(cartRepository).save(cart);
        assertEquals(1, cart.getItems().size());
        assertEquals(3, cart.getItems().get(0).getQuantity());
        // total = 3 * 4.0 = 12.0
        assertEquals(12.0, dto.getTotalPrice(), 0.0001);
    }

    @Test
    @DisplayName("Should remove item completely when remove qty >= current qty")
    void removeFromCart_removesItemCompletely() throws Exception {
        String userId = "u6";
        Long productId = 606L;

        Cart cart = newEmptyCart(123L, userId);
        cart.getItems().add(newItem(cart, productId, "P606", 10.0, 2));

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        CartResponseDTO dto = cartService.removeFromCart(userId, productId, 3);

        verify(cartRepository).save(cart);
        assertTrue(cart.getItems().isEmpty());
        assertEquals(0.0, dto.getTotalPrice(), 0.0001);
        assertEquals(0, dto.getItems().size());
    }

    @Test
    @DisplayName("Should throw CartNotFoundException when removing from non-existent cart")
    void removeFromCart_cartMissing() {
        String userId = "missing";
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(CartNotFoundException.class, () -> cartService.removeFromCart(userId, 1L, 1));
    }

    @Test
    @DisplayName("Should throw ProductNotInCartException when product is not present in cart")
    void removeFromCart_productNotInCart() {
        String userId = "u7";
        Cart cart = newEmptyCart(123L, userId);
        cart.getItems().add(newItem(cart, 700L, "X", 1.0, 1));

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        assertThrows(ProductNotInCartException.class, () -> cartService.removeFromCart(userId, 999L, 1));
    }

    @Test
    @DisplayName("Should clear all items in clearCart and return empty DTO")
    void clearCart_clearsItems() {
        String userId = "u8";
        Cart cart = newEmptyCart(123L, userId);
        cart.getItems().add(newItem(cart, 1L, "A", 2.0, 2));
        cart.getItems().add(newItem(cart, 2L, "B", 3.0, 3));

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(cart)).thenReturn(cart);

        CartResponseDTO dto = cartService.clearCart(userId);

        verify(cartRepository).save(cart);
        assertTrue(cart.getItems().isEmpty());
        assertEquals(0.0, dto.getTotalPrice(), 0.0001);
        assertEquals(0, dto.getItems().size());
    }
}