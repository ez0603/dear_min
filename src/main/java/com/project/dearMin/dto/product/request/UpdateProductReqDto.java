package com.project.dearMin.dto.product.request;

import com.project.dearMin.entity.product.Product;
import lombok.Data;

@Data
public class UpdateProductReqDto {
    private int productId;
    private String productName;
    private int categoryId;
    private String categoryName;
    private int productPrice;
    private String productImg;
    private int costPrice;
    private int productCount;

    public Product toEntity() {
        return Product.builder()
                .productId(productId)
                .productName(productName)
                .categoryId(categoryId)
                .categoryName(categoryName)
                .productPrice(productPrice)
                .productImg(productImg)
                .costPrice(costPrice)
                .productCount(productCount)
                .build();
    }


}
