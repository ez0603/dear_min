package com.project.dearMin.controller.product;

import com.project.dearMin.dto.product.request.*;
import com.project.dearMin.service.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/products")
    public ResponseEntity<?> registerProduct(@RequestBody AdminRegisterProductReqDto adminRegisterProductReqDto) {
        productService.saveProduct(adminRegisterProductReqDto);
        return ResponseEntity.created(null).body(true);
    }

    @PutMapping("/products")
    public ResponseEntity<?> updateProduct(@RequestBody UpdateProductReqDto updateProductReqDto) {
        productService.editProduct(updateProductReqDto);
        return ResponseEntity.ok(true);
    }

    @DeleteMapping("/products")
    public ResponseEntity<?> deleteProduct(@RequestParam int productId) {
        return ResponseEntity.ok(productService.deleteProduct(productId));
    }

    @GetMapping("/products")
    public ResponseEntity<?> getProduct() {
        return ResponseEntity.ok(productService.getProducts());
    }

    @GetMapping("/detail")
    public ResponseEntity<?> getProductDetail(@RequestParam int productId) {
        return ResponseEntity.ok(productService.getProductDetail(productId));
    }

    @PostMapping("/category")
    public ResponseEntity<?> addMenuCategory(@RequestBody AddProductCategoryReqDto addProductCategoryReqDto) {
        productService.insertProductCategory(addProductCategoryReqDto);
        return ResponseEntity.created(null).body(true);
    }

    @PutMapping("/category")
    public ResponseEntity<?> updateProductCategory(@RequestBody UpdateProductCategoryReqDto updateProductCategoryReqDto) {
        productService.editProductCategory(updateProductCategoryReqDto);
        return ResponseEntity.ok(true);
    }

    @DeleteMapping("/category")
    public ResponseEntity<?> deleteProductCategory(@RequestParam int categoryId) {
        return ResponseEntity.ok(productService.deleteProductCategory(categoryId));
    }

    @GetMapping("/category")
    public ResponseEntity<?> getCategory() {
        return ResponseEntity.ok().body(productService.getCategory());
    }

    @GetMapping("/products/category")
    public ResponseEntity<?> getProductCategory(@RequestParam int categoryId) {
        return ResponseEntity.ok(productService.getProductCategory(categoryId));
    }

    @PostMapping("/option/title")
    public ResponseEntity<?> addOptionTitle(@RequestBody AddOptionTitleReqDto addOptionTitleReqDto) {
        productService.insertOptionTitle(addOptionTitleReqDto);
        return ResponseEntity.created(null).body(true);
    }

    @GetMapping("/option/title")
    public ResponseEntity<?> getOptionTitles(@RequestParam int productId) {
        productService.getOptionTitles(productId);
        return ResponseEntity.ok(productService.getOptionTitles(productId));
    }

    @GetMapping("/option/titles")
    public ResponseEntity<?> getAllOptionTitles() {
        return ResponseEntity.ok().body(productService.getAllOptionTitles());
    }

    @PutMapping("/option/title")
    public ResponseEntity<?> updateOptionTitle(@RequestBody UpdateOptionTitleReqDto updateOptionTitleReqDto) {
        productService.editOptionTitle(updateOptionTitleReqDto);
        return ResponseEntity.ok(true);
    }

    @DeleteMapping("/option/title")
    public ResponseEntity<?> deleteOptionTitle(@RequestBody DeleteOptionTitleReqDto deleteOptionTitleReqDto) {
        productService.deleteOptionTitle(deleteOptionTitleReqDto);
        return ResponseEntity.ok(true);
    }

    @PostMapping("/option/name")
    public ResponseEntity<?> addOptionName(@RequestBody AddOptionNameReqDto addOptionNameReqDto) {
        productService.insertOptionName(addOptionNameReqDto);
        return ResponseEntity.created(null).body(true);
    }

    @GetMapping("/option")
    public ResponseEntity<?> getOptionsByAdminId(@RequestParam int optionTitleId) {
        return ResponseEntity.ok(productService.getOptionsByOptionTitleId(optionTitleId));
    }

    @PutMapping("/option")
    public ResponseEntity<?> updateOptionName(@RequestBody UpdateOptionNameReqDto updateOptionNameReqDto) {
        productService.editOptionName(updateOptionNameReqDto);
        return ResponseEntity.ok(true);
    }

    @PostMapping("/material")
    public ResponseEntity<?> addProductWithMaterials(@RequestBody ProductWithMaterialsReqDto productWithMaterialsReqDto) {
        int productId = productService.addProductMaterial(
                productWithMaterialsReqDto.getProductReqDto(),
                productWithMaterialsReqDto.getOptionNameIds(),
                productWithMaterialsReqDto.getProductQuantities()
        );
        return ResponseEntity.created(null).body(Collections.singletonMap("productId", productId));
    }

    @PutMapping("/material")
    public ResponseEntity<?> updateProductWithMaterials(
            @RequestParam int productId,
            @RequestBody ProductWithMaterialsReqDto productWithMaterialsReqDto) {
        productService.updateProductMaterial(
                productId,
                productWithMaterialsReqDto.getProductReqDto(),
                productWithMaterialsReqDto.getOptionNameIds(),
                productWithMaterialsReqDto.getProductQuantities()
        );
        return ResponseEntity.ok(Collections.singletonMap("message", "Product updated successfully"));
    }

}
