package com.ecom.productservice.controllers;


import com.ecom.productservice.dtos.CategoryRequestDto;
import com.ecom.productservice.exceptions.InvalidCategoryException;
import com.ecom.productservice.exceptions.ProductNotFoundException;
import com.ecom.productservice.models.Category;
import com.ecom.productservice.services.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category")
/**
 * REST controller for managing product categories.
 * Exposes endpoints to create and delete categories.
 */
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Constructs the CategoryController with its required service.
     *
     * @param categoryService service handling category operations
     */
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @RequestMapping(method = RequestMethod.POST,value="/add")
    /**
     * Creates a new category.
     *
     * @param categoryRequestDto category data (id, name, description)
     * @return the created Category wrapped in HTTP 200 OK
     */
    public ResponseEntity<Category> addCategory(@RequestBody CategoryRequestDto categoryRequestDto){
        Category category=categoryService.addCategory(categoryRequestDto);
        return ResponseEntity.ok().body(category);
    }

    @RequestMapping(method = RequestMethod.DELETE,value = "/delete/{id}")
    /**
     * Deletes a category by its identifier.
     *
     * @param id the category identifier
     * @return confirmation message wrapped in HTTP 200 OK
     * @throws ProductNotFoundException if a related product constraint is violated
     * @throws InvalidCategoryException if the category does not exist
     */
    public ResponseEntity<String> removeCategoryById(@PathVariable("id") Long id) throws ProductNotFoundException, InvalidCategoryException {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok().body("category with category id " + id+" has been removed");
    }
}
