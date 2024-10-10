package com.shopping.web.category.service;

import com.shopping.common.entity.Category;
import com.shopping.common.exception.CategoryNotFoundException;
import com.shopping.web.category.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> listAllEnabledCategoryHasNoChildren() {
        List<Category> enabledCategories = categoryRepository.findALlEnabledCategory();
        List<Category> enabledCategoriesHasNoChildren = new ArrayList<Category>();
        for(Category item : enabledCategories) {
            Set<Category> children = item.getChildren();
            if(children==null || children.size()==0)
            {
                enabledCategoriesHasNoChildren.add(item);
            }
        }

        return enabledCategoriesHasNoChildren;
    }

    @Override
    public Category getCategoryByAlias(String alias) throws CategoryNotFoundException{
        Category category = categoryRepository.findEnabledCategoryByAlias(alias);

        if(category==null)
            throw new CategoryNotFoundException("Could not find any category with alias: "+alias);

        return categoryRepository.findEnabledCategoryByAlias(alias);
    }

    @Override
    public List<Category> listCategoryParents(Category child) {
        List<Category> categoryParentList = new ArrayList<Category>();

        // get Category parent of child:
        Category parent = child.getParent();

        while(parent!=null) {
            // add vào vị trí đầu tiên của List (tk parent có mức to nhất sẽ nằm đầu tiên):
            categoryParentList.add(0, parent);
            // get Category parent of parent :
            parent = parent.getParent();
        }

        // add child vào (và nó là tk cuối cùng trong List):
        categoryParentList.add(child);

        return categoryParentList;
    }
}
