package com.project.dearMin.service.product;

import com.project.dearMin.dto.product.request.AddProductCategoryReqDto;
import com.project.dearMin.dto.product.request.AdminRegisterProductReqDto;
import com.project.dearMin.dto.product.request.UpdateProductCategoryReqDto;
import com.project.dearMin.dto.product.request.UpdateProductReqDto;
import com.project.dearMin.dto.product.response.ProductDetailRespDto;
import com.project.dearMin.dto.product.response.SearchProductRespDto;
import com.project.dearMin.entity.product.Category;
import com.project.dearMin.entity.product.OptionName;
import com.project.dearMin.entity.product.OptionTitle;
import com.project.dearMin.entity.product.Product;
import com.project.dearMin.repository.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Transactional(rollbackFor = Exception.class)
    public void saveProduct(AdminRegisterProductReqDto adminRegisterProductReqDto) {
        // 1. product_tb에 상품 저장
        Product product = adminRegisterProductReqDto.toEntity();
        productMapper.saveProduct(product);  // 여기서 productId가 생성됩니다.

        // 3. 옵션 타이틀 및 옵션 이름 저장
        for (OptionTitle optionTitle : adminRegisterProductReqDto.getOptionTitles()) {
            optionTitle.setProductId(product.getProductId()); // productId 설정
            productMapper.saveOptionTitle(optionTitle); // 옵션 타이틀 저장

            // 옵션 타이틀에 연결된 옵션 이름 저장
            for (OptionName optionName : adminRegisterProductReqDto.getOptionNames()) {
                // 옵션 이름을 저장할 때 옵션 타이틀 ID도 함께 설정
                if (optionName.getOptionTitleId() == optionTitle.getOptionTitleId()) {
                    optionName.setProductId(product.getProductId()); // productId 설정
                    optionName.setOptionTitleId(optionTitle.getOptionTitleId()); // 옵션 타이틀 ID 설정
                    productMapper.saveOptionName(optionName); // 옵션 이름 저장
                }
            }
        }
    }

    public List<SearchProductRespDto> getProducts() {
        List<Product> products = productMapper.getProducts();

        return products.stream().map(Product::toSearchProductRespDto).collect(Collectors.toList());
    }

    @Transactional
    public void editProduct(UpdateProductReqDto updateProductReqDto) {
        Product product = updateProductReqDto.toEntity();
        productMapper.updateProduct(product);
    }

    @Transactional(rollbackFor = Exception.class)
    public int deleteProduct(int productId) {
        return productMapper.deleteProduct(productId);
    }

    @Transactional(rollbackFor = Exception.class)
    public ProductDetailRespDto getProductDetail(int productId) {
        List<Product> products = productMapper.getProductDetail(productId);

        if (products == null || products.isEmpty()) {
            throw new RuntimeException("Product not found with id: " + productId);
        }

        Product product = products.get(0);

        Map<Integer, ProductDetailRespDto.OptionTitleDetail> optionTitleMap = new HashMap<>();
        List<ProductDetailRespDto.OptionNameDetail> optionNames = new ArrayList<>();

        for (Product prod : products) {
            int optionTitleId = prod.getOptionTitleId();
            if (!optionTitleMap.containsKey(optionTitleId)) {
                ProductDetailRespDto.OptionTitleDetail optionTitle = ProductDetailRespDto.OptionTitleDetail.builder()
                        .optionTitleId(optionTitleId)
                        .titleName(prod.getTitleName())
                        .build();
                optionTitleMap.put(optionTitleId, optionTitle);
            }

            ProductDetailRespDto.OptionNameDetail optionName = ProductDetailRespDto.OptionNameDetail.builder()
                    .optionNameId(prod.getOptionNameId())
                    .optionName(prod.getOptionName())
                    .optionTitleId(optionTitleId)
                    .build();
            optionNames.add(optionName);
        }

        return ProductDetailRespDto.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .categoryId(product.getCategoryId())
                .categoryName(product.getCategoryName())
                .productPrice(product.getProductPrice())
                .productImg(product.getProductImg())
                .costPrice(product.getCostPrice())
                .createDate(product.getCreateDate())
                .updateDate(product.getUpdateDate())
                .optionTitles(new ArrayList<>(optionTitleMap.values()))
                .optionNames(optionNames)
                .build();
    }


    public void insertProductCategory(AddProductCategoryReqDto addProductCategoryReqDto) {
        productMapper.saveProductCategory(addProductCategoryReqDto.toEntity());
    }

    public List<Category> getCategory() {
        return productMapper.getCategory();
    }

    public void editProductCategory(UpdateProductCategoryReqDto updateProductCategoryReqDto) {
        productMapper.updateProductCategory(updateProductCategoryReqDto.toEntity());
    }

    @Transactional(rollbackFor = Exception.class)
    public int deleteProductCategory(int categoryId) {
        return productMapper.deleteProductCategory(categoryId);
    }
}
