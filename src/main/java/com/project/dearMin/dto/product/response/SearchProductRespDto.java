package com.project.dearMin.dto.product.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SearchProductRespDto {
    private int productId;
    private String productName;
    private int categoryId;
    private String categoryName;
    private int productPrice;
    private String productImg;
    private int costPrice;
    private int productCount;
    private int optionTitleId;
    private String titleName;
    private int optionNameId;
    private String optionName;
}
