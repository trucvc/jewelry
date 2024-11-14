package com.hnue.commerce.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Data
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private int id;

    @Column(name = "category_name")
    private String name;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "category",
            cascade = CascadeType.ALL)
    private List<Product> products;

    public Category(){

    }

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void add(Product theProduct){
        if (products == null){
            products = new ArrayList<>();
        }
        products.add(theProduct);
        theProduct.setCategory(this);
    }
}
