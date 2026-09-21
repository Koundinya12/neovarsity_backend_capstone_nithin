package com.ecom.productservice.controllers;

import com.ecom.productservice.dtos.ProductRequestDto;
import com.ecom.productservice.dtos.ProductResponseDto;
import com.ecom.productservice.dtos.UpdateProductRequestDto;
import com.ecom.productservice.exceptions.InvalidCategoryException;
import com.ecom.productservice.exceptions.NoProductsFoundException;
import com.ecom.productservice.exceptions.ProductNotFoundException;
import com.ecom.productservice.security.JwtService;
import com.ecom.productservice.services.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void addProduct_success() throws Exception {
        ProductRequestDto requestDto = new ProductRequestDto();
        ProductResponseDto responseDto = new ProductResponseDto();
        responseDto.setName("TestProduct");

        when(productService.addProduct(any(ProductRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/product/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("TestProduct"));
    }

    @Test
    void addAllProducts_success() throws Exception {
        List<ProductRequestDto> requestDtos = Arrays.asList(new ProductRequestDto(), new ProductRequestDto());
        List<ProductResponseDto> responseDtos = Arrays.asList(new ProductResponseDto(), new ProductResponseDto());

        when(productService.addAllProducts(any())).thenReturn(responseDtos);

        mockMvc.perform(post("/product/add-all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDtos)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getAllProducts_success() throws Exception {
        List<ProductResponseDto> responseDtos = Arrays.asList(new ProductResponseDto(), new ProductResponseDto());
        when(productService.getAllProducts()).thenReturn(responseDtos);

        mockMvc.perform(get("/product/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getAllProductsByPage_success() throws Exception {
        List<ProductResponseDto> products = Arrays.asList(new ProductResponseDto(), new ProductResponseDto());
        Page<ProductResponseDto> page = new PageImpl<>(products);
        when(productService.getAllProducts(0, 2)).thenReturn(page);

        mockMvc.perform(get("/product/products/page")
                        .param("pageNumber", "0")
                        .param("pageSize", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void getProductById_success() throws Exception {
        ProductResponseDto responseDto = new ProductResponseDto();
        responseDto.setName("Laptop");
        responseDto.setPrice(999.99);

        when(productService.getProductById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(999.99));
    }

    @Test
    void removeProductById_success() throws Exception {
        doNothing().when(productService).removeProductById(1L);

        mockMvc.perform(delete("/product/delete/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("product with product id 1 has been removed"));
    }

    @Test
    void updateProduct_success() throws Exception {
        UpdateProductRequestDto updateDto = new UpdateProductRequestDto();
        ProductResponseDto responseDto = new ProductResponseDto();
        responseDto.setName("UpdatedProduct");

        when(productService.updateProduct(any(UpdateProductRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(patch("/product/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UpdatedProduct"));
    }

    @Test
    void getAllProducts_noProductsFound() throws Exception {
        when(productService.getAllProducts()).thenThrow(new NoProductsFoundException("No products"));

        mockMvc.perform(get("/product/products"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getProductById_notFound() throws Exception {
        when(productService.getProductById(99L)).thenThrow(new ProductNotFoundException("Product not found"));

        mockMvc.perform(get("/product/99"))
                .andExpect(status().isBadRequest());
    }
}
