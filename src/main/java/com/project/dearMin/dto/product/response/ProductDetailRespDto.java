package com.project.dearMin.dto.product.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailRespDto {
    private int productId;
    private String productName;
    private int categoryId;
    private String categoryName;
    private int productPrice;
    private String productImg;
    private int costPrice;
    private int productCount;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private List<OptionTitleDetail> optionTitles;
    private List<OptionNameDetail> optionNames;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptionTitleDetail {
        private int optionTitleId;
        private String titleName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptionNameDetail {
        private int optionNameId;
        private String optionName;
        private int optionTitleId;
    }
}
