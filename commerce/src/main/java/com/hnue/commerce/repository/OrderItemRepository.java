package com.hnue.commerce.repository;

import com.hnue.commerce.model.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderItemRepository extends JpaRepository<OrderItems, Integer> {
}
