package com.ecom.productservice.repositories;


import com.ecom.productservice.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    List<Product> findAll();
    Page<Product> findAll(Pageable pageable);
    Optional<Product> getProductByProductId(Long id);
    @Query("SELECT p FROM Product p WHERE "
            + "(:category IS NULL OR p.category.name = :category) AND "
            + "(:brand IS NULL OR p.brand = :brand) AND "
            + "(:minPrice IS NULL OR p.price >= :minPrice) AND "
            + "(:maxPrice IS NULL OR p.price <= :maxPrice)")
    List<Product> findByFilters(@Param("category") String category,
                                @Param("brand") String brand,
                                @Param("minPrice") Double minPrice,
                                @Param("maxPrice") Double maxPrice);
}
