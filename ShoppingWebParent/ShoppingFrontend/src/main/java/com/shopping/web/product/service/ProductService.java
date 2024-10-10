package com.shopping.web.product.service;

import com.shopping.common.entity.Category;
import com.shopping.common.entity.Product;
import com.shopping.common.exception.ProductNotFoundException;
import org.springframework.data.domain.Page;


public interface ProductService {
    public Page<Product> listProductByCategory(Integer pageNumber, Category category);
    public Product getProductByAlias(String alias) throws ProductNotFoundException;
}
