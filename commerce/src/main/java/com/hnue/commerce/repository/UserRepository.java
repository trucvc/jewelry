package com.hnue.commerce.repository;

import com.hnue.commerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByEmail(String email);

    User findByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.productFavorites pf WHERE u.email = :email")
    User findUserWithProductFavoriteAndProduct(@Param("email") String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.orders WHERE u.email = :email")
    User findAllOrderWithItemForUser(@Param("email") String email);

    long count();

    @Query("SELECT u FROM User u ORDER BY u.createdAt DESC")
    List<User> findNewUser();
}
