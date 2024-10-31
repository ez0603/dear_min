package com.project.dearMin.repository;

import com.project.dearMin.entity.product.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ProductMapper {
    public int saveProduct(Product product);

    int saveOptionTitle(OptionTitle optionTitle);

    int saveOptionName(OptionName optionName);

    public List<Product> getProducts();

    public int updateProduct(Product product);

    public int deleteProduct(int productId);

    List<Product> getProductDetail(@Param("productId") int productId);

    public int saveProductCategory(Category category);

    public List<Category> getCategory();

    public int updateProductCategory(Category category);

    public int deleteProductCategory(int categoryId);

    public List<Product> getProductCategory(@Param("categoryId") int categoryId);

    List<OptionTitle> getOptionTitleByproductId(@Param("productId") int productId);

    List<OptionTitle> getAllOptionTitles();

    int updateOptionTitle(OptionTitle optionTitle);

    int deleteOptionTitle(OptionTitle optionTitle);

    Product getProductById(int productId);

    OptionName getOptionNameById(int optionNameId);

    void updateProductCostPrice(@Param("productId") int productId, @Param("costPrice") int costPrice);

    int updateOptionName(OptionName optionName);

    List<OptionName> getOptionsByOptionTitleId(int optionTitleId);

    public int saveProductMaterial(ProductMaterial productMaterial);

    int getMaterialPriceById(@Param("productMaterialId") int productMaterialId);

    int getOptionPriceById(int optionNameId);
}
