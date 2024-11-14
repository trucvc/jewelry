package com.hnue.commerce.controller;

import com.hnue.commerce.dto.ReviewDTO;
import com.hnue.commerce.model.OrderItems;
import com.hnue.commerce.model.Product;
import com.hnue.commerce.model.User;
import com.hnue.commerce.service.OrderItemService;
import com.hnue.commerce.service.ProductService;
import com.hnue.commerce.service.ReviewService;
import com.hnue.commerce.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final ProductService productService;
    private final UserService userService;
    private final OrderItemService orderItemService;

    @InitBinder
    public void initBinder(WebDataBinder dataBinder){
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("/review/{id}")
    public String reviewProduct(@PathVariable("id") int id, Model theModel){
        OrderItems orderItems = orderItemService.getOrderItems(id);
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setIdItem(orderItems.getId());

        theModel.addAttribute("orderItems", orderItems);
        theModel.addAttribute("reviewDTO", reviewDTO);

        return "review";
    }

    @PostMapping("/add-review")
    public String addReview(@Valid @ModelAttribute("reviewDTO") ReviewDTO reviewDTO, BindingResult result, Model theModel){
        if (result.hasErrors()){
            OrderItems orderItems = orderItemService.getOrderItems(reviewDTO.getIdItem());
            theModel.addAttribute("orderItems", orderItems);
            return "review";
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        OrderItems orderItems = orderItemService.getOrderItems(reviewDTO.getIdItem());
        orderItems.setIsReviewed(1);
        orderItemService.updategetOrderItems(orderItems);
        reviewService.addReview(reviewDTO, user, orderItems.getProduct());
        return "redirect:/shop/"+orderItems.getProduct().getCode();
    }
}
