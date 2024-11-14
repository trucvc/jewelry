package com.hnue.commerce.controller;

import com.hnue.commerce.model.Cart;
import com.hnue.commerce.model.CartItems;
import com.hnue.commerce.model.User;
import com.hnue.commerce.service.CartItemsService;
import com.hnue.commerce.service.CartService;
import com.hnue.commerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final UserService userService;
    private final CartItemsService cartItemsService;

    @GetMapping("/cart")
    public String cart(Model theModel){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        int theId = user.getCart().getId();
        Cart cart = cartService.getCart(theId);
        List<CartItems> cartItems = new ArrayList<>(cart.getCartItems());
        List<CartItems> updateItems = new ArrayList<>();
        for (CartItems item : cartItems){
            if (item.getActive() == 1){
                if(item.getProduct().getStockQuantity() == 0){
                    item.setActive(0);
                }
            }else {
                if (item.getProduct().getStockQuantity() != 0){
                    item.setActive(1);
                }
            }
            if (item.getActive() == 1){
                if (item.getProduct().getStockQuantity() > 0 && item.getQuantity() > item.getProduct().getStockQuantity()){
                    item.setQuantity(item.getProduct().getStockQuantity());
                }
            }
            cartItemsService.update(item);
            updateItems.add(item);
        }
        double amount = 0;
        long count = 0;
        for (CartItems items : updateItems){
            if (items.getActive() == 0){
                continue;
            }
            amount += items.getQuantity()*items.getProduct().getPrice();
            count++;
        }
        cart.setCartItems(updateItems);

        theModel.addAttribute("cart", cart);
        theModel.addAttribute("count", count);
        theModel.addAttribute("amount", amount);

        return "cart";
    }

    @GetMapping("/cart/{id}")
    public String deleteItem(@PathVariable("id") int id){
        cartItemsService.deleteCartItem(id);
        return "redirect:/cart";
    }

    @PostMapping("/save-cart")
    public String saveCart(@RequestParam Map<String, String> items, RedirectAttributes redirectAttributes){
        List<String> errors = new ArrayList<>();
        boolean quantityInvalid = false;
        boolean quantityInvalidStock = false;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        int theId = user.getCart().getId();
        Cart cart = cartService.getCart(theId);
        for (Map.Entry<String, String> entry : items.entrySet()){
            int itemId = Integer.parseInt(entry.getKey());
            int quantity = Integer.parseInt(entry.getValue());
            if (quantity < 1){
                quantityInvalid = true;
                continue;
            }
            CartItems item = new CartItems();
            for (CartItems cartItem : cart.getCartItems()) {
                if (cartItem.getId() == itemId) {
                    item = cartItem;
                    break;
                }
            }
            if (quantity > item.getProduct().getStockQuantity()){
                quantityInvalidStock = true;
            }else {
                item.setQuantity(quantity);
                cartItemsService.update(item);
            }
        }
        if (quantityInvalid){
            errors.add("Sản phẩm phải lớn hơn hoặc bằng 1");
        }
        if (quantityInvalidStock){
            errors.add("Số lượng sản phẩm còn lại không đủ");
        }
        redirectAttributes.addFlashAttribute("errors", errors);

        return "redirect:/cart";
    }
}
