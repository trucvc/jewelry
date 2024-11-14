package com.hnue.commerce.repository;

import com.hnue.commerce.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.PathVariable;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    boolean existsByName(String name);

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.products WHERE c.id = :id")
    Category getProductsForCategory(@PathVariable("id") int id);

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.products WHERE c.id = :id")
    Category getRelatedProductsForCategory(@PathVariable("id") int id);
}
