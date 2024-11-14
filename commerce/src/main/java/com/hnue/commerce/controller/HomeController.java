package com.hnue.commerce.controller;

import com.hnue.commerce.component.VisitCountFilter;
import com.hnue.commerce.model.Product;
import com.hnue.commerce.service.OrderService;
import com.hnue.commerce.service.ProductService;
import com.hnue.commerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final VisitCountFilter visitCountFilter;
    private final UserService userService;
    private final ProductService productService;
    private final OrderService orderService;

    @GetMapping("/")
    public String home(Model theModel){
        List<Product> products = productService.getLimitProductsWithImages();
        theModel.addAttribute("products", products);
        return "index";
    }

    @GetMapping("/about")
    public String about(){
        return "about";
    }

    @GetMapping("/admin")
    public String dashboard(Model theModel){
        Map<LocalDate, BigDecimal> dailyRevenue = orderService.getDailyRevenueForDeliveredOrdersCurrentMonth();
        List<String> labels = new ArrayList<>();
        List<BigDecimal> data = new ArrayList<>();
        for (Map.Entry<LocalDate, BigDecimal> entry : dailyRevenue.entrySet()){
            int dayOfMonth = entry.getKey().getDayOfMonth();
            labels.add(String.format("%02d", dayOfMonth));
            data.add(entry.getValue());
        }

        Map<Integer, BigDecimal> monthlyRevenue = orderService.getMonthlyRevenueForDeliveredOrdersCurrentYear();
        List<String> labelsMonthly = new ArrayList<>();
        List<BigDecimal> dataMonthly = new ArrayList<>();
        for (Map.Entry<Integer, BigDecimal> entry : monthlyRevenue.entrySet()){
            labelsMonthly.add(String.format("%02d", entry.getKey()));
            dataMonthly.add(entry.getValue());
        }

        theModel.addAttribute("count", visitCountFilter.getTotalVisits());
        theModel.addAttribute("user", userService.getUserCount());
        theModel.addAttribute("total", orderService.getTotalAmountByStatus(3));
        theModel.addAttribute("order", orderService.getOrderCount());
        theModel.addAttribute("labels", labels);
        theModel.addAttribute("data", data);
        theModel.addAttribute("labelsMonthly", labelsMonthly);
        theModel.addAttribute("dataMonthly", dataMonthly);
        theModel.addAttribute("newUser", userService.getNewUser());
        theModel.addAttribute("newOrder", orderService.getNewOrderCompleted());

        return "admin/index";
    }
}
