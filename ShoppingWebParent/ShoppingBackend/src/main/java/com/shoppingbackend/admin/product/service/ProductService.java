package com.shoppingbackend.admin.product.service;

import com.shopping.common.entity.Product;
import com.shoppingbackend.admin.product.exception.ProductNotFoundException;

import java.util.List;

public interface ProductService {
    public List<Product> listAllProducts();
    public Product getProductById(Integer id) throws ProductNotFoundException;
    public Product saveProduct(Product product);
    public void updateEnabledStatus(boolean enabledStatus, Integer id);
    public void deleteProductById(Integer id) throws ProductNotFoundException;

}
