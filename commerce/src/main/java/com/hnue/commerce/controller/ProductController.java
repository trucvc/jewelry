package com.hnue.commerce.controller;

import com.hnue.commerce.dto.ImageDTO;
import com.hnue.commerce.dto.ProductDTO;
import com.hnue.commerce.model.*;
import com.hnue.commerce.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductController {
    private final UserService userService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final CartService cartService;
    private final ProductFavoriteService productFavoriteService;
    private final ProductImageService productImageService;
    private final FirebaseStorageService firebaseStorageService;

    @InitBinder
    public void initBinder(WebDataBinder dataBinder){
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("/shop")
    public String shop(Model theModel, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "9") int size, @RequestParam(required = false) String search,
                       @RequestParam(required = false) Integer category, @RequestParam(required = false) String tag, @RequestParam(required = false) String price, @RequestParam(required = false) String sort){
        Page<Product> productPage = productService.findAllProducts(page, size, search, category, tag, price, sort);
        List<Product> products = productService.getAllProducts();
        List<String> tags = productService.findAllDistinctTags();
        List<Category> categories = categoryService.getAllCategories();

        theModel.addAttribute("productPage", productPage);
        theModel.addAttribute("products", products);
        theModel.addAttribute("tags", tags);
        theModel.addAttribute("categories", categories);
        theModel.addAttribute("search", search);
        theModel.addAttribute("selectedCategory", category);
        theModel.addAttribute("selectedTag", tag);
        theModel.addAttribute("selectedPrice", price);
        theModel.addAttribute("selectedSort", sort);

        return "shop";
    }

    @GetMapping("/shop/{code}")
    public String singleProduct(Model theModel, @PathVariable("code") String code){
        Product product = productService.getProductWithReviews(code);
        Product p = productService.getProductWithImage(code);
        double rv = 0.0;
        if (product.getReviews() != null){
            product.getReviews().sort(Comparator.comparing(Review::getReviewDate).reversed());
            rv = product.getReviews().stream().mapToDouble(Review::getRating).sum() / product.getReviews().size();
            rv = Math.round(rv * 10.0) / 10.0;
        }
        Category category = categoryService.getCategoryWithLimitedProducts(product.getCategory().getId(), code);
        category.getProducts().remove(product);
        List<Product> products = productService.getRelatedProducts(category.getProducts());

        theModel.addAttribute("product", product);
        theModel.addAttribute("p", p);
        theModel.addAttribute("rv", rv);
        theModel.addAttribute("products", products);

        return "single-product";
    }

    @GetMapping("/add-product")
    public String addProduct(@RequestParam("code") String code,
                             @RequestParam("quantity") int quantity,
                             RedirectAttributes redirectAttributes){
        if (quantity <= 0){
            redirectAttributes.addFlashAttribute("error", "Số lượng phải lớn hơn 0");
            return "redirect:/shop/" + code;
        }
        Product product = productService.getProduct(code);
        if (product == null){
            return "redirect:/shop";
        }
        if (quantity > product.getStockQuantity()){
            redirectAttributes.addFlashAttribute("error", "Không đủ sản phẩm");
            return "redirect:/shop/" + code;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        int theId = user.getCart().getId();

        Cart cart = cartService.getCart(theId);
        boolean check = true;
        for (CartItems item : cart.getCartItems()){
            if (item.getProduct().getCode().equals(code)){
                int quan = item.getQuantity();
                item.setQuantity(quan+quantity);
                check = false;
            }
        }

        if (check){
            CartItems cartItems = new CartItems();
            cartItems.setProduct(product);
            cartItems.setQuantity(quantity);
            cart.addCartItem(cartItems);
        }

        cartService.save(cart);
        return "redirect:/cart";
    }

    @GetMapping("/add-wishlist")
    public String addWishList(@RequestParam("code") String code){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        Product product = productService.getProduct(code);
        boolean check = true;
        for (ProductFavorite pf : user.getProductFavorites()){
            if (pf.getProduct() == product){
                check = false;
                break;
            }
        }
        if (check){
            ProductFavorite productFavorite = new ProductFavorite();
            productFavorite.setUser(user);
            productFavorite.setProduct(product);
            productFavoriteService.create(productFavorite);
        }
        return "redirect:/wishlist";
    }

    @GetMapping("/add-product-thumbnail")
    public String addProductThumbnail(@RequestParam("code") String code){
        int quantity = 1;
        return "redirect:/add-product?code=" + code + "&quantity=" + quantity;
    }

    @GetMapping("/admin/category/{id}/create")
    public String createProductForAdmin(@PathVariable("id") int id, Model theModel){
        ProductDTO productDTO = new ProductDTO();
        productDTO.setPrice(1);
        theModel.addAttribute("productDTO", productDTO);
        theModel.addAttribute("actionUrl", "/admin/category/"+id+"/create");
        theModel.addAttribute("url", "/admin/category/"+id);
        return "admin/product-form";
    }

    @PostMapping("/admin/category/{id}/create")
    public String processCreateProductForAdmin(@PathVariable("id") int id, @Valid @ModelAttribute("productDTO") ProductDTO productDTO,
                                               BindingResult result, Model theModel){
        if (productDTO.getCode() != null){
            if (productService.existsByCode(productDTO.getCode().toUpperCase())){
                result.rejectValue("code", "error.code", "Sản phẩm này đã tồn tại");
            }
        }
        if (result.hasErrors()){
            theModel.addAttribute("productDTO", productDTO);
            theModel.addAttribute("actionUrl", "/admin/category/"+id+"/create");
            theModel.addAttribute("url", "/admin/category/"+id);
            return "admin/product-form";
        }
        String tag = null;
        if (productDTO.getTag() != null){
            tag = productDTO.getTag().toLowerCase();
        }
        Product product = new Product(productDTO.getCode().toUpperCase(), productDTO.getName().toLowerCase(), productDTO.getDescription(),
                productDTO.getPrice(), productDTO.getStockQuantity(), tag);
        Category category = categoryService.getCategory(id);
        product.setCategory(category);
        productService.updateProduct(product);
        return "redirect:/admin/category/"+id;
    }

    @GetMapping("/admin/product/{id}")
    public String product(@PathVariable("id") int id, Model theModel){
        Product product = productService.getProductWithImages(id);
        theModel.addAttribute("product", product);
        return "admin/product-detail";
    }

    @GetMapping("/admin/products/{id}")
    public String updateProductForAdmin(@PathVariable("id") int id, Model theModel){
        Product product = productService.getProductById(id);
        ProductDTO productDTO = ProductDTO.builder()
                .id(product.getId()).code(product.getCode()).description(product.getDescription()).name(product.getName())
                .price(product.getPrice()).stockQuantity(product.getStockQuantity()).tag(product.getTag())
                .build();
        theModel.addAttribute("productDTO", productDTO);
        theModel.addAttribute("actionUrl", "/admin/products/"+id);
        theModel.addAttribute("url", "/admin/category/"+product.getCategory().getId());
        return "admin/product-form";
    }

    @PostMapping("/admin/products/{id}")
    public String processUpdateProductForAdmin(@PathVariable("id") int id, @Valid @ModelAttribute("productDTO") ProductDTO productDTO,
                                               BindingResult result, Model theModel){
        if (productDTO.getCode() != null){
            if (!productService.preUpdateProduct(id, productDTO.getCode())){
                if (productService.existsByCode(productDTO.getCode())){
                    result.rejectValue("code", "error.code", "Sản phẩm này đã tồn tại");
                }
            }
        }
        if (result.hasErrors()){
            Product product = productService.getProductById(id);
            theModel.addAttribute("productDTO", productDTO);
            theModel.addAttribute("actionUrl", "/admin/products/"+id);
            theModel.addAttribute("url", "/admin/category/"+product.getCategory().getId());
            return "admin/product-form";
        }
        Product product = productService.getProductById(id);
        product.setCode(productDTO.getCode().toUpperCase());
        product.setName(productDTO.getName().toLowerCase());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setStockQuantity(productDTO.getStockQuantity());
        if (productDTO.getTag() != null){
            product.setTag(productDTO.getTag().toLowerCase());
        }
        productService.updateProduct(product);
        return "redirect:/admin/category/"+product.getCategory().getId();
    }

    @GetMapping("/admin/products/delete/{id}")
    public String deleteProductForAdmin(@PathVariable("id") int id){
        Product product = productService.getProductById(id);
        int theId = product.getCategory().getId();
        productService.delete(id);
        return "redirect:/admin/category/"+theId;
    }

    @GetMapping("/admin/product/{id}/add")
    public String addImageWithProductForAdmin(@PathVariable("id") int id, Model theModel){
        ImageDTO imageDTO = new ImageDTO();
        theModel.addAttribute("imageDTO", imageDTO);
        theModel.addAttribute("actionUrl", "/admin/product/"+id+"/add");
        theModel.addAttribute("url", "/admin/product/"+id);
        return "admin/add-image";
    }

    @PostMapping("/admin/product/{id}/add")
    public String processAddImageWithProductForAdmin(@PathVariable("id") int id, @Valid @ModelAttribute("imageDTO") ImageDTO imageDTO,
                                                     BindingResult result, Model theModel){
        if (result.hasErrors()){
            theModel.addAttribute("imageDTO", imageDTO);
            theModel.addAttribute("actionUrl", "/admin/product/"+id+"/add");
            theModel.addAttribute("url", "/admin/product/"+id);
            return "admin/add-image";
        }
        Product product = productService.getProductById(id);
        for (MultipartFile file : imageDTO.getImages()){
            try {
                String url = firebaseStorageService.uploadFile(file);
                ProductImage productImage = new ProductImage(url);
                product.addProductImage(productImage);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        productService.updateProduct(product);
        return "redirect:/admin/product/"+id;
    }

    @GetMapping("/admin/product/{id}/delete/{img}")
    public String deleteImange(@PathVariable("id") int id, @PathVariable("img") int img){
        ProductImage productImage = productImageService.getProductImage(img);
        firebaseStorageService.deleteFile(productImage.getImageUrl());
        productImageService.deleteImage(img);
        return "redirect:/admin/product/"+id;
    }
}
