package com.hnue.commerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private int id;

    @NotBlank(message = "Không để trống mã sản phẩm")
    private String code;

    @NotBlank(message = "Không để trống mã sản phẩm")
    private String name;

    private String description;

    @NotNull(message = "Không để trống giá sản phẩm")
    @Min(value = 1, message = "Giá phải lớn hơn 0")
    private double price;

    @NotNull(message = "Không để trống giá sản phẩm")
    @Min(value = 0, message = "Số lượng phải từ 0 trở lên")
    private Integer stockQuantity;

    private String tag;
}
