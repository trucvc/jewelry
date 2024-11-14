package com.hnue.commerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    @NotBlank(message = "Tên không được để trống")
    private String fullName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại không hợp lệ")
    private String phone;

    @NotBlank(message = "Vui lòng chọn thành phố")
    private String city;

    @NotBlank(message = "Vui lòng chọn quận")
    private String district;

    @NotBlank(message = "Địa chỉ không được để trống")
    @Pattern(regexp = "^[a-zA-Z0-9, /]+$", message = "Địa chỉ chỉ được chứa chữ cái; chữ số; dấu ',' và dấu '/'")
    private String address;

    private String note;

    @NotBlank(message = "Vui lòng chọn phương thức thanh toán")
    private String paymentMethod;
}
