package com.hnue.commerce.service;

import com.hnue.commerce.model.User;
import com.hnue.commerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean isEmail(String email){
        return userRepository.existsByEmail(email);
    }

    public void addUser(User theUser){
        String pass = passwordEncoder.encode(theUser.getPassword());
        theUser.setPassword(pass);
        theUser.setCreatedAt(new Date());
        theUser.createCart();
        userRepository.save(theUser);
    }

    public User getUser(int id){
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy user với id"+id));
    }

    public User getUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public User getUserWithProductFavoriteAndProduct(String email){
        return userRepository.findUserWithProductFavoriteAndProduct(email);
    }

    public User updateUser(User theUser){
        return userRepository.save(theUser);
    }

    public User getAllOrderWithItemForUser(String email){
        return userRepository.findAllOrderWithItemForUser(email);
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public void deleteUser(int id){
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy user với id"+id));
        userRepository.delete(user);
    }

    public long getUserCount() {
        return userRepository.count();
    }

    public List<User> getNewUser(){
        List<User> users = userRepository.findNewUser();
        if (users.size() > 7){
            return users.subList(0,7);
        }else{
            return users;
        }
    }
}
