package com.hnue.commerce.service;

import com.hnue.commerce.model.Category;
import com.hnue.commerce.model.Product;
import com.hnue.commerce.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public void createCategory(Category category){
        categoryRepository.save(category);
    }

    public Category getCategory(int id){
        return categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Không có danh mục với id: "+id));
    }

    public Category getCategoryWithLimitedProducts(int id, String code){
        Category category = categoryRepository.getRelatedProductsForCategory(id);
        Product productToRemove = category.getProducts().stream()
                .filter(product -> product.getCode().equals(code))
                .findFirst()
                .orElse(null);
        if (productToRemove != null) {
            category.getProducts().remove(productToRemove);
        }
        Collections.shuffle(category.getProducts());
        if (category.getProducts().size() > 4){
            category.setProducts(category.getProducts().subList(0,4));
        }
        return category;
    }

    public Category getProductsForCategory(int id){
        return categoryRepository.getProductsForCategory(id);
    }

    public List<Category> getAllCategories(){
        return categoryRepository.findAll();
    }

    public Category updateCategory(Category category){
        return categoryRepository.save(category);
    }

    public void deleteCategory(int id){
        Category category = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Không có danh mục với id: "+id));
        categoryRepository.delete(category);
    }

    public boolean existsByName(String name){
        return categoryRepository.existsByName(name);
    }

    public boolean preUpdateCategory(int id, String name){
        Category category = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Không có danh mục với id: "+id));
        return category.getName().equals(name);
    }
}
