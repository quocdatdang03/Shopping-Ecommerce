package com.shopping.web.product.service;

import com.shopping.common.entity.Category;
import com.shopping.common.entity.Product;
import com.shopping.common.exception.ProductNotFoundException;
import com.shopping.web.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class ProductServiceImpl implements ProductService{

    public static final Integer NUMBER_PRODUCT_PER_PAGE = 12;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Page<Product> listProductByCategory(Integer pageNumber, Category category, String keyword) {
        // sort products by name in ascending order:
        Sort sort = Sort.by("name").ascending();

        Integer categoryId = category.getId();
        String allParentCategoryId = String.valueOf("-"+categoryId+"-");

        Pageable pageable = PageRequest.of(pageNumber-1, NUMBER_PRODUCT_PER_PAGE, sort);

        if(keyword!=null && !keyword.isBlank())
            return productRepository.searchProductWithKeyword(categoryId, allParentCategoryId, keyword, pageable);
        else
            return productRepository.listProductByCategory(categoryId, allParentCategoryId, pageable);
    }

    @Override
    public Product getProductByAlias(String alias) throws ProductNotFoundException {
        Product product = productRepository.findProductByAlias(alias);

        if(product==null)
            throw new ProductNotFoundException("Could not find any product with alias: "+alias);

        return product;
    }
}
