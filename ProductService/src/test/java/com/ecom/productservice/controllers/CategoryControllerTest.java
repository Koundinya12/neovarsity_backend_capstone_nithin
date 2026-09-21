package com.ecom.productservice.controllers;

import com.ecom.productservice.dtos.CategoryRequestDto;
import com.ecom.productservice.exceptions.InvalidCategoryException;
import com.ecom.productservice.exceptions.ProductNotFoundException;
import com.ecom.productservice.models.Category;
import com.ecom.productservice.security.JwtService;
import com.ecom.productservice.services.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private JwtService  jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void addCategory_success() throws Exception {
        CategoryRequestDto requestDto = new CategoryRequestDto();
        Category category = new Category();
        category.setName("Electronics");

        when(categoryService.addCategory(any(CategoryRequestDto.class))).thenReturn(category);

        mockMvc.perform(post("/category/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Electronics"));
    }

    @Test
    void removeCategoryById_success() throws Exception {
        doNothing().when(categoryService).deleteCategory(1L);

        mockMvc.perform(delete("/category/delete/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("category with category id 1 has been removed"));
    }

    @Test
    void removeCategoryById_invalidCategory() throws Exception {
        doThrow(new InvalidCategoryException("Invalid category")).when(categoryService).deleteCategory(2L);

        mockMvc.perform(delete("/category/delete/2"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid category"));
    }

}
