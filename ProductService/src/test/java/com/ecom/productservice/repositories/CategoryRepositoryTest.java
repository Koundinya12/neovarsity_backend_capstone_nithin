package com.ecom.productservice.repositories;

import com.ecom.productservice.models.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EntityScan(basePackages = "com.ecom.productservice.models")
class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("Test save and findByCategoryId")
    void testSaveAndFindByCategoryId() {
        Category category = new Category();
        category.setCategoryId(100L);
        category.setName("TestCat");
        category.setDescription("desc");
        categoryRepository.save(category);

        Optional<Category> found = categoryRepository.findByCategoryId(100L);
        assertTrue(found.isPresent());
        assertEquals("TestCat", found.get().getName());
    }

    @Test
    @DisplayName("Test findByCategoryId returns empty for missing id")
    void testFindByCategoryIdNotFound() {
        Optional<Category> found = categoryRepository.findByCategoryId(999L);
        assertFalse(found.isPresent());
    }
}

