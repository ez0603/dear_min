package com.project.dearMin.dto.product.request;

import lombok.Data;

@Data
public class ProductMaterialReqDto {
    private int productId;
    private int optionNameId;
    private int productQuantity;
}
