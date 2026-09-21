package com.ecom.cartservice.repositories;

import com.ecom.cartservice.models.Cart;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EntityScan(basePackages = "com.ecom.cartservice.models")
class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Test
    @DisplayName("Should save and find cart by userId")
    void testFindByUserId() {
        // Arrange
        Cart cart = new Cart();
        cart.setUserId("user123");
        cartRepository.save(cart);

        // Act
        Optional<Cart> foundCart = cartRepository.findByUserId("user123");

        // Assert
        assertThat(foundCart).isPresent();
        assertThat(foundCart.get().getUserId()).isEqualTo("user123");
    }

    @Test
    @DisplayName("Should return empty when cart not found by userId")
    void testFindByUserIdNotFound() {
        Optional<Cart> foundCart = cartRepository.findByUserId("nonexistentUser");
        assertThat(foundCart).isEmpty();
    }
}
