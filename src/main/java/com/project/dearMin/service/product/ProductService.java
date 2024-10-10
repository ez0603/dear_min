package com.project.dearMin.service.product;

import com.project.dearMin.dto.product.request.*;
import com.project.dearMin.dto.product.response.OptionTitlesRespDto;
import com.project.dearMin.dto.product.response.OptionsRespDto;
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

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductMapper productMapper;

//    @Transactional(rollbackFor = Exception.class)
//    public void saveProduct(AdminRegisterProductReqDto adminRegisterProductReqDto) {
//        // 1. 상품 저장 (여기서 productId가 생성됨)
//        Product product = adminRegisterProductReqDto.toEntity();
//        productMapper.saveProduct(product);
//
//        int productId = product.getProductId(); // 생성된 productId 가져오기
//
//        // 2. 옵션 타이틀 및 옵션 이름 저장
//        for (OptionTitle optionTitle : adminRegisterProductReqDto.getOptionTitles()) {
//            optionTitle.setProductId(productId); // 생성된 productId 설정
//            productMapper.saveOptionTitle(optionTitle); // 옵션 타이틀 저장
//
//            // 옵션 타이틀에 연결된 옵션 이름 저장
//            for (OptionName optionName : optionTitle.getOptionNames()) {
//                optionName.setProductId(productId); // 생성된 productId 설정
//                optionName.setOptionTitleId(optionTitle.getOptionTitleId()); // 옵션 타이틀 ID 설정
//                productMapper.saveOptionName(optionName); // 옵션 이름 저장
//            }
//        }
//    }

    @Transactional(rollbackFor = Exception.class)
    public void saveProduct(AdminRegisterProductReqDto adminRegisterProductReqDto) {
        // 1. 상품 저장 (여기서 productId가 생성됨)
        Product product = adminRegisterProductReqDto.toEntity();
        productMapper.saveProduct(product);

        int productId = product.getProductId(); // 생성된 productId 가져오기

        int totalCostPrice = 0; // costPrice를 계산하기 위한 변수

        // 2. 옵션 타이틀 및 옵션 이름 저장
        for (OptionTitle optionTitle : adminRegisterProductReqDto.getOptionTitles()) {
            optionTitle.setProductId(productId); // 생성된 productId 설정
            productMapper.saveOptionTitle(optionTitle); // 옵션 타이틀 저장

            // 옵션 타이틀에 연결된 옵션 이름 저장
            for (OptionName optionName : optionTitle.getOptionNames()) {
                optionName.setProductId(productId); // 생성된 productId 설정
                optionName.setOptionTitleId(optionTitle.getOptionTitleId()); // 옵션 타이틀 ID 설정

                // 3. 옵션 가격과 해당 옵션 수량을 이용하여 costPrice 계산
                int optionPrice = optionName.getOptionPrice(); // 옵션 가격 가져오기
                int productQuantity = optionName.getProductQuantity(); // 옵션에서 productQuantity 가져오기
                int optionCost = optionPrice / productQuantity; // 옵션 가격을 해당 옵션 수량으로 나눈 값
                totalCostPrice += optionCost; // 총 costPrice에 더해줌

                productMapper.saveOptionName(optionName); // 옵션 이름 저장
            }
        }

        // 4. 최종적으로 계산된 costPrice를 product에 저장
        product.setCostPrice(totalCostPrice);

        // 5. productId와 costPrice를 updateProductCostPrice 메서드에 전달
        productMapper.updateProductCostPrice(productId, totalCostPrice); // DB에 costPrice 업데이트
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

    public List<SearchProductRespDto> getProductCategory(int categoryId) {
        List<Product> products = productMapper.getProductCategory(categoryId);

        return products.stream().map(Product::toSearchProductRespDto).collect(Collectors.toList());
    }

    public void insertOptionTitle(AddOptionTitleReqDto addOptionTitleReqDto) {
        productMapper.saveOptionTitle(addOptionTitleReqDto.toEntity());
    }

    // 제품 옵션 타이틀 조회
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

        // 옵션 타이틀의 ID와 이름을 리스트에 추가
        for (OptionTitle optionTitle : optionTitles) {
            optionTitleIds.add(optionTitle.getOptionTitleId());
            optionTitleNames.add(optionTitle.getTitleName());
        }

        optionTitlesRespDto.setOptionTitlesId(optionTitleIds);
        optionTitlesRespDto.setOptionTitleNames(optionTitleNames);

        return optionTitlesRespDto;
    }

    // 옵션 타이틀 수정
    public void editOptionTitle(UpdateOptionTitleReqDto updateOptionTitleReqDto) {
        productMapper.updateOptionTitle(updateOptionTitleReqDto.toEntity());
    }

    // 옵션 타이틀 삭제
    @Transactional(rollbackFor = Exception.class)
    public void deleteOptionTitle(DeleteOptionTitleReqDto deleteOptionTitleReqDto) {
        productMapper.deleteOptionTitle(deleteOptionTitleReqDto.toEntity());
    }

    // 옵션 이름 등록
    @Transactional(rollbackFor = Exception.class)
    public void insertOptionName(AddOptionNameReqDto addOptionNameReqDto) {
        // 옵션 저장
        OptionName optionName = addOptionNameReqDto.toEntity();
        productMapper.saveOptionName(optionName);

        // 옵션 가격과 수량 가져오기
        int optionPrice = optionName.getOptionPrice();
        int productQuantity = optionName.getProductQuantity();

        // 단가 계산 (단가 = 옵션 가격 / 수량, 실수 계산을 위해 double로 변환)
        double unitPrice = (double) optionPrice / productQuantity;

        // 기존 cost_price 가져오기
        Product product = productMapper.getProductById(optionName.getProductId());
        int currentCostPrice = product.getCostPrice();

        // 새로운 옵션 가격을 더한 새로운 cost_price 계산
        int updatedCostPrice = currentCostPrice + (int) unitPrice;

        // product의 cost_price 업데이트
        productMapper.updateProductCostPrice(product.getProductId(), updatedCostPrice);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editOptionName(UpdateOptionNameReqDto updateOptionNameReqDto) {
        // 기존 옵션 정보 가져오기
        OptionName existingOptionName = productMapper.getOptionNameById(updateOptionNameReqDto.getOptionNameId());

        // 기존 옵션 가격과 수량 가져오기
        int existingOptionPrice = existingOptionName.getOptionPrice();
        int existingProductQuantity = existingOptionName.getProductQuantity();

        // 기존 단가 계산
        double existingUnitPrice = (double) existingOptionPrice / existingProductQuantity;

        // 기존 cost_price 가져오기
        Product product = productMapper.getProductById(existingOptionName.getProductId());
        int currentCostPrice = product.getCostPrice();

        // 기존 옵션 가격을 뺀 새로운 cost_price 계산
        int updatedCostPrice = currentCostPrice - (int) existingUnitPrice;

        // 옵션 업데이트
        OptionName updatedOptionName = updateOptionNameReqDto.toEntity();
        productMapper.updateOptionName(updatedOptionName);

        // 수정된 옵션 가격과 수량 가져오기
        int newOptionPrice = updatedOptionName.getOptionPrice();
        int newProductQuantity = updatedOptionName.getProductQuantity();

        // 수정된 단가 계산
        double newUnitPrice = (double) newOptionPrice / newProductQuantity;

        // 수정된 옵션 가격을 더한 새로운 cost_price 계산
        updatedCostPrice = updatedCostPrice + (int) newUnitPrice;

        // product의 cost_price 업데이트
        productMapper.updateProductCostPrice(product.getProductId(), updatedCostPrice);
    }

    // 제품 별 옵션 조회
    @Transactional(rollbackFor = Exception.class)
    public OptionsRespDto getOptionsByMenuId(int productId) {
        List<OptionName> options = productMapper.getOptionsByMenuId(productId);
        Set<Integer> optionTitlesIdSet = new HashSet<>();
        Set<String> optionTitleNamesSet = new HashSet<>();
        List<Integer> optionNameIds = new ArrayList<>();
        List<String> optionNames = new ArrayList<>();

        for (OptionName optionName : options) {
            optionTitlesIdSet.add(optionName.getOptionTitle().getOptionTitleId());
            optionTitleNamesSet.add(optionName.getOptionTitle().getTitleName());
            optionNameIds.add(optionName.getOptionNameId());
            optionNames.add(optionName.getOptionName());
        }

        return OptionsRespDto.builder()
                .productId(productId)
                .optionTitlesId(new ArrayList<>(optionTitlesIdSet))
                .optionTitleNames(new ArrayList<>(optionTitleNamesSet))
                .optionNameIds(optionNameIds)
                .optionNames(optionNames)
                .build();
    }

}
