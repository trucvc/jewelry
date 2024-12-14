package com.hnue.commerce.controller;

import com.hnue.commerce.dto.OrderDTO;
import com.hnue.commerce.model.*;
import com.hnue.commerce.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final UserService userService;
    private final ProductService productService;
    private final CartService cartService;
    private final CartItemsService cartItemsService;
    private final OrderService orderService;
    private final VNPAYService vnpayService;
    private static String temporaryNote = "";

    @InitBinder
    public void initBinder(WebDataBinder dataBinder){
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("/checkout")
    public String checkout(Model theModel, RedirectAttributes redirectAttributes){
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
            if (item.getActive() == 1){
                updateItems.add(item);
            }
        }
        double amount = 0;
        for (CartItems items : updateItems){
            amount += items.getQuantity()*items.getProduct().getPrice();
        }
        cart.setCartItems(updateItems);
        if (amount == 0){
            redirectAttributes.addFlashAttribute("mess", "Bạn chưa có sản phẩm nào trong giỏ hàng!");
            return "redirect:/cart";
        }
        String[] addr = new String[0];
        if (user.getAddress() != null){
            try {
                addr = user.getAddress().split("-");
            }catch (Exception e){
                addr = new String[0];
            }
        }
        OrderDTO orderDTO = new OrderDTO(user.getFullName(), user.getPhoneNumber(), "", "", "", "", "");
        if (addr.length >= 3){
            orderDTO.setCity(addr[2]);
            orderDTO.setDistrict(addr[1]);
            orderDTO.setAddress(addr[0]);
        }

        theModel.addAttribute("cart", cart);
        theModel.addAttribute("count", updateItems.size());
        theModel.addAttribute("email", email);
        theModel.addAttribute("orderDTO", orderDTO);
        theModel.addAttribute("amount", amount);

        return "checkout";
    }

    @PostMapping("/add-order")
    public String addOrder(@Valid @ModelAttribute("orderDTO") OrderDTO orderDTO, BindingResult result, HttpServletRequest request, Model theModel){
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
            if (item.getActive() == 1){
                updateItems.add(item);
            }
        }
        double amount = 0;
        for (CartItems items : updateItems){
            amount += items.getQuantity()*items.getProduct().getPrice();
        }
        cart.setCartItems(updateItems);
        if (result.hasErrors()){
            theModel.addAttribute("cart", cart);
            theModel.addAttribute("count", updateItems.size());
            theModel.addAttribute("email", email);
            theModel.addAttribute("amount", amount);
            return "checkout";
        }
        user.setFullName(orderDTO.getFullName());
        user.setPhoneNumber(orderDTO.getPhone());
        user.setAddress(orderDTO.getAddress()+"-"+orderDTO.getDistrict()+"-"+orderDTO.getCity());
        userService.updateUser(user);

        String paymentMethod = orderDTO.getPaymentMethod();
        String note = orderDTO.getNote();
        if (note != null){
            temporaryNote = note.trim();
        }
        if (paymentMethod.equals("cod")){
            return "redirect:/cod";
        }else {
            String url = vnpayService.createPaymentUrl(request);
            return "redirect:"+url;
        }
    }

    @GetMapping("/cod")
    public String cod(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        int theId = user.getCart().getId();
        Cart cart = cartService.getCart(theId);
        Order order = new Order();
        order.setName(user.getFullName());
        order.setPhone(user.getPhoneNumber());
        order.setAddress(user.getAddress());
        order.setUser(user);
        order.setStatus(0);
        if (!temporaryNote.isEmpty()){
            order.setNotes(temporaryNote);
        }
        Payment payment = new Payment();
        payment.setPaymentMethod("Cash");
        payment.setOrder(order);
        order.setPayment(payment);
        double amount = 0;
        for (CartItems items : cart.getCartItems()){
            if (items.getActive() == 0){
                continue;
            }
            OrderItems orderItem = new OrderItems(items.getQuantity(), items.getProduct().getPrice(), 0);
            orderItem.setProduct(items.getProduct());
            amount += orderItem.getPrice()*items.getQuantity();
            order.addOrderItem(orderItem);
            Product product = items.getProduct();
            product.setStockQuantity(items.getProduct().getStockQuantity() - items.getQuantity());
            productService.updateProduct(product);
            cartItemsService.deleteCartItem(items.getId());
        }
        order.setOrderDate(new Date());
        order.setTotalAmount(amount);
        order.setCode("ACS"+getRandomNumber(8));
        orderService.createOrder(order);
        return "redirect:/order";
    }

    @GetMapping("/callback")
    public String callback(HttpServletRequest request, @RequestParam("vnp_PayDate") @DateTimeFormat(pattern = "yyyyMMddHHmmss") Date payDate, RedirectAttributes redirectAttributes){
        String status = request.getParameter("vnp_ResponseCode");
        String amount = request.getParameter("vnp_Amount");
        String orderInfo = request.getParameter("vnp_OrderInfo");
        if (status.equals("00")){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = userService.getUserByEmail(email);
            int theId = user.getCart().getId();
            Cart cart = cartService.getCart(theId);
            Order order = new Order();
            order.setName(user.getFullName());
            order.setPhone(user.getPhoneNumber());
            order.setAddress(user.getAddress());
            order.setUser(user);
            order.setStatus(0);
            order.setOrderDate(payDate);
            order.setTotalAmount(Double.parseDouble(amount)/100);
            if (!temporaryNote.isEmpty()){
                order.setNotes(temporaryNote);
            }
            Payment payment = new Payment(payDate, "Pay");
            payment.setOrder(order);
            order.setPayment(payment);
            for (CartItems items : cart.getCartItems()){
                if (items.getActive() == 0){
                    continue;
                }
                OrderItems orderItem = new OrderItems(items.getQuantity(), items.getProduct().getPrice(), 0);
                orderItem.setProduct(items.getProduct());
                order.addOrderItem(orderItem);
                Product product = items.getProduct();
                product.setStockQuantity(items.getProduct().getStockQuantity() - items.getQuantity());
                productService.updateProduct(product);
                cartItemsService.deleteCartItem(items.getId());
            }
            order.setCode("ASC"+orderInfo.substring(orderInfo.lastIndexOf(":") + 1).trim());
            orderService.createOrder(order);
            return "redirect:/order";
        }else{
            redirectAttributes.addFlashAttribute("error", "Thanh toán VNPay lỗi!");
            return "redirect:/checkout";
        }
    }

    @GetMapping("/order")
    public String getAllOrderForUser(Model theModel){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.getAllOrderWithItemForUser(email);

        List<Order> completedOrders = new ArrayList<>();
        List<Order> pendingOrders = new ArrayList<>();

        for (Order o : user.getOrders()){
            if (o.getStatus() == 3 || o.getStatus() == 4){
                completedOrders.add(o);
            }else{
                pendingOrders.add(o);
            }
        }

        completedOrders.sort(Comparator.comparing(Order::getOrderDate).reversed());
        pendingOrders.sort(Comparator.comparing(Order::getOrderDate).reversed());

        theModel.addAttribute("completed", completedOrders);
        theModel.addAttribute("pending", pendingOrders);

        return "order";
    }

    @GetMapping("/order/{id}")
    public String getOrderForUser(@PathVariable("id") int id, Model theModel){
        Order order = orderService.getOrderWithOrderItems(id);

        theModel.addAttribute("order", order);

        return "order-detail";
    }

    public String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    @GetMapping("/admin/orders")
    public String getAllOrderForAdmin(Model theModel){
        List<Order> orders = orderService.getAllOrder();
        List<Order> completedOrders = new ArrayList<>();
        List<Order> pendingOrders = new ArrayList<>();
        if (orders != null){
            for (Order o : orders){
                if (o.getStatus() == 3 || o.getStatus() == 4){
                    completedOrders.add(o);
                }else{
                    pendingOrders.add(o);
                }
            }
        }
        completedOrders.sort(Comparator.comparing(Order::getOrderDate).reversed());
        pendingOrders.sort(Comparator.comparing(Order::getStatus));
        theModel.addAttribute("completed", completedOrders);
        theModel.addAttribute("pending", pendingOrders);
        return "admin/order";
    }

    @GetMapping("/admin/orders/{id}")
    public String updateOrderStatus(@PathVariable("id") int id){
        Order order = orderService.getOrder(id);
        if (!(order.getStatus() == 4)){
            if (order.getStatus()+1 == 3){
                if (order.getPayment().getPaymentMethod().equals("Cash")){
                    order.getPayment().setPaymentDate(new Date());
                }
            }
            order.setStatus(order.getStatus()+1);
        }
        orderService.updateOrder(order);
        return "redirect:/admin/orders";
    }

    @GetMapping("/admin/orders/cancel/{id}")
    public String cancelOrderStatus(@PathVariable("id") int id){
        Order order = orderService.getOrder(id);
        order.setStatus(4);
        for (OrderItems items : order.getOrderItems()){
            int quan = items.getQuantity();
            Product product = items.getProduct();
            product.setStockQuantity(product.getStockQuantity()+quan);
            productService.updateProduct(product);
        }
        orderService.updateOrder(order);
        return "redirect:/admin/orders";
    }

    @GetMapping("/admin/orders/delete/{id}")
    public String deleteOrder(@PathVariable("id") int id){
        orderService.deleteOrder(id);
        return "redirect:/admin/orders";
    }

    @GetMapping("/admin/order/{id}")
    private String getOrderDetailForAdmin(@PathVariable("id") int id, Model theModel){
        Order order = orderService.getOrderWithOrderItems(id);
        theModel.addAttribute("order", order);
        return "admin/order-detail";
    }
}
