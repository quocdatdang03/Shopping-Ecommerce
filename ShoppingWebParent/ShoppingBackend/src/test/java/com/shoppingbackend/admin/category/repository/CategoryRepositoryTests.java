package com.shoppingbackend.admin.category.repository;

import com.shopping.common.entity.Category;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Set;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CategoryRepositoryTests {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void createParentCategory() {
        Category category = new Category("Electronics");
        Category savedCategory = categoryRepository.save(category);

        Assertions.assertThat(savedCategory.getId()).isGreaterThan(0);

    }

    @Test
    public void createSubCategory() {
        Category parentCategory = categoryRepository.findById(4).get();

        Category subCategory = new Category("Smartphones", parentCategory);

        Category savedSubCategory = categoryRepository.save(subCategory);

        Assertions.assertThat(savedSubCategory.getId()).isGreaterThan(0);
    }

    @Test
    public void testGetCategoryAndSubCategoryItself() {
        Category parentCategory = categoryRepository.findById(4).get();
        System.out.println(parentCategory.getName());

        // get subCategory:
        Set<Category> categories = parentCategory.getChildren();
        for(Category category : categories)
        {
            System.out.println("--"+category.getName());
        }

        Assertions.assertThat(categories.size()).isGreaterThan(0);
    }

    @Test
    public void testPrintHierarchicalCategory() {
        Iterable<Category> categories = categoryRepository.findAll();
        for(Category category : categories)
        {
            if(category.getParent()==null)
            {
                System.out.println(category.getName());

                // SubCategory level 1:
                Set<Category> children = category.getChildren();
                for(Category subCategory : children) {
                    System.out.println("--"+subCategory.getName());
                    printChildren(subCategory, 1);
                }
            }
        }
    }

    @Test
    public void printChildren(Category parent, int subLevel) {
        int newSubLevel = subLevel+1;
        for(Category subCategory : parent.getChildren()) {
            for(int i=0; i<newSubLevel; i++) {
                System.out.print("--");
            }
            System.out.println(subCategory.getName());

            printChildren(subCategory, newSubLevel);
        }
    }
}
