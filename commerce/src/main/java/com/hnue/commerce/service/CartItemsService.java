package com.hnue.commerce.service;

import com.hnue.commerce.model.CartItems;
import com.hnue.commerce.repository.CartItemsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartItemsService {
    private final CartItemsRepository cartItemsRepository;

    public void update(CartItems theCartItems){
        cartItemsRepository.save(theCartItems);
    }

    public Long countCartItemsByCartId(int id){
        return cartItemsRepository.countCartItemsByCartId(id);
    }

    @Transactional
    public void deleteCartItem(int id){
//        CartItems cartItems = cartItemsRepository.findById(id).orElseThrow(() -> new RuntimeException("Không có item với id: "+id));
//        cartItemsRepository.delete(cartItems);

        cartItemsRepository.deleteCartItem(id);
    }
}
