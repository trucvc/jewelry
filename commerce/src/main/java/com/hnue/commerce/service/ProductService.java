package com.hnue.commerce.service;

import com.hnue.commerce.model.Product;
import com.hnue.commerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Product getProductById(int id){
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với id: "+id));
    }

    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }

    public Page<Product> findAllProducts(int page, int size, String search, Integer category, String tag, String price, String sort) {
        Specification<Product> spec = (root, query, criteriaBuilder) -> {
            var predicates= criteriaBuilder.conjunction();

            if (search != null && !search.trim().isEmpty()) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.or(
                        criteriaBuilder.like(root.get("name"), "%" + search.toLowerCase() + "%"),
                        criteriaBuilder.like(root.get("code"), "%" + search.toLowerCase() + "%")
                    )
                );
            }

            if (category != null) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.equal(root.join("category").get("id"), category));
            }

            if (tag != null && !tag.trim().isEmpty()) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.equal(root.get("tag"), tag));
            }

            if (price != null && !price.trim().isEmpty()) {
                String[] priceRange = price.split("-");
                if (priceRange.length == 2) {
                    double minPrice = Double.parseDouble(priceRange[0]);
                    double maxPrice = Double.parseDouble(priceRange[1]);
                    predicates = criteriaBuilder.and(predicates,
                            criteriaBuilder.between(root.get("price"), minPrice, maxPrice));
                } else if (priceRange.length == 1) {
                    double minPrice = Double.parseDouble(priceRange[0]);
                    predicates = criteriaBuilder.and(predicates,
                            criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
                }
            }

            if (sort != null) {
                switch (sort) {
                    case "price-acs":
                        query.orderBy(criteriaBuilder.asc(root.get("price")));
                        break;
                    case "price-des":
                        query.orderBy(criteriaBuilder.desc(root.get("price")));
                        break;
                    default:
                        break;
                }
            }

            return predicates;
        };
        return productRepository.findAll(spec, PageRequest.of(page, size));
    }

    public List<String> findAllDistinctTags(){
        return productRepository.findAllDistinctTags();
    }

    public Product getProductWithReviews(String code){
        return productRepository.getProductWithReviews(code);
    }

    public Product getProductWithImage(String code){
        return productRepository.getProductWithImage(code);
    }

    public Product getProductWithImages(int id){
        return productRepository.getProductWithImages(id);
    }

    public List<Product> getRelatedProducts(List<Product> products){
        for (int i = 0; i < products.size(); i++) {
            Product updatedProduct = productRepository.getProductWithImages(products.get(i).getId());
            products.set(i, updatedProduct);
        }
        return products;
    }

    public List<Product> getLimitProductsWithImages(){
        List<Product> products = productRepository.getAllProductsWithImages();
        Collections.shuffle(products);
        if (products.size() > 7){
            return products.subList(0,7);
        }else{
            return products;
        }
    }

    public Product updateProduct(Product product){
        return productRepository.save(product);
    }

    public Product getProduct(String code){
        Product product = productRepository.findByCode(code).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với code: "+code));
        return product;
    }

    public void delete(int theId){
        Product product = productRepository.findById(theId).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với id: "+theId));
        productRepository.delete(product);
    }

    public boolean preUpdateProduct(int id, String code){
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với id: "+id));
        return product.getCode().equals(code.toUpperCase());
    }

    public boolean existsByCode(String code){
        return productRepository.existsByCode(code.toUpperCase());
    }
}
