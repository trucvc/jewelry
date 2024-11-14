package com.hnue.commerce.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private int id;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private double price;

    @Column(name = "stock_quantity")
    private int stockQuantity;

    @Column(name = "tag")
    private String tag;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductImage> images;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductFavorite> productFavorites;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<CartItems> cartItems;

    @OneToMany(mappedBy = "product", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<OrderItems> orderItems;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Review> reviews;

    public Product(){

    }

    public Product(String code, String name, String description, double price, int stockQuantity, String tag) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.tag = tag;
    }

    public void addProductImage(ProductImage theProductImage){
        if (images == null){
            images = new ArrayList<>();
        }
        images.add(theProductImage);
        theProductImage.setProduct(this);
    }

    public void addProductFavorite(ProductFavorite theProductFavorite){
        if (productFavorites == null){
            productFavorites = new ArrayList<>();
        }
        productFavorites.add(theProductFavorite);
        theProductFavorite.setProduct(this);
    }

    public void addReview(Review theReview){
        if (reviews == null){
            reviews = new ArrayList<>();
        }
        reviews.add(theReview);
        theReview.setProduct(this);
    }

    public void addCartItems(CartItems theCartItems){
        if (cartItems == null){
            cartItems = new ArrayList<>();
        }
        cartItems.add(theCartItems);
        theCartItems.setProduct(this);
    }

    public void addOrderItems(OrderItems theOrderItems){
        if (orderItems == null){
            orderItems = new ArrayList<>();
        }
        orderItems.add(theOrderItems);
        theOrderItems.setProduct(this);
    }
}
