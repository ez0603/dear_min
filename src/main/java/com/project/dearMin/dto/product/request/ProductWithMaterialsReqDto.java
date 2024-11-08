package com.project.dearMin.dto.product.request;

import lombok.Data;

import java.util.List;

@Data
public class ProductWithMaterialsReqDto {
    private AdminRegisterProductReqDto productReqDto;
    private List<Integer> optionNameIds;
    private List<Integer> productQuantities;
}
