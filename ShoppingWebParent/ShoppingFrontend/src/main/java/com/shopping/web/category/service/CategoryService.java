package com.shopping.web.category.service;

import com.shopping.common.entity.Category;
import com.shopping.common.exception.CategoryNotFoundException;

import java.util.List;

public interface CategoryService {
    public List<Category> listAllEnabledCategoryHasNoChildren();
    public Category getCategoryByAlias(String alias) throws CategoryNotFoundException;
    public List<Category> listCategoryParents(Category child);
}
