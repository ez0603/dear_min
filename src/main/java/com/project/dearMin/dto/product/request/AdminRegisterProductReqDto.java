package com.project.dearMin.dto.product.request;

import com.project.dearMin.entity.product.OptionName;
import com.project.dearMin.entity.product.OptionTitle;
import com.project.dearMin.entity.product.Product;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AdminRegisterProductReqDto {
    private String productName;
    private int categoryId;
    private int productPrice;
    private String productImg;
    private int costPrice;


    private List<OptionTitle> optionTitles = new ArrayList<>();
    private List<OptionName> optionNames = new ArrayList<>();

    public Product toEntity() {
        return Product.builder()
                .productName(productName)
                .categoryId(categoryId)
                .productPrice(productPrice)
                .productImg(productImg)
                .costPrice(costPrice)
                .build();
    }



//    public List<OptionTitle> getOptionTitles() {
//        return optionTitles;
//    }
//
//    // 옵션 이름 리스트를 반환하는 메서드
//    public List<OptionName> getOptionNames() {
//        return optionNames;
//    }
}
