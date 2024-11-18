package com.hnue.commerce.repository;

import com.hnue.commerce.model.Product;
import com.hnue.commerce.model.ProductFavorite;
import com.hnue.commerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductFavoriteRepository extends JpaRepository<ProductFavorite, Integer> {
    Optional<ProductFavorite> findByUserAndProduct(User user, Product product);
}
