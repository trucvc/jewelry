package com.hnue.commerce.service;

import com.hnue.commerce.dto.ReviewDTO;
import com.hnue.commerce.model.Product;
import com.hnue.commerce.model.Review;
import com.hnue.commerce.model.User;
import com.hnue.commerce.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public void addReview(ReviewDTO reviewDTO, User user, Product product){
        Review review = new Review();
        review.setReviewDate(new Date());
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());
        review.setUser(user);
        review.setProduct(product);
        reviewRepository.save(review);
    }
}
