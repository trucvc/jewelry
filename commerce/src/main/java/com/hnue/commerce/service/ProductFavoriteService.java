package com.hnue.commerce.service;

import com.hnue.commerce.model.Product;
import com.hnue.commerce.model.ProductFavorite;
import com.hnue.commerce.model.User;
import com.hnue.commerce.repository.ProductFavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductFavoriteService {
    private final ProductFavoriteRepository productFavoriteRepository;

    public void create(ProductFavorite theProductFavorite){
        productFavoriteRepository.save(theProductFavorite);
    }

    public void delete(int theId){
        ProductFavorite productFavorite = productFavoriteRepository.findById(theId).orElseThrow(() -> new  RuntimeException("Không tìm thấy sản phẩm với id: " + theId));
        productFavoriteRepository.delete(productFavorite);
    }

    public boolean isProductInFavorites(User user, Product product) {
        Optional<ProductFavorite> favorite = productFavoriteRepository.findByUserAndProduct(user, product);
        return favorite.isPresent();
    }
}
