package com.project.dearMin.dto.product.request;

import com.project.dearMin.entity.product.OptionTitle;
import lombok.Data;

@Data
public class AddOptionTitleReqDto {
    private int productId;
    private int menuId;
    private String titleName;

    public OptionTitle toEntity() {
        return OptionTitle.builder()
                .productId(productId)
                .titleName(titleName)
                .build();
    }
}
