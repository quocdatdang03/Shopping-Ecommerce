package com.shoppingbackend.admin.category.service;

import com.shopping.common.entity.Category;
import com.shopping.common.exception.CategoryNotFoundException;
import com.shoppingbackend.admin.category.dto.CategoryPageInfo;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CategoryService {
    public List<Category> findAll(Integer pageNumber, String sortDir, CategoryPageInfo categoryPageInfo, String keyword);
    public List<Category> listCategoryInForm();
    public Category saveCategory(Category category);
    public Category getCategoryById(Integer id) throws CategoryNotFoundException, CategoryNotFoundException;
    public void updateEnabledStatus(boolean enabledStatus, Integer id);
    public void deleteCategory(Integer id) throws CategoryNotFoundException;
}
