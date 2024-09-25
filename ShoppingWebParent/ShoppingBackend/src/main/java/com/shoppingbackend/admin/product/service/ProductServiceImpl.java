package com.shoppingbackend.admin.product.service;

import com.shopping.common.entity.Product;
import com.shoppingbackend.admin.product.exception.ProductNotFoundException;
import com.shoppingbackend.admin.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Product> listAllProducts() {
        return (List<Product>) productRepository.findAll();
    }

    @Override
    public Product saveProduct(Product product) {

        if(product.getId()==null) {
            // In case create new product -> set current date for created time field
            product.setCreatedTime(new Date());
        }

        if(product.getAlias()==null || product.getAlias().isEmpty())
        {
            // In case alias doesn't have specified value -> set alias value by default is product name (replace white spaces to dashes character):
            product.setAlias(product.getName().trim().replaceAll(" ", "-"));
        }
        else {
            // In case alias has specified value -> replace value itself from white spaces to dashes character :
            product.setAlias(product.getAlias().trim().replaceAll(" ", "-"));
        }

        // Create or Edit always set updatedTime :
        product.setUpdatedTime(new Date()); // set updated time is current date


        return productRepository.save(product);
    }

    @Override
    public void updateEnabledStatus(boolean enabledStatus, Integer id) {
        productRepository.updateEnabledStatus(enabledStatus, id);
    }

    @Override
    public void deleteProductById(Integer id) throws ProductNotFoundException {
        // check if product not found:
        Integer countResult = productRepository.countById(id);
        if(countResult==null || countResult==0)
            throw new ProductNotFoundException("Could not find any product with id: "+id);

        // delete product :
        productRepository.deleteById(id);
    }

}
