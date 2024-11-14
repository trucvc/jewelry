package com.hnue.commerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private int id;

    @Email(message = "Phải đúng định dạng email")
    @NotBlank(message = "Không để trống email")
    private String email;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$",
            message = "Mật khẩu phải có ít nhất một chữ hoa, một chữ thường, một chữ số, một ký tự đặc biệt và tối thiểu 8 ký tự")
    private String password;

    @NotBlank(message = "Không để trống tên")
    @Length(min = 3, max = 20, message = "Tên nằm trong khoảng từ 3-20 kí tự")
    private String fullName;

    @NotBlank(message = "Không để trống chức vụ")
    private String role;

    @Pattern(regexp = "^[a-zA-Z0-9/ -]+$", message = "Địa chỉ không chứa kí tự đặc biệt trừ kí tự / và -")
    private String address;

    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại không hợp lệ")
    private String phone;
}
