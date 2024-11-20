package com.hnue.commerce.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int id;

    @Email(message = "Phải đúng định dạng email")
    @NotBlank(message = "Không để trống email")
    @Column(name = "email")
    private String email;

    @NotBlank(message = "Không để trống mật khẩu")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$",
            message = "Mật khẩu phải có ít nhất một chữ hoa, một chữ thường, một chữ số, một ký tự đặc biệt và tối thiểu 8 ký tự")
    @Column(name = "password")
    private String password;

    @NotBlank(message = "Không để trống tên")
    @Length(min = 3, max = 20, message = "Tên nằm trong khoảng từ 3-20 kí tự")
    @Pattern(regexp = "^[A-Za-zÀ-Ýà-ỹĐđ\\s]+$", message = "Tên chỉ được chứa chữ cái và khoảng trắng")
    @Column(name = "full_name")
    private String fullName;

    @Column(name = "role")
    private String role;

    @Column(name = "address")
    private String address;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "created_at")
    private Date createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ProductFavorite> productFavorites;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Cart cart;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Review> reviews;

    public User(){

    }

    public User(String email, String password, String fullName, String role, String address, String phoneNumber, Date createdAt) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.createdAt = createdAt;
    }

    public void addProductFavorite(ProductFavorite theProductFavorite){
        if (productFavorites == null){
            productFavorites = new ArrayList<>();
        }
        productFavorites.add(theProductFavorite);
        theProductFavorite.setUser(this);
    }

    public void addOrder(Order theOrder){
        if (orders == null){
            orders = new ArrayList<>();
        }
        orders.add(theOrder);
        theOrder.setUser(this);
    }

    public void addReview(Review theReview){
        if (reviews == null){
            reviews = new ArrayList<>();
        }
        reviews.add(theReview);
        theReview.setUser(this);
    }

    public void createCart(){
        this.cart = new Cart();
        this.cart.setUser(this);
    }
}
