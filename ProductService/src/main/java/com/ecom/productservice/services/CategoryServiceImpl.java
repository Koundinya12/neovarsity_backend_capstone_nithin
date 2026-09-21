package com.ecom.productservice.services;

import com.ecom.productservice.dtos.CategoryRequestDto;
import com.ecom.productservice.exceptions.InvalidCategoryException;
import com.ecom.productservice.exceptions.ProductNotFoundException;
import com.ecom.productservice.models.Category;
import com.ecom.productservice.models.Product;
import com.ecom.productservice.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService{

    private CategoryRepository categoryRepository;
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    @Override
    public void deleteCategory(Long id) throws InvalidCategoryException {
        Optional<Category> optionalCategory=categoryRepository.findByCategoryId(id);
        if(optionalCategory.isEmpty()){
            throw new InvalidCategoryException("Category not found");
        }
        categoryRepository.delete(optionalCategory.get());
    }

    @Override
    public Category addCategory(CategoryRequestDto categoryRequestDto) {
        Category category = new Category();
        category.setCategoryId(categoryRequestDto.getId());
        category.setName(categoryRequestDto.getName());
        category.setDescription(categoryRequestDto.getDescription());
        return categoryRepository.save(category);
    }
}
