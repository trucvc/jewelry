package com.hnue.commerce.service;

import com.hnue.commerce.model.Cart;
import com.hnue.commerce.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;

    public void save(Cart theCart){
        cartRepository.save(theCart);
    }

    public Cart getCart(int id){
        Cart cart = cartRepository.getCartAndCartItems(id);
        return cart;
    }

    public void update(Cart theCart){
        cartRepository.save(theCart);
    }
}
