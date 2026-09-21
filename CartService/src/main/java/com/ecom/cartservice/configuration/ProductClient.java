package com.ecom.cartservice.configuration;

import com.ecom.cartservice.dtos.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", path="/product",configuration = FeignConfig.class)
public interface ProductClient {

    @GetMapping("/{id}")
    ProductDTO getProductById(@PathVariable("id") Long id);
}