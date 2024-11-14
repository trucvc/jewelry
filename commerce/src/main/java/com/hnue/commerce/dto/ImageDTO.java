package com.hnue.commerce.dto;

import com.hnue.commerce.validation.ValidImage;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageDTO {
    @NotNull(message = "Không để trống ảnh")
    @Size(min = 1, message = "Phải chọn ít nhất một ảnh")
    @ValidImage
    private List<MultipartFile> images;
}
