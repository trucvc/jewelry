package com.hnue.commerce.service;

import com.hnue.commerce.model.Order;
import com.hnue.commerce.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public void createOrder(Order theOrder){
        orderRepository.save(theOrder);
    }

    public Order getOrder(int id){
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với id: "+id));
    }

    public List<Order> getAllOrder(){
        return orderRepository.findAll();
    }

    public Order getOrderWithOrderItems(int id){
        return orderRepository.getOrderWithOrderItems(id);
    }

    public Order updateOrder(Order order){
        return orderRepository.save(order);
    }

    public void deleteOrder(int id){
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với id: "+id));
        orderRepository.delete(order);
    }

    public Double getTotalAmountByStatus(int status) {
        return orderRepository.sumTotalAmountByStatus(status);
    }

    public long getOrderCount() {
        return orderRepository.count();
    }

    public Map<LocalDate, BigDecimal> getDailyRevenueForDeliveredOrdersCurrentMonth() {
        List<Object[]> results = orderRepository.findDailyRevenueForDeliveredOrdersCurrentMonth();
        Map<LocalDate, BigDecimal> dailyRevenue = new HashMap<>();

        for (Object[] result : results) {
            LocalDate date = ((Date) result[0]).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            BigDecimal amount = BigDecimal.valueOf((double) result[1]);
            dailyRevenue.put(date, amount);
        }

        return dailyRevenue;
    }

    public Map<Integer, BigDecimal> getMonthlyRevenueForDeliveredOrdersCurrentYear() {
        List<Object[]> results = orderRepository.findMonthlyRevenueForDeliveredOrdersCurrentYear();
        Map<Integer, BigDecimal> monthlyRevenue = new HashMap<>();

        for (Object[] result : results) {
            Integer month = (Integer) result[0];
            BigDecimal amount = BigDecimal.valueOf((double) result[1]);
            monthlyRevenue.put(month, amount);
        }

        return monthlyRevenue;
    }

    public List<Order> getNewOrderCompleted(){
        List<Order> orders = orderRepository.findNewOrderCompleted();
        if (orders.size() > 7){
            return orders.subList(0,7);
        }else{
            return orders;
        }
    }
}
