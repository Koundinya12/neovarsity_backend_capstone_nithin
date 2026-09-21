package com.ecom.productservice.services;

import com.ecom.productservice.dtos.ProductRequestDto;
import com.ecom.productservice.dtos.ProductResponseDto;
import com.ecom.productservice.dtos.UpdateProductRequestDto;
import com.ecom.productservice.exceptions.InvalidCategoryException;
import com.ecom.productservice.exceptions.NoProductsFoundException;
import com.ecom.productservice.exceptions.ProductNotFoundException;
import com.ecom.productservice.mappers.ProductMapper;
import com.ecom.productservice.models.Category;
import com.ecom.productservice.models.Product;
import com.ecom.productservice.repositories.CategoryRepository;
import com.ecom.productservice.repositories.ProductRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.HashOperations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private RedisTemplate redisTemplate;

    @Mock
    private HashOperations hashOperations;

    @InjectMocks
    private ProductServiceImpl productService;

    private Category category;
    private Product product;
    private ProductRequestDto requestDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        category = new Category();
        category.setCategoryId(1L);
        category.setName("Electronics");

        product = new Product();
        product.setProductId(100L);
        product.setName("Phone");
        product.setDescription("Smartphone");
        product.setPrice(999.99);
        product.setCategory(category);

        requestDto = new ProductRequestDto();
        requestDto.setName("Phone");
        requestDto.setDescription("Smartphone");
        requestDto.setPrice(999.99);
        requestDto.setCategoryId(1L);

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
    }

    @Test
    void testAddProduct_Success() throws InvalidCategoryException {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponseDto response = productService.addProduct(requestDto);

        assertEquals("Phone", response.getName());
        verify(productRepository, times(1)).save(any(Product.class));
        verify(hashOperations, times(1)).put(eq("PRODUCTS"), anyString(), any());
    }

    @Test
    void testAddProduct_InvalidCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(InvalidCategoryException.class, () -> productService.addProduct(requestDto));
    }

    @Test
    void testAddAllProducts_Success() throws InvalidCategoryException {
        List<ProductRequestDto> requestList = List.of(requestDto);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.saveAll(anyList())).thenReturn(List.of(product));

        List<ProductResponseDto> responseList = productService.addAllProducts(requestList);

        assertEquals(1, responseList.size());
        assertEquals("Phone", responseList.get(0).getName());
        verify(hashOperations, times(1)).put(eq("PRODUCTS"), anyString(), any());
    }

    @Test
    void testRemoveProductById_Success() throws ProductNotFoundException {
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));

        productService.removeProductById(100L);

        verify(productRepository, times(1)).deleteById(100L);
    }

    @Test
    void testRemoveProductById_NotFound() {
        when(productRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.removeProductById(100L));
    }

    @Test
    void testUpdateProduct_Success() throws ProductNotFoundException {
        UpdateProductRequestDto updateDto = new UpdateProductRequestDto();
        updateDto.setProductId(100L);
        updateDto.setName("Updated Phone");
        updateDto.setPrice(899.99);

        when(productRepository.findById(100L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponseDto response = productService.updateProduct(updateDto);

        assertEquals("Updated Phone", response.getName());
        verify(hashOperations, times(1)).put(eq("PRODUCTS"), anyString(), any());
    }

    @Test
    void testUpdateProduct_NotFound() {
        UpdateProductRequestDto updateDto = new UpdateProductRequestDto();
        updateDto.setProductId(999L);

        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(updateDto));
    }

    @Test
    void testGetAllProducts_FromCache() throws NoProductsFoundException {
        ProductResponseDto dto = ProductMapper.toDto(product);
        when(hashOperations.values("PRODUCTS")).thenReturn(List.of(dto));

        List<ProductResponseDto> response = productService.getAllProducts();

        assertEquals(1, response.size());
        assertEquals("Phone", response.get(0).getName());
    }

    @Test
    void testGetAllProducts_FromDB() throws NoProductsFoundException {
        when(hashOperations.values("PRODUCTS")).thenReturn(Collections.emptyList());
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<ProductResponseDto> response = productService.getAllProducts();

        assertEquals(1, response.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testGetAllProducts_NoProductsFound() {
        when(hashOperations.values("PRODUCTS")).thenReturn(Collections.emptyList());
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(NoProductsFoundException.class, () -> productService.getAllProducts());
    }

    @Test
    void testGetAllProducts_Paginated() {
        Page<Product> productPage = new PageImpl<>(List.of(product));
        when(productRepository.findAll(any(PageRequest.class))).thenReturn(productPage);

        Page<ProductResponseDto> result = productService.getAllProducts(0, 10);

        assertEquals(1, result.getTotalElements());
        verify(productRepository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    void testGetProductById_FromCache() throws ProductNotFoundException {
        ProductResponseDto dto = ProductMapper.toDto(product);
        when(hashOperations.get("PRODUCTS", "PRODUCT_100")).thenReturn(dto);

        ProductResponseDto response = productService.getProductById(100L);

        assertEquals("Phone", response.getName());
        verify(productRepository, never()).findById(anyLong());
    }

    @Test
    void testGetProductById_FromDB() throws ProductNotFoundException {
        when(hashOperations.get("PRODUCTS", "PRODUCT_100")).thenReturn(null);
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));

        ProductResponseDto response = productService.getProductById(100L);

        assertEquals("Phone", response.getName());
        verify(hashOperations, times(1)).put(eq("PRODUCTS"), eq("PRODUCT_100"), any());
    }

    @Test
    void testGetProductById_NotFound() {
        when(hashOperations.get("PRODUCTS", "PRODUCT_100")).thenReturn(null);
        when(productRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(100L));
    }

    @Test
    void testSearchProducts() {
        when(productRepository.findByFilters(any(), any(), any(), any())).thenReturn(List.of(product));

        List<ProductResponseDto> result = productService.searchProducts("Electronics", "BrandX", 100.0, 2000.0);

        assertEquals(1, result.size());
        assertEquals("Phone", result.get(0).getName());
        verify(productRepository, times(1)).findByFilters(any(), any(), any(), any());
    }
}
