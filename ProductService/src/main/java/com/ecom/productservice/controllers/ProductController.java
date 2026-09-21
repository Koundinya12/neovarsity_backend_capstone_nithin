package com.ecom.productservice.controllers;



import com.ecom.productservice.dtos.ProductRequestDto;
import com.ecom.productservice.exceptions.InvalidCategoryException;
import com.ecom.productservice.exceptions.NoProductsFoundException;
import com.ecom.productservice.exceptions.ProductNotFoundException;
import com.ecom.productservice.models.Product;
import com.ecom.productservice.dtos.*;
import com.ecom.productservice.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @RequestMapping(method = RequestMethod.POST,value="/add")
    public ResponseEntity<ProductResponseDto> addProduct(@RequestBody ProductRequestDto productDTO) throws InvalidCategoryException {
        log.info("Inside Product controller, Adding product {}", productDTO);
        ProductResponseDto _product=productService.addProduct(productDTO);
        return ResponseEntity.ok().body(_product);
    }

    @RequestMapping(method = RequestMethod.POST,value="/add-all")
    public ResponseEntity<List<ProductResponseDto>> addAllProducts(@RequestBody List<ProductRequestDto> productDTO) throws InvalidCategoryException {
        log.info("Inside Product controller, Adding all products {}", productDTO);
        List<ProductResponseDto> products=productService.addAllProducts(productDTO);
        return ResponseEntity.ok().body(products);
    }

    @RequestMapping(method = RequestMethod.GET,value = "/products")
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() throws NoProductsFoundException {
        log.info("Inside Product controller, Getting all products");
        List<ProductResponseDto> products=productService.getAllProducts();
        return ResponseEntity.ok().body(products);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDto>> searchProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        List<ProductResponseDto> products = productService.searchProducts(category, brand, minPrice, maxPrice);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/products/page")
    public Page<ProductResponseDto> getAllProductsByPage(@RequestParam("pageNumber") int pageNumber,
                                        @RequestParam("pageSize") int pageSize) {
        log.info("Inside Product controller, Getting all products by page {}", pageNumber);
        return productService.getAllProducts(pageNumber, pageSize);
    }

    @RequestMapping(method = RequestMethod.GET,value = "/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable("id") Long id) throws ProductNotFoundException {
        log.info("Inside Product controller, Getting product by id {}", id);
        ProductResponseDto product=productService.getProductById(id);
        return ResponseEntity.ok().body(product);
    }

    @RequestMapping(method = RequestMethod.DELETE,value = "/delete/{id}")
    public ResponseEntity<String> removeProductById(@PathVariable("id") Long id) throws ProductNotFoundException {
        log.info("Inside Product controller, Removing product by id {}", id);
        productService.removeProductById(id);
        return ResponseEntity.ok().body("product with product id " + id+" has been removed");
    }

    @RequestMapping(method = RequestMethod.PATCH,value = "/update")
    public ResponseEntity<ProductResponseDto> updateProduct(@RequestBody UpdateProductRequestDto productRequestDto) throws ProductNotFoundException {
        log.info("Inside Product controller, Updating product {}", productRequestDto);
        ProductResponseDto product=productService.updateProduct(productRequestDto);
        return ResponseEntity.ok().body(product);
    }
}
