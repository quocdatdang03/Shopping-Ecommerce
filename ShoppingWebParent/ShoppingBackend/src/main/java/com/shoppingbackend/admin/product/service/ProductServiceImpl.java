package com.shoppingbackend.admin.product.service;

import com.shopping.common.entity.Product;
import com.shoppingbackend.admin.product.exception.ProductNotFoundException;
import com.shoppingbackend.admin.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ProductServiceImpl implements ProductService {

    public static final Integer NUMBER_PRODUCT_PER_PAGE = 5;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Product> listAllProducts() {
        return (List<Product>) productRepository.findAll();
    }

    @Override
    public Page<Product> listProductByPage(Integer pageNumber, String sortField, String sortDir, String keyword, Integer categoryId) {
        Sort sort;
        if(sortField==null || sortField.isEmpty())
            sort = Sort.by("name");
        else
            sort = Sort.by(sortField);

        sort = (sortDir.equals("asc")) ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNumber-1,NUMBER_PRODUCT_PER_PAGE,sort);


        if(keyword!=null && !keyword.isBlank())
        {
            // in case filter by category and search with keyword :
            if(categoryId!=null && categoryId>0)
            {
                String parentCategoryId = "-"+String.valueOf(categoryId)+"-";
                return productRepository.searchWithKeywordAndFilterByCategory(categoryId, parentCategoryId, keyword.trim(), pageable);
            }

            // else in case search with keyword only
            return productRepository.searchWithKeyword(keyword.trim(), pageable);
        }


        // in case filter by category without search with keyword
        if(categoryId!=null && categoryId>0) {
            String parentCategoryId = "-"+String.valueOf(categoryId)+"-";
            return productRepository.findAllProductByCategory(categoryId, parentCategoryId, pageable);
        }

        return productRepository.findAll(pageable);
    }

    @Override
    public Product getProductById(Integer id) throws ProductNotFoundException {
        try {
            Product product = productRepository.findById(id).get();
            return product;
        }
        catch(NoSuchElementException e)
        {
            throw new ProductNotFoundException("Could not find any product with id : "+id);
        }
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
    public Product saveProductPrice(Product product) throws ProductNotFoundException{
        // This is method for Authorize Role Salesperson (Update only price, cost, discount percent)
        try {
            Product productInDB = productRepository.findById(product.getId()).get();
            productInDB.setPrice(product.getPrice());
            productInDB.setCost(product.getCost());
            productInDB.setDiscountPercent(product.getDiscountPercent());

            return productRepository.save(productInDB);
        }
        catch(NoSuchElementException e)
        {
            throw new ProductNotFoundException("Could not find any product with id : "+product.getId());
        }
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
