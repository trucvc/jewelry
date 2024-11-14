package com.hnue.commerce.repository;

import com.hnue.commerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {
    Page<Product> findAll(Pageable pageable);

    @Query("SELECT DISTINCT p.tag FROM Product p WHERE p.tag IS NOT NULL")
    List<String> findAllDistinctTags();

    Optional<Product> findByCode(String code);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.reviews WHERE p.code = :code")
    Product getProductWithReviews(@PathVariable("code") String code);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images WHERE p.code = :code")
    Product getProductWithImage(@PathVariable("code") String code);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images WHERE p.id = :id")
    Product getProductWithImages(@PathVariable("id") int id);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images")
    List<Product> getAllProductsWithImages();

    boolean existsByCode(String code);
}
