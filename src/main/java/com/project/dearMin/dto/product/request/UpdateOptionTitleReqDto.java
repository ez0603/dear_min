package com.project.dearMin.dto.product.request;

import com.project.dearMin.entity.product.OptionTitle;
import lombok.Data;

@Data
public class UpdateOptionTitleReqDto {
    private int optionTitleId;
    private int productId;
    private String titleName;

    public OptionTitle toEntity() {
        return OptionTitle.builder()
                .optionTitleId(optionTitleId)
                .productId(productId)
                .titleName(titleName)
                .build();
    }
}