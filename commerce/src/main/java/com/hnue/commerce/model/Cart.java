package com.hnue.commerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cart")
@Data
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private int id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    private List<CartItems> cartItems;

    public Cart(){

    }

    public void addCartItem(CartItems theCartItems){
        if (cartItems == null){
            cartItems = new ArrayList<>();
        }
        cartItems.add(theCartItems);
        theCartItems.setCart(this);
    }
}
