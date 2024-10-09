package com.project.dearMin.dto.product.request;

import com.project.dearMin.entity.product.OptionName;
import lombok.Data;

@Data
public class AddOptionNameReqDto {
    private int productId;
    private int optionTitleId;
    private String optionName;
    private int optionPrice;
    private int optionCount;
    private String optionImg;
    private int productQuantity;

    public OptionName toEntity() {
        return OptionName.builder()
                .productId(productId)
                .optionTitleId(optionTitleId)
                .optionName(optionName)
                .optionPrice(optionPrice)
                .optionCount(optionCount)
                .optionImg(optionImg)
                .productQuantity(productQuantity)
                .build();
    }
}
