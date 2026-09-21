package com.ecom.productservice.repositories;

import com.ecom.productservice.models.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EntityScan(basePackages = "com.ecom.productservice.models")
class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("Test save and getProductByProductId")
    void testSaveAndGetProductByProductId() {
        Product product = new Product();
        product.setName("TestProd");
        product.setDescription("desc");
        product.setPrice(99.99);
        productRepository.save(product);

        Optional<Product> found = productRepository.getProductByProductId(product.getProductId());
        assertTrue(found.isPresent());
        assertEquals("TestProd", found.get().getName());
    }

    @Test
    @DisplayName("Test getProductByProductId returns empty for missing id")
    void testGetProductByProductIdNotFound() {
        Optional<Product> found = productRepository.getProductByProductId(999L);
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Test findAll and findAll(Pageable)")
    void testFindAllAndFindAllPageable() {
        Product product1 = new Product();
        product1.setName("Prod1");
        product1.setDescription("desc1");
        product1.setPrice(10.0);

        Product product2 = new Product();
        product2.setName("Prod2");
        product2.setDescription("desc2");
        product2.setPrice(20.0);

        productRepository.save(product1);
        productRepository.save(product2);

        assertEquals(2, productRepository.findAll().size());

        Page<Product> page = productRepository.findAll(PageRequest.of(0, 1));
        assertEquals(1, page.getContent().size());
    }
}
