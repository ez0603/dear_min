package com.project.dearMin.service.product;

import com.project.dearMin.dto.product.request.*;
import com.project.dearMin.dto.product.response.OptionTitlesRespDto;
import com.project.dearMin.dto.product.response.OptionsRespDto;
import com.project.dearMin.dto.product.response.ProductDetailRespDto;
import com.project.dearMin.dto.product.response.SearchProductRespDto;
import com.project.dearMin.entity.product.*;
import com.project.dearMin.repository.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Transactional(rollbackFor = Exception.class)
    public void saveProduct(AdminRegisterProductReqDto adminRegisterProductReqDto) {
        productMapper.saveProduct(adminRegisterProductReqDto.toEntity());
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
                .productCount(product.getProductCount())
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

    public List<SearchProductRespDto> getProductCategory(int categoryId) {
        List<Product> products = productMapper.getProductCategory(categoryId);
        return products.stream().map(Product::toSearchProductRespDto).collect(Collectors.toList());
    }

    public void insertOptionTitle(AddOptionTitleReqDto addOptionTitleReqDto) {
        productMapper.saveOptionTitle(addOptionTitleReqDto.toEntity());
    }

    @Transactional(rollbackFor = Exception.class)
    public OptionTitlesRespDto getOptionTitles(int productId) {
        List<OptionTitle> optionTitles = productMapper.getOptionTitleByproductId(productId);
        OptionTitlesRespDto optionTitlesRespDto = new OptionTitlesRespDto();

        List<Integer> optionTitleIds = new ArrayList<>();
        List<String> optionTitleNames = new ArrayList<>();

        for (OptionTitle optionTitle : optionTitles) {
            optionTitleIds.add(optionTitle.getOptionTitleId());
            optionTitleNames.add(optionTitle.getTitleName());
        }

        optionTitlesRespDto.setOptionTitlesId(optionTitleIds);
        optionTitlesRespDto.setOptionTitleNames(optionTitleNames);

        return optionTitlesRespDto;
    }

    @Transactional(rollbackFor = Exception.class)
    public OptionTitlesRespDto getAllOptionTitles() {
        List<OptionTitle> optionTitles = productMapper.getAllOptionTitles();
        OptionTitlesRespDto optionTitlesRespDto = new OptionTitlesRespDto();

        List<Integer> optionTitleIds = new ArrayList<>();
        List<String> optionTitleNames = new ArrayList<>();

        for (OptionTitle optionTitle : optionTitles) {
            optionTitleIds.add(optionTitle.getOptionTitleId());
            optionTitleNames.add(optionTitle.getTitleName());
        }

        optionTitlesRespDto.setOptionTitlesId(optionTitleIds);
        optionTitlesRespDto.setOptionTitleNames(optionTitleNames);

        return optionTitlesRespDto;
    }

    public void editOptionTitle(UpdateOptionTitleReqDto updateOptionTitleReqDto) {
        productMapper.updateOptionTitle(updateOptionTitleReqDto.toEntity());
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteOptionTitle(DeleteOptionTitleReqDto deleteOptionTitleReqDto) {
        productMapper.deleteOptionTitle(deleteOptionTitleReqDto.toEntity());
    }

    @Transactional(rollbackFor = Exception.class)
    public void insertOptionName(AddOptionNameReqDto addOptionNameReqDto) {
        OptionName optionName = addOptionNameReqDto.toEntity();
        productMapper.saveOptionName(optionName);

        int optionPrice = optionName.getOptionPrice();

        int productId = optionName.getProductId();
        Product product = productMapper.getProductById(productId);
        int currentCostPrice = product.getCostPrice();
        int updatedCostPrice = currentCostPrice + optionPrice;

        productMapper.updateProductCostPrice(product.getProductId(), updatedCostPrice);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editOptionName(UpdateOptionNameReqDto updateOptionNameReqDto) {
        OptionName existingOptionName = productMapper.getOptionNameById(updateOptionNameReqDto.getOptionNameId());

        int existingOptionPrice = existingOptionName.getOptionPrice();
        Product product = productMapper.getProductById(existingOptionName.getProductId());
        int currentCostPrice = product.getCostPrice();
        int updatedCostPrice = currentCostPrice - existingOptionPrice;

        OptionName updatedOptionName = updateOptionNameReqDto.toEntity();
        productMapper.updateOptionName(updatedOptionName);

        int newOptionPrice = updatedOptionName.getOptionPrice();
        updatedCostPrice = updatedCostPrice + newOptionPrice;

        productMapper.updateProductCostPrice(product.getProductId(), updatedCostPrice);
    }

    @Transactional(rollbackFor = Exception.class)
    public OptionsRespDto getOptionsByOptionTitleId(int optionTitleId) {
        List<OptionName> options = productMapper.getOptionsByOptionTitleId(optionTitleId);

        Set<Integer> optionTitlesIdSet = new HashSet<>();
        Set<String> optionTitleNamesSet = new HashSet<>();
        List<Integer> optionNameIds = new ArrayList<>();
        List<String> optionNames = new ArrayList<>();
        List<Integer> optionPrices = new ArrayList<>();
        List<Integer> optionCounts = new ArrayList<>();

        for (OptionName optionName : options) {
            optionTitlesIdSet.add(optionName.getOptionTitle().getOptionTitleId());
            optionTitleNamesSet.add(optionName.getOptionTitle().getTitleName());
            optionNameIds.add(optionName.getOptionNameId());
            optionNames.add(optionName.getOptionName());
            optionPrices.add(optionName.getOptionPrice());
            optionCounts.add(optionName.getOptionCount());
        }

        return OptionsRespDto.builder()
                .optionTitlesId(new ArrayList<>(optionTitlesIdSet))
                .optionTitleNames(new ArrayList<>(optionTitleNamesSet))
                .optionNameIds(optionNameIds)
                .optionNames(optionNames)
                .optionPrices(optionPrices)
                .optionCounts(optionCounts)
                .build();
    }


//    @Transactional(rollbackFor = Exception.class)
//    public void addProductMaterial(ProductMaterialReqDto productMaterialReqDto) {
//
//        ProductMaterial productMaterial = new ProductMaterial();
//        productMaterial.setProductId(productMaterialReqDto.getProductId());
//        productMaterial.setOptionNameId(productMaterialReqDto.getOptionNameId());
//
//        productMapper.saveProductMaterial(productMaterial);
//    }

    @Transactional(rollbackFor = Exception.class)
    public int addProductMaterial(AdminRegisterProductReqDto productReqDto, List<Integer> optionNameIds, List<Integer> productQuantities) {

        Product product = productReqDto.toEntity();
        productMapper.saveProduct(product);
        int productId = product.getProductId();

        for (int i = 0; i < optionNameIds.size(); i++) {
            ProductMaterial productMaterial = new ProductMaterial();
            productMaterial.setProductId(productId);
            productMaterial.setOptionNameId(optionNameIds.get(i));
            productMaterial.setProductQuantity(productQuantities.get(i));
            productMapper.saveProductMaterial(productMaterial);
        }
        return productId;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateProductMaterial(int productId, AdminRegisterProductReqDto productReqDto, List<Integer> optionNameIds, List<Integer> productQuantities) {
        Product product = productReqDto.toEntity();
        product.setProductId(productId);
        productMapper.updateProduct(product);

        productMapper.deleteProductMaterials(productId);
        for (int i = 0; i < optionNameIds.size(); i++) {
            ProductMaterial productMaterial = new ProductMaterial();
            productMaterial.setProductId(productId);
            productMaterial.setOptionNameId(optionNameIds.get(i));
            productMaterial.setProductQuantity(productQuantities.get(i));
            productMapper.saveProductMaterial(productMaterial);
        }
    }


}
