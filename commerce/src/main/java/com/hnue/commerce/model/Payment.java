package com.hnue.commerce.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "payments")
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private int id;

    @Column(name = "payment_date")
    private Date paymentDate;

    @Column(name = "payment_method")
    private String paymentMethod;

    @OneToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "order_id")
    private Order order;

    public Payment(){

    }

    public Payment(Date paymentDate, String paymentMethod) {
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
    }
}
