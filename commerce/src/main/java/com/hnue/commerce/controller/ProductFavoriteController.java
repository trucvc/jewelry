package com.hnue.commerce.controller;

import com.hnue.commerce.model.ProductFavorite;
import com.hnue.commerce.model.User;
import com.hnue.commerce.service.ProductFavoriteService;
import com.hnue.commerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductFavoriteController {
    private final ProductFavoriteService productFavoriteService;
    private final UserService userService;

    @GetMapping("/wishlist")
    public String wishList(Model theModel){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();
        User user = userService.getUserWithProductFavoriteAndProduct(email);
        List<ProductFavorite> productFavorites = user.getProductFavorites();

        theModel.addAttribute("favorites", productFavorites);

        return "wishlist";
    }

    @GetMapping("/remove-wishlist")
    public String removeWishList(@RequestParam("id") int id){
        productFavoriteService.delete(id);
        return "redirect:/wishlist";
    }
}
