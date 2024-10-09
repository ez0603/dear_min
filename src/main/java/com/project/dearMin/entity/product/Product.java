package com.project.dearMin.entity.product;

import com.project.dearMin.dto.product.response.SearchProductRespDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private int productId;
    private String productName;
    private int categoryId;
    private String categoryName;
    private int productPrice;
    private String productImg;
    private int costPrice;
    private int optionTitleId;
    private String titleName;
    private int optionNameId;
    private String optionName;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    public SearchProductRespDto toSearchProductRespDto() {
        return SearchProductRespDto.builder()
                .productId(productId)
                .categoryId(categoryId)
                .productName(productName)
                .productPrice(productPrice)
                .productImg(productImg)
                .costPrice(costPrice)
                .categoryName(categoryName)
                .optionTitleId(optionTitleId)
                .titleName(titleName)
                .optionNameId(optionNameId)
                .optionName(optionName)
                .build();
    }
}