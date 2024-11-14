package com.hnue.commerce.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class ImageValidator implements ConstraintValidator<ValidImage, List<MultipartFile>> {
    @Override
    public boolean isValid(List<MultipartFile> multipartFiles, ConstraintValidatorContext constraintValidatorContext) {
        if (multipartFiles == null || multipartFiles.isEmpty()) {
            return false;
        }

        for (MultipartFile file : multipartFiles) {
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return false;
            }
        }
        return true;
    }
}
