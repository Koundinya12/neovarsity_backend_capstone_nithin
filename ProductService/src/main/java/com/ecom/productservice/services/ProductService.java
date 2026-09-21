package com.ecom.productservice.services;



import com.ecom.productservice.dtos.ProductRequestDto;
import com.ecom.productservice.exceptions.InvalidCategoryException;
import com.ecom.productservice.exceptions.NoProductsFoundException;
import com.ecom.productservice.exceptions.ProductNotFoundException;
import com.ecom.productservice.models.Product;
import com.ecom.productservice.dtos.*;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public interface ProductService {
    public ProductResponseDto addProduct(ProductRequestDto product) throws InvalidCategoryException;
    public List<ProductResponseDto> getAllProducts() throws NoProductsFoundException;

    public Page<ProductResponseDto> getAllProducts(int pageNumber, int pageSize);
    public ProductResponseDto getProductById(Long id) throws ProductNotFoundException;

    public List<ProductResponseDto> addAllProducts(List<ProductRequestDto> productDTO) throws InvalidCategoryException;

    public void removeProductById(Long id) throws ProductNotFoundException;

    ProductResponseDto updateProduct(UpdateProductRequestDto productRequestDto) throws ProductNotFoundException;

    public List<ProductResponseDto> searchProducts(String category, String brand, Double minPrice, Double maxPrice);
}
