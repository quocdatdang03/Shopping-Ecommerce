package com.shopping.web.category.repository;

import com.shopping.common.entity.Category;
import com.shopping.common.entity.Product;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void testFindAllEnabledProduct() {
        List<Category> enabledCategories = categoryRepository.findALlEnabledCategory();

        enabledCategories.forEach(item -> System.out.println(item.getName() + " - " + item.isEnabled()));

        Assertions.assertThat(enabledCategories.size()).isGreaterThan(0);
    }

    @Test
    public void testFindAllEnabledCategoryHasNoChildren() {
        List<Category> enabledCategories = categoryRepository.findALlEnabledCategory();
        List<Category> enabledCategoriesHasNoChildren = new ArrayList<Category>();
        for(Category item : enabledCategories) {
            Set<Category> children = item.getChildren();
            if(children==null || children.size()==0)
            {
                enabledCategoriesHasNoChildren.add(item);
            }
        }

        enabledCategoriesHasNoChildren.forEach(item -> System.out.println(item.getName() + " - " + item.isEnabled()));

        Assertions.assertThat(enabledCategoriesHasNoChildren.size()).isGreaterThan(0);
    }

}
