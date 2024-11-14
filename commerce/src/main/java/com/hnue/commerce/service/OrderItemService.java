package com.hnue.commerce.service;

import com.hnue.commerce.model.OrderItems;
import com.hnue.commerce.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;

    public OrderItems getOrderItems(int id){
        return orderItemRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy order item với id:"+id));
    }

    public OrderItems updategetOrderItems(OrderItems orderItems){
        return orderItemRepository.save(orderItems);
    }
}
