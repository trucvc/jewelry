package com.hnue.commerce.repository;

import com.hnue.commerce.model.ProductFavorite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductFavoriteRepository extends JpaRepository<ProductFavorite, Integer> {
}
