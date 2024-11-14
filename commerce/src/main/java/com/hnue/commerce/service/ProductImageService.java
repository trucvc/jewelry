package com.hnue.commerce.service;

import com.hnue.commerce.model.ProductImage;
import com.hnue.commerce.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductImageService {
    private final ProductImageRepository productImageRepository;

    public ProductImage getProductImage(int id){
        return productImageRepository.findById(id).orElseThrow(() -> new RuntimeException("Không có ảnh với id:"+id));
    }

    public void deleteImage(int id){
        ProductImage productImage = productImageRepository.findById(id).orElseThrow(() -> new RuntimeException("Không có ảnh với id:"+id));
        productImageRepository.delete(productImage);
    }
}
