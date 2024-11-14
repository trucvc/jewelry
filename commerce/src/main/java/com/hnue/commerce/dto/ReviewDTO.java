package com.hnue.commerce.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    @Min(value = 1, message = "Chỉ trong khoảng từ 1 - 5")
    @Max(value = 5, message = "Chỉ trong khoảng từ 1 - 5")
    private int rating;

    @NotBlank(message = "Nội dung không được để trống")
    private String comment;

    private int idItem;
}
