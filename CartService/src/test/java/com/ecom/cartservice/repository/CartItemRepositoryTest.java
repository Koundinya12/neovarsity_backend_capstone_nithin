package com.ecom.cartservice.repository;

import com.ecom.cartservice.models.CartItem;
import com.ecom.cartservice.repositories.CartItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EntityScan(basePackages = "com.ecom.cartservice.models")
class CartItemRepositoryTest {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Test
    @DisplayName("Should save and retrieve a cart item")
    void testSaveCartItem() {
        // Arrange
        CartItem item = new CartItem();
        item.setProductId(101L);
        item.setQuantity(2);

        // Act
        CartItem savedItem = cartItemRepository.save(item);
        CartItem foundItem = cartItemRepository.findById(savedItem.getId()).orElse(null);

        // Assert
        assertThat(foundItem).isNotNull();
        assertThat(foundItem.getProductId()).isEqualTo(101L);
        assertThat(foundItem.getQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should delete cart item successfully")
    void testDeleteCartItem() {
        CartItem item = new CartItem();
        item.setProductId(202L);
        item.setQuantity(3);

        CartItem savedItem = cartItemRepository.save(item);
        cartItemRepository.delete(savedItem);

        boolean exists = cartItemRepository.existsById(savedItem.getId());
        assertThat(exists).isFalse();
    }
}
