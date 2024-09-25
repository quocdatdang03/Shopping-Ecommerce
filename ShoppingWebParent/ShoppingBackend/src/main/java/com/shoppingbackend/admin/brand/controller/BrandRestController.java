package com.shoppingbackend.admin.brand.controller;

import com.shopping.common.entity.Brand;
import com.shopping.common.entity.Category;
import com.shoppingbackend.admin.brand.exception.BrandNotFoundException;
import com.shoppingbackend.admin.brand.service.BrandServiceImpl;
import com.shoppingbackend.admin.category.dto.CategoryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
public class BrandRestController {

    @Autowired
    private BrandServiceImpl brandService;

    @GetMapping("/brands/{id}/categories")
    public List<CategoryDTO> listCategoriesByBrand(@PathVariable("id") Integer id) throws BrandNotFoundException {
        List<CategoryDTO> categoryDTOList = new ArrayList<CategoryDTO>();
        try {
            Brand brand = brandService.getBrandById(id);
            Set<Category> categorySet = brand.getCategories();
            for(Category category : categorySet)
            {
                categoryDTOList.add(new CategoryDTO(category.getId(), category.getName()));
            }

            return categoryDTOList;
        } catch (BrandNotFoundException e) {
            throw new BrandNotFoundException(e.getMessage());
        }
    }
}
