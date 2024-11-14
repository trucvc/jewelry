package com.hnue.commerce.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private int id;

    @Column(name = "order_date")
    private Date orderDate;

    @Column(name = "status")
    private int status;

    @Column(name = "total_amount")
    private double totalAmount;

    @Column(name = "notes")
    private String notes;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItems> orderItems;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;

    public Order(){

    }

    public Order(Date orderDate, int status, double totalAmount, String notes, String code) {
        this.orderDate = orderDate;
        this.status = status;
        this.totalAmount = totalAmount;
        this.notes = notes;
        this.code = code;
    }

    public String getStatusDescription() {
        return switch (status) {
            case 0 -> "Chưa xử lý";
            case 1 -> "Đang xử lý";
            case 2 -> "Đang giao";
            case 3 -> "Đã giao";
            case 4 -> "Đã hủy";
            default -> "Không xác định";
        };
    }

    public String getPreStatusDescription() {
        return switch (status+1) {
            case 0 -> "Chưa xử lý";
            case 1 -> "Đang xử lý";
            case 2 -> "Đang giao";
            case 3 -> "Đã giao";
            case 4 -> "Đã hủy";
            default -> "Không xác định";
        };
    }

    public void addOrderItem(OrderItems theOrderItems){
        if (orderItems == null){
            orderItems = new ArrayList<>();
        }
        orderItems.add(theOrderItems);
        theOrderItems.setOrder(this);
    }
}
