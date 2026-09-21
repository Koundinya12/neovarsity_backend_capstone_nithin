package com.ecom.productservice.services;

import com.ecom.productservice.dtos.CategoryRequestDto;
import com.ecom.productservice.exceptions.InvalidCategoryException;
import com.ecom.productservice.models.Category;
import org.springframework.stereotype.Service;

@Service
public interface CategoryService {
    public void deleteCategory(Long id) throws InvalidCategoryException;
    public Category addCategory(CategoryRequestDto categoryRequestDto);
}
