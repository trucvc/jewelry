package com.hnue.commerce.repository;

import com.hnue.commerce.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Query("SELECT o FROM Order o JOIN FETCH o.orderItems WHERE o.id = :id")
    Order getOrderWithOrderItems(@PathVariable("id") int id);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = :status")
    Double sumTotalAmountByStatus(@PathVariable("status") int status);

    long count();

    @Query("SELECT o.orderDate, SUM(o.totalAmount) " +
            "FROM Order o " +
            "WHERE MONTH(o.orderDate) = MONTH(CURRENT_DATE) AND YEAR(o.orderDate) = YEAR(CURRENT_DATE) AND o.status = 3 " +
            "GROUP BY o.orderDate ORDER BY o.orderDate")
    List<Object[]> findDailyRevenueForDeliveredOrdersCurrentMonth();

    @Query("SELECT MONTH(o.orderDate), SUM(o.totalAmount) " +
            "FROM Order o " +
            "WHERE YEAR(o.orderDate) = YEAR(CURRENT_DATE) AND o.status = 3 " +
            "GROUP BY MONTH(o.orderDate) ORDER BY MONTH(o.orderDate)")
    List<Object[]> findMonthlyRevenueForDeliveredOrdersCurrentYear();

    @Query("SELECT o FROM Order o WHERE o.status = 3 ORDER BY o.orderDate DESC")
    List<Order> findNewOrderCompleted();
}
