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
import com.ecom.productservice.repositories.*;
//import org.springframework.cache.annotation.Cacheable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

     private final ProductRepository productRepository;

     private final CategoryRepository categoryRepository;

    private final RedisTemplate redisTemplate;

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

     public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository,RedisTemplate redisTemplate) {
         this.productRepository = productRepository;
         this.categoryRepository=categoryRepository;
         this.redisTemplate=redisTemplate;
     }

     public ProductResponseDto addProduct(ProductRequestDto dto) throws InvalidCategoryException {
         log.info("Adding product "+dto.getName() +"to inventory");
         Category category = categoryRepository.findById(dto.getCategoryId())
                 .orElseThrow(() -> new InvalidCategoryException("Category not found"));

         Product product = new Product();
         product.setName(dto.getName());
         product.setDescription(dto.getDescription());
         product.setPrice(dto.getPrice());
         product.setCategory(category);


         productRepository.save(product);
         redisTemplate.opsForHash().put("PRODUCTS", "PRODUCT_" + product.getProductId(), ProductMapper.toDto(product));
         return ProductMapper.toDto(product);
     }

    public List<ProductResponseDto> addAllProducts(List<ProductRequestDto> dto) throws InvalidCategoryException {
         log.info("Adding all products "+dto);
         List<Product> products = new ArrayList<>();
         for (ProductRequestDto productRequestDto : dto) {
             Category category = categoryRepository.findById(productRequestDto.getCategoryId())
                     .orElseThrow(() -> new InvalidCategoryException("Category not found"));

             Product product = new Product();
             product.setName(productRequestDto.getName());
             product.setDescription(productRequestDto.getDescription());
             product.setPrice(productRequestDto.getPrice());
             product.setCategory(category);
             products.add(product);
         }
        List<Product> productList=productRepository.saveAll(products);
         for(Product product:productList)
            redisTemplate.opsForHash().put("PRODUCTS", "PRODUCT_" + product.getProductId(), ProductMapper.toDto(product));
         return  productList.stream().map(ProductMapper::toDto).toList();
    }

    @Override
    public void removeProductById(Long id) throws ProductNotFoundException {
         log.info("Removing product "+id);
        Optional<Product> optionalProduct=productRepository.findById(id);
        if(optionalProduct.isEmpty()){
            throw new ProductNotFoundException("Product not found");
        }
        productRepository.deleteById(id);
    }

    @Override
    public ProductResponseDto updateProduct(UpdateProductRequestDto productRequestDto) throws ProductNotFoundException {
        log.info("Updating product "+productRequestDto.getName());
         Optional<Product> productOpt=productRepository.findById(productRequestDto.getProductId());
        if(productOpt.isEmpty()){
            throw new ProductNotFoundException("Product not found");
        }
        Product product=productOpt.get();
        // Update fields only if provided
        if (productRequestDto.getName() != null) product.setName(productRequestDto.getName());
        if (productRequestDto.getDescription() != null) product.setDescription(productRequestDto.getDescription());
        if (productRequestDto.getPrice()!=null) product.setPrice(productRequestDto.getPrice());
        Product savedProduct=productRepository.save(product);
        redisTemplate.opsForHash().put("PRODUCTS", "PRODUCT_" + product.getProductId(), ProductMapper.toDto(savedProduct));
        return ProductMapper.toDto(savedProduct);
    }

    @Override
    public List<ProductResponseDto> getAllProducts() throws NoProductsFoundException {
        log.info("Fetching all products");
        List<ProductResponseDto> products = redisTemplate.opsForHash()
                .values("PRODUCTS")
                .stream()
                .map(obj -> (ProductResponseDto) obj)
                .toList();
        System.out.println("Products from cache: "+products);

        if(!products.isEmpty()) return products;
        List<Product> productList = productRepository.findAll();
        if(productList.isEmpty()){
            throw new NoProductsFoundException("No products found!");
        }

        return productList.stream()
                .map(ProductMapper::toDto)
                .toList();
    }

    @Override
    public Page<ProductResponseDto> getAllProducts(int pageNumber, int pageSize) {
        Page<Product> productPage= productRepository.findAll(PageRequest.of(pageNumber,
                pageSize,
                Sort.by("price").ascending()));
        return productPage.map(ProductMapper::toDto);
    }


    @Override
    public ProductResponseDto getProductById(Long id) throws ProductNotFoundException {
       log.info("Fetching product with product id "+id);
        ProductResponseDto cached_product = (ProductResponseDto) redisTemplate.opsForHash().get("PRODUCTS", "PRODUCT_" + id);
        System.out.println("Product from cache: "+cached_product);
        if(cached_product!=null) return cached_product;
        Optional<Product> productOptional=productRepository.findById(id);
        if(productOptional.isEmpty()){
            throw new ProductNotFoundException("Product not found");
        }

        Product product=productOptional.get();
        redisTemplate.opsForHash().put("PRODUCTS", "PRODUCT_" + id, ProductMapper.toDto(product));
        return ProductMapper.toDto(product);
    }

    public List<ProductResponseDto> searchProducts(String category, String brand, Double minPrice, Double maxPrice) {
        // Example using JPA Specification or custom query logic
        // If using a repository, you can build a dynamic query here
        List<Product> products=productRepository.findByFilters(category, brand, minPrice, maxPrice);
        return products.stream().map(ProductMapper::toDto).toList();
    }

}
