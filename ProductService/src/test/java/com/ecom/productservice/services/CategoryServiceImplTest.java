package com.ecom.productservice.services;

import com.ecom.productservice.dtos.CategoryRequestDto;
import com.ecom.productservice.exceptions.InvalidCategoryException;
import com.ecom.productservice.models.Category;
import com.ecom.productservice.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CategoryServiceImplTest {
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addCategory_success() {
        CategoryRequestDto dto = new CategoryRequestDto();
        dto.setId(1L);
        dto.setName("Electronics");
        dto.setDescription("Electronic items");
        Category category = new Category();
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        Category result = categoryService.addCategory(dto);
        assertNotNull(result);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void deleteCategory_success() throws InvalidCategoryException {
        Category category = new Category();
        when(categoryRepository.findByCategoryId(1L)).thenReturn(Optional.of(category));
        doNothing().when(categoryRepository).delete(category);
        categoryService.deleteCategory(1L);
        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    void deleteCategory_notFound() {
        when(categoryRepository.findByCategoryId(2L)).thenReturn(Optional.empty());
        assertThrows(InvalidCategoryException.class, () -> categoryService.deleteCategory(2L));
        verify(categoryRepository, never()).delete(any(Category.class));
    }
}

