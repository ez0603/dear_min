package com.project.dearMin.entity.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductMaterial {
    private int productMaterialId;
    private int productId;
    private int optionNameId;
    private int productQuantity;
}
