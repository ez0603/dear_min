package com.project.dearMin.dto.product.request;

import com.project.dearMin.entity.product.OptionTitle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteOptionTitleReqDto {
    private int productId;
    private int optionTitleId;

    public OptionTitle toEntity() {
        return OptionTitle.builder()
                .productId(productId)
                .optionTitleId(optionTitleId)
                .build();
    }
}
