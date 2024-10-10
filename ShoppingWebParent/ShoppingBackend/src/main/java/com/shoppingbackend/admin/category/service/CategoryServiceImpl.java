package com.shoppingbackend.admin.category.service;

import com.shopping.common.entity.Category;
import com.shopping.common.exception.CategoryNotFoundException;
import com.shoppingbackend.admin.category.dto.CategoryPageInfo;
import com.shoppingbackend.admin.category.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CategoryServiceImpl implements  CategoryService{

    public static final int ROOT_CATEGORY_PER_PAGE = 3;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> findAll(Integer pageNumber, String sortDir, CategoryPageInfo categoryPageInfo, String keyword) {
        Sort sort = Sort.by("name");

        if (sortDir.equals("asc")) {
            sort = sort.ascending();
        } else if (sortDir.equals("desc")) {
            sort = sort.descending();
        }

        // Pagination:
        Pageable pageable = PageRequest.of(pageNumber-1, ROOT_CATEGORY_PER_PAGE, sort);

        Page<Category> pageCategories = null;

        // In case Search:
        if(keyword!=null && !keyword.isEmpty())
        {
            pageCategories = categoryRepository.searchWithKeyword(keyword, pageable);
            List<Category> searchListResult = pageCategories.getContent();
            // get Information of Page and set to obj CategoryPageInfo :
            categoryPageInfo.setTotalPages(pageCategories.getTotalPages());
            categoryPageInfo.setTotalElements(pageCategories.getTotalElements());
            return searchListResult;
        }
        else
        {
            // In case no search:
            pageCategories = categoryRepository.listRootCategories(pageable);
            List<Category> rootCategories = pageCategories.getContent();
            // get Information of Page and set to obj CategoryPageInfo :
            categoryPageInfo.setTotalPages(pageCategories.getTotalPages());
            categoryPageInfo.setTotalElements(pageCategories.getTotalElements());
            return listHierarchicalCategory(rootCategories,sortDir);
        }
    }

    /* Hierarchical List Table */
    private List<Category> listHierarchicalCategory(List<Category> rootCategories, String sortDir) {
        List<Category> hierarchicalCategories = new ArrayList<Category>();
        for(Category category : rootCategories) {
            // add root categories:
            hierarchicalCategories.add(Category.copyFull(category));

            // add sub categories:
            Set<Category> subCategories = sortSubCategories(category.getChildren(),sortDir);
            for(Category subCategory : subCategories) {
                String nameCategory = "--"+subCategory.getName();
                hierarchicalCategories.add(Category.copyFull(subCategory, nameCategory));
                listSubCategories(hierarchicalCategories, subCategory, 1, sortDir);
            }
        }
        return hierarchicalCategories;
    }

    private void listSubCategories(List<Category> hierarchicalCategories, Category parent, int subLevel, String sortDir) {
        int newSubLevel = subLevel+1;
        Set<Category> sortedSubCategories = sortSubCategories(parent.getChildren(),sortDir);
        for(Category subCategory : sortedSubCategories) {
            String newName = "";
            for(int i=0; i<newSubLevel;i++)
            {
                newName += "--";
            }
            newName += subCategory.getName();
            hierarchicalCategories.add(Category.copyFull(subCategory, newName));
            listSubCategories(hierarchicalCategories, subCategory, newSubLevel,sortDir);
        }
    }

    // Sorting subCategory:
    private Set<Category> sortSubCategories(Set<Category> subCategories, String sortDir)
    {
        Set<Category> sortedCategories = new TreeSet<Category>(new Comparator<Category>() {
            @Override
            public int compare(Category o1, Category o2) {
                if(sortDir.equals("asc"))
                    return o1.getName().compareTo(o2.getName());
                else
                    return o2.getName().compareTo(o1.getName());
            }
        });

        sortedCategories.addAll(subCategories);
        return sortedCategories;
    }

    /* End Hierarchical List Table */


    /* Hierarchical Drop Down */
    @Override
    public List<Category> listCategoryInForm() {
        Sort sort = Sort.by("name");
        List<Category> listCategoryInDB = (List<Category>) categoryRepository.findAll(sort);
        List<Category> listCategoryInForm = new ArrayList<Category>();

        for(Category category : listCategoryInDB) {
            if(category.getParent()==null) {
                // Parent Category
                listCategoryInForm.add(Category.copyFull(category));

                // SubCategory:
                Set<Category> children = sortSubCategories(category.getChildren());
                for(Category subCategory : children) {
                    String nameCategory = "--"+subCategory.getName();
                    listCategoryInForm.add(Category.copyFull(subCategory,nameCategory));
                    listChildrenInForm(listCategoryInForm, subCategory, 1);
                }
            }
        }
        return listCategoryInForm;
    }

    private void listChildrenInForm(List<Category> listCategoryInForm, Category parent, int subLevel)
    {
        int newSubLevel = subLevel+1;
        Set<Category> sortedSubCategories = sortSubCategories(parent.getChildren());
        for(Category subCategory : sortedSubCategories) {
            String newName = "";
            for(int i=0; i<newSubLevel;i++)
            {
                newName += "--";
            }
            newName += subCategory.getName();
            listCategoryInForm.add(Category.copyFull(subCategory, newName));

            listChildrenInForm(listCategoryInForm, subCategory, newSubLevel);
        }
    }

    // Sorting subCategory:
    private Set<Category> sortSubCategories(Set<Category> subCategories)
    {
        // Trong List Categories của Form thì luôn sort ascending
        Set<Category> sortedCategories = new TreeSet<Category>(new Comparator<Category>() {
            @Override
            public int compare(Category o1, Category o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        sortedCategories.addAll(subCategories);
        return sortedCategories;
    }

    /* End Hierarchical Drop Down */

    // SAVE :
    @Override
    public Category saveCategory(Category category) {
        Category parent = category.getParent();

        // set all parent ids for category:
        if(parent!=null)
        {
            String allParentIds = parent.getAllParentIds()==null ? "-" : parent.getAllParentIds();
            allParentIds += parent.getId()+"-";
            category.setAllParentIds(allParentIds);
        }
        return categoryRepository.save(category);
    }

    @Override
    public Category getCategoryById(Integer id) throws CategoryNotFoundException {
        try {
            return categoryRepository.findById(id).get();
        }
        catch(NoSuchElementException e)
        {
            throw new CategoryNotFoundException("Could not find any Category with id: "+id +"!");
        }

    }

    @Override
    public void updateEnabledStatus(boolean enabledStatus, Integer id) {
        categoryRepository.updateEnabledStatus(enabledStatus, id);
    }

    @Override
    public void deleteCategory(Integer id) throws CategoryNotFoundException {
        Integer resultCount = categoryRepository.countById(id);
        if(resultCount==null || resultCount==0)
            throw new CategoryNotFoundException("Could not find any Category with id: "+id +"!");

        categoryRepository.deleteById(id);

    }
}
