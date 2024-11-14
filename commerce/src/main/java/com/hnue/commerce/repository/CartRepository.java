package com.hnue.commerce.repository;

import com.hnue.commerce.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.cartItems WHERE c.id = :id")
    Cart getCartAndCartItems(@Param("id") int id);
}
