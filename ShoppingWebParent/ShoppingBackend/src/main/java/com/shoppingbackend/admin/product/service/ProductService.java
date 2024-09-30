package com.shoppingbackend.admin.product.service;

import com.shopping.common.entity.Product;
import com.shoppingbackend.admin.product.exception.ProductNotFoundException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    public List<Product> listAllProducts();
    public Page<Product> listProductByPage(Integer pageNumber, String sortField, String sortDir, String keyword, Integer categoryId);
    public Product getProductById(Integer id) throws ProductNotFoundException;
    public Product saveProduct(Product product);
    public Product saveProductPrice(Product product) throws ProductNotFoundException;
    public void updateEnabledStatus(boolean enabledStatus, Integer id);
    public void deleteProductById(Integer id) throws ProductNotFoundException;

}
