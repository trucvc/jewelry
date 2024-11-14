package com.hnue.commerce.controller;

import com.hnue.commerce.dto.CategoryDTO;
import com.hnue.commerce.model.Category;
import com.hnue.commerce.model.Product;
import com.hnue.commerce.service.CategoryService;
import com.hnue.commerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final ProductService productService;

    @GetMapping("/admin/categories")
    public String categoryForAdmin(Model theModel){
        theModel.addAttribute("categories", categoryService.getAllCategories());
        return "admin/category";
    }

    @GetMapping("/admin/categories/create")
    public String createCategoryForAdmin(Model theModel){
        CategoryDTO categoryDTO = new CategoryDTO();
        theModel.addAttribute("categoryDTO", categoryDTO);
        theModel.addAttribute("actionUrl", "/admin/categories/create");
        return "admin/category-form";
    }

    @PostMapping("/admin/categories/create")
    public String processCreateCategoryForAdmin(@Valid @ModelAttribute("categoryDTO") CategoryDTO categoryDTO,
                                                BindingResult result, Model theModel){
        if (categoryService.existsByName(categoryDTO.getName().toLowerCase())){
            result.rejectValue("name", "error.name", "Danh mục này đã tồn tại");
        }
        if (result.hasErrors()){
            theModel.addAttribute("categoryDTO", categoryDTO);
            theModel.addAttribute("actionUrl", "/admin/categories/create");
            return "admin/category-form";
        }
        Category category = new Category(categoryDTO.getName().toLowerCase(), categoryDTO.getDescription().toLowerCase());
        categoryService.createCategory(category);
        return "redirect:/admin/categories";
    }

    @GetMapping("/admin/category/{id}")
    public String getProductForAdmin(@PathVariable("id") int id, Model theModel){
        Category category = categoryService.getProductsForCategory(id);
        theModel.addAttribute("category", category);
        return "admin/product-category";
    }

    @GetMapping("/admin/categories/{id}")
    public String updateCategoryForAdmin(@PathVariable("id") int id, Model theModel){
        Category category = categoryService.getCategory(id);
        CategoryDTO categoryDTO = CategoryDTO.builder()
                .id(category.getId()).name(category.getName()).description(category.getDescription())
                .build();

        theModel.addAttribute("categoryDTO", categoryDTO);
        theModel.addAttribute("actionUrl", "/admin/categories/"+id);

        return "admin/category-form";
    }

    @PostMapping("/admin/categories/{id}")
    public String processUpdateCategoryForAdmin(@PathVariable("id") int id, @Valid @ModelAttribute("categoryDTO") CategoryDTO categoryDTO,
                                                BindingResult result, Model theModel ){
        if (!categoryService.preUpdateCategory(id, categoryDTO.getName().toLowerCase())){
            if (categoryService.existsByName(categoryDTO.getName().toLowerCase())){
                result.rejectValue("name", "error.name", "Danh mục này đã tồn tại");
            }
        }
        if (result.hasErrors()){
            theModel.addAttribute("categoryDTO", categoryDTO);
            theModel.addAttribute("actionUrl", "/admin/categories/"+id);

            return "admin/category-form";
        }
        Category category = categoryService.getCategory(id);
        category.setName(categoryDTO.getName().toLowerCase());
        category.setDescription(categoryDTO.getDescription());
        categoryService.updateCategory(category);
        return "redirect:/admin/categories";
    }

    @GetMapping("/admin/categories/delete/{id}")
    public String deleteCategoryForAdmin(@PathVariable("id") int id){
        categoryService.deleteCategory(id);
        return "redirect:/admin/categories";
    }
}
