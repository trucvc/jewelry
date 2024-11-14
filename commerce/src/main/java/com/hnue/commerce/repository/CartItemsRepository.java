package com.hnue.commerce.repository;

import com.hnue.commerce.model.CartItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemsRepository extends JpaRepository<CartItems, Integer> {
    @Modifying
    @Query("DELETE FROM CartItems ci WHERE ci.id = :id")
    void deleteCartItem(@Param("id") int id);

    @Query("SELECT COUNT(cs) FROM CartItems cs WHERE cs.cart.id = :id")
    Long countCartItemsByCartId(@Param("id") int id);
}
