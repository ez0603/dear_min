package com.project.dearMin.repository;

import com.project.dearMin.entity.product.Category;
import com.project.dearMin.entity.product.OptionName;
import com.project.dearMin.entity.product.OptionTitle;
import com.project.dearMin.entity.product.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
}
